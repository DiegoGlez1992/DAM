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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.CommitTimestampSupport.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class DefragmentServicesImpl implements DefragmentServices {	

	public static abstract class DbSelector {
		DbSelector() {
		}
		
		abstract LocalObjectContainer db(DefragmentServicesImpl context);

		Transaction transaction(DefragmentServicesImpl context) {
			return db(context).systemTransaction();
		}
	}
	
	public final static DbSelector SOURCEDB=new DbSelector() {
		LocalObjectContainer db(DefragmentServicesImpl context) {
			return context._sourceDb;
		}
	};

	public final static DbSelector TARGETDB=new DbSelector() {
		LocalObjectContainer db(DefragmentServicesImpl context) {
			return context._targetDb;
		}
	};

	private final LocalObjectContainer _sourceDb;
	private final LocalObjectContainer _targetDb;
	private final IdMapping _mapping;
	private DefragmentListener _listener;
	private Queue4 _unindexed=new NonblockingQueue();

	private DefragmentConfig _defragConfig;
	

	public DefragmentServicesImpl(DefragmentConfig defragConfig,DefragmentListener listener) throws IOException {
		_listener=listener;
		Config4Impl originalConfig =  (Config4Impl) defragConfig.db4oConfig();
		
		Storage storage = defragConfig.backupStorage();
		if(defragConfig.readOnly()){
			storage = new NonFlushingStorage(storage); 
		}
		
		Config4Impl sourceConfig = prepareConfig(originalConfig, storage, defragConfig.readOnly());
		_sourceDb = (LocalObjectContainer)Db4o.openFile(sourceConfig,defragConfig.tempPath()).ext();
		
		_sourceDb.showInternalClasses(true);
		defragConfig.db4oConfig().blockSize(_sourceDb.blockSize());
		if (!originalConfig.generateCommitTimestamps().definiteNo()) {
			defragConfig.db4oConfig().generateCommitTimestamps(_sourceDb.config().generateCommitTimestamps().definiteYes());
		}
		
		_targetDb = freshTargetFile(defragConfig);
		_mapping=defragConfig.mapping();
		_mapping.open();
		_defragConfig = defragConfig;
	}

	private Config4Impl prepareConfig(Config4Impl originalConfig, Storage storage, boolean readOnly) {
		Config4Impl sourceConfig=(Config4Impl) originalConfig.deepClone(null);
		sourceConfig.weakReferences(false);
		sourceConfig.storage(storage);
		sourceConfig.readOnly(readOnly);
		return sourceConfig;
	}
	
	static LocalObjectContainer freshTempFile(String fileName,int blockSize) throws IOException {
		FileStorage storage = new FileStorage();
		storage.delete(fileName);
		Configuration db4oConfig = DefragmentConfig.vanillaDb4oConfig(blockSize);
		db4oConfig.objectClass(IdSlotMapping.class).objectField("_id").indexed(true);
		db4oConfig.storage(storage);
		return (LocalObjectContainer)Db4o.openFile(db4oConfig,fileName).ext();
	}
	
	static LocalObjectContainer freshTargetFile(DefragmentConfig  config) throws IOException {
		config.db4oConfig().storage().delete(config.origPath());
		return (LocalObjectContainer) Db4o.openFile(config.clonedDb4oConfig(),config.origPath());
	}
	
	public int mappedID(int oldID,int defaultID) {
		int mapped=internalMappedID(oldID);
		return (mapped!=0 ? mapped : defaultID);
	}

	public int strictMappedID(int oldID) throws MappingNotFoundException {
		int mapped=internalMappedID(oldID);
		if(mapped==0) {
			throw new MappingNotFoundException(oldID);
		}
		return mapped;
	}

	public int mappedID(int id) {
		if(id == 0){
			return 0;
		}
		int mapped = internalMappedID(id);
		if(mapped==0) {
			_listener.notifyDefragmentInfo(new DefragmentInfo("No mapping found for ID "+id));
			return Const4.INVALID_OBJECT_ID;
		}
		return mapped;
	}

	private int internalMappedID(int oldID) throws MappingNotFoundException {
		if(oldID==0) {
			return 0;
		}
		int mappedId = _mapping.mappedId(oldID);
		if(mappedId == 0 && _sourceDb.handlers().isSystemHandler(oldID)){
			return oldID;
		}
		return mappedId;
	}

	public void mapIDs(int oldID,int newID, boolean isClassID) {
		_mapping.mapId(oldID,newID, isClassID);
	}

	public void close() {
		_sourceDb.close();
		_targetDb.close();
		_mapping.close();
	}
	
	public ByteArrayBuffer bufferByID(DbSelector selector,int id) {
		Slot slot=committedSlot(selector, id);
		return bufferByAddress(selector,slot.address(),slot.length());
	}

	private Slot committedSlot(DbSelector selector, int id) {
		return selector.db(this).idSystem().committedSlot(id);
	}

	public ByteArrayBuffer sourceBufferByAddress(int address,int length) throws IOException {
		return bufferByAddress(SOURCEDB, address, length);
	}

	public ByteArrayBuffer targetBufferByAddress(int address,int length) throws IOException {
		return bufferByAddress(TARGETDB, address, length);
	}

	public ByteArrayBuffer bufferByAddress(DbSelector selector,int address,int length) {
		return selector.db(this).decryptedBufferByAddress(address,length);
	}

	public StatefulBuffer targetStatefulBufferByAddress(int address,int length) throws IllegalArgumentException {
		return _targetDb.readWriterByAddress(TARGETDB.transaction(this),address,length);
	}
	
	public Slot allocateTargetSlot(int length) {
		return _targetDb.allocateSlot(length);
	}

	public void targetWriteBytes(DefragmentContextImpl context,int address) {
		context.write(_targetDb,address);
	}

	public void targetWriteBytes(ByteArrayBuffer reader,int address) {
		_targetDb.writeBytes(reader,address,0);
	}

	public StoredClass[] storedClasses(DbSelector selector) {
		LocalObjectContainer db = selector.db(this);
		db.showInternalClasses(true);
		try {
			return db.classCollection().storedClasses();
		} finally {
			db.showInternalClasses(false);
		}
	}
	
	public LatinStringIO stringIO() {
		return _sourceDb.stringIO();
	}
	
	public void targetCommit() {
		_targetDb.commit();
	}
	
	public TypeHandler4 sourceHandler(int id) {
	    return _sourceDb.typeHandlerForClassMetadataID(id);
	}
	
	public int sourceClassCollectionID() {
		return _sourceDb.classCollection().getID();
	}

	private Hashtable4 _classIndices=new Hashtable4(16);

	public int classIndexID(ClassMetadata classMetadata) {
		return classIndex(classMetadata).id();
	}

	public void traverseAll(ClassMetadata classMetadata, Visitor4 command) {
		if(!classMetadata.hasClassIndex()) {
			return;
		}
		classMetadata.index().traverseAll(SOURCEDB.transaction(this), command);
	}
	
	public void traverseAllIndexSlots(ClassMetadata classMetadata,Visitor4 command) {
		Iterator4 slotIDIter=classMetadata.index().allSlotIDs(SOURCEDB.transaction(this));
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}

	public void traverseAllIndexSlots(BTree btree,Visitor4 command) {
		Iterator4 slotIDIter=btree.allNodeIds(SOURCEDB.transaction(this));
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}
	
	public void registerBTreeIDs(BTree btree, final IDMappingCollector collector) {
		collector.createIDMapping(this, btree.getID(), false);
		traverseAllIndexSlots(btree, new Visitor4() {
			public void visit(Object obj) {
				int id=((Integer)obj).intValue();
				collector.createIDMapping(DefragmentServicesImpl.this, id, false);
			}
		});
	}

	public int databaseIdentityID(DbSelector selector) {
		LocalObjectContainer db = selector.db(this);
		Db4oDatabase identity = db.identity();
		if(identity==null) {
			return 0;
		}
		return identity.getID(selector.transaction(this));
	}
	
	private ClassIndexStrategy classIndex(ClassMetadata classMetadata) {
		ClassIndexStrategy classIndex=(ClassIndexStrategy)_classIndices.get(classMetadata);
		if(classIndex==null) {
			classIndex=new BTreeClassIndexStrategy(classMetadata);
			_classIndices.put(classMetadata,classIndex);
			classIndex.initialize(_targetDb);
		}
		return classIndex;
	}

	public Transaction systemTrans() {
		return SOURCEDB.transaction(this);
	}

	public void copyIdentity() {
		_targetDb.setIdentity(_sourceDb.identity());
	}

	public void replaceClassMetadataRepository() {
		
		Transaction systemTransaction = _targetDb.systemTransaction();
		
		// Can't use strictMappedID because the repository ID can
		// be lower than HandlerRegisrtry _highestBuiltinTypeID and
		// the ClassRepository ID would be treated as a system handler
		// and the unmapped ID would be returned.
		int newRepositoryId = _mapping.mappedId(sourceClassCollectionID());
		int sourceIdentityID = databaseIdentityID(DefragmentServicesImpl.SOURCEDB);
		int targetIdentityID = _mapping.mappedId(sourceIdentityID);
		int targetUuidIndexID = _mapping.mappedId(sourceUuidIndexID());
		int oldIdentityId = _targetDb.systemData().identity().getID(systemTransaction);
		int oldRepositoryId = _targetDb.classCollection().getID();
		
		ClassMetadataRepository oldRepository = _targetDb.classCollection();
		
		ClassMetadataRepository newRepository = new ClassMetadataRepository(systemTransaction);
		newRepository.setID(newRepositoryId);
		newRepository.read(systemTransaction);
		newRepository.initOnUp(systemTransaction);
		
		_targetDb.systemData().classCollectionID(newRepositoryId);
		_targetDb.replaceClassMetadataRepository(newRepository);
		
		_targetDb.systemData().uuidIndexId(targetUuidIndexID);
		Db4oDatabase identity = (Db4oDatabase) _targetDb.getByID(systemTransaction, targetIdentityID);
		_targetDb.setIdentity(identity);

		
		ClassMetadataIterator iterator = oldRepository.iterator();
		while(iterator.moveNext()){
			ClassMetadata classMetadata = iterator.currentClass();
			BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) classMetadata.index();
			index.btree().free(_targetDb.localSystemTransaction());
			freeById(classMetadata.getID());
		}
		freeById(oldIdentityId);
		freeById(oldRepositoryId);
	}

	public void defragIdToTimestampBtree() {
		
		if (_sourceDb.systemData().idToTimestampIndexId() == 0) {
			return;
		}
		
		final LocalTransaction targetTransaction = (LocalTransaction)_targetDb.systemTransaction();
		final LocalTransaction sourceTransaction = (LocalTransaction)_sourceDb.systemTransaction();
		
		final CommitTimestampSupport target = targetTransaction.commitTimestampSupport();
		final CommitTimestampSupport source = sourceTransaction.commitTimestampSupport();
		
		if (source.idToTimestamp() == null) {
			return;
		}
		
		source.idToTimestamp().traverseKeys(sourceTransaction, new Visitor4<TimestampEntry>() {
			public void visit(TimestampEntry te) {
				int mappedID = mappedID(te.parentID());
				target.put(targetTransaction, mappedID, te.getCommitTimestamp());
			}
		});
	}
	
	private void freeById(int id){
		_targetDb.systemTransaction().idSystem().notifySlotDeleted(id, SlotChangeFactory.SYSTEM_OBJECTS);
	}

	public ByteArrayBuffer sourceBufferByID(int sourceID)  {
		return bufferByID(SOURCEDB,sourceID);
	}
	
	public BTree sourceUuidIndex() {
		if(sourceUuidIndexID()==0) {
			return null;
		}
		return _sourceDb.uUIDIndex().getIndex(systemTrans());
	}
	
	public void targetUuidIndexID(int id) {
		_targetDb.systemData().uuidIndexId(id);
	}

	public int sourceUuidIndexID() {
		return _sourceDb.systemData().uuidIndexId();
	}
	
	public int sourceIdToTimestampIndexID() {
		return _sourceDb.systemData().idToTimestampIndexId();
	}
	
	public ClassMetadata classMetadataForId(int id) {
		return _sourceDb.classMetadataForID(id);
	}
	
	public void registerUnindexed(int id) {
		_unindexed.add(new Integer(id));
	}

	public IdSource unindexedIDs() {
		return new IdSource(_unindexed);
	}

	public ObjectHeader sourceObjectHeader(ByteArrayBuffer buffer) {
		return new ObjectHeader(_sourceDb, buffer);
	}

	public int blockSize() {
		return _sourceDb.blockSize();
	}

	public int sourceAddressByID(int sourceID) {
		return committedSlot(SOURCEDB, sourceID).address();
	}
	
	public int targetAddressByID(int sourceID) {
		return _mapping.addressForId(sourceID);
	}

	public boolean accept(StoredClass klass) {
		return this._defragConfig.storedClassFilter().accept(klass);
	}
	
	public int targetNewId() {
		return _targetDb.idSystem().newId();
	}
	
	public IdMapping mapping(){
		return _mapping;
	}
	
	public void commitIds(){
		FreespaceCommitter freespaceCommitter = new FreespaceCommitter(_targetDb.freespaceManager());
		freespaceCommitter.transactionalIdSystem(systemTrans().idSystem());
		_targetDb.idSystem().commit(mapping().slotChanges(), freespaceCommitter);
		freespaceCommitter.commit();
	}
	
}