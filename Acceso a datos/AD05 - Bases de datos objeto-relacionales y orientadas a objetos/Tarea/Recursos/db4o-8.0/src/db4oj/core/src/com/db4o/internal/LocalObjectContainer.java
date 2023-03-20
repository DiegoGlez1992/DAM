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
package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.events.*;
import com.db4o.internal.fileheader.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.qlin.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.internal.references.*;
import com.db4o.internal.slots.*;
import com.db4o.qlin.*;


/**
 * @exclude
 */
public abstract class LocalObjectContainer extends ExternalObjectContainer implements InternalObjectContainer, EmbeddedObjectContainer{
    
	protected FileHeader       _fileHeader;
    
    private final Collection4  _dirtyClassMetadata = new Collection4();
    
    private FreespaceManager _freespaceManager;
    
    private boolean             i_isServer = false;

    private Lock4 				_semaphoresLock = new Lock4();
    private Hashtable4          _semaphores;

    private int _blockEndAddress;
    
    private SystemData          _systemData;
    
    private IdSystem _idSystem;
    
	private final byte[] _pointerBuffer = new byte[Const4.POINTER_LENGTH];

	protected final ByteArrayBuffer _pointerIo = new ByteArrayBuffer(Const4.POINTER_LENGTH);    

    LocalObjectContainer(Configuration config) {
        super(config);
    }
    
    public Transaction newTransaction(Transaction parentTransaction, ReferenceSystem referenceSystem, boolean isSystemTransaction) {
    	TransactionalIdSystem systemIdSystem = null; 
    	if(! isSystemTransaction){
    		systemIdSystem = systemTransaction().idSystem();
    	}
    	Closure4<IdSystem> idSystem = new Closure4<IdSystem>() {
			public IdSystem run() {
				return idSystem();
			}
		};
		TransactionalIdSystem transactionalIdSystem = newTransactionalIdSystem(systemIdSystem, idSystem);
		return new LocalTransaction(this, parentTransaction, transactionalIdSystem, referenceSystem);
	}

	public TransactionalIdSystem newTransactionalIdSystem(TransactionalIdSystem systemIdSystem, Closure4<IdSystem> idSystem) {
		return new TransactionalIdSystemImpl(
    			new Closure4<FreespaceManager>() {
					public FreespaceManager run() {
						return freespaceManager();
					}
				}, 
				idSystem, 
				(TransactionalIdSystemImpl)systemIdSystem);
	}

    public FreespaceManager freespaceManager() {
		return _freespaceManager;
	}
    
    public void blockSizeReadFromFile(int size){
        blockSize(size);
        setRegularEndAddress(fileLength());
    }
    
    public void setRegularEndAddress(long address){
        _blockEndAddress = _blockConverter.bytesToBlocks(address);
    }
    
    final protected void close2() {
    	try {
	    	if (!_config.isReadOnly()) {
				commitTransaction();
				shutdown();
			}
    	}
    	finally {
    		shutdownObjectContainer();
    	}
    }

    public void commit1(Transaction trans) {
        trans.commit();
    }

    void configureNewFile() {
    	
    	blockSize(configImpl().blockSize());
    	_fileHeader = FileHeader.newCurrentFileHeader();
    	setRegularEndAddress(_fileHeader.length());
    	
        newSystemData(configImpl().freespaceSystem(), configImpl().idSystemType());
        systemData().converterVersion(Converter.VERSION);
        createStringIO(_systemData.stringEncoding());
        createIdSystem();
        
        initializeClassMetadataRepository();
        initalizeWeakReferenceSupport();
        
        generateNewIdentity();
        
        
        AbstractFreespaceManager blockedFreespaceManager = AbstractFreespaceManager.createNew(this);
		installFreespaceManager(blockedFreespaceManager);
        
        
        initNewClassCollection();
        initializeEssentialClasses();
        
        _fileHeader.initNew(this);
        
        blockedFreespaceManager.start(0);
    }
    
    private void newSystemData(byte freespaceSystemType, byte idSystemType){
        _systemData = new SystemData();
        _systemData.stringEncoding(configImpl().encoding());
        _systemData.freespaceSystem(freespaceSystemType);
        _systemData.idSystemType(idSystemType);
    }
    
    public int converterVersion() {
        return _systemData.converterVersion();
    }
    
    public long currentVersion() {
        return _timeStampIdGenerator.last();
    }

    void initNewClassCollection() {
        // overridden in YapObjectCarrier to do nothing
        classCollection().initTables(1);
    }
    
    public final BTree createBTreeClassIndex(int id){
        return new BTree(_transaction, id, new IDHandler());
    }
    
    public final AbstractQueryResult newQueryResult(Transaction trans) {
    	return newQueryResult(trans, config().evaluationMode());
    }

    public final AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode) {
    	if (trans == null) {
    		throw new ArgumentNullException();
    	}
    	if(mode == QueryEvaluationMode.IMMEDIATE){
        	return new IdListQueryResult(trans);
    	}
    	return new HybridQueryResult(trans, mode);
    }

    public final boolean delete4(Transaction transaction, ObjectReference ref, Object obj, int cascade, boolean userCall) {
        int id = ref.getID();
        StatefulBuffer reader = readStatefulBufferById(transaction, id);
        if (reader != null) {
            if (obj != null) {
                if ((!showInternalClasses())
                    && Const4.CLASS_INTERNAL.isAssignableFrom(obj.getClass())) {
                    return false;
                }
            }
            reader.setCascadeDeletes(cascade);
            transaction.idSystem().notifySlotDeleted(id, SlotChangeFactory.USER_OBJECTS);
            ClassMetadata classMetadata = ref.classMetadata();
            classMetadata.delete(reader, obj);

            return true;
        }
        return false;
    }

    public abstract long fileLength();

    public abstract String fileName();
    
    public void free(Slot slot) {
        if(slot.isNull()){
        	return;
        	
        	// TODO: This should really be an IllegalArgumentException but old database files 
        	//       with index-based FreespaceManagers appear to deliver zeroed slots.
            // throw new IllegalArgumentException();
        }
        if(_freespaceManager == null){
            // Can happen on early free before freespacemanager
            // is up, during conversion.
           return;
        }
        
        
        if(DTrace.enabled){
            DTrace.FILE_FREE.logLength(slot.address(), slot.length());
        }
        
        _freespaceManager.free(slot);

    }
    
    public void free(int address, int a_length) {
        free(new Slot(address, a_length));
    }
    
    public void generateNewIdentity(){
    	synchronized(_lock){
    		setIdentity(Db4oDatabase.generate());
    	}
    }

    public AbstractQueryResult queryAllObjects(Transaction trans) {
        return getAll(trans, config().evaluationMode());
    }
    
    public AbstractQueryResult getAll(Transaction trans, QueryEvaluationMode mode) {
    	final AbstractQueryResult queryResult = newQueryResult(trans, mode);
    	queryResult.loadFromClassIndexes(classCollection().iterator());
        return queryResult;
    }

    public int allocatePointerSlot() {
    	
        int id = allocateSlot(Const4.POINTER_LENGTH).address();
        if(!isValidPointer(id)){
        	return allocatePointerSlot();
        }
        
        // write a zero pointer first
        // to prevent delete interaction trouble
        writePointer(id, Slot.ZERO);
        
        if(DTrace.enabled){
            DTrace.GET_POINTER_SLOT.log(id);
        }
            
        return id;
    }

	protected boolean isValidPointer(int id) {
		// We have to make sure that object IDs do not collide
        // with built-in type IDs.
		return ! _handlers.isSystemHandler(id);
	}

	public Slot allocateSlot(int length){
        if(length <= 0){
        	throw new IllegalArgumentException();
        }
        if(_freespaceManager != null && _freespaceManager.isStarted()){
        	Slot slot = _freespaceManager.allocateSlot(length);
            if(slot != null){
                if(DTrace.enabled){
                    DTrace.GET_SLOT.logLength(slot.address(), slot.length());
                }
                return slot;
            }
            while(growDatabaseByConfiguredSize()){
            	slot = _freespaceManager.allocateSlot(length);
                if(slot != null){
                    if(DTrace.enabled){
                        DTrace.GET_SLOT.logLength(slot.address(), slot.length());
                    }
                    return slot;
                }
            }
        }
        Slot appendedSlot = appendBytes(length);
        if(DTrace.enabled){
            DTrace.GET_SLOT.logLength(appendedSlot.address(), appendedSlot.length());
        }
		return appendedSlot;
    }

	private boolean growDatabaseByConfiguredSize() {
		int reservedStorageSpace = configImpl().databaseGrowthSize();
		if(reservedStorageSpace <= 0){
			return false;
		}
		int reservedBlocks = _blockConverter.bytesToBlocks(reservedStorageSpace);
		int reservedBytes = _blockConverter.blocksToBytes(reservedBlocks);
		Slot slot = new Slot(_blockEndAddress, reservedBlocks);
        if (Debug4.xbytes && Deploy.overwrite) {
            overwriteDeletedBlockedSlot(slot);
        }else{
			writeBytes(new ByteArrayBuffer(reservedBytes), _blockEndAddress, 0);
        }
		_freespaceManager.free(_blockConverter.toNonBlockedLength(slot));
		_blockEndAddress += reservedBlocks;
		return true;
	}
    
    public final Slot appendBytes(long bytes){
    	int blockCount = _blockConverter.bytesToBlocks(bytes);
		int blockedStartAddress = _blockEndAddress;
		int blockedEndAddress = _blockEndAddress + blockCount;
		checkBlockedAddress(blockedEndAddress);
		_blockEndAddress = blockedEndAddress;
		Slot slot = new Slot(blockedStartAddress, blockCount);
		if (Debug4.xbytes && Deploy.overwrite) {
		    overwriteDeletedBlockedSlot(slot);
		}
    	return _blockConverter.toNonBlockedLength(slot);
    }
    
    private void checkBlockedAddress(int blockedAddress) {
    	if(blockedAddress < 0) {
    		switchToReadOnlyMode();
    		throw new DatabaseMaximumSizeReachedException();
    	}
    }

	private void switchToReadOnlyMode() {
		_config.readOnly(true);
	}
    
	// When a file gets opened, it uses the file size to determine where 
	// new slots can be appended. If this method would not be called, the
	// freespace system could already contain a slot that points beyond
	// the end of the file and this space could be allocated and used twice,
	// for instance if a slot was allocated and freed without ever being
	// written to file.
    void ensureLastSlotWritten(){
        if (!Debug4.xbytes){
            if(Deploy.overwrite){
                if(_blockEndAddress > _blockConverter.bytesToBlocks(fileLength())){
                    StatefulBuffer writer = createStatefulBuffer(systemTransaction(), _blockEndAddress - 1, blockSize());
                    writer.write();
                }
            }
        }
    }

    public Db4oDatabase identity() {
        return _systemData.identity();
    }
    
    public void setIdentity(Db4oDatabase identity){
    	synchronized(lock()){
	        _systemData.identity(identity);
	        
	        // The dirty TimeStampIdGenerator triggers writing of
	        // the variable part of the systemdata. We need to
	        // make it dirty here, so the new identity is persisted:
	        _timeStampIdGenerator.generate();
	        
	        _fileHeader.writeVariablePart(this);
    	}
    }

    boolean isServer() {
        return i_isServer;
    }

    public final int idForNewUserObject(Transaction trans) {
    	return trans.idSystem().newId(SlotChangeFactory.USER_OBJECTS);
    }

    public void raiseCommitTimestamp(long minimumVersion) {
        synchronized (lock()) {
            _timeStampIdGenerator.setMinimumNext(minimumVersion);
        }
    }

    public StatefulBuffer readStatefulBufferById(Transaction a_ta, int a_id) {
        return readStatefulBufferById(a_ta, a_id, false);
    }
    
    @Override
    public ByteArrayBuffer[] readSlotBuffers(Transaction transaction, int ids[]) {
    	ByteArrayBuffer[] buffers = new ByteArrayBuffer[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			if (ids[i] == 0) {
				buffers[i] = null;
			} else {
				buffers[i] = readBufferById(transaction, ids[i]);
			}
		}
		return buffers;
	}
    
    public ByteArrayBuffer readBufferById(Transaction trans, int id) {
        return readBufferById(trans, id, false);
    }
    
    public final ByteArrayBuffer readBufferById(Transaction trans, int id, boolean lastCommitted) {
		if (id <= 0) {
			throw new IllegalArgumentException();
		}

		Slot slot = lastCommitted ? trans.idSystem().committedSlot(id) :  trans.idSystem().currentSlot(id);

		if(DTrace.enabled){
			DTrace.SLOT_READ.logLength(id, slot);
		}

		return readBufferBySlot(slot);
    }
    
    public StatefulBuffer readStatefulBufferById(Transaction trans, int id, boolean lastCommitted) {
		if (id <= 0) {
			throw new IllegalArgumentException("id=" + id);
		}

		Slot slot = lastCommitted ? trans.idSystem().committedSlot(id) :  trans.idSystem().currentSlot(id);
		
		if(DTrace.enabled){
			DTrace.SLOT_READ.logLength(id, slot);
		}
		
		return readStatefulBufferBySlot(trans, id, slot);
    }
    
	public ByteArrayBuffer readBufferBySlot(Slot slot) {
		if (Slot.isNull(slot)) {
			return null;
		}
		
		if (DTrace.enabled) {
			DTrace.READ_SLOT.logLength(slot.address(), slot.length());
		}

		ByteArrayBuffer buffer = new ByteArrayBuffer(slot.length());

		buffer.readEncrypt(this, slot.address());
		return buffer;
	}
	
	public StatefulBuffer readStatefulBufferBySlot(Transaction trans, int id, Slot slot) {
		if (Slot.isNull(slot)) {
			return null;
		}
		
		if (DTrace.enabled) {
			DTrace.READ_SLOT.logLength(slot.address(), slot.length());
		}

		StatefulBuffer buffer = createStatefulBuffer(trans, slot.address(), slot.length());
		buffer.setID(id);
		buffer.readEncrypt(this, slot.address());
		return buffer;
	}
    
    protected boolean doFinalize() {
    	return _fileHeader != null;
    }

    void readThis() throws OldFormatException {
        newSystemData(AbstractFreespaceManager.FM_LEGACY_RAM, StandardIdSystemFactory.LEGACY);
        blockSizeReadFromFile(1);
        
        _fileHeader = FileHeader.read(this);
        
        if (config().generateCommitTimestamps().isUnspecified()) {
        	config().generateCommitTimestamps(_systemData.idToTimestampIndexId() != 0);
        }
        
        createStringIO(_systemData.stringEncoding());
        
        createIdSystem();
        
        initializeClassMetadataRepository();
        initalizeWeakReferenceSupport();

        setNextTimeStampId(systemData().lastTimeStampID());
        
        
        classCollection().setID(_systemData.classCollectionID());
        classCollection().read(systemTransaction());
        
        Converter.convert(new ConversionStage.ClassCollectionAvailableStage(this));
        
        _fileHeader.readIdentity(this);
        
        if(_config.isReadOnly()) {
        	return;
        }
        
        if (!configImpl().commitRecoveryDisabled()) {
        	_fileHeader.completeInterruptedTransaction(this);
        }
        
        FreespaceManager blockedFreespaceManager = AbstractFreespaceManager.createNew(this,
				_systemData.freespaceSystem());
        
        installFreespaceManager(blockedFreespaceManager);
		
        blockedFreespaceManager.read(this, _systemData.inMemoryFreespaceSlot());
        blockedFreespaceManager.start(_systemData.bTreeFreespaceId());
        
        _fileHeader = _fileHeader.convert(this);
        
        if(freespaceMigrationRequired(blockedFreespaceManager)){
        	migrateFreespace(blockedFreespaceManager);
        }
        
        writeHeader(true, false);

        if(Converter.convert(new ConversionStage.SystemUpStage(this))){
            _systemData.converterVersion(Converter.VERSION);
            _fileHeader.writeVariablePart(this);
            transaction().commit();
        }
        
    }

	private void installFreespaceManager(
			FreespaceManager blockedFreespaceManager) {
		_freespaceManager = blockSize() == 1 ?
        		blockedFreespaceManager :
        		new BlockAwareFreespaceManager(blockedFreespaceManager, _blockConverter);
	}
    
    protected void createIdSystem() {
        _idSystem = StandardIdSystemFactory.newInstance(this);
	}

	private boolean freespaceMigrationRequired(FreespaceManager freespaceManager) {
		if(freespaceManager == null){
			return false;
		}
		byte readSystem = _systemData.freespaceSystem();
		byte configuredSystem = configImpl().freespaceSystem();
		if(freespaceManager.systemType() == configuredSystem){
			return false;
		}
		if (configuredSystem != 0){
			return true;
		}
		return AbstractFreespaceManager.migrationRequired(readSystem);
	}

	private void migrateFreespace(FreespaceManager oldFreespaceManager) {
		
		FreespaceManager newFreespaceManager = AbstractFreespaceManager.createNew(this, configImpl().freespaceSystem());
		newFreespaceManager.start(0);
		
		systemData().freespaceSystem(configImpl().freespaceSystem());
        
        installFreespaceManager(newFreespaceManager);
		
		AbstractFreespaceManager.migrate(oldFreespaceManager, newFreespaceManager);
		_fileHeader.writeVariablePart(this);
	}

    public final void releaseSemaphore(String name) {
        releaseSemaphore(null, name);
    }

    public final void releaseSemaphore(final Transaction trans, final String name) {
        synchronized(_lock){
            if (_semaphores == null) {
                return;
            }
        }
        _semaphoresLock.run(new Closure4() { public Object run() {
            Transaction transaction = checkTransaction(trans);
            if (_semaphores != null && transaction == _semaphores.get(name)) {
                _semaphores.remove(name);
            }
            _semaphoresLock.awake();
            
            return null;
        }});
    }

    public void releaseSemaphores(final Transaction trans) {
        if (_semaphores != null) {
            final Hashtable4 semaphores = _semaphores;
            _semaphoresLock.run(new Closure4() { public Object run() {
                semaphores.forEachKeyForIdentity(new Visitor4() {
                    public void visit(Object a_object) {
                        semaphores.remove(a_object);
                    }
                }, trans);
                
                _semaphoresLock.awake();
                return null;
             }});            
        }
    }

    public final void rollback1(Transaction trans) {
        trans.rollback();
    }

    public final void setDirtyInSystemTransaction(PersistentBase a_object) {
        a_object.setStateDirty();
        a_object.cacheDirty(_dirtyClassMetadata);
    }

    public final boolean setSemaphore(String name, int timeout) {
        return setSemaphore(null, name, timeout);
    }

    public final boolean setSemaphore(final Transaction trans, final String name, final int timeout) {
        if (name == null) {
            throw new NullPointerException();
        }
        synchronized (_lock) {
        	if (_semaphores == null) {
            	_semaphores = new Hashtable4(10);
            }
        }
        
        final BooleanByRef acquired = new BooleanByRef();
        _semaphoresLock.run(new Closure4() { public Object run() {
        	try{
	            Transaction transaction = checkTransaction(trans);
	            Object candidateTransaction = _semaphores.get(name);
	            if (trans == candidateTransaction) {
	            	acquired.value = true;
	                return null;
	            }
	            
	            if (candidateTransaction == null) {
	                _semaphores.put(name, transaction);
	                acquired.value = true;
	                return null;
	            }
	            
	            long endtime = System.currentTimeMillis() + timeout;
	            long waitTime = timeout;
	            while (waitTime > 0) {
	                _semaphoresLock.awake();
					_semaphoresLock.snooze(waitTime);
					
	                if (classCollection() == null) {
	                    acquired.value = false;
	                	return null;
	                }
	
	                candidateTransaction = _semaphores.get(name);	
	                if (candidateTransaction == null) {
	                    _semaphores.put(name, transaction);
	                    acquired.value = true;
	                    return null;
	                }
	
	                waitTime = endtime - System.currentTimeMillis();
	            }
	            
	            acquired.value = false;
	            return null;
        	} finally{
        		_semaphoresLock.awake();
        	}        	
        }});
        
        return acquired.value;
    }

    public void setServer(boolean flag) {
        i_isServer = flag;
    }

    public abstract void syncFiles();
    
    public abstract void syncFiles(Runnable runnable);

    protected String defaultToString() {
        return fileName();
    }

    public void shutdown() {
        writeHeader(false, true);
    }
    
    public final void commitTransaction() {
        _transaction.commit();
    }

    public abstract void writeBytes(ByteArrayBuffer buffer, int blockedAddress, int addressOffset);

    public final void writeDirtyClassMetadata() {        
        writeCachedDirty();
    }

	private void writeCachedDirty() {
		Iterator4 i = _dirtyClassMetadata.iterator();
        while (i.moveNext()) {
        	PersistentBase dirty = (PersistentBase) i.current();
            dirty.write(systemTransaction());
            dirty.notCachedDirty();
        }
        _dirtyClassMetadata.clear();
	}
	
    public final void writeEncrypt(ByteArrayBuffer buffer, int address, int addressOffset) {
        _handlers.encrypt(buffer);
        writeBytes(buffer, address, addressOffset);
        _handlers.decrypt(buffer);
    }
    
    public void writeHeader(boolean startFileLockingThread, boolean shuttingDown) {
        if(shuttingDown){
            _freespaceManager.write(this);
            _freespaceManager = null;
        }
        
        StatefulBuffer writer = createStatefulBuffer(systemTransaction(), 0, _fileHeader.length());
        
        _fileHeader.writeFixedPart(this, startFileLockingThread, shuttingDown, writer, blockSize());
        
        if(shuttingDown){
            ensureLastSlotWritten();
        }
        syncFiles();
    }

    public final void writeNew(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ByteArrayBuffer buffer) {
        writeEncrypt(buffer, pointer.address(), 0);
        if(classMetadata == null){
            return;
        }
        classMetadata.addToIndex(trans, pointer.id());
    }

    // This is a reroute of writeBytes to write the free blocks
    // unchecked.

    public abstract void overwriteDeletedBytes(int address, int length);
    
    public void overwriteDeletedBlockedSlot(Slot slot) {
    	overwriteDeletedBytes(slot.address(), _blockConverter.blocksToBytes(slot.length()));	
    }

    public final void writeTransactionPointer(int pointer) {
        _fileHeader.writeTransactionPointer(systemTransaction(), pointer);
    }
    
    public final Slot allocateSlotForUserObjectUpdate(Transaction trans, int id, int length){
        Slot slot = allocateSlot(length);
        trans.idSystem().notifySlotUpdated(id, slot, SlotChangeFactory.USER_OBJECTS);
        return slot;
    }
    
    public final Slot allocateSlotForNewUserObject(Transaction trans, int id, int length){
        Slot slot = allocateSlot(length);
        trans.idSystem().notifySlotCreated(id, slot, SlotChangeFactory.USER_OBJECTS);
        return slot;
    }

    public final void writeUpdate(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ArrayType arrayType, ByteArrayBuffer buffer) {
        int address = pointer.address();
        if(address == 0){
            address = allocateSlotForUserObjectUpdate(trans, pointer.id(), pointer.length()).address();
        }
        writeEncrypt(buffer, address, 0);
    }

    public void setNextTimeStampId(long val) {
        _timeStampIdGenerator.setMinimumNext(val);
    }
    
    public SystemInfo systemInfo() {
        return new SystemInfoFileImpl(this);
    }

	public FileHeader getFileHeader() {
		return _fileHeader;
	}

    public void installDebugFreespaceManager(FreespaceManager manager) {
        _freespaceManager = manager;
    }

    public SystemData systemData() {
        return _systemData;
    }
    
    public long[] getIDsForClass(Transaction trans, ClassMetadata clazz){
		final IntArrayList ids = new IntArrayList();
        clazz.index().traverseAll(trans, new Visitor4() {
        	public void visit(Object obj) {
        		ids.add(((Integer)obj).intValue());
        	}
        });        
        return ids.asLong();
    }
    
    public QueryResult classOnlyQuery(QQueryBase query, ClassMetadata clazz){
        if (!clazz.hasClassIndex()) {
        	return new IdListQueryResult(query.transaction());
		}
		
		final AbstractQueryResult queryResult = newQueryResult(query.transaction());
		queryResult.loadFromClassIndex(clazz);
		return queryResult;
    }
    
    public QueryResult executeQuery(QQuery query){
    	AbstractQueryResult queryResult = newQueryResult(query.transaction());
    	queryResult.loadFromQuery(query);
    	return queryResult;
    }

	public LocalTransaction localSystemTransaction() {
		return (LocalTransaction)systemTransaction();
	}
	
	public int instanceCount(ClassMetadata clazz, Transaction trans) {
		synchronized(lock()) {
			return clazz.indexEntryCount(trans);
		}
	}
	
	public ObjectContainer openSession(){
		synchronized(lock()) {
			return new ObjectContainerSession(this);
		}
	}
	
	@Override
	public boolean isDeleted(Transaction trans, int id){
		return trans.idSystem().isDeleted(id);
	}
	
	public void writePointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(id);
            DTrace.WRITE_POINTER.logLength(slot);
        }
        _pointerIo.seek(0);
        if (Deploy.debug) {
            _pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        _pointerIo.writeInt(slot.address());
    	_pointerIo.writeInt(slot.length());
        if (Deploy.debug) {
            _pointerIo.writeEnd();
        }
        if(Debug4.xbytes){
        	_pointerIo.checkXBytes(false);
        }
        writeBytes(_pointerIo, id, 0);
    }
	
	public Slot debugReadPointerSlot(int id) {
        if (Deploy.debug) {
    		readBytes(_pointerIo._buffer, id, Const4.POINTER_LENGTH);
    		_pointerIo.seek(0);
    		_pointerIo.readBegin(Const4.YAPPOINTER);
    		int debugAddress = _pointerIo.readInt();
    		int debugLength = _pointerIo.readInt();
    		_pointerIo.readEnd();
    		return new Slot(debugAddress, debugLength);
        }
        return null;
	}
    
    public final Slot readPointerSlot(int id) {
        if (Deploy.debug) {
            return debugReadPointerSlot(id);
        }
        if(!isValidId(id)){
        	throw new InvalidIDException(id);
        }
        
       	readBytes(_pointerBuffer, id, Const4.POINTER_LENGTH);
        int address = (_pointerBuffer[3] & 255)
            | (_pointerBuffer[2] & 255) << 8 | (_pointerBuffer[1] & 255) << 16
            | _pointerBuffer[0] << 24;
        int length = (_pointerBuffer[7] & 255)
            | (_pointerBuffer[6] & 255) << 8 | (_pointerBuffer[5] & 255) << 16
            | _pointerBuffer[4] << 24;
        
        if(!isValidSlot(address, length)){
        	throw new InvalidSlotException(address, length, id);
        }
        
        return new Slot(address, length);
    }
    
	private boolean isValidId(int id) {
		return fileLength() >= id;
	}
	
	private boolean isValidSlot(int address, int length) {
		// just in case overflow 
		long fileLength = fileLength();
		
		boolean validAddress = fileLength >= address;
        boolean validLength = fileLength >= length ;
        boolean validSlot = fileLength >= (address+length);
        
        return validAddress && validLength && validSlot;
	}
	
	protected void closeIdSystem(){
		if(_idSystem != null){
			_idSystem.close();
		}
	}
	
	public IdSystem idSystem(){
		return _idSystem;
	}

	public Runnable commitHook() {
        _systemData.lastTimeStampID(_timeStampIdGenerator.last());
        return _fileHeader.commit(false);
	}
	
	public final Slot allocateSafeSlot(int length) {
		Slot reusedSlot = freespaceManager().allocateSafeSlot(length);
		if(reusedSlot != null){
			return reusedSlot;
		}
		return appendBytes(length);
	}
	
	public EventRegistryImpl newEventRegistry(){
		return new EventRegistryImpl();
	}
	
	public <T> QLin<T> from(Class<T> clazz) {
		return new QLinRoot<T>(query(), clazz);
	}
	
}