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

import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class MarshallingBuffer implements WriteBuffer{
    
    private static final int SIZE_NEEDED = Const4.LONG_LENGTH;
    
    private static final int NO_PARENT = - Integer.MAX_VALUE;
    
    private ByteArrayBuffer _delegate;
    
    private int _lastOffSet;
    
    private int _addressInParent = NO_PARENT;
    
    private List4 _children;
    
    private FieldMetadata _indexedField;
    
    public int length() {
        return offset();
    }
    
    public int offset(){
        if(_delegate == null){
            return 0;
        }
        return _delegate.offset();
    }
    
    public void writeByte(byte b) {
        prepareWrite();
        _delegate.writeByte(b);
    }
    
    public void writeBytes(byte[] bytes) {
        prepareWrite(bytes.length);
        _delegate.writeBytes(bytes);
    }

    public void writeInt(int i) {
        prepareWrite();
        _delegate.writeInt(i);
    }
    
    public void writeLong(long l) {
        prepareWrite();
        _delegate.writeLong(l);
    }
    
    private void prepareWrite(){
        prepareWrite(SIZE_NEEDED);
    }
    
    public void prepareWrite(int sizeNeeded){
        if(_delegate == null){
            _delegate = new ByteArrayBuffer(sizeNeeded); 
        }
        _lastOffSet = _delegate.offset();
        if(remainingSize() < sizeNeeded){
            resize(sizeNeeded);
        }
    }

    private int remainingSize() {
        return _delegate.length() - _delegate.offset();
    }

    private void resize(int sizeNeeded) {
        int newSize = _delegate.length() * 2;
        if(newSize - _lastOffSet < sizeNeeded){
            newSize += sizeNeeded;
        }
        ByteArrayBuffer temp = new ByteArrayBuffer(newSize);
        temp.seek(_lastOffSet);
        _delegate.copyTo(temp, 0, 0, _delegate.length());
        _delegate = temp;
    }
    
    public void transferLastWriteTo(MarshallingBuffer other, boolean storeLengthInLink){
        other.addressInParent(_lastOffSet, storeLengthInLink);
        int length = _delegate.offset() - _lastOffSet;
        other.prepareWrite(length);
        int otherOffset = other._delegate.offset();
        System.arraycopy(_delegate._buffer, _lastOffSet, other._delegate._buffer, otherOffset, length);
        _delegate.seek(_lastOffSet);
        other._delegate.seek(otherOffset + length);
        other._lastOffSet = otherOffset;
    }
    
    private void addressInParent(int offset, boolean storeLengthInLink) {
        _addressInParent = storeLengthInLink ? offset : -offset;
    }

    public void transferContentTo(ByteArrayBuffer buffer){
        transferContentTo(buffer, length());
    }
    
    public void transferContentTo(ByteArrayBuffer buffer, int length){
    	if(_delegate == null){
    		return;
    	}
        System.arraycopy(_delegate._buffer, 0, buffer._buffer, buffer._offset, length);
        buffer._offset += length;
    }
    
    public ByteArrayBuffer testDelegate(){
        return _delegate;
    }
    
    public MarshallingBuffer addChild() {
        return addChild(true, false);
    }
    
    public MarshallingBuffer addChild(boolean reserveLinkSpace, boolean storeLengthInLink) {
        MarshallingBuffer child = new MarshallingBuffer();
        child.addressInParent(offset(), storeLengthInLink);
        _children = new List4(_children, child);
        if(reserveLinkSpace){
            reserveChildLinkSpace(storeLengthInLink);
        }
        return child;
    }

    public void reserveChildLinkSpace(boolean storeLengthInLink) {
        int length = storeLengthInLink ? Const4.INT_LENGTH * 2 : Const4.INT_LENGTH;
        prepareWrite(length);
        _delegate.incrementOffset(length);
    }
    
    public void mergeChildren(MarshallingContext context, int masterAddress, int linkOffset) {
        mergeChildren(context, masterAddress, this, this, linkOffset);
    }

    private static void mergeChildren(MarshallingContext context, int masterAddress, MarshallingBuffer writeBuffer, MarshallingBuffer parentBuffer, int linkOffset) {
        if(parentBuffer._children == null){
            return;
        }
        Iterator4 i = new Iterator4Impl(parentBuffer._children);
        while(i.moveNext()){
            merge(context, masterAddress, writeBuffer, parentBuffer, (MarshallingBuffer) i.current(), linkOffset);
        }
    }
    
    private static void merge(MarshallingContext context, int masterAddress, MarshallingBuffer writeBuffer, MarshallingBuffer parentBuffer, MarshallingBuffer childBuffer, int linkOffset) {
        
        int childPosition = writeBuffer.offset();
        
        writeBuffer.reserve(childBuffer.blockedLength());
        
        mergeChildren(context,  masterAddress, writeBuffer, childBuffer, linkOffset);
        
        int savedWriteBufferOffset = writeBuffer.offset();
        writeBuffer.seek(childPosition);
        childBuffer.transferContentTo(writeBuffer._delegate);
        writeBuffer.seek(savedWriteBufferOffset);
        
        parentBuffer.writeLink(childBuffer, childPosition + linkOffset, childBuffer.unblockedLength());
        
        childBuffer.writeIndex(context, masterAddress, childPosition + linkOffset);
        
    }
    
    public void seek(int offset) {
        _delegate.seek(offset);
    }

    public ReservedBuffer reserve(int length) {
        prepareWrite(length);
        ReservedBuffer reservedBuffer = new ReservedBuffer() {
            private final int reservedOffset = _delegate.offset();
            public void writeBytes(byte[] bytes) {
                int currentOffset = _delegate.offset();
                _delegate.seek(reservedOffset);
                _delegate.writeBytes(bytes);
                _delegate.seek(currentOffset);
            }
        };
        _delegate.seek(_delegate.offset() + length );
        return reservedBuffer;
    }

    private void writeLink(MarshallingBuffer child, int position, int length){
        int offset = offset();
        _delegate.seek(child.addressInParent());
        _delegate.writeInt(position);
        if(child.storeLengthInLink()){
            _delegate.writeInt(length);
        }
        _delegate.seek(offset);
    }
    
    private void writeIndex(MarshallingContext context, int masterAddress, int position) {
        if(_indexedField != null){
            
            // for now this is a String index only, it takes the entire slot.
            
            StatefulBuffer buffer = new StatefulBuffer(context.transaction(), unblockedLength());
            
            int blockedPosition = context.container().blockConverter().bytesToBlocks(position);
            
            int indexID = masterAddress + blockedPosition;
            
            buffer.setID(indexID);
            buffer.address(indexID);

            transferContentTo(buffer, unblockedLength());

            _indexedField.addIndexEntry(context.transaction(), context.objectID(), buffer);
        }
    }

    private int addressInParent() {
        if(! hasParent()){
            throw new IllegalStateException();
        }
        if(_addressInParent < 0){
            return - _addressInParent;
        }
        return _addressInParent;
    }

    public void debugDecrementLastOffset(int count){
        _lastOffSet -= count;
    }
    
    public boolean hasParent(){
        return _addressInParent != NO_PARENT;
    }
    
    private boolean storeLengthInLink(){
        return _addressInParent > 0;
    }

    public void requestIndexEntry(FieldMetadata fieldMetadata) {
        _indexedField = fieldMetadata;
    }
    
    public MarshallingBuffer checkBlockAlignment(MarshallingContext context, MarshallingBuffer precedingBuffer, IntByRef precedingLength) {
        
        _lastOffSet = offset();
        
        if(doBlockAlign()){
            precedingBuffer.blockAlign(context, precedingLength.value);
        }
        if(precedingBuffer != null){
            precedingLength.value += precedingBuffer.length();
        }
        precedingBuffer = this;
        if(_children != null){
            Iterator4 i = new Iterator4Impl(_children);
            while(i.moveNext()){
                precedingBuffer = ((MarshallingBuffer) i.current()).checkBlockAlignment(context, precedingBuffer, precedingLength);
            }
        }
        return precedingBuffer;
    }

    private void blockAlign(MarshallingContext context, int precedingLength) {
        int totalLength = context.container().blockConverter().blockAlignedBytes(precedingLength + length());
        int newLength = totalLength - precedingLength;
        blockAlign(newLength);
    }

    public int marshalledLength() {
        int length = length();
        if(_children != null){
            Iterator4 i = new Iterator4Impl(_children);
            while(i.moveNext()){
                length += ((MarshallingBuffer) i.current()).marshalledLength();
            }
        }
        return length;
    }

    private void blockAlign(int length) {
    	if(_delegate == null){
    		return;
    	}
        if(length > _delegate.length()){
            int sizeNeeded = length - _delegate.offset();
            prepareWrite(sizeNeeded);
        }
        _delegate.seek(length);
    }

    private boolean doBlockAlign() {
        return hasParent();  // For now we block align every linked entry. Indexes could be created late.
    }
    
    private int blockedLength(){
        return length();
    }
    
    private int unblockedLength(){
        // This is only valid after checkBlockAlignMent has been called. 
        return _lastOffSet;
    }

}
