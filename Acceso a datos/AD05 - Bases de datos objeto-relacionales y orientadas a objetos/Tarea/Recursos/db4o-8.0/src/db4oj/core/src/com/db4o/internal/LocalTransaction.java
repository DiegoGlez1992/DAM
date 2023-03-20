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

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;

/**
 * @exclude
 */
public class LocalTransaction extends Transaction {

    private final IdentitySet4 _participants = new IdentitySet4(); 

    Tree _writtenUpdateAdjustedIndexes;
    
	protected final LocalObjectContainer _file;
	
	private final CommittedCallbackDispatcher _committedCallbackDispatcher;
	
	private final TransactionalIdSystem _idSystem;
	
	private CommitTimestampSupport _commitTimestampSupport = null;
	
    private long _timestamp;
    
    private List<Long> _concurrentReplicationTimestamps;
	
	public LocalTransaction(ObjectContainerBase container, Transaction parentTransaction, TransactionalIdSystem idSystem, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
		_file = (LocalObjectContainer) container;
        _committedCallbackDispatcher = new CommittedCallbackDispatcher() {
    		public boolean willDispatchCommitted() {
    			return callbacks().caresAboutCommitted();
    		}
    		public void dispatchCommitted(CallbackObjectInfoCollections committedInfo) {
    			callbacks().commitOnCompleted(LocalTransaction.this, committedInfo, false);
    		}
    	};
    	_idSystem = idSystem;
	}

	public Config4Impl config() {
		return container().config();
	}

	public LocalObjectContainer localContainer() {
		return _file;
	}
	
    public void commit() {
    	commit(_committedCallbackDispatcher);
    }
    
    public void commit(CommittedCallbackDispatcher dispatcher) {
        synchronized (container().lock()) {
        	
        	commitListeners();
        	dispatchCommittingCallback();   
        	
        	if (!doCommittedCallbacks(dispatcher)) {
        		commitImpl();
        		commitClearAll();
    		} else {
    			Collection4 deleted = collectCommittedCallbackDeletedInfo();
                commitImpl();
                final CallbackObjectInfoCollections committedInfo = collectCommittedCallbackInfo(deleted);
        		commitClearAll();
        		dispatcher.dispatchCommitted(
        				CallbackObjectInfoCollections.EMTPY == committedInfo
        				? committedInfo
        				: new CallbackObjectInfoCollections(
        						committedInfo.added,
        						committedInfo.updated,
        						new ObjectInfoCollectionImpl(deleted)));
    		}
        }
    }	

	private void dispatchCommittingCallback() {
		if(doCommittingCallbacks()){
			callbacks().commitOnStarted(this, collectCommittingCallbackInfo());
		}
	}

	private boolean doCommittedCallbacks(CommittedCallbackDispatcher dispatcher) {
        if (isSystemTransaction()){
            return false;
        }
		return dispatcher.willDispatchCommitted();
	}

	private boolean doCommittingCallbacks() {
		if (isSystemTransaction()) {
			return false;
		}
		return callbacks().caresAboutCommitting();
	}
    
	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException();
		}
		checkSynchronization();	
		if (!_participants.contains(participant)) {
			_participants.add(participant);
		}
	}

	private void commitImpl(){
        
        if(DTrace.enabled){
            DTrace.TRANS_COMMIT.logInfo( "server == " + container().isServer() + ", systemtrans == " +  isSystemTransaction());
        }
        
        commitClassMetadata();
        
        commitParticipants();
        
        container().writeDirtyClassMetadata();
        
        idSystem().commit(new FreespaceCommitter(localContainer().freespaceManager()));
        
    }
	
	private void commitListeners(){
        commitParentListeners(); 
        commitTransactionListeners();
    }

	private void commitParentListeners() {
		if (_systemTransaction != null) {
            parentLocalTransaction().commitListeners();
        }
	}
	
    private void commitParticipants() {
        if (parentLocalTransaction() != null) {
        	parentLocalTransaction().commitParticipants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    private void commitClassMetadata(){
        container().processPendingClassUpdates();
        container().writeDirtyClassMetadata();
        container().classCollection().write(container().systemTransaction());
    }
    
	private LocalTransaction parentLocalTransaction() {
		return (LocalTransaction) _systemTransaction;
	}
    
	private void commitClearAll(){
		if(_systemTransaction != null){
            parentLocalTransaction().commitClearAll();
        }
        clearAll();
    }

	
	protected void clear() {
		idSystem().clear();
		disposeParticipants();
        _participants.clear();
	}
	
	private void disposeParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).dispose(this);
		}
	}
	
    public void rollback() {
        synchronized (container().lock()) {
            
            rollbackParticipants();
            
            idSystem().rollback();
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }
    
    private void rollbackParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}
	
    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        _file.syncFiles();
    }
    
	public void processDeletes() {
		if (_delete == null) {
			_writtenUpdateAdjustedIndexes = null;
			return;
		}

		while (_delete != null) {

			Tree delete = _delete;
			_delete = null;

			delete.traverse(new Visitor4() {
				public void visit(Object a_object) {
					DeleteInfo info = (DeleteInfo) a_object;
					// if the object has been deleted
					if (localContainer().isDeleted(LocalTransaction.this, info._key)) {
						return;
					}
					
					// We need to hold a hard reference here, otherwise we can get 
					// intermediate garbage collection kicking in.
					Object obj = null;  
					
					if (info._reference != null) {
						obj = info._reference.getObject();
					}
					if (obj == null || info._reference.getID() < 0) {

						// This means the object was gc'd.

						// Let's try to read it again, but this may fail in
						// CS mode if another transaction has deleted it. 

						HardObjectReference hardRef = container().getHardObjectReferenceById(
							LocalTransaction.this, info._key);
						if(hardRef == HardObjectReference.INVALID){
							return;
						}
						info._reference = hardRef._reference;
						info._reference.flagForDelete(container().topLevelCallId());
						obj = info._reference.getObject();
					}
					container().delete3(LocalTransaction.this, info._reference,
							obj, info._cascade, false);
				}
			});
		}
		_writtenUpdateAdjustedIndexes = null;
	}
	
	
	public void writeUpdateAdjustIndexes(int id, ClassMetadata clazz, ArrayType typeInfo) {
    	new WriteUpdateProcessor(this, id, clazz, typeInfo).run();
    }
    
	private Callbacks callbacks(){
		return container().callbacks();
	}
	
	private Collection4 collectCommittedCallbackDeletedInfo() {
		final Collection4 deleted = new Collection4();
		collectCallBackInfo(new CallbackInfoCollector() {
			public void deleted(int id) {
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}

			public void updated(int id) {
			}
		
			public void added(int id) {
			}
		});
		return deleted;
	}
	
	private CallbackObjectInfoCollections collectCommittedCallbackInfo(Collection4 deleted) {
		if (! idSystem().isDirty()) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		final Collection4 added = new Collection4();
		final Collection4 updated = new Collection4();		
		collectCallBackInfo(new CallbackInfoCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id) {
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections collectCommittingCallbackInfo() {
		if (! idSystem().isDirty()) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();		
		collectCallBackInfo(new CallbackInfoCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id){
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections newCallbackObjectInfoCollections(
			final Collection4 added,
			final Collection4 updated,
			final Collection4 deleted) {
		return new CallbackObjectInfoCollections(
				new ObjectInfoCollectionImpl(added),
				new ObjectInfoCollectionImpl(updated),
				new ObjectInfoCollectionImpl(deleted));
	}

	private void collectCallBackInfo(final CallbackInfoCollector collector) {
		idSystem().collectCallBackInfo(collector);
	}
	
	public TransactionalIdSystem idSystem() {
		return _idSystem;
	}

	public ObjectInfo frozenReferenceFor(final int id) {
		ObjectReference ref = referenceForId(id);
		if(ref != null){
			if (isStruct(ref)) return null;
			return new FrozenObjectInfo(this, ref, true);
		}
		ref = container().peekReference(systemTransaction(), id, new FixedActivationDepth(0), true);
		if(ref == null || ref.getObject() == null || isStruct(ref)){
			return null;
		}
		return new FrozenObjectInfo(systemTransaction(), ref, true);
	}
	
	private boolean isStruct(ObjectReference ref) {
		return ref.classMetadata().isStruct();
	}

	public LazyObjectReference lazyReferenceFor(final int id) {
		return new LazyObjectReference(LocalTransaction.this, id);
	}
	
	public long versionForId(int id) {
		return commitTimestampSupport().versionForId(id);
	}

	public CommitTimestampSupport commitTimestampSupport() {
		
		if (!isSystemTransaction()) {
			throw new IllegalStateException();
		}
		
		if (_commitTimestampSupport == null) {
			_commitTimestampSupport = new CommitTimestampSupport(localContainer());
		}
		
		return _commitTimestampSupport;
	}
	
	public long generateTransactionTimestamp(long forcedTimeStamp){
		if(forcedTimeStamp > 0){
			_timestamp = forcedTimeStamp;
		} else {
			_timestamp = localContainer().generateTimeStampId();
		}
		return _timestamp;
	}
	
	public void useDefaultTransactionTimestamp(){
		_timestamp = 0;
		_concurrentReplicationTimestamps = null;
	}
	
	public long timestamp(){
		return _timestamp;
	}

	public void notifyAboutOtherReplicationCommit(long replicationVersion, List<Long> concurrentTimestamps) {
		if(timestamp() == 0){
			return;
		}
		if(_concurrentReplicationTimestamps == null){
			_concurrentReplicationTimestamps = new ArrayList<Long>();
		}
		_concurrentReplicationTimestamps.add(replicationVersion);
		concurrentTimestamps.add(timestamp());
	}
	
	public List<Long> concurrentReplicationTimestamps(){
		if(_concurrentReplicationTimestamps != null){
			return _concurrentReplicationTimestamps;
		}
		return new ArrayList<Long>();
	}
	
	@Override
	public void postOpen(){
		super.postOpen();
    	if (isSystemTransaction()) {
    		commitTimestampSupport().ensureInitialized();
    	}
	}

}
