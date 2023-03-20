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
package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.api.*;

public class BloatMethodRef extends BloatMemberRef implements MethodRef {

	private TypeRef[] _paramTypes;

	BloatMethodRef(BloatReferenceProvider provider, MemberRef method) {
		super(provider, method);
	}
	
	public TypeRef declaringType() {
		return typeRef(_member.declaringClass());
	}

	public TypeRef returnType() {
		return typeRef(_member.type().returnType());
	}

	public TypeRef[] paramTypes() {
		if (null == _paramTypes) {
			_paramTypes = buildParamTypes();
		}
		return _paramTypes;
	}

	private TypeRef[] buildParamTypes() {
		Type[] paramTypes = _member.type().paramTypes();
		TypeRef[] types = new TypeRef[paramTypes.length];
		for (int i=0; i<paramTypes.length; ++i) {
			types[i] = typeRef(paramTypes[i]);
		}
		return types;
	}
}
