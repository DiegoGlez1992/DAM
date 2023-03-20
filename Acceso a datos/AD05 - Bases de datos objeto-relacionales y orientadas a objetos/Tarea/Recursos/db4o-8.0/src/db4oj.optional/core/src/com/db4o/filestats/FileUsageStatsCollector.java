/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.filestats;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.collections.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

/**
 * Collects database file usage statistics and prints them
 * to the console.
 * @sharpen.partial
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FileUsageStatsCollector {
	
	private final Map<String, MiscCollector> MISC_COLLECTORS;
	
	/**
	 * @sharpen.ignore
	 */
	@decaf.RemoveFirst(decaf.Platform.JDK11)
	private void registerBigSetCollector() {
		MISC_COLLECTORS.put(BigSet.class.getName(), new BigSetMiscCollector());
	}
	
	/**
	 * Usage: FileUsageStatsCollector <db path> [<collect gaps (true|false)>]
	 */
	public static void main(String[] args) {
		String dbPath = args[0];
		boolean collectSlots = args.length > 1 && "true".equals(args[1]);
		System.out.println(dbPath + ": " + new File(dbPath).length());
		FileUsageStats stats = runStats(dbPath, collectSlots);
		System.out.println(stats);
	}

	public static FileUsageStats runStats(String dbPath) {
		return runStats(dbPath, false);
	}

	public static FileUsageStats runStats(String dbPath, boolean collectSlots) {
		return runStats(dbPath, collectSlots, Db4oEmbedded.newConfiguration());
	}

	public static FileUsageStats runStats(String dbPath, boolean collectSlots, EmbeddedConfiguration config) {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, dbPath);
		try {
			return new FileUsageStatsCollector(db, collectSlots).collectStats();
		}
		finally {
			db.close();
		}
	}
	
	private final LocalObjectContainer _db;
	private FileUsageStats _stats;
	private BlockConverter _blockConverter;
	private final SlotMap _slots;

	public FileUsageStatsCollector(ObjectContainer db, boolean collectSlots) {
		MISC_COLLECTORS = new HashMap<String, MiscCollector>();
		registerBigSetCollector();
		_db = (LocalObjectContainer) db;
		byte blockSize = _db.blockSize();
		_blockConverter = blockSize > 1 ? (BlockConverter)new BlockSizeBlockConverter(blockSize) : (BlockConverter)new DisabledBlockConverter();
		_slots = collectSlots ? (SlotMap)new SlotMapImpl(_db.fileLength()) : (SlotMap)new NullSlotMap();
	}

	public FileUsageStats collectStats() {
		
		_stats = new FileUsageStats(
			_db.fileLength(), 
			fileHeaderUsage(), 
			idSystemUsage(), 
			freespace(), 
			classMetadataUsage(), 
			freespaceUsage(), 
			uuidUsage(), 
			_slots, 
			commitTimestampUsage());
		
		Set<ClassNode> classRoots = ClassNode.buildHierarchy(_db.classCollection());
		for (ClassNode classRoot : classRoots) {
			collectClassSlots(classRoot.classMetadata());
			collectClassStats(_stats, classRoot);
		}
		return _stats;
	}

	private long collectClassStats(FileUsageStats stats, ClassNode classNode) {
		long subClassSlotUsage = 0;
		for(ClassNode curSubClass : classNode.subClasses()) {
			subClassSlotUsage += collectClassStats(stats, curSubClass);
		}
		ClassMetadata clazz = classNode.classMetadata();
		long classIndexUsage = 0;
		if(clazz.hasClassIndex()) {
			classIndexUsage = bTreeUsage(((BTreeClassIndexStrategy)clazz.index()).btree());
		}
		long fieldIndexUsage = fieldIndexUsage(clazz);
		InstanceUsage instanceUsage = classSlotUsage(clazz);
		long totalSlotUsage = instanceUsage.slotUsage;
		long ownSlotUsage = totalSlotUsage - subClassSlotUsage;
		ClassUsageStats classStats = new ClassUsageStats(clazz.getName(), ownSlotUsage, classIndexUsage, fieldIndexUsage, instanceUsage.miscUsage);
		stats.addClassStats(classStats);
		return totalSlotUsage;
	}

	private long fieldIndexUsage(ClassMetadata classMetadata) {
		final LongByRef usage = new LongByRef(); 
		classMetadata.traverseDeclaredFields(new Procedure4<FieldMetadata>() {
			public void apply(FieldMetadata field) {
				if(field.isVirtual() || !field.hasIndex()) {
					return;
				}
				usage.value += bTreeUsage(field.getIndex(_db.systemTransaction()));
			}
		});
		return usage.value;
	}
	
	private long bTreeUsage(BTree btree) {
		return bTreeUsage(_db, btree, _slots);
	}
	
	static long bTreeUsage(LocalObjectContainer db, BTree btree, SlotMap slotMap) {
		return bTreeUsage(db.systemTransaction(), db.idSystem(), btree, slotMap);
	}

	private static long bTreeUsage(Transaction transaction, IdSystem idSystem, BTree btree, SlotMap slotMap) {
		Iterator4<Integer> nodeIter = btree.allNodeIds(transaction);
		Slot btreeSlot = idSystem.committedSlot(btree.getID());
		slotMap.add(btreeSlot);
		long usage = btreeSlot.length();
		while(nodeIter.moveNext()) {
			Integer curNodeId = nodeIter.current();
			Slot slot = idSystem.committedSlot(curNodeId);
			slotMap.add(slot);
			usage += slot.length();
		}
		return usage;
	}

	private InstanceUsage classSlotUsage(ClassMetadata clazz) {
		if(!clazz.hasClassIndex()) {
			return new InstanceUsage(0, 0);
		}
		final MiscCollector miscCollector = MISC_COLLECTORS.get(clazz.getName());
		final LongByRef slotUsage = new LongByRef();
		final LongByRef miscUsage = new LongByRef();
		BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
		index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
			public void visit(Integer id) {
				slotUsage.value += slotSizeForId(id);
				if(miscCollector != null) {
					miscUsage.value += miscCollector.collectFor(_db, id, _slots);
				}
			}
		});
		return new InstanceUsage(slotUsage.value, miscUsage.value);
	}

	private void collectClassSlots(ClassMetadata clazz) {
		if(!clazz.hasClassIndex()) {
			return;
		}
		BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
		index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
			public void visit(Integer id) {
				_slots.add(slot(id));
			}
		});
	}

	private long freespace() {
		_db.freespaceManager().traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				_slots.add(slot);
			}
		});
		return _db.freespaceManager().totalFreespace();
	}

	private long freespaceUsage() {
		return freespaceUsage(_db.freespaceManager());
	}

	private long freespaceUsage(FreespaceManager fsm) {
		if(fsm instanceof InMemoryFreespaceManager) {
			return 0;
		}
		if(fsm instanceof BTreeFreespaceManager) {
			return bTreeUsage((BTree)fieldValue(fsm, "_slotsByAddress")) + bTreeUsage((BTree)fieldValue(fsm, "_slotsByLength")); 
		}
		if(fsm instanceof BlockAwareFreespaceManager) {
			return freespaceUsage((FreespaceManager) fieldValue(fsm, "_delegate"));
		}
		throw new IllegalStateException("Unknown freespace manager: " + fsm);
	}
	
	private long idSystemUsage() {
		final IntByRef usage = new IntByRef();
		_db.idSystem().traverseOwnSlots(new Procedure4<Pair<Integer, Slot>>() {			
			public void apply(Pair<Integer, Slot> idSlot) {
				Slot slot = idSlot.second;
				usage.value += slot.length();
				_slots.add(slot);
			}
		});
		return usage.value;
	}
	
	private long classMetadataUsage() {
		Slot classRepositorySlot = slot(_db.classCollection().getID());
		_slots.add(classRepositorySlot);
		long usage = classRepositorySlot.length();
		Iterator4<Integer> classIdIter = _db.classCollection().ids();
		while(classIdIter.moveNext()) {
			int curClassId = classIdIter.current();
			Slot classSlot = slot(curClassId);
			_slots.add(classSlot);
			usage += classSlot.length();
		}
		return usage;
	}
	
	private long fileHeaderUsage() {
		int headerLength = _db.getFileHeader().length();
		int usage = _blockConverter.blockAlignedBytes(headerLength);
		FileHeaderVariablePart2 variablePart = (FileHeaderVariablePart2)fieldValue(_db.getFileHeader(), "_variablePart");
		usage += _blockConverter.blockAlignedBytes(variablePart.marshalledLength());
		_slots.add(new Slot(0, headerLength));
		_slots.add(new Slot(variablePart.address(), variablePart.marshalledLength()));
		return usage;
	}
	
	private long uuidUsage() {
		if(_db.systemData().uuidIndexId() <= 0) {
			return 0;
		}
		BTree index = _db.uUIDIndex().getIndex(_db.systemTransaction());
		return index == null ? 0 : bTreeUsage(index);
	}

	private long commitTimestampUsage() {
		LocalTransaction st = (LocalTransaction) _db.systemTransaction();
		CommitTimestampSupport commitTimestampSupport = st.commitTimestampSupport();
		if(commitTimestampSupport == null){
			return 0;
		}
		BTree idToTimestampBtree = commitTimestampSupport.idToTimestamp();
		long idToTimestampBTreeSize = idToTimestampBtree == null ? 0 : bTreeUsage(idToTimestampBtree);

		BTree timestampToIdBtree = commitTimestampSupport.timestampToId();
		long timestampToIdBTreeSize = timestampToIdBtree == null ? 0 : bTreeUsage(timestampToIdBtree);
		
		return idToTimestampBTreeSize + timestampToIdBTreeSize;
	}

	private int slotSizeForId(int id) {
		return slot(id).length();
	}

	private static <T> T fieldValue(Object parent, String fieldName) {
		return (T) Reflection4.getFieldValue(parent, fieldName);
	}
	
	private static class InstanceUsage {
		public final long slotUsage;
		public final long miscUsage;
		
		public InstanceUsage(long slotUsage, long miscUsage) {
			this.slotUsage = slotUsage;
			this.miscUsage = miscUsage;
		}
	}
	
	private Slot slot(int id) {
		return _db.idSystem().committedSlot(id);
	}
}
