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


import java.lang.reflect.*;
import java.util.*;

import com.db4o.internal.*;

public class TCompare {

    public boolean isEqual(Object a_compare, Object a_with) {
        return isEqual(a_compare, a_with, null, null);
    }

    public boolean isEqual(Object a_compare, Object a_with, String a_path, Stack a_stack) {
        if (a_path == null || a_path.length() < 1) {
            if (a_compare != null) {
                a_path = a_compare.getClass().getName() + ":";
            } else {
                if (a_with != null) {
                    a_path = a_with.getClass().getName() + ":";
                }
            }
        }

        String path = a_path;

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

        if (a_stack == null) {
            a_stack = new Stack();
        }

        // takes care of repeating calls to the same object
        if (a_stack.contains(a_compare)) {
            return true;
        }
        a_stack.push(a_compare);

        Field fields[] = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (storeableField(clazz, fields[i])) {
                Platform4.setAccessible(fields[i]);
                try {
                    path = a_path + fields[i].getName() + ":";
                    Object compare = fields[i].get(a_compare);
                    Object with = fields[i].get(a_with);
                    if (compare == null) {
                        if (with != null) {
                            return false;
                        }
                    } else if (with == null) {
                        return false;
                    } else {
                        if (compare.getClass().isArray()) {
                            if (!with.getClass().isArray()) {
                                return false;
                            } else {
                                compare = normalizeNArray(compare);
                                with = normalizeNArray(with);
                                int len = Array.getLength(compare);
                                if (len != Array.getLength(with)) {
                                    return false;
                                } else {
                                    for (int j = 0; j < len; j++) {
                                        Object elementCompare = Array.get(compare, j);
                                        Object elementWith = Array.get(with, j);
                                        //										if (l_persistentArray)
                                        if (!isEqual(elementCompare, elementWith, path, a_stack)) {
                                            return false;
                                        } else if (elementCompare == null) {
                                            if (elementWith != null) {
                                                return false;
                                            }
                                        } else if (elementWith == null) {
                                            return false;
                                        } else {
                                            Class elementCompareClass = elementCompare.getClass();
                                            if (elementCompareClass != elementWith.getClass()) {
                                                return false;
                                            }
                                            if (hasPublicConstructor(elementCompareClass)) {
                                                if (!isEqual(elementCompare,
                                                    elementWith,
                                                    path,
                                                    a_stack)) {
                                                    return false;

                                                }
                                            } else if (!elementCompare.equals(elementWith)) {
                                                return false;
                                            }
                                        }

                                    }

                                }
                            }
                        } else if (hasPublicConstructor(fields[i].getType())) {
                            if (!isEqual(compare, with, path, a_stack)) {
                                return false;
                            }
                        } else {
                            if (!compare.equals(with)) {
                                return false;
                            }
                        }
                    }
                } catch (IllegalAccessException ex) {
                    // probably JDK 1
                    // never mind this field
                    return true;
                } catch (Exception e) {
                    System.err.println("TCompare failure executing path:" + path);
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    boolean hasPublicConstructor(Class a_class) {
        if (a_class != String.class) {
            try {
                return a_class.newInstance() != null;
            } catch (Throwable t) {
            }
        }
        return false;
    }

    Object normalizeNArray(Object a_object) {
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

    int normalizeNArray1(Object a_object, Object a_all, int a_next, int a_dim[], int a_index) {
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

    int[] arrayDimensions(Object a_object) {
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

    int arrayElementCount(int a_dim[]) {
        int elements = a_dim[0];
        for (int i = 1; i < a_dim.length; i++) {
            elements *= a_dim[i];
        }
        return elements;
    }

    public boolean storeableField(Class a_class, Field a_field) {
        return (!Modifier.isStatic(a_field.getModifiers()))
            && (!Modifier.isTransient(a_field.getModifiers())
                & !(a_field.getName().indexOf("$") > -1));
    }


}
