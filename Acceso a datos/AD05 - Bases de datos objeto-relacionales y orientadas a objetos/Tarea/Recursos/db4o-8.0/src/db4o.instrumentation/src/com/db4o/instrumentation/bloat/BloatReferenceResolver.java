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

import java.lang.reflect.*;

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;

public class BloatReferenceResolver implements ReferenceResolver {
	
	private final NativeClassFactory _loader;

	public BloatReferenceResolver(NativeClassFactory loader) {
		if (null == loader) throw new ArgumentNullException();
		_loader = loader;
	}

	public Method resolve(MethodRef methodRef) {
		final Class declaringClass = resolve(methodRef.declaringType());
		final Class[] paramTypes = resolve(methodRef.paramTypes());
		try {
			return declaringClass.getDeclaredMethod(methodRef.name(), paramTypes);
		} catch (Exception e) {
			throw new InstrumentationException(e);
		}
	}

	private Class[] resolve(TypeRef[] paramTypes) {
		Class[] classes = new Class[paramTypes.length];
		for (int i=0; i<paramTypes.length; ++i) {
			classes[i] = resolve(paramTypes[i]);
		}
		return classes;
	}

	private Class resolve(TypeRef typeRef) {
		try {
			return _loader.forName(typeRef.name());
		} catch (ClassNotFoundException e) {
			throw new InstrumentationException(e);
		}
	}

}
