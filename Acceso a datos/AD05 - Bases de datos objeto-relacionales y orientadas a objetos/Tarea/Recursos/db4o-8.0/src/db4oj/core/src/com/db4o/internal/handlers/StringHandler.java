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
package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class StringHandler implements ValueTypeHandler, IndexableTypeHandler, BuiltinTypeHandler, VariableLengthTypeHandler, QueryableTypeHandler {
    
    private ReflectClass _classReflector;
    
    public ReflectClass classReflector(){
    	return _classReflector;
    }
    
    public void delete(DeleteContext context){
        // do nothing, we are in a slot indirection anyway, the 
    	// buffer position does not need to be changed.
    }
    
    byte getIdentifier() {
        return Const4.YAPSTRING;
    }   
    
    public boolean descendsIntoMembers() {
    	return false;
    }

    public final Object indexEntryToObject(Context context, Object indexEntry){
        if(indexEntry instanceof Slot){
            Slot slot = (Slot)indexEntry;
            indexEntry = context.transaction().container().decryptedBufferByAddress(slot.address(), slot.length());
        }
        return readStringNoDebug(context, (ReadBuffer)indexEntry);
    }
    
    /**
     * This readIndexEntry method reads from the parent slot.
     */
    public Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer buffer) throws CorruptionException, Db4oIOException {
        int payLoadOffSet = buffer.readInt();
        int length = buffer.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return buffer.readPayloadWriter(payLoadOffSet, length);
    }
    
    public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException{
        int payLoadOffSet = context.readInt();
        int length = context.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return ((StatefulBuffer)context.buffer()).readPayloadWriter(payLoadOffSet, length);
    }

    /**
     * This readIndexEntry method reads from the actual index in the file.
     */
    public Object readIndexEntry(Context context, ByteArrayBuffer reader) {
    	Slot s = new Slot(reader.readInt(), reader.readInt());
    	if (isInvalidSlot(s)){
    		return null;
    	}
    	return s; 
    }

	private boolean isInvalidSlot(Slot slot) {
		return slot.isNull();
	}
    
    public void writeIndexEntry(Context context, ByteArrayBuffer writer, Object entry) {
        if(entry == null){
            writer.writeInt(0);
            writer.writeInt(0);
            return;
        }
         if(entry instanceof StatefulBuffer){
             StatefulBuffer entryAsWriter = (StatefulBuffer)entry;
             writer.writeInt(entryAsWriter.getAddress());
             writer.writeInt(entryAsWriter.length());
             return;
         }
         if(entry instanceof Slot){
             Slot s = (Slot) entry;
             writer.writeInt(s.address());
             writer.writeInt(s.length());
             return;
         }
         throw new IllegalArgumentException();
    }
    
	public final void writeShort(Transaction trans, String str, ByteArrayBuffer buffer) {
		stringIo(trans.container()).writeLengthAndString(buffer, str);
	}

    ByteArrayBuffer val(Object obj, Context context) {
        if(obj instanceof ByteArrayBuffer) {
            return (ByteArrayBuffer)obj;
        }
        
        ObjectContainerBase oc = context.transaction().container();
        
        if(obj instanceof String) {
            return writeToBuffer((InternalObjectContainer) oc, (String)obj);
        }
        if (obj instanceof Slot) {
			Slot s = (Slot) obj;
			return oc.decryptedBufferByAddress(s.address(), s.length());
		}
        
		return null;
    }

    /** 
     * returns: -x for left is greater and +x for right is greater
     * 
     * FIXME: The returned value is the wrong way around.
     *
     * TODO: You will need collators here for different languages.  
     */
    final int compare(ByteArrayBuffer a_compare, ByteArrayBuffer a_with) {
        if (a_compare == null) {
            if (a_with == null) {
                return 0;
            }
            return -1;
        }
        if (a_with == null) {
            return 1;
        }
        return compare(a_compare._buffer, a_with._buffer);
    }
    
    public static final int compare(byte[] compare, byte[] with){
        int min = compare.length < with.length ? compare.length : with.length;
        int start = Const4.INT_LENGTH;
        if(Deploy.debug) {
            start += Const4.LEADING_LENGTH;
            min -= Const4.BRACKETS_BYTES;
        }
        for(int i = start;i < min;i++) {
            if (compare[i] != with[i]) {
                return compare[i] - with[i];
            }
        }
        return compare.length - with.length;
    }

	public void defragIndexEntry(DefragmentContextImpl context) {
		context.copyAddress();
		// length
		context.incrementIntSize();
	}
	
    public void write(WriteContext context, Object obj) {
        internalWrite((InternalObjectContainer) context.objectContainer(), context, (String) obj);
    }
    
    protected static void internalWrite(InternalObjectContainer objectContainer, WriteBuffer buffer, String str){
        if (Deploy.debug) {
            Debug4.writeBegin(buffer, Const4.YAPSTRING);
        }
        stringIo(objectContainer).writeLengthAndString(buffer, str);
        if (Deploy.debug) {
            Debug4.writeEnd(buffer);
        }
    }
    
    public static ByteArrayBuffer writeToBuffer(InternalObjectContainer container, String str){
        ByteArrayBuffer buffer = new ByteArrayBuffer(stringIo(container).length(str));
        internalWrite(container, buffer, str);
        return buffer;
    }
    
	protected static LatinStringIO stringIo(Context context) {
	    return stringIo((InternalObjectContainer) context.objectContainer());
	}
	
	protected static LatinStringIO stringIo(InternalObjectContainer objectContainer){
	    return objectContainer.container().stringIO();
	}

    public static String readString(Context context, ReadBuffer buffer) {
        if (Deploy.debug) {
            Debug4.readBegin(buffer, Const4.YAPSTRING);
        }
        String str = readStringNoDebug(context, buffer);
        if (Deploy.debug) {
            Debug4.readEnd(buffer);
        }
        return str;
    }
    
    public static String readStringNoDebug(Context context, ReadBuffer buffer) {
    	return intern(context, stringIo(context).readLengthAndString(buffer));
    }
    
    protected static String intern(Context context, String str){
        if(context.objectContainer().ext().configure().internStrings()){
            return str.intern();
        }
        return str;
    }
    
    public Object read(ReadContext context) {
        return readString(context, context);
    }
    
    public void defragment(DefragmentContext context) {
    	context.incrementOffset(linkLength());
    }
    
	public PreparedComparison prepareComparison(final Context context, final Object obj) {
	    final ByteArrayBuffer sourceBuffer = val(obj, context);
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				ByteArrayBuffer targetBuffer = val(target, context);
				return compare(sourceBuffer, targetBuffer);
			}
		};

	}

    public int linkLength() {
        return Const4.INDIRECTION_LENGTH;
    }

	public void registerReflector(Reflector reflector) {
        _classReflector = reflector.forClass(String.class);
	}

}
