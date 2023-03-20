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


/**
 * @exclude
 */
public class AspectType {
    
    public final byte _id;
    
    public static final AspectType FIELD = new AspectType((byte)1);
    public static final AspectType TRANSLATOR = new AspectType((byte)2);
    public static final AspectType TYPEHANDLER = new AspectType((byte)3);
    
    
    private AspectType(byte id) {
        _id = id;
    }
    
    public static AspectType forByte(byte b){
        switch (b){
            case 1:
                return FIELD;
            case 2:
                return TRANSLATOR;
            case 3:
                return TYPEHANDLER;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public boolean isFieldMetadata() {
        return isField() || isTranslator();
    }

	public boolean isTranslator() {
		return this == AspectType.TRANSLATOR;
	}

	public boolean isField() {
		return this == AspectType.FIELD;
	}

}
