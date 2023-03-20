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
package com.db4o.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.util.*;
import com.db4o.nativequery.expr.cmp.operand.*;

public class TypeRefUtil {

	private final static String[] PRIMITIVE_WRAPPER_NAMES = {
		Boolean.class.getName(), Byte.class.getName(),
		Short.class.getName(), Character.class.getName(),
		Integer.class.getName(), Long.class.getName(),
		Double.class.getName(), Float.class.getName(),
		String.class.getName(), Date.class.getName() };

	static {
		Arrays.sort(PRIMITIVE_WRAPPER_NAMES);
	}

	public static boolean isPrimitiveWrapper(Type type) {
		return Arrays.binarySearch(PRIMITIVE_WRAPPER_NAMES,
				BloatUtil.normalizeClassName(type)) >= 0;
	}

	public static boolean isPrimitiveWrapper(TypeRef type) {
		return Arrays.binarySearch(PRIMITIVE_WRAPPER_NAMES,
				BloatUtil.normalizeClassName(type.name())) >= 0;
	}

	public static boolean isPrimitiveBoolean(TypeRef fieldType) {
		return isType(fieldType, Boolean.TYPE);
	}

	public static boolean isBooleanField(FieldValue fieldVal) {
		final TypeRef type = fieldVal.field().type();
		return isPrimitiveBoolean(type)
			|| isType(type, Boolean.class);
	}

	private static boolean isType(TypeRef fieldType, final Class type) {
		return fieldType.name().equals(type.getName());
	}
	
}
