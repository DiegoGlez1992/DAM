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

import com.db4o.foundation.*;

public class RawFieldSpec {
    private final AspectType _type;
	private final String _name;
	private final int _fieldTypeID;
	private final boolean _isPrimitive;
	private final boolean _isArray;
	private final boolean _isNArray;
	private final boolean _isVirtual;
	private int _indexID;

	public RawFieldSpec(AspectType aspectType, final String name, final int fieldTypeID, final byte attribs) {
        _type = aspectType;
        _name = name;
		_fieldTypeID = fieldTypeID;
		BitMap4 bitmap = new BitMap4(attribs);
        _isPrimitive = bitmap.isTrue(0);
        _isArray = bitmap.isTrue(1);
        _isNArray = bitmap.isTrue(2);
        _isVirtual=false;
        _indexID=0;
	}

	public RawFieldSpec(AspectType aspectType, final String name) {
	    _type = aspectType;
		_name = name;
		_fieldTypeID = 0;
        _isPrimitive = false;
        _isArray = false;
        _isNArray = false;
        _isVirtual=true;
        _indexID=0;
	}

	public String name() {
		return _name;
	}
	
	public int fieldTypeID() {
		return _fieldTypeID;
	}
	
	public boolean isPrimitive() {
		return _isPrimitive;
	}

	public boolean isArray() {
		return _isArray;
	}

	public boolean isNArray() {
		return _isNArray;
	}
	
	public boolean isVirtual() {
		return _isVirtual;
	}
	
	public boolean isVirtualField() {
		return isVirtual() && isField();
	}
	
	public boolean isField() {
		return _type.isField();
	}

	public int indexID() {
		return _indexID;
	}
	
	void indexID(int indexID) {
		_indexID=indexID;
	}
	
	public String toString() {
		return "RawFieldSpec(" + name() + ")"; 
	}

    public boolean isFieldMetadata() {
        return _type.isFieldMetadata();
    }

	public boolean isTranslator() {
		return _type.isTranslator();
	}
}
