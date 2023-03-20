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
package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class MarshallingContext implements MarshallingInfo, WriteContext {
    
    private static final int HEADER_LENGTH = Const4.LEADING_LENGTH 
            + Const4.ID_LENGTH  // YapClass ID
            + 1 // Marshaller Version
            + Const4.INT_LENGTH; // number of fields
    
    private final Transaction _transaction;
    
    private final ObjectReference _reference;
    
    private UpdateDepth _updateDepth;
    
    private final boolean _isNew;
    
    private final BitMap4 _nullBitMap;
    
    private final MarshallingBuffer _writeBuffer;
    
    private MarshallingBuffer _currentBuffer;
    
    private ByteArrayBuffer _debugPrepend;
    
    private Object _currentMarshalledObject;
    
    private Object _currentIndexEntry;
    
	private int _declaredAspectCount;
    

    public MarshallingContext(Transaction trans, ObjectReference ref, UpdateDepth updateDepth, boolean isNew) {
        _transaction = trans;
        _reference = ref;
        _nullBitMap = new BitMap4(aspectCount());
        _updateDepth = classMetadata().adjustUpdateDepth(trans, updateDepth);
        _isNew = isNew;
        _writeBuffer = new MarshallingBuffer();
        _currentBuffer = _writeBuffer;
    }

    private int aspectCount() {
        return classMetadata().aspectCount();
    }

    public ClassMetadata classMetadata() {
        return _reference.classMetadata();
    }

    public boolean isNew() {
        return _isNew;
    }

    public boolean isNull(int fieldIndex) {
        // TODO Auto-generated method stub
        
        return false;
    }

    public void isNull(int fieldIndex, boolean flag) {
        _nullBitMap.set(fieldIndex, flag);
    }

    public Transaction transaction() {
        return _transaction;
    }
    
    public Slot allocateNewSlot(int length){
        if(_transaction instanceof LocalTransaction){
        	return localContainer().allocateSlotForNewUserObject(_transaction, objectID(), length);
        }
        return new Slot(Slot.NEW, length);
    }
    
    private Slot allocateUpdateSlot(int length){
        if(_transaction instanceof LocalTransaction){
            return localContainer().allocateSlotForUserObjectUpdate(transaction(), objectID(), length);
        }
        return new Slot(Slot.UPDATE, length);
    }

	private LocalObjectContainer localContainer() {
		return ((LocalTransaction)transaction()).localContainer();
	}
    
    public Pointer4 allocateSlot(){
        int length = container().blockConverter().blockAlignedBytes(marshalledLength());
        Slot slot = isNew() ? allocateNewSlot(length) : allocateUpdateSlot(length);
        return new Pointer4(objectID(), slot);
    }

    public ByteArrayBuffer toWriteBuffer(Pointer4 pointer) {
        
        ByteArrayBuffer buffer = new ByteArrayBuffer(pointer.length());
        _writeBuffer.mergeChildren(this, pointer.address(), writeBufferOffset());
        
        if (Deploy.debug) {
            buffer.writeBegin(Const4.YAPOBJECT);
        }
        
        writeObjectClassID(buffer, classMetadata().getID());
        buffer.writeByte(HandlerRegistry.HANDLER_VERSION);
        buffer.writeInt(aspectCount());
        buffer.writeBitMap(_nullBitMap);
        
        _writeBuffer.transferContentTo(buffer);
        
        if (Deploy.debug) {
            buffer.writeEnd();
        }
        
        return buffer;
    }
    
    private int writeBufferOffset(){
        return HEADER_LENGTH + _nullBitMap.marshalledLength();
    }

    public int marshalledLength() {
        int length = writeBufferOffset();
        _writeBuffer.checkBlockAlignment(this, null, new IntByRef(length));
        return length + _writeBuffer.marshalledLength() + Const4.BRACKETS_BYTES;
    }
    
    public int requiredLength(MarshallingBuffer buffer, boolean align) {
        if(! align){
            return buffer.length();
        }
        return container().blockConverter().blockAlignedBytes(buffer.length());
    }
    
    private void writeObjectClassID(ByteArrayBuffer reader, int id) {
        reader.writeInt(-id);
    }

    public Object getObject() {
        return _reference.getObject();
    }

    public Config4Class classConfiguration() {
        return classMetadata().config();
    }

    public UpdateDepth updateDepth() {
        return _updateDepth;
    }

    public void updateDepth(UpdateDepth depth) {
        _updateDepth = depth;
    }

    public int objectID() {
        return _reference.getID();
    }

    public Object currentIndexEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectContainerBase container() {
        return transaction().container();
    }

    public ObjectContainer objectContainer() {
        return transaction().objectContainer();
    }

	public void writeByte(byte b) {
	    preWrite();
	    _currentBuffer.writeByte(b);
	    postWrite();
	}
	
	public void writeBytes(byte[] bytes) {
	    preWrite();
	    _currentBuffer.writeBytes(bytes);
	    postWrite();
	}

    public void writeInt(int i) {
        preWrite();
        _currentBuffer.writeInt(i);
        postWrite();
    }
    
    public void writeLong(long l) {
        preWrite();
        _currentBuffer.writeLong(l);
        postWrite();
    }
    
	private void preWrite() {
        if(Deploy.debug){
            if(_debugPrepend != null){
                for (int i = 0; i < _debugPrepend.offset(); i++) {
                    _currentBuffer.writeByte(_debugPrepend._buffer[i]);
                }
            }
        }
    }
	
	private void postWrite(){
	    if(Deploy.debug){
	        if(_debugPrepend != null){
	            _currentBuffer.debugDecrementLastOffset(_debugPrepend.offset());
	            _debugPrepend = null;
	        }
	    }
	}

    public void createChildBuffer(boolean storeLengthInLink) {
        MarshallingBuffer childBuffer = _currentBuffer.addChild(false, storeLengthInLink);
        _currentBuffer.reserveChildLinkSpace(storeLengthInLink);
        _currentBuffer = childBuffer;
    }

    public void beginSlot(){
        _currentBuffer = _writeBuffer;
    }
    
    public void writeDeclaredAspectCount(int count) {
        _writeBuffer.writeInt(count);
    }

    public void debugPrependNextWrite(ByteArrayBuffer prepend) {
        if(Deploy.debug){
            _debugPrepend = prepend;
        }
    }

    public void debugWriteEnd(byte b) {
        _currentBuffer.writeByte(b);
    }

    public void writeObject(Object obj) {
        int id = container().storeInternal(transaction(), obj, _updateDepth, true);
        writeInt(id);
        _currentMarshalledObject = obj;
        _currentIndexEntry = new Integer(id);
    }
    
    public void writeObject(TypeHandler4 handler, Object obj){
        MarshallingContextState state = currentState();
        writeObjectWithCurrentState(handler, obj);
        restoreState(state);
    }

	public void writeObjectWithCurrentState(TypeHandler4 handler, Object obj) {
		if(Handlers4.useDedicatedSlot(this, handler)){
            writeObject(obj);
        }else{
            if(obj == null){
                writeNullReference(handler);
            } else{
                createIndirectionWithinSlot(handler);
                handler.write(this, obj);
            }
        }
	}
    
    private void writeNullReference(TypeHandler4 handler){
        if( isIndirectedWithinSlot(handler)){
            writeNullLink();
            return;
        }
        Handlers4.write(handler, this, Handlers4.nullRepresentationInUntypedArrays(handler));
    }
    
    private void writeNullLink(){
        writeInt(0);
        writeInt(0);
    }
    
    public void addIndexEntry(FieldMetadata fieldMetadata, Object obj) {
        if(! _currentBuffer.hasParent()){
            Object indexEntry = (obj == _currentMarshalledObject) ? _currentIndexEntry : obj; 
            if(_isNew || !updateDepth().canSkip(_reference)) {
            	fieldMetadata.addIndexEntry(transaction(), objectID(), indexEntry);
            }
            return;
        }
        _currentBuffer.requestIndexEntry(fieldMetadata);
    }
    
    public void purgeFieldIndexEntriesOnUpdate(Transaction transaction, ArrayType arrayType) {
		if(!updateDepth().canSkip(_reference)) {
			transaction.writeUpdateAdjustIndexes(_reference.getID(), _reference.classMetadata(), arrayType);
		}
    }
    
    public ObjectReference reference(){
        return _reference;
    }
    
    public void createIndirectionWithinSlot(TypeHandler4 handler) {
        if(isIndirectedWithinSlot(handler)){
        	createIndirectionWithinSlot();
        }
    }
    
    public void createIndirectionWithinSlot() {
    	createChildBuffer(true);
    }

    private boolean isIndirectedWithinSlot(TypeHandler4 handler) {
        return SlotFormat.current().isIndirectedWithinSlot(handler);
    }

    // FIXME: This method was just temporarily added to fulfill contract of MarshallingInfo
    //        It will go, the buffer is never needed in new marshalling. 
    public ReadBuffer buffer() {
        return null;
    }
    
    public MarshallingContextState currentState(){
        return new MarshallingContextState(_currentBuffer);
    }
    
    public void restoreState(MarshallingContextState state){
        _currentBuffer = state._buffer;
    }

    public ReservedBuffer reserve(final int length) {
        preWrite();
        ReservedBuffer reservedBuffer = _currentBuffer.reserve(length);
        postWrite();
        return reservedBuffer;
    }

	public int declaredAspectCount() {
		return _declaredAspectCount;
	}

	public void declaredAspectCount(int count) {
		_declaredAspectCount = count;
	}
    


}
