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
import com.db4o.activation.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * A weak reference to an known object.
 * 
 * "Known" ~ has been stored and/or retrieved within a transaction.
 * 
 * References the corresponding ClassMetaData along with further metadata:
 * internal id, UUID/version information, ...
 * 
 * @exclude
 */
public class ObjectReference extends Identifiable implements ObjectInfo, Activator {
	
	private ClassMetadata _class;
	private Object _object;
	private VirtualAttributes _virtualAttributes;

	private ObjectReference _idPreceding;
	private ObjectReference _idSubsequent;
	private int _idSize;

	private ObjectReference _hcPreceding;
	private ObjectReference _hcSubsequent;
	private int _hcSize;
	public int _hcHashcode; // redundant hashCode
	
	private int _lastTopLevelCallId;
    
    public ObjectReference(){
    }
	
	public ObjectReference(int id) {
		_id = id;
		if(DTrace.enabled){
		    DTrace.OBJECT_REFERENCE_CREATED.log(id);
		}
	}

	public ObjectReference(ClassMetadata classMetadata, int id) {
	    this(id);
		_class = classMetadata;
	}
	
	public void activate(ActivationPurpose purpose) {
		activateOn(container().transaction(), purpose);
	}

	public void activateOn(final Transaction transaction, ActivationPurpose purpose) {
		if (activating()) {
			return;
		}
		
		try {
			activating(true);
			
			final ObjectContainerBase container = transaction.container();		
			
			if (!(container.activationDepthProvider() instanceof TransparentActivationDepthProvider)) {
				return;
			}
			
			final TransparentActivationDepthProvider provider = (TransparentActivationDepthProvider) container.activationDepthProvider();
	    	if (ActivationPurpose.WRITE == purpose) {
	    		synchronized(container.lock()){
	    			provider.addModified(getObject(), transaction);
	    		}
	    	}
	    	
			if (isActive()) {    			
				return;
			}
			
			synchronized(container.lock()){
				activate(transaction, getObject(), new DescendingActivationDepth(provider, ActivationMode.ACTIVATE));
			} 
		}
		finally {
			activating(false);
		}
	}

	private boolean activating() {
		return bitIsTrue(Const4.ACTIVATING);
	}

	private void activating(boolean isActivating) {
		if (isActivating) {
			bitTrue(Const4.ACTIVATING);
		} else {
			bitFalse(Const4.ACTIVATING);
		}
	}

	public void activate(Transaction ta, Object obj, ActivationDepth depth) {
		final ObjectContainerBase container = ta.container();
	    activateInternal(container.activationContextFor(ta, obj, depth));
		container.activatePending(ta);
	}
	
	void activateInternal(ActivationContext context) {
		if (null == context) {
			throw new ArgumentNullException();
		}
		
		if (!context.depth().requiresActivation()) {
			return;
		}
		
		ObjectContainerBase container = context.container();
	    if (context.depth().mode().isRefresh()){
			logActivation(container, "refresh");
	    } else {
			if (isActive()) {
				_class.cascadeActivation(context);
				return;
			}
			logActivation(container, "activate");
	    }
		readForActivation(context);
	}

	private void readForActivation(ActivationContext context) {
		read(context.transaction(), null, context.targetObject(), context.depth(), Const4.ADD_MEMBERS_TO_ID_TREE_ONLY, false);
	}
	
	private void logActivation(ObjectContainerBase container, String event) {
		logEvent(container, event, Const4.ACTIVATION);
	}

	private void logEvent(ObjectContainerBase container, String event, final int level) {
		if (container.configImpl().messageLevel() > level) {
			container.message("" + getID() + " " + event + " " + _class.getName());
		}
	}

	/** return false if class not completely initialized, otherwise true **/
	boolean continueSet(Transaction trans, UpdateDepth updateDepth) {
		if (! bitIsTrue(Const4.CONTINUE)) {
		    return true;
		}
		
	    if(! _class.stateOK()){
	        return false;
	    }
	    
	    if(! _class.aspectsAreInitialized()){
	    	return false;
	    }
        
        if(DTrace.enabled){
            DTrace.CONTINUESET.log(getID());
        }
        
		bitFalse(Const4.CONTINUE);
        
        MarshallingContext context = new MarshallingContext(trans, this, updateDepth, true);
        
        Handlers4.write(classMetadata().typeHandler(), context, getObject());
        
        Pointer4 pointer = context.allocateSlot();
        ByteArrayBuffer buffer = context.toWriteBuffer(pointer);

        ObjectContainerBase container = trans.container();
		container.writeNew(trans, pointer, _class, buffer);

        Object obj = _object;
		objectOnNew(trans, obj);
		
        if(_class.hasIdentity()){
            _object = container.newWeakReference(this, obj);
        }
		
		setStateClean();
		endProcessing();
		
		return true;
	}

	private void objectOnNew(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnNew(transaction, this);
		_class.dispatchEvent(transaction, obj, EventDispatchers.NEW);
	}

	public void deactivate(Transaction trans, ActivationDepth depth) {
		if (!depth.requiresActivation()) {
			return;
		}
		Object obj = getObject();
		if (obj == null) {
			return;
		}
	    ObjectContainerBase container = trans.container();
		logActivation(container, "deactivate");
		setStateDeactivated();
		_class.deactivate(trans, this, depth);
	}
	
	public byte getIdentifier() {
		return Const4.YAPOBJECT;
	}
	
	public long getInternalID() {
		return getID();
	}
	
	public Object getObject() {
		if (Platform4.hasWeakReferences()) {
			return Platform4.getYapRefObject(_object);
		}
		return _object;
	}
	
	public Object getObjectReference(){
		return _object;
	}
    
    public ObjectContainerBase container(){
        if(_class == null){
            throw new IllegalStateException();
        }
        return _class.container();
    }
    
    public Transaction transaction(){
        return container().transaction();
    }
    
    public Db4oUUID getUUID(){
        VirtualAttributes va = virtualAttributes(transaction());
        if(va != null && va.i_database != null){
            return new Db4oUUID(va.i_uuid, va.i_database.i_signature);
        }
        return null;
    }
	
    public long getVersion(){
    	return getCommitTimestamp();
    }
    
    public long getCommitTimestamp() {
    	synchronized (container().lock()) {
    		return container().systemTransaction().versionForId(getID());
    	}
    }

	public final ClassMetadata classMetadata() {
		return _class;
	}
	
    public void classMetadata(ClassMetadata classMetadata) {
    	if (_class == classMetadata) {
    		return;
    	}
    	
    	if (_class != null) {
    		throw new IllegalStateException("Object types aren't supposed to change!");
    	}
    	
        _class = classMetadata;
    }

	public int ownLength() {
        throw Exceptions4.shouldNeverBeCalled();
	}
	
	public VirtualAttributes produceVirtualAttributes() {
		if(_virtualAttributes == null){
			_virtualAttributes = new VirtualAttributes();
		}
		return _virtualAttributes;
	}
	
	final void peekPersisted(Transaction trans, ActivationDepth depth) {
        setObject(read(trans, depth, Const4.TRANSIENT, false));
	}
	
	final Object read(Transaction trans, ActivationDepth instantiationDepth,int addToIDTree,boolean checkIDTree) {
		return read(trans, null, null, instantiationDepth, addToIDTree, checkIDTree); 
	}
	
	public final Object read(
		Transaction trans,
		ByteArrayBuffer buffer,
		Object obj,
		ActivationDepth instantiationDepth,
		int addToIDTree,
        boolean checkIDTree) {
		
		UnmarshallingContext context = new UnmarshallingContext(trans, buffer, this, addToIDTree, checkIDTree);
		context.persistentObject(obj);
		context.activationDepth(instantiationDepth);
		return context.read();
	}

	public Object readPrefetch(Transaction trans, ByteArrayBuffer buffer, final int addToIDTree) {
	    final UnmarshallingContext context = new UnmarshallingContext(trans, buffer, this, addToIDTree, false);
	    context.activationDepth(new FixedActivationDepth(1, ActivationMode.PREFETCH));
		return context.read();
    }

	public final void readThis(Transaction trans, ByteArrayBuffer buffer) {
		if (Deploy.debug) {
			System.out.println(
				"YapObject.readThis should never be called. All handling takes place in read");
		}
	}

	public void setObjectWeak(ObjectContainerBase container, Object obj) {
		if(_object != null){
			Platform4.killYapRef(_object);
		}
		_object = container.newWeakReference(this, obj);
	}

	public void setObject(Object obj) {
		_object = obj;
	}

	final void store(Transaction trans, ClassMetadata classMetadata, Object obj){
		_object = obj;
		_class = classMetadata;
		
		int id = trans.container().idForNewUserObject(trans);

        setID(id);

        // will be ended in continueset()
        beginProcessing();

        bitTrue(Const4.CONTINUE);
	}
	
	public void flagForDelete(int callId){
		_lastTopLevelCallId = - callId;
	}
	
	public boolean isFlaggedForDelete(){
		return _lastTopLevelCallId < 0;
	}
	
	public void flagAsHandled(int callId){
		_lastTopLevelCallId = callId;
	}
	
	public final boolean isFlaggedAsHandled(int callID){
		return _lastTopLevelCallId == callID;
	}
	
	public final boolean isValid() {
		return isValidId(getID()) && getObject() != null;
	}
	
	public static final boolean isValidId(int id){
		return id > 0;
	}
	
	public VirtualAttributes virtualAttributes(){
		return _virtualAttributes;
	}
	public VirtualAttributes virtualAttributes(Transaction trans, boolean lastCommitted){
        if(trans == null){
            return _virtualAttributes;
        }
        synchronized(trans.container().lock()){
    	    if(_virtualAttributes == null){ 
                if(_class.hasVirtualAttributes()){
                    _virtualAttributes = new VirtualAttributes();
                    _class.readVirtualAttributes(trans, this, lastCommitted);
                }
    	    }else{
                if(! _virtualAttributes.suppliesUUID()){
                    if(_class.hasVirtualAttributes()){
                        _class.readVirtualAttributes(trans, this, lastCommitted);
                    }
                }
            }
    	    return _virtualAttributes;
        }
	}
	
	public VirtualAttributes virtualAttributes(Transaction trans){
		return virtualAttributes(trans, false);
	}
    
    public void setVirtualAttributes(VirtualAttributes at){
        _virtualAttributes = at;
    }

	public void writeThis(Transaction trans, ByteArrayBuffer buffer) {
		if (Deploy.debug) {
			System.out.println("YapObject.writeThis should never be called.");
		}
	}

	public void writeUpdate(Transaction transaction, UpdateDepth updatedepth) {
		 
		continueSet(transaction, updatedepth);
		// make sure, a concurrent new, possibly triggered by objectOnNew
		// is written to the file
		
		// preventing recursive
		if ( !beginProcessing() ) {
		    return;
		}
		    
	    Object obj = getObject();
	    
	    if( !objectCanUpdate(transaction, obj) ||  
	    	!isActive()  || 
	    	obj == null || 
	    	! classMetadata().isModified(obj)){
	        endProcessing();
	        return;
	    }
			
		if (Deploy.debug) {
			if (!(getID() > 0)) {
				throw new IllegalStateException("ID invalid");
			}
			if (_class == null) {
				throw new IllegalStateException("ClassMetadata invalid");
			}
		}
		
		MarshallingContext context = new MarshallingContext(transaction, this, updatedepth, false);
		if (context.updateDepth().negative()) {
			endProcessing();
			return;
		}
		
        ObjectContainerBase container = transaction.container();
		
		logEvent(container, "update", Const4.STATE);
		
		setStateClean();
		
		context.purgeFieldIndexEntriesOnUpdate(transaction, container._handlers.arrayType(obj));
		
        Handlers4.write(_class.typeHandler(), context, obj);
        
        if(context.updateDepth().canSkip(this)) {
        	endProcessing();
        	return;
        }
        
        Pointer4 pointer = context.allocateSlot();
        ByteArrayBuffer buffer = context.toWriteBuffer(pointer);
        
        container.writeUpdate(transaction, pointer, _class, container._handlers.arrayType(obj), buffer);
        
        if (isActive()) {
            setStateClean();
        }
        endProcessing();
        
        container.callbacks().objectOnUpdate(transaction, this);
        classMetadata().dispatchEvent(transaction, obj, EventDispatchers.UPDATE);
		
	}

	protected boolean objectCanUpdate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanUpdate(transaction, this)
			&& _class.dispatchEvent(transaction, obj, EventDispatchers.CAN_UPDATE);
	}

	public void ref_init() {
		hc_init();
		id_init();
	}
	
	/***** HCTREE *****/

	public ObjectReference hc_add(ObjectReference newRef) {
		if (newRef.getObject() == null) {
			return this;
		}
		newRef.hc_init();
		return hc_add1(newRef);
	}
    
    private void hc_init(){
        _hcPreceding = null;
        _hcSubsequent = null;
        _hcSize = 1;
        _hcHashcode = hc_getCode(getObject());
    }
    
	private ObjectReference hc_add1(ObjectReference newRef) {
		int cmp = hc_compare(newRef);
		if (cmp < 0) {
			if (_hcPreceding == null) {
				_hcPreceding = newRef;
				_hcSize++;
			} else {
				_hcPreceding = _hcPreceding.hc_add1(newRef);
				if (_hcSubsequent == null) {
					return hc_rotateRight();
				} 
				return hc_balance();
			}
		} else {
			if (_hcSubsequent == null) {
				_hcSubsequent = newRef;
				_hcSize++;
			} else {
				_hcSubsequent = _hcSubsequent.hc_add1(newRef);
				if (_hcPreceding == null) {
					return hc_rotateLeft();
				} 
				return hc_balance();
			}
		}
		return this;
	}

	private ObjectReference hc_balance() {
		int cmp = _hcSubsequent._hcSize - _hcPreceding._hcSize;
		if (cmp < -2) {
			return hc_rotateRight();
		} else if (cmp > 2) {
			return hc_rotateLeft();
		} else {
			_hcSize = _hcPreceding._hcSize + _hcSubsequent._hcSize + 1;
			return this;
		}
	}

	private void hc_calculateSize() {
		if (_hcPreceding == null) {
			if (_hcSubsequent == null) {
				_hcSize = 1;
			} else {
				_hcSize = _hcSubsequent._hcSize + 1;
			}
		} else {
			if (_hcSubsequent == null) {
				_hcSize = _hcPreceding._hcSize + 1;
			} else {
				_hcSize = _hcPreceding._hcSize + _hcSubsequent._hcSize + 1;
			}
		}
	}

	private int hc_compare(ObjectReference toRef) {
	    int cmp = toRef._hcHashcode - _hcHashcode;
	    if(cmp == 0){
	        cmp = toRef._id - _id;
	    }
		return cmp;
	}

	public ObjectReference hc_find(Object obj) {
		return hc_find(hc_getCode(obj), obj);
	}

	private ObjectReference hc_find(int id, Object obj) {
		int cmp = id - _hcHashcode;
		if (cmp < 0) {
			if (_hcPreceding != null) {
				return _hcPreceding.hc_find(id, obj);
			}
		} else if (cmp > 0) {
			if (_hcSubsequent != null) {
				return _hcSubsequent.hc_find(id, obj);
			}
		} else {
			if (obj == getObject()) {
				return this;
			}
			if (_hcPreceding != null) {
				ObjectReference inPreceding = _hcPreceding.hc_find(id, obj);
				if (inPreceding != null) {
					return inPreceding;
				}
			}
			if (_hcSubsequent != null) {
				return _hcSubsequent.hc_find(id, obj);
			}
		}
		return null;
	}

	public static int hc_getCode(Object obj) {
		int hcode = System.identityHashCode(obj);
		if (hcode < 0) {
			hcode = ~hcode;
		}
		return hcode;
	}

	private ObjectReference hc_rotateLeft() {
		ObjectReference tree = _hcSubsequent;
		_hcSubsequent = tree._hcPreceding;
		hc_calculateSize();
		tree._hcPreceding = this;
		if(tree._hcSubsequent == null){
			tree._hcSize = 1 + _hcSize;
		}else{
			tree._hcSize = 1 + _hcSize + tree._hcSubsequent._hcSize;
		}
		return tree;
	}

	private ObjectReference hc_rotateRight() {
		ObjectReference tree = _hcPreceding;
		_hcPreceding = tree._hcSubsequent;
		hc_calculateSize();
		tree._hcSubsequent = this;
		if(tree._hcPreceding == null){
			tree._hcSize = 1 + _hcSize;
		}else{
			tree._hcSize = 1 + _hcSize + tree._hcPreceding._hcSize;
		}
		return tree;
	}

	private ObjectReference hc_rotateSmallestUp() {
		if (_hcPreceding != null) {
			_hcPreceding = _hcPreceding.hc_rotateSmallestUp();
			return hc_rotateRight();
		}
		return this;
	}

	public ObjectReference hc_remove(ObjectReference findRef) {
		if (this == findRef) {
			return hc_remove();
		}
		int cmp = hc_compare(findRef);
		if (cmp <= 0) {
			if (_hcPreceding != null) {
				_hcPreceding = _hcPreceding.hc_remove(findRef);
			}
		}
		if (cmp >= 0) {
			if (_hcSubsequent != null) {
				_hcSubsequent = _hcSubsequent.hc_remove(findRef);
			}
		}
		hc_calculateSize();
		return this;
	}
    
    public void hc_traverse(Visitor4 visitor){
        if(_hcPreceding != null){
            _hcPreceding.hc_traverse(visitor);
        }
        if(_hcSubsequent != null){
            _hcSubsequent.hc_traverse(visitor);
        }
        
        // Traversing the leaves first allows to add ObjectReference 
        // nodes to different ReferenceSystem trees during commit
        
        visitor.visit(this);
    }

	private ObjectReference hc_remove() {
		if (_hcSubsequent != null && _hcPreceding != null) {
			_hcSubsequent = _hcSubsequent.hc_rotateSmallestUp();
			_hcSubsequent._hcPreceding = _hcPreceding;
			_hcSubsequent.hc_calculateSize();
			return _hcSubsequent;
		}
		if (_hcSubsequent != null) {
			return _hcSubsequent;
		}
		return _hcPreceding;
	}

	/***** IDTREE *****/

	public ObjectReference id_add(ObjectReference newRef) {
		newRef.id_init();
		return id_add1(newRef);
	}

	private void id_init() {
		_idPreceding = null;
		_idSubsequent = null;
		_idSize = 1;
	}
	
	private ObjectReference id_add1(ObjectReference newRef) {
		int cmp = newRef._id - _id;
		if (cmp < 0) {
			if (_idPreceding == null) {
				_idPreceding = newRef;
				_idSize++;
			} else {
				_idPreceding = _idPreceding.id_add1(newRef);
				if (_idSubsequent == null) {
					return id_rotateRight();
				} 
				return id_balance();
			}
		} else if(cmp > 0) {
			if (_idSubsequent == null) {
				_idSubsequent = newRef;
				_idSize++;
			} else {
				_idSubsequent = _idSubsequent.id_add1(newRef);
				if (_idPreceding == null) {
					return id_rotateLeft();
				} 
				return id_balance();
			}
		}
		return this;
	}

	private ObjectReference id_balance() {
		int cmp = _idSubsequent._idSize - _idPreceding._idSize;
		if (cmp < -2) {
			return id_rotateRight();
		} else if (cmp > 2) {
			return id_rotateLeft();
		} else {
			_idSize = _idPreceding._idSize + _idSubsequent._idSize + 1;
			return this;
		}
	}

	private void id_calculateSize() {
		if (_idPreceding == null) {
			if (_idSubsequent == null) {
				_idSize = 1;
			} else {
				_idSize = _idSubsequent._idSize + 1;
			}
		} else {
			if (_idSubsequent == null) {
				_idSize = _idPreceding._idSize + 1;
			} else {
				_idSize = _idPreceding._idSize + _idSubsequent._idSize + 1;
			}
		}
	}

	public ObjectReference id_find(int id) {
		int cmp = id - _id;
		if (cmp > 0) {
			if (_idSubsequent != null) {
				return _idSubsequent.id_find(id);
			}
		} else if (cmp < 0) {
			if (_idPreceding != null) {
				return _idPreceding.id_find(id);
			}
		} else {
			return this;
		}
		return null;
	}

	private ObjectReference id_rotateLeft() {
		ObjectReference tree = _idSubsequent;
		_idSubsequent = tree._idPreceding;
		id_calculateSize();
		tree._idPreceding = this;
		if(tree._idSubsequent == null){
			tree._idSize = _idSize + 1;
		}else{
			tree._idSize = _idSize + 1 + tree._idSubsequent._idSize;
		}
		return tree;
	}

	private ObjectReference id_rotateRight() {
		ObjectReference tree = _idPreceding;
		_idPreceding = tree._idSubsequent;
		id_calculateSize();
		tree._idSubsequent = this;
		if(tree._idPreceding == null){
			tree._idSize = _idSize + 1;
		}else{
			tree._idSize = _idSize + 1 + tree._idPreceding._idSize;
		}
		return tree;
	}

	private ObjectReference id_rotateSmallestUp() {
		if (_idPreceding != null) {
			_idPreceding = _idPreceding.id_rotateSmallestUp();
			return id_rotateRight();
		}
		return this;
	}

	public ObjectReference id_remove(ObjectReference ref) {
		int cmp = ref._id - _id;
		if (cmp < 0) {
			if (_idPreceding != null) {
				_idPreceding = _idPreceding.id_remove(ref);
			}
		} else if (cmp > 0) {
			if (_idSubsequent != null) {
				_idSubsequent = _idSubsequent.id_remove(ref);
			}
		} else {
			if(this == ref){
				return id_remove();
			}
			return this;
		}
		id_calculateSize();
		return this;
	}

	private ObjectReference id_remove() {
		if (_idSubsequent != null && _idPreceding != null) {
			_idSubsequent = _idSubsequent.id_rotateSmallestUp();
			_idSubsequent._idPreceding = _idPreceding;
			_idSubsequent.id_calculateSize();
			return _idSubsequent;
		}
		if (_idSubsequent != null) {
			return _idSubsequent;
		}
		return _idPreceding;
	}
	
	public String toString(){
	    try{
		    int id = getID();
		    String str = "ObjectReference\nID=" + id;
	        Object obj = getObject();
		    if(obj == null && _class != null){
		        ObjectContainerBase container = _class.container();
		        if(container != null && id > 0){
		            obj = container.peekPersisted(container.transaction(), id, container.defaultActivationDepth(classMetadata()), true).toString();
		        }
		    }
		    if(obj == null){
		        str += "\nfor [null]";
		    }else{
		        String objToString ="";
			    try{
			        objToString = obj.toString();
			    }catch(Exception e){
			    }
			    if(classMetadata() != null){
				    ReflectClass claxx = classMetadata().reflector().forObject(obj);
				    str += "\n" + claxx.getName();
			    }
				str += "\n" + objToString;
		    }
		    return str;
	    }catch(Exception e){
	        
	    }
	    return "ObjectReference " + getID();
	}

	
}
