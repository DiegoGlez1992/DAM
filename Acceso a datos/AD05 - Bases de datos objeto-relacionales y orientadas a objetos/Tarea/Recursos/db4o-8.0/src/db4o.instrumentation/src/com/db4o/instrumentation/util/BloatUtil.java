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
package com.db4o.instrumentation.util;

import java.io.*;

import com.db4o.instrumentation.core.*;

import EDU.purdue.cs.bloat.editor.*;

/**
 * @exclude
 */
public class BloatUtil {

	public static String normalizeClassName(Type type) {
		return normalizeClassName(type.className());
	}

	public static String normalizeClassName(String className) {
		return className.replace('/', '.');
	}

	public static Class classForEditor(ClassEditor ce, ClassLoader loader) throws ClassNotFoundException {
		String clazzName = normalizeClassName(ce.name());
		return loader.loadClass(clazzName);
	}

	public static boolean isPlatformClassName(String name) {
		return name.startsWith("java.") || name.startsWith("javax.")
				|| name.startsWith("sun.");
	}

	public static String classNameForPath(String classPath) {
		String className = classPath.substring(0, classPath.length()-".class".length());
		return className.replace(File.separatorChar,'.');
	}

	public static String classPathForName(String className) {
		String classPath = className.replace('.', '/');
		return classPath + ".class";
	}

	private BloatUtil() {
	}

	public static LoadStoreInstructions loadStoreInstructionsFor(Type type) {
		if (type.isPrimitive()) {
			switch (type.typeCode()) {
			case Type.DOUBLE_CODE:
				return new LoadStoreInstructions(Opcode.opc_dload, Opcode.opc_dstore);
			case Type.FLOAT_CODE:
				return new LoadStoreInstructions(Opcode.opc_fload, Opcode.opc_fstore);
			case Type.LONG_CODE:
				return new LoadStoreInstructions(Opcode.opc_lload, Opcode.opc_lstore);
			default:
				return new LoadStoreInstructions(Opcode.opc_iload, Opcode.opc_istore);
			}
		}
		return new LoadStoreInstructions(Opcode.opc_aload, Opcode.opc_astore);
	}

	public static boolean implementsInHierarchy(ClassEditor ce, Class markerInterface, BloatLoaderContext context) throws ClassNotFoundException {
		while(ce != null) {
			if(implementsDirectly(ce, markerInterface)) {
				return true;
			}
			ce = context.classEditor(ce.superclass());
		}
		return false;
	}

	public static boolean implementsDirectly(ClassEditor ce, Class markerInterface) {
		if(markerInterface.getName().equals(normalizeClassName(ce.type()))) {
			return true;
		}
		Type[] interfaces = ce.interfaces();
		for (int idx = 0; idx < interfaces.length; idx++) {
			Type type = interfaces[idx];
			if(normalizeClassName(type).equals(markerInterface.getName())) {
				return true;
			}
		}
		return false;
	}

}
