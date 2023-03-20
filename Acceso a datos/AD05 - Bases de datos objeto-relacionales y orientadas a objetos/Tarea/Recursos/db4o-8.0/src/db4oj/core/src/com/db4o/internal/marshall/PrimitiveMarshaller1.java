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

import java.util.*;

import com.db4o.internal.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public Date readDate(ByteArrayBuffer bytes){
		return new Date(bytes.readLong());
	}
    
    public Object readInteger(ByteArrayBuffer bytes) {
    	return new Integer(bytes.readInt());
    }
    
    public Object readFloat(ByteArrayBuffer bytes) {
    	return PrimitiveMarshaller0.unmarshallFloat(bytes);
    }

	public Object readDouble(ByteArrayBuffer buffer) {
		return PrimitiveMarshaller0.unmarshalDouble(buffer);
	}

	public Object readLong(ByteArrayBuffer buffer) {
		return new Long(buffer.readLong());
	}

	public Object readShort(ByteArrayBuffer buffer) {
		return new Short(PrimitiveMarshaller0.unmarshallShort(buffer));
	}

}
