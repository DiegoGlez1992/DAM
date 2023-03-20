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
package com.db4o.reflect.self;

public class FieldInfo {
	private String _name;
	private Class _clazz;
	private boolean _isPublic; 
	private boolean _isStatic; 
	private boolean _isTransient;

	
	
	public FieldInfo(String name, Class clazz, boolean isPublic, boolean isStatic, boolean isTransient) {
		_name = name;
		_clazz = clazz;
		_isPublic = isPublic;
		_isStatic = isStatic;
		_isTransient = isTransient;
	}

	public String name() {
		return _name;
	}
	
	public Class type() {
		return _clazz;
	}

	public boolean isPublic() {
		return _isPublic;
	}

	public boolean isStatic() {
		return _isStatic;
	}

	public boolean isTransient() {
		return _isTransient;
	}
}
