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
package com.db4o.defragment;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.slots.*;

/**
 * Database based mapping for IDs during a defragmentation run.
 * Use this mapping to keep memory consumption lower than when
 * using the {@link InMemoryIdMapping}.
 * 
 * @see Defragment
 */
public class DatabaseIdMapping extends AbstractIdMapping {

	private String _fileName;

	private LocalObjectContainer _mappingDb;

	private BTree _idTree;
	private BTree _slotTree;

	private MappedIDPair _cache = new MappedIDPair(0, 0);
	
	private BTreeSpec _treeSpec=null;
	
	private int _commitFrequency=0; // <=0 : never commit
	private int _idInsertCount=0;
	private int _slotInsertCount=0;
	
	/**
	 * Will maintain the ID mapping as a BTree in the file with the given path.
	 * If a file exists in this location, it will be DELETED.
	 * 
	 * Node size and cache height of the tree will be the default values used by
	 * the BTree implementation. The tree will never commit.
	 * 
	 * @param fileName The location where the BTree file should be created.
	 */
	public DatabaseIdMapping(String fileName) {
		this(fileName,null,0);
	}

	/**
	 * Will maintain the ID mapping as a BTree in the file with the given path.
	 * If a file exists in this location, it will be DELETED.
	 * 
	 * @param fileName The location where the BTree file should be created.
	 * @param nodeSize The size of a BTree node
	 * @param commitFrequency The number of inserts after which a commit should be issued (<=0: never commit)
	 */
	public DatabaseIdMapping(String fileName,int nodeSize,int commitFrequency) {
		this(fileName,new BTreeSpec(nodeSize),commitFrequency);
	}

	private DatabaseIdMapping(String fileName,BTreeSpec treeSpec,int commitFrequency) {
		_fileName = fileName;
		_treeSpec=treeSpec;
		_commitFrequency=commitFrequency;
	}

	public int mappedId(int oldID) {
		if (_cache.orig() == oldID) {
			return _cache.mapped();
		}
		int classID = mappedClassID(oldID);
		if (classID != 0) {
			return classID;
		}
		BTreeRange range = _idTree.searchRange(trans(), new MappedIDPair(oldID, 0));
		Iterator4 pointers = range.pointers();
		if (pointers.moveNext()) {
			BTreePointer pointer = (BTreePointer) pointers.current();
			_cache = (MappedIDPair) pointer.key();
			return _cache.mapped();
		}
		return 0;
	}

	protected void mapNonClassIDs(int origID, int mappedID) {
		_cache = new MappedIDPair(origID, mappedID);
		_idTree.add(trans(), _cache);
		if(_commitFrequency > 0) {
			_idInsertCount++;
			if(_commitFrequency ==_idInsertCount) {
				_idTree.commit(trans());
				_idInsertCount = 0;
			}
		}
	}

	public void open() throws IOException {
		_mappingDb = DefragmentServicesImpl.freshTempFile(_fileName,1);
		_idTree = (_treeSpec == null ? new BTree(trans(), 0, new MappedIDPairHandler()) : new BTree(trans(), 0, new MappedIDPairHandler(), _treeSpec.nodeSize()));
		_slotTree = (_treeSpec == null ? new BTree(trans(), 0, new BTreeIdSystem.IdSlotMappingHandler()) : new BTree(trans(), 0, new BTreeIdSystem.IdSlotMappingHandler(), _treeSpec.nodeSize()));
	}

	public void close() {
		_mappingDb.close();
	}

	private Transaction trans() {
		return _mappingDb.systemTransaction();
	}
	
	private static class BTreeSpec {
		private int _nodeSize;
		
		public BTreeSpec(int nodeSize) {
			_nodeSize = nodeSize;
		}
		
		public int nodeSize() {
			return _nodeSize;
		}
	}

	public void mapId(int id, Slot slot) {
		_slotTree.add(trans(), new IdSlotMapping(id, slot.address(), slot.length()));
		if(_commitFrequency > 0) {
			_slotInsertCount++;
			if(_commitFrequency == _slotInsertCount) {
				_slotTree.commit(trans());
				_slotInsertCount = 0;
			}
		}
	}

	public Visitable<SlotChange> slotChanges() {
		return new Visitable<SlotChange>() {
			public void accept(final Visitor4<SlotChange> outSideVisitor) {
				_slotTree.traverseKeys(trans(), new Visitor4<IdSlotMapping>() {
					public void visit(IdSlotMapping idSlotMapping) {
						SlotChange slotChange = new SlotChange(idSlotMapping._id);
						slotChange.notifySlotCreated(idSlotMapping.slot());
						outSideVisitor.visit(slotChange);
					}
				});
			}
		};
	}
	
	public int addressForId(int id){		
		BTreeRange range = _slotTree.searchRange(trans(), new IdSlotMapping(id, 0, 0));
		Iterator4 pointers = range.pointers();
		if (pointers.moveNext()) {
			BTreePointer pointer = (BTreePointer) pointers.current();
			return ((IdSlotMapping)pointer.key())._address;
		}
		return 0;
	}

	public void commit() {
		_mappingDb.commit();
	}
	
}