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
package com.db4o.internal;


/**
 * @sharpen.ignore
 */
public class ReflectPlatform {
	
	/**
	 * @param className
	 * @return the Class object for specified className. Returns null if an
	 *         error is encountered during loading the class.
	 */
	public static Class forName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			// e.printStackTrace();
		} catch (LinkageError e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static Object createInstance(String className) {
		Class clazz = forName(className);
		return createInstance(clazz);
	}

	public static Object createInstance(Class clazz) {
		if (clazz == null) {
			return null;
		}
		try {
			return clazz.newInstance();
		} catch (Throwable t) {
//			throw new RuntimeException(t);
		}
		return null;
	}

	public static String fullyQualifiedName(Class clazz) {
		return clazz.getName();
	}

	public static boolean isNamedClass(Class clazz) {
		return !clazz.isPrimitive();
	}

	@decaf.ReplaceFirst("return simpleNameForJdksPriorTo5(clazz);")
	public static String simpleName(Class clazz) {
		return clazz.getSimpleName();
    }
	
	@SuppressWarnings("unused")
    private static String simpleNameForJdksPriorTo5(Class clazz) {
		final String name = clazz.getName();
		final int lastDot = name.lastIndexOf('.');
		return lastDot < 0
			? name
			: name.substring(lastDot + 1);
	}
}
