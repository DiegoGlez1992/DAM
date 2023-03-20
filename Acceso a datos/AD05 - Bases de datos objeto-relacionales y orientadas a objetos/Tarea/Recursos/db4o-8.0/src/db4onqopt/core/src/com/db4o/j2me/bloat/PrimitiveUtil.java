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
package com.db4o.j2me.bloat;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

public class PrimitiveUtil {
	private final static Map PRIMITIVES;
	private final static Map CONVERTIONFUNKTIONS;

	static {
		PRIMITIVES = new HashMap();
		PRIMITIVES.put(Type.BOOLEAN, Boolean.class);
		PRIMITIVES.put(Type.BYTE, Byte.class);
		PRIMITIVES.put(Type.CHARACTER, Character.class);
		PRIMITIVES.put(Type.SHORT, Short.class);
		PRIMITIVES.put(Type.INTEGER, Integer.class);
		PRIMITIVES.put(Type.LONG, Long.class);
		PRIMITIVES.put(Type.FLOAT, Float.class);
		PRIMITIVES.put(Type.DOUBLE, Double.class);

		CONVERTIONFUNKTIONS = new HashMap();
		CONVERTIONFUNKTIONS.put(Byte.class, "byteValue");
		CONVERTIONFUNKTIONS.put(Short.class, "shortValue");
		CONVERTIONFUNKTIONS.put(Integer.class, "intValue");
		CONVERTIONFUNKTIONS.put(Long.class, "longValue");
		CONVERTIONFUNKTIONS.put(Float.class, "floatValue");
		CONVERTIONFUNKTIONS.put(Double.class, "doubleValue");
	}
	
	public static Class wrapper(Type primitive) {
		return (Class)PRIMITIVES.get(primitive);
	}

	public static String conversionFunctionName(Class clazz) {
		return (String)CONVERTIONFUNKTIONS.get(clazz);
	}
}
