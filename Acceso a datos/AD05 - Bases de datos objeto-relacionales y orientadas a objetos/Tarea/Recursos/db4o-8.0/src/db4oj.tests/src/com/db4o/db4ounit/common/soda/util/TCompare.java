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
package com.db4o.db4ounit.common.soda.util;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.internal.*;

import db4ounit.extensions.*;

public class TCompare {

    public static boolean isEqual(Object a_compare, Object a_with) {
        return isEqual(a_compare, a_with, null, new Vector());
    }

    private static boolean isEqual(Object a_compare, Object a_with, String a_path, Vector a_list) {        

        if (a_compare == null) {
            return a_with == null;
        }
        if (a_with == null) {
            return false;
        }
        Class clazz = a_compare.getClass();
        if (clazz != a_with.getClass()) {
            return false;
        }

        if (Platform4.isSimple(clazz)) {
            return a_compare.equals(a_with);
        }

        // takes care of repeating calls to the same object
        if (a_list.contains(a_compare)) {
            return true;
        }
        a_list.addElement(a_compare);
        
        if (a_compare.getClass().isArray()) {
        	return areArraysEqual(normalizeNArray(a_compare), normalizeNArray(a_with), a_path, a_list);
        }
        
        if (hasPublicConstructor(a_compare.getClass())) {        
        	return areFieldsEqual(a_compare, a_with, a_path, a_list);
        }
        return a_compare.equals(a_with);
    }

	private static boolean areFieldsEqual(final Object a_compare, final Object a_with,
			final String a_path, final Vector a_list) {
		String path = getPath(a_compare, a_with, a_path);
        Field fields[] = a_compare.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
			if (Db4oUnitPlatform.isUserField(field)) {
                Platform4.setAccessible(field);
                try {
                    if (!isFieldEqual(field, a_compare, a_with, path, a_list)) {
                    	return false;
                    }
                } catch (Exception e) {
                    System.err.println("TCompare failure executing path:" + path);
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
	}

	private static boolean isFieldEqual(Field field, final Object a_compare,
			final Object a_with, String path, final Vector a_list) {
		Object compare = getFieldValue(field, a_compare);
		Object with = getFieldValue(field, a_with);
		return isEqual(compare, with, path + field.getName() + ":", a_list);
	}

	private static Object getFieldValue(Field field, final Object obj) {
		try {
			return field.get(obj);
		} catch (IllegalAccessException ex) {
            // probably JDK 1
            // never mind this field
            return null;
		}
	}

	private static boolean areArraysEqual(Object compare, Object with,
			String path, Vector a_list) {
		int len = Array.getLength(compare);
		if (len != Array.getLength(with)) {
		    return false;
		} else {
		    for (int j = 0; j < len; j++) {
		        Object elementCompare = Array.get(compare, j);
		        Object elementWith = Array.get(with, j);
		        if (!isEqual(elementCompare, elementWith, path, a_list)) {
		            return false;
		        }
		    }
		}
		return true;
	}

	private static String getPath(Object a_compare, Object a_with, String a_path) {
		if (a_path != null && a_path.length() > 0) {
			return a_path;
		}
        if (a_compare != null) {
            return a_compare.getClass().getName() + ":";
        }
        if (a_with != null) {
        	return a_with.getClass().getName() + ":";
        }
		return a_path;
	}

    static boolean hasPublicConstructor(Class a_class) {
        if (a_class != String.class) {
            try {
                return a_class.newInstance() != null;
            } catch (Throwable t) {
            }
        }
        return false;
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

    static int normalizeNArray1(Object a_object, Object a_all, int a_next, int a_dim[], int a_index) {
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

    private TCompare() {}
}
