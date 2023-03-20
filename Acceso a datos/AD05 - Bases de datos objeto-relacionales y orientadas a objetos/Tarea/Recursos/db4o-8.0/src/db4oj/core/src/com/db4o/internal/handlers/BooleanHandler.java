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


/**
 * @exclude
 */
public final class BooleanHandler extends PrimitiveHandler {

    static final int LENGTH = 1 + Const4.ADDED_LENGTH;
	
	private static final byte TRUE = (byte) 'T';
	private static final byte FALSE = (byte) 'F';
	private static final byte NULL = (byte) 'N';
	
	private static final Boolean DEFAULTVALUE = new Boolean(false);
	
	public Object defaultValue(){
		return DEFAULTVALUE;
	}
	
	public int linkLength(){
		return LENGTH;
	}
	
	public Class primitiveJavaClass(){
		return boolean.class;
	}
	
	Object read1(ByteArrayBuffer a_bytes){
		if (Deploy.debug){
			a_bytes.readBegin(Const4.YAPBOOLEAN);
		}
		byte ret = a_bytes.readByte();
		if (Deploy.debug){
			a_bytes.readEnd();
		}
		
		if(ret == TRUE){
			return new Boolean(true);
		}
		if(ret == FALSE){
			return new Boolean(false);
		}
		
		return null;
	}
	
	public void write(Object obj, ByteArrayBuffer buffer){
		if(Deploy.debug){
			buffer.writeBegin(Const4.YAPBOOLEAN);
		}		
		buffer.writeByte(getEncodedByteValue(obj));
		if(Deploy.debug){
			buffer.writeEnd();
		}
	}

	
	private byte getEncodedByteValue(Object obj) {
		if (obj == null) {
			return NULL;
		}
		if (((Boolean)obj).booleanValue()) {
			return TRUE;
		}
		return FALSE;
	}

	public Object read(ReadContext context) {
        if (Deploy.debug) {
            Debug4.readBegin(context, Const4.YAPBOOLEAN);
        }
        
		byte ret = context.readByte();
		
        if (Deploy.debug) {
            Debug4.readEnd(context);
        }
		if(ret == TRUE){
			return new Boolean(true);
		}
		if(ret == FALSE){
			return new Boolean(false);
		}
		return null;
	}
	
	public void write(WriteContext context, Object obj) {
        if (Deploy.debug) {
            Debug4.writeBegin(context, Const4.YAPBOOLEAN);
        }
		context.writeByte(getEncodedByteValue(obj));
        if (Deploy.debug) {
            Debug4.writeEnd(context);
        }
	}
	
    public Object nullRepresentationInUntypedArrays(){
        return null;
    }
    
    public PreparedComparison internalPrepareComparison(Object source) {
    	final boolean sourceBoolean = ((Boolean)source).booleanValue();
    	return new PreparedComparison() {
			public int compareTo(Object target) {
				if(target == null){
					return 1;
				}
				boolean targetBoolean = ((Boolean)target).booleanValue();
				return sourceBoolean == targetBoolean ? 0 : (sourceBoolean ? 1 : -1); 
			}
		};
    }

	
}
