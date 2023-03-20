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
package com.db4o.test;

import java.lang.reflect.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class Compare {

	static boolean hasPublicConstructor(Class clazz) {
		if (clazz == null || clazz == String.class) {
			return false;
		}
		try {
			Object o = clazz.newInstance();
			if (o != null)
				return true;
		} catch (Throwable t) {
		}
		return false;
	}
	
	static boolean isPrimitiveOrPrimitiveWrapper(Class clazz){
		return clazz.isPrimitive() || Platform4.isSimple(clazz); 
	}

	static Object normalizeNArray(Object obj) {
		if (Array.getLength(obj) > 0) {
			Object first = Array.get(obj, 0);
			if (first != null && first.getClass().isArray()) {
				int dim[] = arrayDimensions(obj);
				Object all = new Object[arrayElementCount(dim)];
				normalizeNArray1(obj, all, 0, dim, 0);
				return all;
			}
		}
		return obj;
	}

	public static void compare(
		com.db4o.ObjectContainer a_con,
		Object a_Compare,
		Object a_With,
		String a_path,
		Collection4 a_list) {
		if (!Regression.DEACTIVATE) {
			a_con.activate(a_With, 1);
		}
		
		

		if (a_list == null) {
			a_list = new Collection4();
		}
		// takes care of repeating calls to the same object
		if (a_list.containsByIdentity(a_Compare)) {
			return;
		}

		a_list.add(a_Compare);

		if (a_path == null || a_path.length() < 1)
			if (a_Compare != null) {
				a_path = a_Compare.getClass().getName() + ":";
			} else {
				if (a_With != null)
					a_path = a_With.getClass().getName() + ":";
			}
		String path = a_path;
		if (a_Compare == null){
			if (a_With == null) {
				return;
			} 
			Regression.addError("1==null:" + path);
			return;
		}
		if (a_With == null) {
			Regression.addError("2==null:" + path);
			return;
		}
		Class compareClass = a_Compare.getClass();
		if (!compareClass.isInstance(a_With)) {
			Regression.addError(
				"class!=:" + path + compareClass.getName() + ":" + a_With.getClass().getName());
			return;
		}
		if(isPrimitiveOrPrimitiveWrapper(compareClass)){
			if(a_Compare.equals(a_With)){
				return;
			}
			Regression.addError(
				"class!=:" + path + compareClass.getName() + ":" + a_With.getClass().getName());
			return;
		}
		
		Field l_Fields[] = compareClass.getDeclaredFields();
		for (int i = 0; i < l_Fields.length; i++) {
			if (storeableField(compareClass, l_Fields[i])) {
				Platform4.setAccessible(l_Fields[i]);
				try {
					path = a_path + l_Fields[i].getName() + ":";
					Object l_Compare = l_Fields[i].get(a_Compare);
					Object l_With = l_Fields[i].get(a_With);
					if (l_Compare == null) {
						if (l_With != null) {
							Regression.addError("f1==null:" + path);
						}
					} else if (l_With == null)
						Regression.addError("f2==null:" + path);
					else if (l_Compare.getClass().isArray()) {
						if (!l_With.getClass().isArray()) {
							Regression.addError("f2!=array:" + path);
						} else {
							l_Compare = normalizeNArray(l_Compare);
							l_With = normalizeNArray(l_With);
							int l_len = Array.getLength(l_Compare);
							if (l_len != Array.getLength(l_With)) {
								Regression.addError("arraylen!=:" + path);
							} else {
								Class fieldClass = l_Fields[i].getType().getComponentType();
								boolean l_persistentArray =
									hasPublicConstructor(fieldClass) ||
									isPrimitiveOrPrimitiveWrapper(fieldClass);
								for (int j = 0; j < l_len; j++) {
									Object l_ElementCompare = Array.get(l_Compare, j);
									Object l_ElementWith = Array.get(l_With, j);
									if (l_persistentArray){
										compare(
											a_con,
											l_ElementCompare,
											l_ElementWith,
											path,
											a_list);
									} else if (l_ElementCompare == null) {
										if (l_ElementWith != null){
											Regression.addError("1e" + j + "==null:" + path);
										}
									} else if (l_ElementWith == null) {
										Regression.addError("2e" + j + "==null:" + path);
									} else {
										Class elementCompareClass = l_ElementCompare.getClass();
										if (elementCompareClass != l_ElementWith.getClass()){
											Regression.addError(
												"e"
													+ j
													+ "!=class:"
													+ path
													+ elementCompareClass.toString()
													+ ":"
													+ l_ElementWith.getClass().toString());
										}else if (hasPublicConstructor(elementCompareClass)) {
											compare(
												a_con,
												l_ElementCompare,
												l_ElementWith,
												path,
												a_list);
										} else {
											if (!l_ElementCompare.equals(l_ElementWith))
												Regression.addError(
													"e"
														+ j
														+ "!=:"
														+ path
														+ l_ElementCompare.toString()
														+ ":"
														+ l_ElementWith.toString());
										}
									}
								}

							}
						}
					} else if (hasPublicConstructor(l_Fields[i].getType()))
						compare(a_con, l_Compare, l_With, path, a_list);
					else if (!l_Compare.equals(l_With))
						Regression.addError("!=:" + path);
				} catch (Exception e) {
					Regression.addError("Exception:" + path);
				}
			}
		}
	}

	static int[] arrayDimensions(Object a_object) {
		int count = 0;
		for (Class clazz = a_object.getClass(); clazz.isArray(); clazz = clazz.getComponentType())
			count++;

		int dim[] = new int[count];
		for (int i = 0; i < count; i++) {
			dim[i] = Array.getLength(a_object);
			a_object = Array.get(a_object, 0);
		}

		return dim;
	}

	static int normalizeNArray1(
		Object a_object,
		Object a_all,
		int a_next,
		int a_dim[],
		int a_index) {
		if (a_index == a_dim.length - 1) {
			for (int i = 0; i < a_dim[a_index]; i++)
				Array.set(a_all, a_next++, Array.get(a_object, i));

		} else {
			for (int i = 0; i < a_dim[a_index]; i++)
				a_next =
					normalizeNArray1(Array.get(a_object, i), a_all, a_next, a_dim, a_index + 1);

		}
		return a_next;
	}

	static int arrayElementCount(int a_dim[]) {
		int elements = a_dim[0];
		for (int i = 1; i < a_dim.length; i++)
			elements *= a_dim[i];

		return elements;
	}

	static String nl() {
		return System.getProperty("line.separator");
	}
	
    public static boolean storeableField(Class a_class, Field a_field) {
        return (!Modifier.isStatic(a_field.getModifiers()))
            && (!Modifier.isTransient(a_field.getModifiers())
                & !(a_field.getName().indexOf("$") > -1));
    }

}
