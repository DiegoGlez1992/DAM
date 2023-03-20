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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class PersistentListTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] args) {
        new PersistentListTestCase().runSolo();
    }
    
    private final List[] lists = new List[]{
        new MockPersistentList(),
    };
    
    private Vector testData(){
        Vector vector = new Vector();
        vector.add("zero");
        vector.add("one");
        vector.add("two");
        return vector;
    }
    
    public void testAdd(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            Vector data = testData();
            addData(list, data);
            assertAreEqual(data, list);
            data.add("one");
            list.add("one");
            assertAreEqual(data, list);
        }
    }

    public void testClear(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            list.clear();
            Assert.areEqual(0, list.size());
            addData(list, testData());
            list.clear();
            Assert.areEqual(0, list.size());
        }
    }
    
    public void testRemove(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            Vector data = testData();
            addData(list, data);
            
            Object item = data.get(1);
            list.remove(item);
            data.remove(item);
            assertAreEqual(data, list);
            
            for (int j = 0; j < data.size(); j++) {
                item = data.get(0);
                list.remove(item);
                data.remove(item);
            }
            assertAreEqual(data, list);
        }
    }
    
    public void testSet(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            Vector data = testData();
            addData(list, data);
            
            int size = data.size();
            for (int j = 0; j < size; j++) {
                data.set(j, new Integer(j));
                list.set(j, new Integer(j));
            }
            
            assertAreEqual(data, list);
        }
    }
    
    public void testAddAtIndex(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            Vector data = testData();
            addData(list, data);
            
            int size = data.size();
            for (int j = 0; j < size; j++) {
                data.add(j + 2, new Integer(j));
                list.add(j + 2, new Integer(j));
            }
            
            assertAreEqual(data, list);
        }
    }
    
    public void testRemoveAtIndex(){
        for (int i = 0; i < lists.length; i++) {
            List list = lists[i];
            
            Vector data = testData();
            addData(list, data);
            
            int size = data.size();
            for (int j = 0; j < size; j++) {
                data.remove(0);
                list.remove(0);
            }
            assertAreEqual(data, list);
            
            
            data = testData();
            addData(list, data);
            
            for (int j = 0; j < size; j++) {
                int pos = data.size() - 1;
                data.remove(pos);
                list.remove(pos);
            }
            assertAreEqual(data, list);

            data = testData();
            addData(list, data);
            
            for (int j = 0; j < size - 2; j++) {
                data.remove(1);
                list.remove(1);
            }
            assertAreEqual(data, list);
            
        }
    }
    
    private void addData(List list, Vector data) {
        for (int j = 0; j < data.size(); j++) {
            list.add(data.get(j));
        }
    }
    
    private void assertAreEqual(Vector vector, List list){
        Assert.areEqual(vector.size(), list.size());
        ArrayAssert.areEqual(vector.toArray(), list.toArray());
    }
    
}
