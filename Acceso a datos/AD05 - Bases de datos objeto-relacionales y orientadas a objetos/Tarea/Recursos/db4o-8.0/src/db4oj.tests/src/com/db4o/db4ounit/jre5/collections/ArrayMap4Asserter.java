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
package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class ArrayMap4Asserter {

    public static int DATA_LENGTH = 10;
    
    private static int MULTIPLE = 100; 
    
    public static void putData(Map<String, Integer> map) {
        for (int i = 0; i < DATA_LENGTH; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * MULTIPLE));
        }
    }

    public static void assertInitalStatus(Map<String, Integer> map) {
        Assert.isNotNull(map);
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    public static void assertClear(Map<String, Integer> map) {
        Assert.areEqual(10, map.size());
        Assert.isFalse(map.isEmpty());

        map.clear();

        checkClear(map);
    }

    public static void checkClear(Map<String, Integer> map) {
        Assert.areEqual(0, map.size());
        Assert.isTrue(map.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public static void assertClone(ArrayMap4<String, Integer> map) {
        Assert.areEqual(DATA_LENGTH, map.size());
        Assert.isFalse(map.isEmpty());

        ArrayMap4<String, Integer> clone = (ArrayMap4<String, Integer>) map
                .clone();

        Assert.areEqual(DATA_LENGTH, clone.size());

        for (int i = 0; i < DATA_LENGTH; i++) {
            Assert.areEqual(Integer.valueOf(i * MULTIPLE), clone.get(String
                    .valueOf(i)));
        }
    }

    public static void assertContainsKey(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < DATA_LENGTH; i++) {
            Assert.isTrue(map.containsKey(String.valueOf(i)));
        }

        Assert.isFalse(map.containsKey(String.valueOf(DATA_LENGTH)));
    }

    public static void assertContainsValue(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < DATA_LENGTH; i++) {
            Assert.isTrue(map.containsValue(Integer.valueOf(i * MULTIPLE)));
        }

        Assert.isFalse(map.containsValue(Integer.valueOf(DATA_LENGTH)));
    }

    public static void assertEntrySet(ArrayMap4<String, Integer> map) {
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        Assert.areEqual(DATA_LENGTH, set.size());

        for (int i = 0; i < DATA_LENGTH; i++) {
            MapEntry4<String, Integer> entry = new MapEntry4<String, Integer>(
                    String.valueOf(i), Integer.valueOf(i * MULTIPLE));
            Assert.isTrue(set.contains(entry));
        }
    }

    public static void assertGet(ArrayMap4<String, Integer> map) {
        for (int i = 0; i < DATA_LENGTH; i++) {
            Integer value = map.get(String.valueOf(i));
            Assert.areEqual(Integer.valueOf(i * MULTIPLE), value);
        }
    }

    public static void assertIsEmpty(ArrayMap4<String, Integer> map) {
        Assert.isFalse(map.isEmpty());
        map.clear();
        Assert.isTrue(map.isEmpty());
    }

    public static void assertKeySet(ArrayMap4<String, Integer> map) {
        Set<String> set = map.keySet();
        Assert.areEqual(DATA_LENGTH, set.size());
        for (int i = 0; i < DATA_LENGTH; i++) {
            set.contains(String.valueOf(i));
        }
    }

    public static void assertPut(ArrayMap4<String, Integer> map) {
        map.put("one", Integer.valueOf(1));
        map.put("two", Integer.valueOf(2));
        map.put("three", Integer.valueOf(3));
        Assert.areEqual(13, map.size());
        Assert.areEqual(Integer.valueOf(1), map.get("one"));
        Assert.areEqual(Integer.valueOf(2), map.get("two"));
        Assert.areEqual(Integer.valueOf(3), map.get("three"));

        map.put("two", Integer.valueOf(-2));
        checkPut(map);
    }
    
    public static void checkPut(ArrayMap4<String, Integer> map) {
        Assert.areEqual(Integer.valueOf(-2), map.get("two"));
    }

    public static void assertPutAll(ArrayMap4<String, Integer> map) {
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = DATA_LENGTH; i < DATA_LENGTH * 2; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * MULTIPLE));
        }

        map.putAll(other);

        checkMap(map, 0, DATA_LENGTH * 2);
    }
    
    public static void checkMap(ArrayMap4<String, Integer> map, int start, int end) {
        Assert.areEqual(end - start, map.size());
        for (int i = start; i < end; i++) {
            Assert.areEqual(Integer.valueOf(i * MULTIPLE), map
                    .get(String.valueOf(i)));
        }
    }

    public static void assertRemove_FromHead(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("0");
        Assert.areEqual(Integer.valueOf(0), value);

        checkRemove(map, 1, DATA_LENGTH, "0");
    }
    
    public static void checkRemove(ArrayMap4<String, Integer> map, int start, int end, String removedKey) {
        checkMap(map, start, end);
        Assert.isNull(map.get(removedKey));
    }

    public static void assertRemove_FromEnd(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("9");
        Assert.areEqual(Integer.valueOf(900), value);

        checkRemove(map, 0, 9, "9");
    }

    public static void assertRemove_FromMiddle(ArrayMap4<String, Integer> map) {
        Integer value = map.remove("5");
        Assert.areEqual(Integer.valueOf(500), value);

        checkRemove_FromMiddle(map);
    }
    
    public static void checkRemove_FromMiddle(ArrayMap4<String, Integer> map) {
        Assert.areEqual(9, map.size());
        for (int i = 0; i < 5; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
        Assert.isNull(map.get("5"));

        for (int i = 6; i < 9; i++) {
            Assert.areEqual(Integer.valueOf(i * 100), map
                    .get(String.valueOf(i)));
        }
    }

    public static void assertSize(ArrayMap4<String, Integer> map) {
        Assert.areEqual(DATA_LENGTH, map.size());
        map.remove("1");
        Assert.areEqual(9, map.size());
        map.put("x", Integer.valueOf(1234));
        Assert.areEqual(DATA_LENGTH, map.size());
    }

    public static void assertValues(ArrayMap4<String, Integer> map) {
        Collection<Integer> values = map.values();
        Assert.areEqual(10, values.size());
        for (int i = 0; i < DATA_LENGTH; i++) {
            Assert.isTrue(values.contains(Integer.valueOf(i * MULTIPLE)));
        }
    }

    public static void assertEquals(ArrayMap4<String, Integer> map) {
        ArrayMap4<String, Integer> other = new ArrayMap4<String, Integer>();
        for (int i = 0; i < DATA_LENGTH; i++) {
            other.put(String.valueOf(i), Integer.valueOf(i * MULTIPLE));
        }

        Assert.isTrue(map.equals(other));
        Assert.isTrue(other.equals(map));
        Assert.areEqual(map.hashCode(), other.hashCode());
        Assert.isFalse(map.equals(null));

        other.remove("5");
        Assert.isFalse(map.equals(other));
    }

    public static void assertIncreaseSize(ArrayMap4<String, Integer> map) {
        for (int i = DATA_LENGTH; i < DATA_LENGTH * 5; i++) {
            map.put(String.valueOf(i), Integer.valueOf(i * MULTIPLE));
        }

        checkMap(map, 0, DATA_LENGTH * 5);
    }
}
