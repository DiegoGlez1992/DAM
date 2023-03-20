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
package com.db4o.test.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.internal.*;
import com.db4o.test.legacy.soda.*;

public class TLogger {
	private static int maximumDepth = Integer.MAX_VALUE;
	private static PrintStream out = System.out;
	private static String cr = "";
	private static String sp = " ";
	private static boolean silent = false;

	public static void log(Object a_object) {
		if (a_object == null) {
			log("[NULL]");
		} else {
			log(a_object.getClass().getName());
			log(a_object, 0, new Stack());
		}
	}

	public static void setOut(PrintStream ps) {
		out = ps;
	}

	public static void setMaximumDepth(int depth) {
		maximumDepth = depth;
	}

	public static void setSilent(boolean flag) {
		silent = flag;
	}

	private static void log(Object a_object, int a_depth, Stack a_stack) {
		if (a_object instanceof SodaTest) {
			return;
		}
		if (a_stack.contains(a_object) || a_depth > maximumDepth) {
			return;
		}
		Class clazz = a_object.getClass();
		for (int i = 0; i < ignore.length; i++) {
			if (clazz.isAssignableFrom(ignore[i])) {
				return;
			}
		}

		a_stack.push(a_object);

		Class[] classes = getClassHierarchy(a_object);

		String spaces = "";
		for (int i = classes.length - 1; i >= 0; i--) {
			spaces = spaces + sp;

			String className = spaces;
			int pos = classes[i].getName().lastIndexOf(".");
			if (pos > 0) {
				className += classes[i].getName().substring(pos);
			} else {
				className += classes[i].getName();
			}

			if (classes[i] == Date.class) {
				String fieldName = className + ".getTime";
				Object obj = new Long(((Date) a_object).getTime());
				log(obj, Long.class, fieldName, a_depth + 1, -1, a_stack);

			} else {
				Field[] fields = classes[i].getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
                    
                    Platform4.setAccessible(fields[j]);

					String fieldName = className + "." + fields[j].getName();

					try {
						Object obj = fields[j].get(a_object);

						if (obj.getClass().isArray()) {
							obj = normalizeNArray(obj);

							int len = Array.getLength(obj);
							for (int k = 0; k < len; k++) {
								Object element = Array.get(obj, k);
								Class arrClass = element == null ? null : element.getClass();
								log(element, arrClass, fieldName, a_depth + 1, k, a_stack);
							}
						} else {
							log(obj, fields[j].getType(), fieldName, a_depth + 1, -1, a_stack);
						}
					} catch (Exception e) {

					}
				}
			}
		}
	}

	private static void log(
		Object a_object,
		Class a_Class,
		String a_fieldName,
		int a_depth,
		int a_arrayElement,
		Stack a_stack) {
		if (a_depth > maximumDepth) {
			return;
		}
		String fieldName =
			(a_arrayElement > -1) ? a_fieldName + sp + sp + a_arrayElement : a_fieldName;
		if (a_object != null) {
			log(a_depth, fieldName, "");
			Class clazz = a_object.getClass();
			if (Platform4.isSimple(clazz)) {
				log(a_depth + 1, a_object.getClass().getName(), a_object.toString());
			} else {
				log(a_object, a_depth, a_stack);
			}
		} else {
			log(a_depth, fieldName, "[NULL]");
		}
	}

	private static void log(String a_msg) {
		if (!silent) {
			out.println(a_msg + cr);
		}
	}

	private static void log(int indent, String a_property, String a_value) {
		for (int i = 0; i < indent; i++) {
			a_property = sp + sp + a_property;
		}
		log(a_property, a_value);
	}

	private static void log(String a_property, String a_value) {
		if (a_value == null)
			a_value = "[NULL]";
		log(a_property + ": " + a_value);
	}

//	private static void log(Exception e, Object obj, String msg) {
//		String l_msg;
//		if (e != null) {
//			l_msg = "!!! " + e.getClass().getName();
//			String l_exMsg = e.getMessage();
//			if (l_exMsg != null) {
//				l_msg += sp + l_exMsg;
//			}
//		} else {
//			l_msg = "!!!Exception log";
//		}
//		if (obj != null) {
//			l_msg += " in " + obj.getClass().getName();
//		}
//		if (msg != null) {
//			l_msg += sp + msg;
//		}
//		log(l_msg);
//	}

	private static Class[] getClassHierarchy(Object a_object) {
		Class[] classes = new Class[] { a_object.getClass()};
		return getClassHierarchy(classes);
	}

	private static Class[] getClassHierarchy(Class[] a_classes) {
		Class clazz = a_classes[a_classes.length - 1].getSuperclass();
		if (clazz.equals(Object.class)) {
			return a_classes;
		}
		Class[] classes = new Class[a_classes.length + 1];
		System.arraycopy(a_classes, 0, classes, 0, a_classes.length);
		classes[a_classes.length] = clazz;
		return getClassHierarchy(classes);
	}

	static Object normalizeNArray(Object a_object) {
		if (Array.getLength(a_object) > 0) {
			Object first = Array.get(a_object, 0);
			if (first != null && first.getClass().isArray()) {
				int dim[] = arrayDimensions(a_object);
				Object all = new Object[arrayElementCount(dim)];
				normalizeNArray1(a_object, all, 0, dim, 0);
				return all;
			}
		}
		return a_object;
	}

	static int normalizeNArray1(
		Object a_object,
		Object a_all,
		int a_next,
		int a_dim[],
		int a_index) {
		if (a_index == a_dim.length - 1) {
			for (int i = 0; i < a_dim[a_index]; i++) {
				Array.set(a_all, a_next++, Array.get(a_object, i));
			}
		} else {
			for (int i = 0; i < a_dim[a_index]; i++) {
				a_next =
					normalizeNArray1(Array.get(a_object, i), a_all, a_next, a_dim, a_index + 1);
			}

		}
		return a_next;
	}

	static int[] arrayDimensions(Object a_object) {
		int count = 0;
		for (Class clazz = a_object.getClass();
			clazz.isArray();
			clazz = clazz.getComponentType()) {
			count++;
		}
		int dim[] = new int[count];
		for (int i = 0; i < count; i++) {
			dim[i] = Array.getLength(a_object);
			a_object = Array.get(a_object, 0);
		}
		return dim;
	}

	static int arrayElementCount(int a_dim[]) {
		int elements = a_dim[0];
		for (int i = 1; i < a_dim.length; i++) {
			elements *= a_dim[i];
		}
		return elements;
	}

	private static final Class[] ignore = { Class.class };

}
