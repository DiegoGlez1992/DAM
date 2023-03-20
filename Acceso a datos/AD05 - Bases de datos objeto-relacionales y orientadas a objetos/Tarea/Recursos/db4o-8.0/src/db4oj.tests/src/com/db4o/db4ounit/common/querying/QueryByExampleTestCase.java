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
package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryByExampleTestCase extends AbstractDb4oTestCase {

    static final int COUNT = 10;

    static LinkedList list = LinkedList.newLongCircularList();
    
    public static class Item {
    	
    	public String _name;
    	
    	public Item(String name){
    		_name = name;
    	}
    }

    public static void main(String[] args) {
        new QueryByExampleTestCase().runAll();
    }

    protected void store() {
        store(list);
    }
    
    public void testDefaultQueryModeIsIdentity(){
		Item itemOne = new Item("one");
    	Item itemTwo = new Item("two");
    	store(itemOne);
    	store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "two";
    	
    	// Query by Identity
    	Query q = db().query();
    	q.constrain(itemOne);
    	ObjectSet objectSet = q.execute();
    	
    	assertItem(objectSet, itemOne);
    }

    
    public void testConstrainByExample(){
    	Item itemOne = new Item("one");
		Item itemTwo = new Item("two");
		store(itemOne);
		store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "two";
    	
    	// Query by Example
    	Query q = db().query();
    	q.constrain(itemOne).byExample();
    	ObjectSet objectSet = q.execute();
    	
    	// Expect to get the other 
    	assertItem(objectSet, itemTwo);
    }

	private void assertItem(ObjectSet objectSet, Item item) {
		Assert.areEqual(1, objectSet.size());
    	Item retrievedItem = (Item) objectSet.next();
    	Assert.areSame(item, retrievedItem);
	}
    
    public void testQueryByExample(){
    	Item itemOne = new Item("one");
		Item itemTwo = new Item("two");
		store(itemOne);
		store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "two";
    	
    	// Query by Example
    	ObjectSet objectSet = db().queryByExample(itemOne);
    	
    	assertItem(objectSet, itemTwo);
    }
    
    public void testQueryByExampleNoneFound(){
    	Item itemOne = new Item("one");
		Item itemTwo = new Item("two");
		store(itemOne);
		store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "three";
    	
    	ObjectSet objectSet = db().queryByExample(itemOne);
    	
    	Assert.areEqual(0, objectSet.size());
    }
    

    public void testByExample() {
        Query q = db().query();
        q.constrain(list).byExample();
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
    }

    public void testByIdentity() {
        Query q = db().query();

        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
        while (result.hasNext()) {
            db().delete(result.next());
        }

        q = db().query();
        q.constrain(LinkedList.class);
        result = q.execute();
        Assert.areEqual(0, result.size());

        LinkedList newList = LinkedList.newLongCircularList();
        db().store(newList);
        q = db().query();
        q.constrain(newList);
        result = q.execute();
        Assert.areEqual(1, result.size());

    }
    
    

    public void testClassConstraint() {
        Query q = db().query();
        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());

        q = db().query();
        q.constrain(LinkedList.class).byExample();
        result = q.execute();
        Assert.areEqual(COUNT, result.size());

    }

    public static class LinkedList {

        public LinkedList _next;

        public transient int _depth;

        public static LinkedList newLongCircularList() {
            LinkedList head = new LinkedList();
            LinkedList tail = head;
            for (int i = 1; i < COUNT; i++) {
                tail._next = new LinkedList();
                tail = tail._next;
                tail._depth = i;
            }
            tail._next = head;
            return head;
        }

        public String toString() {
            return "List[" + _depth + "]";
        }
    }

}
