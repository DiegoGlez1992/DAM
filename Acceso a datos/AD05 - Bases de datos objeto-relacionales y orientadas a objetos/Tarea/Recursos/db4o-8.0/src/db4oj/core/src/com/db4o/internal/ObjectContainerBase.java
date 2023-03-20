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

import static com.db4o.foundation.Environments.*;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.metadata.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.internal.references.*;
import com.db4o.internal.replication.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.threading.*;
import com.db4o.internal.weakref.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;
import com.db4o.types.*;
/**
 * @exclude
 * @sharpen.extends System.IDisposable
 * @sharpen.partial
 */
public abstract class ObjectContainerBase  implements TransientClass, Internal4, ObjectContainerSpec, InternalObjectContainer {
	

    // Collection of all classes
    // if (_classCollection == null) the engine is down.
    protected ClassMetadataRepository      _classCollection;
    
    // the Configuration context for this ObjectContainer
    protected Config4Impl             _config;

    // Counts the number of toplevel calls into YapStream
    private int           _stackDepth;
    
    private final int _maxStackDepth;

    private final ReferenceSystemRegistry _referenceSystemRegistry = new ReferenceSystemRegistry();
    
    private Tree            _justPeeked;

    protected Object            _lock;

    // currently used to resolve self-linking concurrency problems
    // in cylic links, stores only ClassMetadata objects
    private List4           _pendingClassUpdates;

    // a value greater than 0 indicates class implementing the
    // "Internal" interface are visible in queries and can
    // be used.
    int                     _showInternalClasses = 0;
    
    private List4           _stillToActivate;
    private List4           _stillToDeactivate;
    private List4           _stillToSet;
    private boolean 		_handlingStackLimitPendings = false;

    // used for ClassMetadata and ClassMetadataRepository
    // may be parent or equal to i_trans
    private Transaction             _systemTransaction;

    // used for Objects
    protected Transaction             _transaction;
    
    // all the per-YapStream references that we don't
    // want created in YapobjectCarrier
    public HandlerRegistry             _handlers;

    // One of three constants in ReplicationHandler: NONE, OLD, NEW
    // Detailed replication variables are stored in i_handlers.
    // Call state has to be maintained here, so YapObjectCarrier (who shares i_handlers) does
    // not accidentally think it operates in a replication call. 
    int                 _replicationCallState;  

    // weak reference management
    WeakReferenceSupport           _references;

	private NativeQueryHandler _nativeQueryHandler;
    
	private Callbacks _callbacks = new com.db4o.internal.callbacks.NullCallbacks();
    
    protected final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
    
    private int _topLevelCallId = 1;
    
    private IntIdGenerator _topLevelCallIdGenerator = new IntIdGenerator();

	private final Environment _environment;
	
	private ReferenceSystemFactory _referenceSystemFactory;
	
	private String _name;
	
	protected BlockConverter _blockConverter = new DisabledBlockConverter();
	
	protected ObjectContainerBase(Configuration config) {
    	_lock = new Object();
    	_config = (Config4Impl)config;
    	_environment = createEnvironment(_config);
    	_maxStackDepth = _config.maxStackDepth();
    }

	private Environment createEnvironment(Config4Impl config) {
		final ArrayList bindings = new ArrayList();
		bindings.addAll(config.environmentContributions());
		bindings.add(this); // my(ObjectContainer.class)
		bindings.add(config); // my(Configuration.class)
    	return Environments.newConventionBasedEnvironment(bindings.toArray());
	}
	
	protected Environment environment() {
		return _environment;
	}

	protected final void open() throws OldFormatException {
		withEnvironment(new Runnable() { public void run() {
			
			boolean ok = false;
			synchronized (_lock) {
				try {
			        _name = configImpl().nameProvider().name(ObjectContainerBase.this);
					initializeReferenceSystemFactory(_config);
					initializeTransactions();
		        	initialize1(_config);
		        	openImpl();
					initializePostOpen();
					callbacks().openOnFinished(ObjectContainerBase.this);
					ok = true;
				} finally {
					if(!ok) {
						// TODO: This will swallow the causing exception if
						//       an exception occurs during shutdown.
						shutdownObjectContainer();
					}
				}
			}
			
		}});
	}
	
	private void initializeReferenceSystemFactory(Config4Impl config){
		_referenceSystemFactory = config.referenceSystemFactory();
	}
	
	public void withEnvironment(Runnable runnable) {
		runWith(_environment, runnable);
	}

	protected abstract void openImpl() throws Db4oIOException;
    
	public ActivationDepth defaultActivationDepth(ClassMetadata classMetadata) {
		return activationDepthProvider().activationDepthFor(classMetadata, ActivationMode.ACTIVATE);
	}

    public ActivationDepthProvider activationDepthProvider() {
    	return configImpl().activationDepthProvider();
	}
    
    public final void activate(Transaction trans, Object obj){
        synchronized (_lock) {
            activate(trans, obj, defaultActivationDepthForObject(obj));
        }
    }
    
    public final void deactivate(Transaction trans, Object obj){
    	deactivate(trans, obj, 1);
    }
    
    private final ActivationDepth defaultActivationDepthForObject(Object obj) {
        ClassMetadata classMetadata = classMetadataForObject(obj);
        return defaultActivationDepth(classMetadata);
    }

	public final void activate(Transaction trans, final Object obj, final ActivationDepth depth) {
        synchronized (_lock) {
            asTopLevelCall(new Function4<Transaction,Object>() {
				public Object apply(Transaction trans) {
	                stillToActivate(activationContextFor(trans, obj, depth));
	                activatePending(trans);
					return null;
				}
            }, trans);
        }
    }
    
    static final class PendingActivation {
    	public final ObjectReference ref;
    	public final ActivationDepth depth;
    	
    	public PendingActivation(ObjectReference ref_, ActivationDepth depth_) {
    		this.ref = ref_;
    		this.depth = depth_;
    	}
    }

	final void activatePending(Transaction ta){
        while (_stillToActivate != null) {

            // TODO: Optimize!  A lightweight int array would be faster.

            final Iterator4 i = new Iterator4Impl(_stillToActivate);
            _stillToActivate = null;

            while (i.moveNext()) {
            	final PendingActivation item = (PendingActivation) i.current();
                final ObjectReference ref = item.ref;
				final Object obj = ref.getObject();
                if (obj == null) {
                    ta.removeReference(ref);
                } else {
                    ref.activateInternal(activationContextFor(ta, obj, item.depth));
                }
            }
        }
    }

	public void backup(String path) throws DatabaseClosedException, Db4oIOException {
    	backup(configImpl().storage(), path);
    }

	public ActivationContext4 activationContextFor(Transaction ta,
			final Object obj, final ActivationDepth depth) {
		return new ActivationContext4(ta, obj, depth);
	}
    
    public final void bind(Transaction trans, Object obj, long id) throws ArgumentNullException, IllegalArgumentException {
        synchronized (_lock) {
            if(obj == null){
                throw new ArgumentNullException();
            }
            if(DTrace.enabled){
                DTrace.BIND.log(id, " ihc " + System.identityHashCode(obj));
            }
            trans = checkTransaction(trans);
            int intID = (int) id;
            Object oldObject = getByID(trans, id);
            if (oldObject == null) {
                throw new IllegalArgumentException("id");
            }
            ObjectReference ref = trans.referenceForId(intID);
            if(ref == null){
                throw new IllegalArgumentException("obj");
            }
            if (reflectorForObject(obj) == ref.classMetadata().classReflector()) {
                ObjectReference newRef = bind2(trans, ref, obj);
                newRef.virtualAttributes(trans, false);
            } else {
                throw new Db4oException(Messages.get(57));
            }
        }
    }
    
    public final ObjectReference bind2(Transaction trans, ObjectReference oldRef, Object obj){
        int id = oldRef.getID();
        trans.removeReference(oldRef);
        ObjectReference newRef = new ObjectReference(classMetadataForObject(obj), id);
        newRef.setObjectWeak(this, obj);
        newRef.setStateDirty();
        trans.referenceSystem().addExistingReference(newRef);
        return newRef;
    }

	public ClassMetadata classMetadataForObject(Object obj) {
		return produceClassMetadata(reflectorForObject(obj));
	}
    
    public abstract byte blockSize();
     
    private final boolean breakDeleteForEnum(ObjectReference reference, boolean userCall){
        if(Deploy.csharp){
            return false;
        }
        if(userCall){
            return false;
        }
        if(reference == null){
            return false;
        }
        return Platform4.isEnum(reflector(), reference.classMetadata().classReflector());
    }

    boolean canUpdate() {
        return true;
    }

    public final void checkClosed() throws DatabaseClosedException {
        if (_classCollection == null) {
            throw new DatabaseClosedException();
        }
    }
    
	protected final void checkReadOnly() throws DatabaseReadOnlyException {
		if(_config.isReadOnly()) {
    		throw new DatabaseReadOnlyException();
    	}
	}

    final void processPendingClassUpdates() {
		if (_pendingClassUpdates == null) {
			return;
		}
		Iterator4 i = new Iterator4Impl(_pendingClassUpdates);
		while (i.moveNext()) {
			ClassMetadata classMetadata = (ClassMetadata) i.current();
			classMetadata.setStateDirty();
			classMetadata.write(_systemTransaction);
		}
		_pendingClassUpdates = null;
	}
    
    public final Transaction checkTransaction() {
        return checkTransaction(null);
    }

    public final Transaction checkTransaction(Transaction ta) {
        checkClosed();
        if (ta != null) {
            return ta;
        }
        return transaction();
    }

    final public boolean close() {
		synchronized (_lock) {
			callbacks().closeOnStarted(this);
			if(DTrace.enabled){
				DTrace.CLOSE_CALLED.log(this.toString());
			}
			close1();
			return true;
		}
	}
    
    protected void handleExceptionOnClose(Exception exc) {
		fatalException(exc);
    }

    private void close1() {
        if (isClosed()) {
            return;
        }
        processPendingClassUpdates();
        if (stateMessages()) {
            logMsg(2, toString());
        }
        close2();
    }
    
    protected abstract void close2();
    

	public final void shutdownObjectContainer() {
		if (DTrace.enabled) {
			DTrace.CLOSE.log();
		}
		logMsg(3, toString());
		synchronized (_lock) {
			closeUserTransaction();
			closeSystemTransaction();
			closeIdSystem();
			stopSession();
			shutdownDataStorage();
		}
	}
	
	protected abstract void closeIdSystem();

	protected final void closeUserTransaction(){
		closeTransaction(_transaction, false,false);
	}

	protected final void closeSystemTransaction(){
		closeTransaction(_systemTransaction, true,false);
	}
	
	public abstract void closeTransaction(Transaction transaction, boolean isSystemTransaction, boolean rollbackOnClose);

	protected abstract void shutdownDataStorage();
    
    public final void commit(Transaction trans) throws DatabaseReadOnlyException, DatabaseClosedException {
        synchronized (_lock) {
            if(DTrace.enabled){
                DTrace.COMMIT.log();
            }
            checkReadOnly();
            asTopLevelCall(new Function4<Transaction, Object>() {
				public Object apply(Transaction trans) {
	            	commit1(trans);
	            	trans.postCommit();
	            	return null;
				}
            }, trans);
        }
    }

    private <R> R asTopLevelStore(Function4<Transaction,R> block, Transaction trans) {
    	trans = checkTransaction(trans);
    	R result = asTopLevelCall(block, trans);
		if(_stackDepth == 0){
			trans.processDeletes();
		}
		return result;
    }

    /**
     * @sharpen.ignore
     */
    protected <R> R asTopLevelCall(Function4<Transaction,R> block, Transaction trans) {
    	trans = checkTransaction(trans);
    	beginTopLevelCall();
        try{            	
        	return block.apply(trans);
        } 
        catch(Db4oRecoverableException exc) {
        	throw exc;
        }
        catch(RuntimeException exc) {
        	fatalShutdown(exc);
        }
        finally {
        	endTopLevelCall();
        }
        // should never happen - just to make compiler happy
		throw new Db4oException();
    }

	public void fatalShutdown(Throwable origExc)  {
		try {
			stopSession();
			fatalStorageShutdown();
		}
		catch(Throwable exc) {
			throw new CompositeDb4oException(origExc, exc);
		}
		Platform4.throwUncheckedException(origExc);
	}

	protected abstract void fatalStorageShutdown();

	public abstract void commit1(Transaction trans);

    public Configuration configure() {
        return configImpl();
    }
    
    public Config4Impl config(){
        return configImpl();
    }
    
    public abstract int converterVersion();

    public abstract AbstractQueryResult newQueryResult(Transaction trans, QueryEvaluationMode mode);

    protected final void createStringIO(byte encoding) {
    	stringIO(BuiltInStringEncoding.stringIoForEncoding(encoding, configImpl().stringEncoding()));
    }

    final protected void initializeTransactions() {
        _systemTransaction = newSystemTransaction();
        _transaction = newUserTransaction();
    }

	public abstract Transaction newTransaction(Transaction parentTransaction, ReferenceSystem referenceSystem, boolean isSystemTransaction);
	
	public Transaction newUserTransaction(){
	    return newTransaction(systemTransaction(), createReferenceSystem(), false);
	}
	
	public Transaction newSystemTransaction(){
	    return newTransaction(null, createReferenceSystem(), true);
	}
	
    public abstract long currentVersion();
    
    public boolean createClassMetadata(ClassMetadata classMeta, ReflectClass clazz, ClassMetadata superClassMeta) {
        return classMeta.init(superClassMeta);
    }

    /**
     * allows special handling for all Db4oType objects.
     * Redirected here from #set() so only instanceof check is necessary
     * in the #set() method. 
     * @return object if handled here and #set() should not continue processing
     */
    public Db4oType db4oTypeStored(Transaction trans, Object obj) {
        if (!(obj instanceof Db4oDatabase)) {
        	return null;
        }
        Db4oDatabase database = (Db4oDatabase) obj;
        if (trans.referenceForObject(obj) != null) {
            return database;
        }
        showInternalClasses(true);
        try {
        	return database.query(trans);
        } finally {
        	showInternalClasses(false);
        }
    }
    
    public final void deactivate(Transaction trans, final Object obj, final int depth) throws DatabaseClosedException {
        synchronized (_lock) {
            asTopLevelCall(new Function4<Transaction,Object>() {
				public Object apply(Transaction trans) {
	        		deactivateInternal(trans, obj, activationDepthProvider().activationDepth(depth, ActivationMode.DEACTIVATE));
					return null;
				}
            }, trans);
        }
    }

    private final void deactivateInternal(Transaction trans, Object obj, ActivationDepth depth) {
        stillToDeactivate(trans, obj, depth, true);
        deactivatePending(trans);
    }

	private void deactivatePending(Transaction trans) {
		while (_stillToDeactivate != null) {
            Iterator4 i = new Iterator4Impl(_stillToDeactivate);
            _stillToDeactivate = null;
            while (i.moveNext()) {
                PendingActivation item = (PendingActivation) i.current();
				item.ref.deactivate(trans, item.depth);
            }
        }
	}

    public final void delete(Transaction trans, Object obj) throws DatabaseReadOnlyException, DatabaseClosedException {
    	if (null == obj) {
    		throw new ArgumentNullException();
    	}
        synchronized (lock()) {
        	trans = checkTransaction(trans);
        	checkReadOnly();
            delete1(trans, obj, true);
        	unregisterFromTransparentPersistence(trans, obj);
            trans.processDeletes();
        }
    }

    public final void delete1(Transaction trans, final Object obj, final boolean userCall) {
        if (obj == null) {
        	return;
        }
        final ObjectReference ref = trans.referenceForObject(obj);
        if(ref == null){
        	return;
        }
        if(userCall){
        	generateCallIDOnTopLevel();
        }
    	asTopLevelCall(new Function4<Transaction, Object>() {
			public Object apply(Transaction trans) {
	        	delete2(trans, ref, obj, 0, userCall);
	        	return null;
			}
    	}, trans);
    }
    
    public final void delete2(Transaction trans, ObjectReference ref, Object obj, int cascade, boolean userCall) {
        
        // This check is performed twice, here and in delete3, intentionally.
        if(breakDeleteForEnum(ref, userCall)){
            return;
        }
        
        if(obj instanceof Entry){
        	if(! flagForDelete(ref)){
        		return;
        	}
            delete3(trans, ref, obj, cascade, userCall);
            return;
        }
        
        trans.delete(ref, ref.getID(), cascade);
    }

    final void delete3(Transaction trans, ObjectReference ref, Object obj, int cascade, boolean userCall) {
    	
        // The passed reference can be null, when calling from Transaction.
        if(ref == null  || ! ref.beginProcessing()){
        	return;
        }
                
        // This check is performed twice, here and in delete2, intentionally.
        if(breakDeleteForEnum(ref, userCall)){
        	ref.endProcessing();
            return;
        }
        
        if(! ref.isFlaggedForDelete()){
        	ref.endProcessing();
        	return;
        }
        
        ClassMetadata yc = ref.classMetadata();
                
        // We have to end processing temporarily here, otherwise the can delete callback
        // can't do anything at all with this object.
        
        ref.endProcessing();
        
        activateForDeletionCallback(trans, yc, ref, obj);
        
        if (!objectCanDelete(trans, yc, ref)) {
            return;
        }
        
        ref.beginProcessing();

        if(DTrace.enabled){
            DTrace.DELETE.log(ref.getID());
        }
        
        if(delete4(trans, ref, obj, cascade, userCall)){
        	objectOnDelete(trans, yc, ref);
            if (configImpl().messageLevel() > Const4.STATE) {
                message("" + ref.getID() + " delete " + ref.classMetadata().getName());
            }
        }
        
        ref.endProcessing();
    }

	private void unregisterFromTransparentPersistence(Transaction trans, Object obj) {
		if (!(activationDepthProvider() instanceof TransparentActivationDepthProvider)) {
			return;
		}
		
		final TransparentActivationDepthProvider provider = (TransparentActivationDepthProvider) activationDepthProvider();
    	provider.removeModified(obj, trans);
	}

	private void activateForDeletionCallback(Transaction trans, ClassMetadata classMetadata, ObjectReference ref, Object obj) {
		if (!ref.isActive() && (caresAboutDeleting(classMetadata) || caresAboutDeleted(classMetadata))) {
        	// Activate Objects for Callbacks, because in C/S mode Objects are not activated on the Server
			// FIXME: [TA] review activation depth
		    ActivationDepth depth = classMetadata.adjustCollectionDepthToBorders(new FixedActivationDepth(1));
        	activate(trans, obj, depth);
        } 
	}
    
    private boolean caresAboutDeleting(ClassMetadata yc) {
    	return this._callbacks.caresAboutDeleting()
    		|| yc.hasEventRegistered(systemTransaction(), EventDispatchers.CAN_DELETE);
    }
    
    private boolean caresAboutDeleted(ClassMetadata yc) {
    	return this._callbacks.caresAboutDeleted()
    		|| yc.hasEventRegistered(systemTransaction(), EventDispatchers.DELETE);
    }
    
	private boolean objectCanDelete(Transaction transaction, ClassMetadata yc, ObjectInfo objectInfo) {
		return callbacks().objectCanDelete(transaction, objectInfo)
			&& yc.dispatchEvent(transaction, objectInfo.getObject(), EventDispatchers.CAN_DELETE);
	}
	
	private void objectOnDelete(Transaction transaction, ClassMetadata yc, ObjectInfo reference) {
		callbacks().objectOnDelete(transaction, reference);
		yc.dispatchEvent(transaction, reference.getObject(), EventDispatchers.DELETE);
	}
	
    public abstract boolean delete4(Transaction ta, ObjectReference ref, Object obj, int a_cascade, boolean userCall);
    
    Object descend(Transaction trans, Object obj, String[] path){
        synchronized (_lock) {
            trans = checkTransaction(trans);
            ObjectReference ref = trans.referenceForObject(obj);
            if(ref == null){
                return null;
            }
            
            final String fieldName = path[0];
            if(fieldName == null){
                return null;
            }
            ClassMetadata classMetadata = ref.classMetadata();
            final ByRef foundField = new ByRef();
            
            classMetadata.traverseAllAspects(new TraverseFieldCommand() {
			
				@Override
				protected void process(FieldMetadata field) {
                    if(field.canAddToQuery(fieldName)){
                    	foundField.value = field;
                    }
				}
			});
            
            FieldMetadata field = (FieldMetadata) foundField.value;
            if(field == null){
                return null;
            }
            
            Object child = ref.isActive()
            	? field.get(trans, obj)
                : descendMarshallingContext(trans, ref).readFieldValue(field);
            
            if(path.length == 1){
                return child;
            }
            if(child == null){
                return null;
            }
            String[] subPath = new String[path.length - 1];
            System.arraycopy(path, 1, subPath, 0, path.length - 1);
            return descend(trans, child, subPath);
        }
    }

	private UnmarshallingContext descendMarshallingContext(Transaction trans,
			ObjectReference ref) {
		final UnmarshallingContext context = new UnmarshallingContext(trans, ref, Const4.ADD_TO_ID_TREE, false);
		context.activationDepth(activationDepthProvider().activationDepth(1, ActivationMode.ACTIVATE));
		return context;
	}

    public boolean detectSchemaChanges() {
        // overriden in YapClient
        return configImpl().detectSchemaChanges();
    }
    
    public boolean dispatchsEvents() {
        return true;
    }

    protected boolean doFinalize() {
    	return true;
    }

    /*
	 * This method will be exuected on finalization, and vm exit if it's enabled
	 * by configuration.
	 */
    final void shutdownHook() {
		if(isClosed()) {
			return;
		}
		if (allOperationsCompleted()) {
			Messages.logErr(configImpl(), 50, toString(), null);
			close();
		} else {
			shutdownObjectContainer();
			if (operationIsProcessing()) {
				Messages.logErr(configImpl(), 24, null, null);
			}
		}
	}

	private boolean operationIsProcessing() {
		return _stackDepth > 0;
	}

	private boolean allOperationsCompleted() {
		return _stackDepth == 0;
	}

    void fatalException(int msgID) {
		fatalException(null,msgID);
    }

	final void fatalException(Throwable t) {
		fatalException(t,Messages.FATAL_MSG_ID);
    }

    final void fatalException(Throwable t, int msgID) {
    	if(DTrace.enabled){
    		DTrace.FATAL_EXCEPTION.log(t.toString());
    	}
		Messages.logErr(configImpl(), (msgID == Messages.FATAL_MSG_ID ? 18
				: msgID), null, t);
		if (!isClosed()) {
			shutdownObjectContainer();
		}
		throw new Db4oException(Messages.get(msgID));
	}

    /**
     * @sharpen.ignore
     */
    protected void finalize() {
		if (doFinalize() && configuredForAutomaticShutDown()) {
			shutdownHook();
		}
	}

	private boolean configuredForAutomaticShutDown() {
		return (configImpl() == null || configImpl().automaticShutDown());
	}

    void gc() {
        _references.purge();
    }
    
    public final ObjectSet queryByExample(Transaction trans, final Object template) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            QueryResult res = asTopLevelCall(new Function4<Transaction,QueryResult>() {
				public QueryResult apply(Transaction trans) {
	    			return queryByExampleInternal(trans, template);
				}
            }, trans);
            return new ObjectSetFacade(res);
        }
    }

    private final QueryResult queryByExampleInternal(Transaction trans, Object template) {
        if (template == null || template.getClass() == Const4.CLASS_OBJECT || template == Const4.CLASS_OBJECT) {
            return queryAllObjects(trans);
        } 
        Query q = query(trans);
        q.constrain(template).byExample();
        return executeQuery((QQuery)q);
    }
    
    public abstract AbstractQueryResult queryAllObjects(Transaction ta);
    
    public final Object tryGetByID(Transaction ta, long id) throws DatabaseClosedException{
		try {
			return getByID(ta, id);
		} catch (InvalidSlotException ise){
			// can happen return null
		} catch (InvalidIDException iie){
			// can happen return null
		}
		return null;
    }

    public final Object getByID(Transaction ta, long id) throws DatabaseClosedException, InvalidIDException {
        synchronized (_lock) {
            if (id <= 0 || id >= Integer.MAX_VALUE) {
                throw new IllegalArgumentException();
            }
            checkClosed();
            ta = checkTransaction(ta);
            beginTopLevelCall();
            try {
                return getByID2(ta, (int) id);
            } 
            catch(Db4oRecoverableException exc) {
            	throw exc;
            }
            catch(OutOfMemoryError e){
            	throw new Db4oRecoverableException(e);
            } 
            catch(RuntimeException e){
            	throw new Db4oRecoverableException(e);
            } 
            finally {
            	// Never shut down for getById()
            	// There may be OutOfMemoryErrors or similar
            	// The user may want to catch and continue working.
            	endTopLevelCall();
            }
        }
    }
    
    public Object getByID2(Transaction ta, int id) {
		Object obj = ta.objectForIdFromCache(id);
		if (obj != null) {
			// Take care about handling the returned candidate reference.
			// If you loose the reference, weak reference management might
			// also.
			return obj;

		}
		return new ObjectReference(id).read(ta, new LegacyActivationDepth(0), Const4.ADD_TO_ID_TREE, true);
	}
    
    public final Object getActivatedObjectFromCache(Transaction ta, int id){
        Object obj = ta.objectForIdFromCache(id);
        if(obj == null){
            return null;
        }
        activate(ta, obj);
        return obj;
    }
    
    public final Object readActivatedObjectNotInCache(Transaction trans, final int id){
        Object obj = asTopLevelCall(new Function4<Transaction,Object>() {
			public Object apply(Transaction trans) {
	            return new ObjectReference(id).read(trans, UnknownActivationDepth.INSTANCE, Const4.ADD_TO_ID_TREE, true);
			}
        }, trans);
        activatePending(trans);
        return obj;
    }
    
    public final Object getByUUID(Transaction trans, Db4oUUID uuid){
        synchronized (_lock) {
            if(uuid == null){
                return null;
            }
            HardObjectReference hardRef = getHardReferenceBySignature(checkTransaction(trans), 
            					uuid.getLongPart(),
            					uuid.getSignaturePart());
            return hardRef._object; 
        }
    }
    
    public HardObjectReference getHardReferenceBySignature(Transaction trans, long uuid, byte[] signature) {
        return uUIDIndex().getHardObjectReferenceBySignature(trans, uuid, signature);
    }

    public final int getID(Transaction trans, Object obj) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            checkClosed();
    
            if(obj == null){
                return 0;
            }
    
            ObjectReference yo = trans.referenceForObject(obj);
            if (yo != null) {
                return yo.getID();
            }
            return 0;
        }
    }
    
    public final ObjectInfo getObjectInfo (Transaction trans, Object obj){
        synchronized(_lock){
            trans = checkTransaction(trans);
            return trans.referenceForObject(obj);
        }
    }
    
    public final HardObjectReference getHardObjectReferenceById(Transaction trans, int id) {
        if (id <= 0) {
        	return HardObjectReference.INVALID;
        }
        	
        ObjectReference ref = trans.referenceForId(id);
        if (ref != null) {

            // Take care about handling the returned candidate reference.
            // If you loose the reference, weak reference management might also.

            Object candidate = ref.getObject();
            if (candidate != null) {
            	return new HardObjectReference(ref, candidate);
            }
            trans.removeReference(ref);
        }
        ref = new ObjectReference(id);
        Object readObject = ref.read(trans, new LegacyActivationDepth(0), Const4.ADD_TO_ID_TREE, true);
        
        if(readObject == null){
            return HardObjectReference.INVALID;
        }
        
        // check class creation side effect and simply retry recursively
        // if it hits:
        if(readObject != ref.getObject()){
            return getHardObjectReferenceById(trans, id);
        }
        
        return new HardObjectReference(ref, readObject);
    }

    public final StatefulBuffer createStatefulBuffer(Transaction trans, int address, int length) {
        if (Debug4.exceedsMaximumBlockSize(length)) {
            return null;
        }
        return new StatefulBuffer(trans, address, length);
    }

    public final Transaction systemTransaction() {
        return _systemTransaction;
    }

    public final Transaction transaction() {
        return _transaction;
    }
    
    public ClassMetadata classMetadataForReflectClass(ReflectClass claxx){
    	if (null == claxx) {
    		throw new ArgumentNullException();
    	}
    	if(hideClassForExternalUse(claxx)){
    		return null;
    	}
        ClassMetadata classMetadata = _handlers.classMetadataForClass(claxx);
        if (classMetadata != null) {
            return classMetadata;
        }
        return _classCollection.classMetadataForReflectClass(claxx);
    }
    
    // TODO: Some ReflectClass implementations could hold a 
    // reference to ClassMetadata to improve lookup performance here.
    public ClassMetadata produceClassMetadata(ReflectClass claxx) {
    	if (null == claxx) {
    		throw new ArgumentNullException();
    	}
    	if(hideClassForExternalUse(claxx)){
    		return null;
    	}
        ClassMetadata classMetadata = _handlers.classMetadataForClass(claxx);
        if (classMetadata != null) {
            return classMetadata;
        }
        return _classCollection.produceClassMetadata(claxx);
    }
    
    /**
     * Differentiating getActiveClassMetadata from getYapClass is a tuning 
     * optimization: If we initialize a YapClass, #set3() has to check for
     * the possibility that class initialization associates the currently
     * stored object with a previously stored static object, causing the
     * object to be known afterwards.
     * 
     * In this call we only return active YapClasses, initialization
     * is not done on purpose
     */
    final ClassMetadata getActiveClassMetadata(ReflectClass claxx) {
    	if(hideClassForExternalUse(claxx)){
    		return null;
    	}
        return _classCollection.getActiveClassMetadata(claxx);
    }
    
    private final boolean hideClassForExternalUse(ReflectClass claxx){
        if ((!showInternalClasses()) && _handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)) {
            return true;
        }
        return false;
    }
    
    public int classMetadataIdForName(String name) {
        return _classCollection.classMetadataIdForName(name);
    }

    public ClassMetadata classMetadataForName(String name) {
    	return classMetadataForID(classMetadataIdForName(name));
    }
    
    public ClassMetadata classMetadataForID(int id) {
    	if(DTrace.enabled){
    		DTrace.CLASSMETADATA_BY_ID.log(id);
    	}
        if (id == 0) {
            return null;
        }
        ClassMetadata classMetadata = _handlers.classMetadataForId(id);
        if (classMetadata != null) {
            return classMetadata;
        }
        return _classCollection.classMetadataForId(id);
    }
    
    public HandlerRegistry handlers(){
    	return _handlers;
    }

    public boolean needsLockFileThread() {
        if(! Debug4.lockFile){
			return false;
		}
        if (!Platform4.needsLockFileThread()) {
            return false;
        }
        if (configImpl().isReadOnly()) {
            return false;
        }
        return configImpl().lockFile();
    }

    protected boolean hasShutDownHook() {
        return configImpl().automaticShutDown();
    }

    protected void initialize1(Configuration config) {
        _config = initializeConfig(config);
        _handlers = new HandlerRegistry(this, configImpl().encoding(), configImpl().reflector());
        
        if (_references != null) {
            gc();
            _references.stop();
        }

        _references = WeakReferenceSupportFactory.forObjectContainer(this);
        
        if (hasShutDownHook()) {
            Platform4.addShutDownHook(this);
        }
        _handlers.initEncryption(configImpl());
        _stillToSet = null;
    }

	private Config4Impl initializeConfig(Configuration config) {
		Config4Impl impl=((Config4Impl)config);
		impl.container(this);
		impl.reflector().setTransaction(systemTransaction());
		impl.reflector().configuration(new ReflectorConfigurationImpl(impl));
		impl.taint();
		return impl;
	}

    public ReferenceSystem createReferenceSystem() {
        ReferenceSystem referenceSystem = _referenceSystemFactory.newReferenceSystem(this);
        _referenceSystemRegistry.addReferenceSystem(referenceSystem);
        return referenceSystem;
    }

	protected void initalizeWeakReferenceSupport() {
		_references.start();
	}

	protected void initializeClassMetadataRepository() {
		_classCollection = new ClassMetadataRepository(_systemTransaction);
	}

    private void initializePostOpen() {
        _showInternalClasses = 100000;
        initializePostOpenExcludingTransportObjectContainer();
        _showInternalClasses = 0;
    }
    
    protected void initializePostOpenExcludingTransportObjectContainer() {
        initializeEssentialClasses();
		rename(configImpl());
		_classCollection.initOnUp(_systemTransaction);
		_transaction.postOpen();
        if (configImpl().detectSchemaChanges()) {
        	if(! configImpl().isReadOnly()){
        		_systemTransaction.commit();
        	}
        }
        configImpl().applyConfigurationItems(this);
    }

    void initializeEssentialClasses(){
        if(Debug4.staticIdentity){
            return;
        }
        for (int i = 0; i < Const4.ESSENTIAL_CLASSES.length; i++) {
            produceClassMetadata(reflector().forClass(Const4.ESSENTIAL_CLASSES[i]));    
        }
    }

    final boolean isActive(Transaction trans, Object obj) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            if (obj != null) {
                ObjectReference ref = trans.referenceForObject(obj);
                if (ref != null) {
                    return ref.isActive();
                }
            }
            return false;
        }
    }
    
    public boolean isCached(Transaction trans, long id) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            return trans.objectForIdFromCache((int)id) != null;
        }
    }

    /**
     * overridden in ClientObjectContainer
     * The method allows checking whether will make it easier to refactor than
     * an "instanceof YapClient" check.
     */
    public boolean isClient() {
        return false;
    }

    public final boolean isClosed() {
        synchronized (_lock) {
            // this is set to null in close2 and is therefore our check for down.
            return _classCollection == null;
        }
    }

    boolean isServer() {
        return false;
    }

    public final boolean isStored(Transaction trans, Object obj) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            if (obj == null) {
                return false;
            }
            ObjectReference ref = trans.referenceForObject(obj);
            if (ref == null) {
                return false;
            }
            return ! isDeleted(trans, ref.getID());
        }
    }
    
    public ReflectClass[] knownClasses(){
        synchronized(_lock){
            checkClosed();
            return reflector().knownClasses();
        }
    }
    
    public TypeHandler4 typeHandlerForClass(ReflectClass claxx) {
        if(hideClassForExternalUse(claxx)){
            return null;
        }
        TypeHandler4 typeHandler = _handlers.typeHandlerForClass(claxx);
        if(typeHandler != null){
            return typeHandler;
        }
        return _classCollection.produceClassMetadata(claxx).typeHandler();
    }
    
    public TypeHandler4 typeHandlerForClassMetadataID(int id) {
        if (id < 1) {
            return null;
        }
        ClassMetadata classMetadata = classMetadataForID(id);
        if(classMetadata == null){
            return null;
        }
        return classMetadata.typeHandler();
    }

    public Object lock() {
        return _lock;
    }

    public final void logMsg(int code, String msg) {
        Messages.logMsg(configImpl(), code, msg);
    }

    public boolean maintainsIndices() {
        return true;
    }

    void message(String msg) {
        new MessageOutput(this, msg);
    }

    public final void needsUpdate(ClassMetadata classMetadata) {
        _pendingClassUpdates = new List4(_pendingClassUpdates, classMetadata);
    }
    
    public long generateTimeStampId() {
        return _timeStampIdGenerator.generate();
    }

    public abstract int idForNewUserObject(Transaction trans);
    
    public Object peekPersisted(Transaction trans, final Object obj, final ActivationDepth depth, final boolean committed) throws DatabaseClosedException {
    	
    	// TODO: peekPersisted is not stack overflow safe, if depth is too high. 
    	
        synchronized (_lock) {
        	checkClosed();
        	
        	return asTopLevelCall(new Function4<Transaction, Object>() {
				public Object apply(Transaction trans) {
	                trans = checkTransaction(trans);
	                ObjectReference ref = trans.referenceForObject(obj);
	                trans = committed ? _systemTransaction : trans;
	                Object cloned = null;
	                if (ref != null) {
	                    cloned = peekPersisted(trans, ref.getID(), depth, true);
	                }
	                return cloned;
				}
        	}, trans);
        }
    }

    public final Object peekPersisted(Transaction trans, int id, ActivationDepth depth, boolean resetJustPeeked) {
        if(resetJustPeeked){
            _justPeeked = null;
        }else{
            TreeInt ti = new TreeInt(id);
            TreeIntObject tio = (TreeIntObject) Tree.find(_justPeeked, ti);
            if(tio != null){
                return tio._object;
            }
        }
        ObjectReference ref = peekReference(trans, id, depth, resetJustPeeked);
        return ref.getObject(); 
    }

	public ObjectReference peekReference(Transaction trans, int id, ActivationDepth depth, boolean resetJustPeeked) {
		ObjectReference ref = new ObjectReference(id);
		ref.peekPersisted(trans, depth);
        if(resetJustPeeked){
            _justPeeked = null;
        }
		return ref;
	}

	void peeked(int id, Object obj) {
        _justPeeked = Tree
            .add(_justPeeked, new TreeIntObject(id, obj));
    }

	public void purge() {
	    synchronized (_lock) {
	        checkClosed();
	        System.gc();
	        System.runFinalization();
	        System.gc();
	        gc();
	        _classCollection.purge();
	    }
	}
    
    public final void purge(Transaction trans, Object obj) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            trans.removeObjectFromReferenceSystem(obj);
        }
    }

    final void removeFromAllReferenceSystems(Object obj) {
        if (obj == null) {
        	return;
        }
        if (obj instanceof ObjectReference) {
            _referenceSystemRegistry.removeReference((ObjectReference) obj);
            return;
        }
        _referenceSystemRegistry.removeObject(obj);
    }
    
    public final NativeQueryHandler getNativeQueryHandler() {
        synchronized(_lock){
        	if (null == _nativeQueryHandler) {
        		_nativeQueryHandler = new NativeQueryHandler(this);
        	}
        	return _nativeQueryHandler;
        }
    }
    
    public final ObjectSet query(Transaction trans, Predicate predicate){
        return query(trans, predicate,(QueryComparator)null);
    }
    
    public final <T> ObjectSet<T> query(Transaction trans, Predicate<T> predicate,QueryComparator<T> comparator){
        synchronized (_lock) {
            return getNativeQueryHandler().execute(query(trans), predicate, comparator);
        }
    }

    public final <T> ObjectSet<T> query(Transaction trans, Class<T> clazz) {
        return queryByExample(trans, clazz);
    }

    public final Query query(Transaction ta) {
        return new QQuery(checkTransaction(ta), null, null);
    }

    public abstract void raiseCommitTimestamp(long minimumTimestamp);

    public abstract void readBytes(byte[] a_bytes, int a_address, int a_length) throws Db4oIOException;

    public abstract void readBytes(byte[] bytes, int address, int addressOffset, int length) throws Db4oIOException;
    

    public final ByteArrayBuffer decryptedBufferByAddress(int address, int length)
			throws Db4oIOException {
		ByteArrayBuffer reader = rawBufferByAddress(address, length);
		_handlers.decrypt(reader);
		return reader;
	}

	public ByteArrayBuffer rawBufferByAddress(int address, int length) {
		checkAddress(address);
		ByteArrayBuffer reader = new ByteArrayBuffer(length);
		readBytes(reader._buffer, address, length);
		return reader;
	}

	private void checkAddress(int address) throws IllegalArgumentException {
		if (address <= 0) {
			throw new IllegalArgumentException("Invalid address offset: "
					+ address);
		}
	}

    public final StatefulBuffer readWriterByAddress(Transaction a_trans,
        int address, int length) throws Db4oIOException {
    	checkAddress(address);
        StatefulBuffer reader = createStatefulBuffer(a_trans, address, length);
        reader.readEncrypt(this, address);
        return reader;
    }

    public abstract StatefulBuffer readStatefulBufferById(Transaction trans, int id);
    
    public abstract StatefulBuffer readStatefulBufferById(Transaction trans, int id, boolean lastCommitted);

    public abstract ByteArrayBuffer readBufferById(Transaction trans, int id);
    
    public abstract ByteArrayBuffer readBufferById(Transaction trans, int id, boolean lastCommitted);
    
    public abstract ByteArrayBuffer[] readSlotBuffers(Transaction trans, int[] ids);

    private void reboot() {
        commit(null);
        close();
        open();
    }
    
    public GenericReflector reflector(){
        return _handlers._reflector;
    }
    
    public final void refresh(Transaction trans, Object obj, int depth) {
        synchronized (_lock) {
        	refreshInternal(trans, obj, depth);
        }
    }

	protected void refreshInternal(Transaction trans, Object obj, int depth) {
	    activate(trans, obj, refreshActivationDepth(depth));
    }

	private ActivationDepth refreshActivationDepth(int depth) {
		return activationDepthProvider().activationDepth(depth, ActivationMode.REFRESH);
	}

    public abstract void releaseSemaphore(String name);
    
    public void flagAsHandled(ObjectReference ref){
    	ref.flagAsHandled(_topLevelCallId);
    }
    
    boolean flagForDelete(ObjectReference ref){
    	if(ref == null){
    		return false;
    	}
    	if(handledInCurrentTopLevelCall(ref)){
    		return false;
    	}
    	ref.flagForDelete(_topLevelCallId);
    	return true;
    }
    
    public abstract void releaseSemaphores(Transaction ta);

    void rename(Config4Impl config) {
        boolean renamedOne = false;
        if (config.rename() != null) {
            renamedOne = applyRenames(config);
        }
        _classCollection.checkChanges();
        if (renamedOne) {
        	reboot();
        }
    }

    protected boolean applyRenames(Config4Impl config) {
		boolean renamed = false;
		final Iterator4 i = config.rename().iterator();
		while (i.moveNext()) {
			final Rename ren = (Rename) i.current();
			if (alreadyApplied(ren)) {
				continue;
			}
			if (applyRename(ren)) {
				renamed = true;
			}
		}

		return renamed;
	}

	private boolean applyRename(Rename ren) {		
		if (ren.isField()) {
			return applyFieldRename(ren);
		}
		return applyClassRename(ren);
    }

	private boolean applyClassRename(Rename ren) {
	    final ClassMetadata classToRename = _classCollection.getClassMetadata(ren.rFrom);
		if (classToRename == null) {
			return false;
		}
		ClassMetadata existing = _classCollection.getClassMetadata(ren.rTo);
		if (existing != null) {
			logMsg(9, "class " + ren.rTo);
			return false;
		}
		classToRename.setName(ren.rTo);
		commitRenameFor(ren, classToRename);
		return true;
    }

	private boolean applyFieldRename(Rename ren) {
	    final ClassMetadata parentClass = _classCollection.getClassMetadata(ren.rClass);
	    if (parentClass == null) {
	    	return false;
	    }
	    if (!parentClass.renameField(ren.rFrom, ren.rTo)) {
	    	return false;
	    }
	    commitRenameFor(ren, parentClass);
	    return true;
    }

	private void commitRenameFor(Rename rename, ClassMetadata classMetadata) {
	    setDirtyInSystemTransaction(classMetadata);

	    logMsg(8, rename.rFrom + " to " + rename.rTo);

	    deleteInverseRenames(rename);

	    // store the rename, so we only do it once
	    store(systemTransaction(), rename);
    }

	private void deleteInverseRenames(Rename rename) {
	    // delete all that rename from the new name
	    // to allow future backswitching
	    ObjectSet inverseRenames = queryInverseRenames(rename);
	    while (inverseRenames.hasNext()) {
	    	delete(systemTransaction(), inverseRenames.next());
	    }
    }

	private ObjectSet queryInverseRenames(Rename ren) {
	    return queryByExample(systemTransaction(), Renames.forInverseQBE(ren));
    }

	private boolean alreadyApplied(Rename ren) {
	    return queryByExample(systemTransaction(), ren).size() != 0;
    }
    
    public final boolean handledInCurrentTopLevelCall(ObjectReference ref){
    	return ref.isFlaggedAsHandled(_topLevelCallId);
    }

    public abstract void reserve(int byteCount);
    
    public final void rollback(Transaction trans) {
        synchronized (_lock) {
        	trans = checkTransaction(trans);
        	checkReadOnly();
        	rollback1(trans);
        	trans.rollbackReferenceSystem();
        }
    }

    public abstract void rollback1(Transaction trans);

    /** @param obj */
    public void send(Object obj) {
        // TODO: implement
        throw new NotSupportedException();
    }

    public final void store(Transaction trans, Object obj)
			throws DatabaseClosedException, DatabaseReadOnlyException {
    	store(trans, obj, updateDepthProvider().unspecified(NullModifiedObjectQuery.INSTANCE));
    }    
    
	public final int store(Transaction trans, Object obj, UpdateDepth depth)
			throws DatabaseClosedException, DatabaseReadOnlyException {
		synchronized (_lock) {
			try {
				showInternalClasses(true);
				return storeInternal(trans, obj, depth, true);
			} finally {
				showInternalClasses(false);
			}
        }
	}
    
    public final int storeInternal(Transaction trans, Object obj,
			boolean checkJustSet) throws DatabaseClosedException,
			DatabaseReadOnlyException {
       return storeInternal(trans, obj, updateDepthProvider().unspecified(NullModifiedObjectQuery.INSTANCE), checkJustSet);
    }
    
    public int storeInternal(final Transaction trans, final Object obj, final UpdateDepth depth,
			final boolean checkJustSet) throws DatabaseClosedException,
			DatabaseReadOnlyException {
    	checkReadOnly();
    	
    	return asTopLevelStore(new Function4<Transaction, Integer>() {
			public Integer apply(Transaction trans) {
		        return storeAfterReplication(trans, obj, depth, checkJustSet);
			}
    	}, trans);
    }
    
    public final int storeAfterReplication(Transaction trans, Object obj, UpdateDepth depth,  boolean checkJust) {
        
        if (obj instanceof Db4oType) {
            Db4oType db4oType = db4oTypeStored(trans, obj);
            if (db4oType != null) {
                return getID(trans, db4oType);
            }
        }
        
        return store2(trans, obj, depth, checkJust);
    }
    
    public final void storeByNewReplication(Db4oReplicationReferenceProvider referenceProvider, Object obj){
        synchronized(_lock){
            _replicationCallState = Const4.NEW;
            _handlers._replicationReferenceProvider = referenceProvider;
            
            try {
            	store2(checkTransaction(), obj, updateDepthProvider().forDepth(1), false);
            } finally {
            	_replicationCallState = Const4.NONE;
            	_handlers._replicationReferenceProvider = null;
	        }
        }
    }
    
    public void checkStillToSet() {
        List4 postponedStillToSet = null;
        while (_stillToSet != null) {
            Iterator4 i = new Iterator4Impl(_stillToSet);
            _stillToSet = null;
            while (i.moveNext()) {
                PendingSet item = (PendingSet)i.current();
                
                ObjectReference ref = item.ref;
                Transaction trans = item.transaction;
                
                if(! ref.continueSet(trans, item.depth)) {
                    postponedStillToSet = new List4(postponedStillToSet, item);
                }
            }
        }
        _stillToSet = postponedStillToSet;
    }
    
    void notStorable(ReflectClass claxx, Object obj){
        if(! configImpl().exceptionsOnNotStorable()){
            return;
        }
        
        if(claxx == null){
        	throw new ObjectNotStorableException(obj.toString());
        }
        
        if(_handlers.isTransient(claxx)){
        	return;
        }
        throw new ObjectNotStorableException(claxx);
    }

    public final int store2(Transaction trans, Object obj, UpdateDepth updateDepth, boolean checkJustSet) {
        if (obj == null || (obj instanceof TransientClass)) {
            return 0;
        }
        
        ObjectAnalyzer analyzer = new ObjectAnalyzer(this, obj);
        analyzer.analyze(trans);
        if(analyzer.notStorable()){
            return 0;
        }
        
        ObjectReference ref = analyzer.objectReference();
        
		if (ref == null) {
            ClassMetadata classMetadata = analyzer.classMetadata();
            if (!objectCanNew(trans, classMetadata, obj)) {
                return 0;
            }
            ref = new ObjectReference();
            ref.store(trans, classMetadata, obj);
            trans.addNewReference(ref);
			if(obj instanceof Db4oTypeImpl){
			    ((Db4oTypeImpl)obj).setTrans(trans);
			}
			if (configImpl().messageLevel() > Const4.STATE) {
				message("" + ref.getID() + " new " + ref.classMetadata().getName());
			}
			
			flagAsHandled(ref);
			stillToSet(trans, ref, updateDepth);

        } else {
        	if (ref.isFlaggedAsHandled(_topLevelCallId)) {
        		assertNotInCallback();
        	}
            if (canUpdate()) {
                if(checkJustSet){
                    if( (! ref.isNew())  && handledInCurrentTopLevelCall(ref)){
                        return ref.getID();
                    }
                }
                if (updateDepth.sufficientDepth()) {
                    flagAsHandled(ref);
                    ref.writeUpdate(trans, updateDepth);
                }
            }
        }
        processPendingClassUpdates();
        return ref.getID();
    }

	private void assertNotInCallback() {
	    if(InCallback.value()) {
	    	throw new Db4oIllegalStateException("Objects must not be updated in callback");
	    }
    }

    private boolean objectCanNew(Transaction transaction, ClassMetadata yc, Object obj) {
		return callbacks().objectCanNew(transaction, obj)
			&& yc.dispatchEvent(transaction, obj, EventDispatchers.CAN_NEW);
	}

    public abstract void setDirtyInSystemTransaction(PersistentBase a_object);

    public abstract boolean setSemaphore(String name, int timeout);
    
    public abstract boolean setSemaphore(final Transaction trans, final String name, final int timeout);
    
    public abstract void releaseSemaphore(final Transaction trans, final String name);

    void stringIO(LatinStringIO io) {
        _handlers.stringIO(io);
    }

    final boolean showInternalClasses() {
        return isServer() || _showInternalClasses > 0;
    }

    /**
     * Objects implementing the "Internal4" marker interface are
     * not visible to queries, unless this flag is set to true.
     * The caller should reset the flag after the call.
     */
    public synchronized void showInternalClasses(boolean show) {
        if (show) {
            _showInternalClasses++;
        } else {
            _showInternalClasses--;
        }
        if (_showInternalClasses < 0) {
            _showInternalClasses = 0;
        }
    }
    
    private final boolean stackIsSmall(){
        return _stackDepth < _maxStackDepth;
    }

    boolean stateMessages() {
        return true; // overridden to do nothing in YapObjectCarrier
    }

    final List4 stillTo1(Transaction trans, List4 still, Object obj, ActivationDepth depth) {
    	
        if (obj == null || !depth.requiresActivation()) {
        	return still;
        }
        
        ObjectReference ref = trans.referenceForObject(obj);
        if (ref != null) {
        	if(handledInCurrentTopLevelCall(ref)){
        		return still;
        	}
        	flagAsHandled(ref);
            return new List4(still, new PendingActivation(ref, depth));
        } 
       
        final ReflectClass clazz = reflectorForObject(obj);
		if (clazz.isArray()) {
			if (!clazz.getComponentType().isPrimitive()) {
                Iterator4 arr = ArrayHandler.iterator(clazz, obj);
                while (arr.moveNext()) {
                	final Object current = arr.current();
                    if(current == null){
                        continue;
                    }
                    ClassMetadata classMetadata = classMetadataForObject(current);
                    still = stillTo1(trans, still, current, depth.descend(classMetadata));
                }
			}
			return still;
        } else {
            if (obj instanceof Entry) {
                still = stillTo1(trans, still, ((Entry) obj).key, depth);
                still = stillTo1(trans, still, ((Entry) obj).value, depth);
            } else  {
	            if (depth.mode().isDeactivate()) {
	                // Special handling to deactivate .net structs
	                ClassMetadata metadata = classMetadataForObject(obj);
	                if (metadata != null && metadata.isStruct()) {
	                    metadata.forceDeactivation(trans, depth, obj);
	                }
	            }
	        }
        }
        return still;
    }
    
    public final void stillToActivate(ActivationContext context) {

        // TODO: We don't want the simple classes to search the hc_tree
        // Kick them out here.

        //		if (a_object != null) {
        //			Class clazz = a_object.getClass();
        //			if(! clazz.isPrimitive()){
        
        if(processedByImmediateActivation(context)){
            return;
        }

        _stillToActivate = stillTo1(context.transaction(), _stillToActivate, context.targetObject(), context.depth());
    }

    private boolean processedByImmediateActivation(ActivationContext context) {
        if(! stackIsSmall()){
            return false;
        }
        if (!context.depth().requiresActivation()) {
            return true;
        }
        ObjectReference ref = context.transaction().referenceForObject(context.targetObject());
        if(ref == null){
            return false;
        }
        if(handledInCurrentTopLevelCall(ref)){
            return true;
        }
        flagAsHandled(ref);
        incStackDepth();
        try{
            ref.activateInternal(context);
        } finally {
            decStackDepth();
        }
        return true;
    }

    
	private int decStackDepth() {
		int i = _stackDepth--;
		
		if (stackIsSmall() && !_handlingStackLimitPendings) {
			
			_handlingStackLimitPendings = true;
			try {
				handleStackLimitPendings();
			} finally {
				_handlingStackLimitPendings = false;
			}
		}
		return i;
	}

	private void handleStackLimitPendings() {
		checkStillToSet();
//		activatePending();
//		deactivatePending();
	}

	private int incStackDepth() {
		return _stackDepth++;
	}

    public final void stillToDeactivate(Transaction trans, Object a_object, ActivationDepth a_depth,
        boolean a_forceUnknownDeactivate) {
        _stillToDeactivate = stillTo1(trans, _stillToDeactivate, a_object, a_depth);
    }
    
    static class PendingSet {
    	public final Transaction transaction;
    	public final ObjectReference ref;
    	public final UpdateDepth depth;
    	
    	public PendingSet(Transaction transaction_, ObjectReference ref_, UpdateDepth depth_) {
    		this.transaction = transaction_;
    		this.ref = ref_;
    		this.depth = depth_;
		}
    }

    void stillToSet(Transaction transaction, ObjectReference ref, UpdateDepth updateDepth) {
        if(stackIsSmall()){
            if(ref.continueSet(transaction, updateDepth)){
                return;
            }
        }
        _stillToSet = new List4(_stillToSet, new PendingSet(transaction, ref, updateDepth));
    }

    protected final void stopSession() {
        if (hasShutDownHook()) {
            Platform4.removeShutDownHook(this);
        }
        _classCollection = null;
        if(_references != null){
        	_references.stop();
        }
        _systemTransaction = null;
        _transaction = null;
    }
    
    public final StoredClass storedClass(Transaction trans, Object clazz) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            ReflectClass claxx = ReflectorUtils.reflectClassFor(reflector(), clazz);
            if (claxx == null) {
            	return null;
            }
            ClassMetadata classMetadata = classMetadataForReflectClass(claxx);
            if(classMetadata == null){
                return null;
            }
            return new StoredClassImpl(trans, classMetadata);
        }
    }
    
    public StoredClass[] storedClasses(Transaction trans) {
        synchronized (_lock) {
            trans = checkTransaction(trans);
            StoredClass[] classMetadata = _classCollection.storedClasses();
            StoredClass[] storedClasses = new StoredClass[classMetadata.length];
            for (int i = 0; i < classMetadata.length; i++) {
                storedClasses[i] = new StoredClassImpl(trans, (ClassMetadata)classMetadata[i]);
            }
            return storedClasses;
        }
    }
		
    public LatinStringIO stringIO(){
    	return _handlers.stringIO();
    }
    
    public abstract SystemInfo systemInfo();
    
    private final void beginTopLevelCall(){
    	if(DTrace.enabled){
    		DTrace.BEGIN_TOP_LEVEL_CALL.log();
    	}
    	generateCallIDOnTopLevel();
    	incStackDepth();
    }
    
    private final void endTopLevelCall(){
    	if(DTrace.enabled){
    		DTrace.END_TOP_LEVEL_CALL.log();
    	}
    	decStackDepth();
    	generateCallIDOnTopLevel();
    }
    
    private final void generateCallIDOnTopLevel(){
    	if(_stackDepth == 0){
    		_topLevelCallId = _topLevelCallIdGenerator.next();
    	}
    }
    
    public int stackDepth(){
    	return _stackDepth;
    }
    
    public void stackDepth(int depth){
    	_stackDepth = depth;
    }
    
    public int topLevelCallId(){
    	return _topLevelCallId;
    }
    
    public void topLevelCallId(int id){
    	_topLevelCallId = id;
    }

    public long version(){
    	synchronized(_lock){
    		return currentVersion();
    	}
    }

    public abstract void shutdown();

    public abstract void writeDirtyClassMetadata();

    public abstract void writeNew(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ByteArrayBuffer buffer);

    public abstract void writeUpdate(Transaction trans, Pointer4 pointer, ClassMetadata classMetadata, ArrayType arrayType, ByteArrayBuffer buffer);

    public Callbacks callbacks() {
    	return _callbacks;
    }
    
    public void callbacks(Callbacks cb) {
		if (cb == null) {
			throw new IllegalArgumentException();
		}
		_callbacks = cb;
    }

    public Config4Impl configImpl() {
        return _config;
    }
    
	public UUIDFieldMetadata uUIDIndex() {
		return _handlers.indexes()._uUID;
	}
	
	public VersionFieldMetadata versionIndex() {
		return _handlers.indexes()._version;
	}

	public CommitTimestampFieldMetadata commitTimestampIndex() {
		return _handlers.indexes()._commitTimestamp;
	}

    public ClassMetadataRepository classCollection() {
        return _classCollection;
    }
    
    public abstract long[] getIDsForClass(Transaction trans, ClassMetadata clazz);
    
	public abstract QueryResult classOnlyQuery(QQueryBase queryBase, ClassMetadata clazz);
	
	public abstract QueryResult executeQuery(QQuery query);
	
	public void replicationCallState(int state) {
		_replicationCallState = state;
	}

	public ReferenceSystemRegistry referenceSystemRegistry(){
	    return _referenceSystemRegistry;
	}
	   
    public ObjectContainerBase container(){
        return this;
    }
    
	public void deleteByID(Transaction transaction, int id, int cascadeDeleteDepth) {
		if(id <= 0){
			throw new IllegalArgumentException("ID: " + id);
//			return;
		}
        if (cascadeDeleteDepth <= 0) {
        	return;
        }
        Object obj = getByID2(transaction, id);
        if(obj == null){
        	return;
        }
        cascadeDeleteDepth--;
        ReflectClass claxx = reflectorForObject(obj);
		if (claxx.isCollection()) {
            cascadeDeleteDepth += 1;
        }
        ObjectReference ref = transaction.referenceForId(id);
        if (ref == null) {
        	return;
        }
        delete2(transaction, ref, obj,cascadeDeleteDepth, false);
	}
	
	ReflectClass reflectorForObject(Object obj){
	    return reflector().forObject(obj);
	}

    
    public <R> R syncExec(Closure4<R> block) {
    	synchronized(_lock) {
    		checkClosed();
    		return block.run();
    	}
    }
	
	/**
     * @sharpen.ignore
     */
    @decaf.Ignore(decaf.Platform.JDK11)
    public ObjectSet query(Predicate predicate,Comparator comparator) {
		return query(null, predicate,new JdkComparatorWrapper(comparator));
	}
    
	public void storeAll(Transaction transaction, Iterator4 objects) {
		while(objects.moveNext()){
			store(transaction, objects.current());
		}
	}

	public void storeAll(Transaction transaction, Iterator4 objects, UpdateDepth depth) {
		while(objects.moveNext()){
			store(transaction, objects.current(), depth);
		}
	}

	public void withTransaction(Transaction transaction, Runnable runnable) {
		synchronized (_lock) {
			final Transaction old = _transaction;
			_transaction = transaction;
			try {
				runnable.run();
			} finally {
				_transaction = old;
			}
		}
    }

	public ThreadPool4 threadPool() {
        return environment().provide(ThreadPool4.class);
    }

	public Object newWeakReference(ObjectReference referent, Object obj) {
		return _references.newWeakReference(referent, obj);
	}
	
	@Override
	public final String toString() {
		if(_name != null) {
			return _name;
		}
		return defaultToString();
	}

	protected abstract String defaultToString();
	
	public abstract boolean isDeleted(Transaction trans, int id);
	
    public abstract void blockSize(int size);
	
	public BlockConverter blockConverter(){
		return _blockConverter;
	}
	
	protected void createBlockConverter(int blockSize) {
		if(blockSize == 1){
    		_blockConverter = new DisabledBlockConverter(); 	
    	} else {
    		_blockConverter = new BlockSizeBlockConverter(blockSize);	
    	}
	}

	public UpdateDepthProvider updateDepthProvider() {
		return configImpl().updateDepthProvider();
	}
	
	public void replaceClassMetadataRepository(ClassMetadataRepository repository){
		_classCollection = repository;
	}
	
	public final long generateTransactionTimestamp(long forcedTimestamp){
		synchronized (lock()) {
			return checkTransaction().generateTransactionTimestamp(forcedTimestamp);
		}
	}
	
	public final void useDefaultTransactionTimestamp(){
		synchronized (lock()) {
			checkTransaction().useDefaultTransactionTimestamp();
		}
	}


}