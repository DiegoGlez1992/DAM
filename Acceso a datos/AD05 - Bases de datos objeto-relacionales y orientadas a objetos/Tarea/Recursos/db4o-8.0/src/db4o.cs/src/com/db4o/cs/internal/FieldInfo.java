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
package com.db4o.cs.internal;

public class FieldInfo {

	public String _fieldName;

	public ClassInfo _fieldClass;

	public boolean _isPrimitive;

	public boolean _isArray;

	public boolean _isNArray;

	public FieldInfo() {
	}

	public FieldInfo(String fieldName, ClassInfo fieldClass,
			boolean isPrimitive, boolean isArray, boolean isNArray) {
		_fieldName = fieldName;
		_fieldClass = fieldClass;
		_isPrimitive = isPrimitive;
		_isArray = isArray;
		_isNArray = isNArray;
	}

	public ClassInfo getFieldClass() {
		return _fieldClass;
	}

	public String getFieldName() {
		return _fieldName;
	}
}
