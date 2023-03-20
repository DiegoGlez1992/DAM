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

public class ClassInfo {
	
	public static ClassInfo newSystemClass(String className) {
		return new ClassInfo(className, true);
	}
	
	public static ClassInfo newUserClass(String className) {
		return new ClassInfo(className, false);
	}
	
	public String _className;

	public boolean _isSystemClass;

	public ClassInfo _superClass;

	public FieldInfo[] _fields;
	
	public ClassInfo() {
	}
	
	private ClassInfo(String className, boolean systemClass) {
		_className = className;
		_isSystemClass = systemClass;
	}

	public FieldInfo[] getFields() {
		return _fields;
	}

	public void setFields(FieldInfo[] fields) {
		this._fields = fields;
	}

	public ClassInfo getSuperClass() {
		return _superClass;
	}

	public void setSuperClass(ClassInfo superClass) {
		this._superClass = superClass;
	}

	public String getClassName() {
		return _className;
	}

	public boolean isSystemClass() {
		return _isSystemClass;
	}
}
