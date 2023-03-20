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
package com.db4o.test.nativequery.mocks;

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;

public class MockFieldRef implements FieldRef {

	private final String _name;
	private final TypeRef _type;

	public MockFieldRef(String name) {
		this(name, new MockTypeRef(Object.class));
	}

	public MockFieldRef(String name, TypeRef typeRef) {
		if (null == name) throw new ArgumentNullException();
		if (null == typeRef) throw new ArgumentNullException();
		_name = name;
		_type = typeRef;
	}

	public String name() {
		return _name;
	}

	public TypeRef type() {
		return _type;
	}
	
	public String toString() {
		return name();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof FieldRef)) {
			return false;
		}
		FieldRef other = (FieldRef)obj;
		return _name.equals(other.name())
			&& _type.equals(other.type());
	}
	
	public int hashCode() {
		return _name.hashCode() + 29*_type.hashCode();
	}
}
