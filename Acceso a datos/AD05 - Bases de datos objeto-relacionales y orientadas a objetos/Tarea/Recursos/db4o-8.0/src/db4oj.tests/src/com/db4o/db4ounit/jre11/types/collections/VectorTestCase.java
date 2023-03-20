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
package com.db4o.db4ounit.jre11.types.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class VectorTestCase extends AbstractDb4oTestCase {
    
    private static final String VALUE_TWO = "two";
    
    private static final String VALUE_ONE = "one";

    private static final ReferenceTypeElement REFERENCE_TYPE_ELEMENT_ONE = new ReferenceTypeElement(VALUE_ONE);
    
    private static final ReferenceTypeElement REFERENCE_TYPE_ELEMENT_TWO = new ReferenceTypeElement(VALUE_TWO);

    public static class Item {
        
        public int id;
        
        public Vector vector;
        
    }
    
    public static class ReferenceTypeElement {
        
        public String name;
        
        public ReferenceTypeElement(String name_) {
            name = name_;
        }
        
        public boolean equals(Object obj) {
            if(obj == null){
                return false;
            }
            if(obj.getClass() != getClass()){
                return false;
            }
            ReferenceTypeElement other = (ReferenceTypeElement) obj;
            if(name == null){
                return other.name == null;
            }
            return name.equals(other.name);
        }
        
    }
    
    protected void store() throws Exception {
        storeItem(1, new Object[] {VALUE_ONE});
        storeItem(2, new Object[] {VALUE_TWO});
        storeItem(3, new Object[] {REFERENCE_TYPE_ELEMENT_ONE});
        storeItem(4, new Object[] {REFERENCE_TYPE_ELEMENT_TWO});
    }

    private void storeItem(int id, Object[] values) {
        Item item = new Item();
        item.vector = new Vector();
        for (int i = 0; i < values.length; i++) {
            item.vector.addElement(values[i]);
        }
        item.id = id;
        store(item);
    }
    
    public void testRetrieveInstance(){
        Vector vector = retrieveFirstVector();
        assertSingleElementValue(vector, VALUE_ONE);
    }

    private void assertSingleElementValue(Vector vector, Object itemValue) {
        Object firstItem = vector.elementAt(0);
        Assert.areEqual(itemValue, firstItem);
        Assert.areEqual(1, vector.size());
    }

    private Vector retrieveFirstVector() {
        Item item = retrieveFirstItem();
        return item.vector;
    }

    public void testUpdate() throws Exception{
        Vector vector = retrieveFirstVector();
        vector.removeElementAt(0);
        vector.addElement(VALUE_TWO);
        store(vector);
        reopen();
        vector = retrieveFirstVector();
        assertSingleElementValue(vector, VALUE_TWO);
    }
    
    private Item retrieveFirstItem() {
        return retrieveItemById(1); 
    }

    private Item retrieveItemById(int id) {
        Query q = db().query();
        q.constrain(Item.class);
        q.descend("id").constrain(new Integer(id));
        ObjectSet objectSet = q.execute();
        Assert.areEqual(1, objectSet.size());
        return (Item) objectSet.next();
    }
    
    public void testStringQuery(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                ((Query) arg).descend("vector").constrain(VALUE_ONE);
            }
        }, new Object[]{VALUE_ONE});

    }
    
    public void testReferenceTypeElementQuery(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                ((Query) arg).descend("vector").descend("name").constrain(VALUE_ONE);
            }
        }, new Object[]{REFERENCE_TYPE_ELEMENT_ONE});
    }
    
    public void testQueryOrReferenceTypeMember(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                Query query = ((Query) arg).descend("vector");
                Constraint oneConstraint = query.descend("name").constrain(VALUE_ONE);
                Constraint twoConstraint = query.descend("name").constrain(VALUE_TWO);
                oneConstraint.or(twoConstraint);
            }
        }, new Object[]{REFERENCE_TYPE_ELEMENT_ONE, REFERENCE_TYPE_ELEMENT_TWO});
    }
    
    public void testQueryOrString(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                Query query = ((Query) arg).descend("vector");
                Constraint oneConstraint = query.constrain(VALUE_ONE);
                Constraint twoConstraint = query.constrain(VALUE_TWO);
                oneConstraint.or(twoConstraint);
            }
        }, new Object[]{VALUE_ONE, VALUE_TWO});
    }

    
    public void testQuerySelfOr(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                Query query = ((Query) arg).descend("vector");
                Constraint nameConstraint = query.constrain(VALUE_ONE);
                Constraint secondNameConstraint = query.constrain(VALUE_ONE);
                nameConstraint.or(secondNameConstraint);
            }
        }, new Object[]{VALUE_ONE});
    }
    
    public void _testQueryOrOverTwoLevelsBroken(){
        assertVectorQuery(new Procedure4() {
            public void apply(Object arg) {
                Query query = ((Query) arg).descend("vector");
                Constraint nameConstraint = query.constrain(VALUE_ONE);
                Constraint referenceNameConstraint = query.descend("name").constrain(VALUE_ONE);
                nameConstraint.or(referenceNameConstraint);
            }
        }, new Object[]{VALUE_ONE, REFERENCE_TYPE_ELEMENT_ONE});
    }

    private void assertVectorQuery(Procedure4 constraint, Object[] expectedItems) {
        Query q = db().query();
        q.constrain(Item.class);
        
        constraint.apply(q);
        
        ObjectSet objectSet = q.execute();
        Assert.areEqual(expectedItems.length, objectSet.size());
        while(objectSet.hasNext()){
            Item item = (Item) objectSet.next();
            Object firstElement = item.vector.elementAt(0);
            assertContains(expectedItems, firstElement);
        }
        for (int i = 0; i < expectedItems.length; i++) {
            Assert.isNull(expectedItems[i]);
        }
    }

    private void assertContains(Object[] expectedItems, Object firstElement) {
        boolean found = false;
        for (int i = 0; i < expectedItems.length; i++) {
            if(firstElement.equals(expectedItems[i])){
                expectedItems[i] = null;
                found = true;
            }
        }
        Assert.isTrue(found);
    }
    
}
