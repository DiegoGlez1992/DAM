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

public class MockTypeRef implements TypeRef {

	private final Class _type;

	public MockTypeRef(Class type) {
		_type = type;
	}

	public TypeRef elementType() {
		throw new NotImplementedException();
	}

	public boolean isPrimitive() {
		return _type.isPrimitive();
	}

	public String name() {
		return _type.getName();
	}
	
	public String toString() {
		return name();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof TypeRef)) {
			return false;
		}
		
		TypeRef other = (TypeRef)obj;
		return isPrimitive() == other.isPrimitive()
			&& name().equals(other.name());
	}
	
	public int hashCode() {
		return _type.hashCode();
	}
}
