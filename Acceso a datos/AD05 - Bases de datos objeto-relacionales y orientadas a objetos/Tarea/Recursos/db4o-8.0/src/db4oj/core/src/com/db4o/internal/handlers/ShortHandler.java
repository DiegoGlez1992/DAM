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
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

public class ShortHandler extends PrimitiveHandler {
	
    static final int LENGTH = Const4.SHORT_BYTES + Const4.ADDED_LENGTH;
	
	private static final Short DEFAULTVALUE = new Short((short)0);
	
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toShort(obj);
    }
    public Object defaultValue(){
		return DEFAULTVALUE;
	}
	
	public int linkLength(){
		return LENGTH;
	}
	
	public Class primitiveJavaClass(){
		return short.class;
	}
	
	public Object read(MarshallerFamily mf, StatefulBuffer buffer,
			boolean redirect) throws CorruptionException {

		return mf._primitive.readShort(buffer);
	}
	
	Object read1(ByteArrayBuffer buffer){
		return primitiveMarshaller().readShort(buffer);
	}

	public void write(Object a_object, ByteArrayBuffer a_bytes){
	    writeShort(((Short)a_object).shortValue(), a_bytes);
	}
	
	static final void writeShort(int a_short, ByteArrayBuffer a_bytes){
		if(Deploy.debug){
			a_bytes.writeBegin(Const4.YAPSHORT);
		}
		for (int i = 0; i < Const4.SHORT_BYTES; i++){
			a_bytes._buffer[a_bytes._offset++] = (byte) (a_short >> ((Const4.SHORT_BYTES - 1 - i) * 8));
		}
		if(Deploy.debug){
			a_bytes.writeEnd();
		}
	}
	
	@Override
    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug4.readBegin(context, Const4.YAPSHORT);
        }
        int value = ((context.readByte() & 0xff) << 8) + (context.readByte() & 0xff);
        if (Deploy.debug) {
            Debug4.readEnd(context);
        }
        return new Short((short) value);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug4.writeBegin(context, Const4.YAPSHORT);
        }
        int shortValue = ((Short)obj).shortValue();
        context.writeBytes(new byte [] {
            (byte) (shortValue >> 8),
            (byte) shortValue
        });
        if (Deploy.debug) {
            Debug4.writeEnd(context);
        }
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final short sourceShort = ((Short)source).shortValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				short targetShort = ((Short)target).shortValue();
				return sourceShort == targetShort ? 0 : (sourceShort < targetShort ? - 1 : 1); 
			}
		};
    }
	
}
