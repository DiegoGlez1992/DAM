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
import com.db4o.marshall.*;



public final class CharHandler extends PrimitiveHandler {

    static final int LENGTH = Const4.CHAR_BYTES + Const4.ADDED_LENGTH;
	
	private static final Character DEFAULTVALUE = new Character((char)0);
	
	public Object defaultValue(){
		return DEFAULTVALUE;
	}
	
	public int linkLength() {
		return LENGTH;
	}

	public Class primitiveJavaClass() {
		return char.class;
	}

	Object read1(ByteArrayBuffer a_bytes) {
		if (Deploy.debug) {
			a_bytes.readBegin(Const4.YAPCHAR);
		}
		byte b1 = a_bytes.readByte();
		byte b2 = a_bytes.readByte();
		if (Deploy.debug) {
			a_bytes.readEnd();
		}
		char ret = (char) ((b1 & 0xff) | ((b2 & 0xff) << 8));
		return new Character(ret);
	}

	public void write(Object a_object, ByteArrayBuffer a_bytes) {
		if (Deploy.debug) {
			a_bytes.writeBegin(Const4.YAPCHAR);
		}
		char char_ = ((Character) a_object).charValue();
		a_bytes.writeByte((byte) (char_ & 0xff));
		a_bytes.writeByte((byte) (char_ >> 8));
		if (Deploy.debug) {
			a_bytes.writeEnd();
		}
	}

    public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug4.readBegin(context, Const4.YAPCHAR);
        }
        
        byte b1 = context.readByte();
        byte b2 = context.readByte();
        char charValue = (char) ((b1 & 0xff) | ((b2 & 0xff) << 8));
        
        if (Deploy.debug) {
            Debug4.readEnd(context);
        }
        
        return new Character(charValue);
    }

    public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug4.writeBegin(context, Const4.YAPCHAR);
        }
        
        char charValue = ((Character) obj).charValue();
        
        context.writeBytes(new byte[]{
            (byte)(charValue & 0xff),
            (byte)(charValue >> 8)
        });
        
        if (Deploy.debug) {
            Debug4.writeEnd(context);
        }
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final char sourceChar = ((Character)source).charValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				char targetChar = ((Character)target).charValue();
				return sourceChar == targetChar ? 0 : (sourceChar < targetChar ? - 1 : 1); 
			}
		};
    }

}
