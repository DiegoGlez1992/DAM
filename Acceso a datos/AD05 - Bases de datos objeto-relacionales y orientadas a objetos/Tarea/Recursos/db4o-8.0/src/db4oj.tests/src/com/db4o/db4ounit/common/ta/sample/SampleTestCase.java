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
package com.db4o.db4ounit.common.ta.sample;

import java.lang.reflect.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class SampleTestCase extends AbstractDb4oTestCase implements OptOutTA, OptOutDefragSolo {

    public static void main(String[] args) {
        new SampleTestCase().runAll(); 
    }
    
    private long customerID;
    
    private long countryID;
    
    private Db4oUUID customerUUID;
    
    private Db4oUUID countryUUID;
    
    protected void configure(Configuration config) throws Exception {
        config.add(new TransparentActivationSupport());
        config.generateUUIDs(ConfigScope.GLOBALLY);
    }
    
    protected void store(){
        Customer customer = new Customer();
        customer._name = "db4objects";
        Address address = new Address();
        customer._addresses = new Address[]{address};
        Country country = new Country();
        address._country = country;
        address._firstLine = "Suite 350";
        State state = new State();
        country._states = new State[]{state};
        state._name = "California";
        City city = new City();
        state._cities = new City[] {city};
        store(customer);
    }
    
    protected void db4oSetupAfterStore() throws Exception {
        Object customer = retrieveOnlyInstance(Customer.class);
        Object country = retrieveOnlyInstance(Country.class);
        
        customerID = db().getID(customer);
        countryID = db().getID(country);
        
        customerUUID = db().getObjectInfo(customer).getUUID();
        countryUUID = db().getObjectInfo(country).getUUID();
        
        reopen();
    }
    
    public void testRetrieveNonActivatable() throws Exception{
        checkGraphActivation((Customer) retrieveOnlyInstance(Customer.class));
    }
    
    public void testRetrieveActivatable() throws Exception{
        checkGraphActivation((Country) retrieveOnlyInstance(Country.class));
    }
    
    public void testPeekPersisted(){
        run(new ActAndAssert(){
            public Object actOnRoot(Object obj) {
                return db().peekPersisted(obj, Integer.MAX_VALUE, true);
            }
            public void assertOnLeaves(Object obj) {
                Assert.isNotNull(obj);
                Assert.isFalse(db().isStored(obj));
            }
        });
    }
    
    public void testFullActivation(){
        run(new ActAndAssert(){
            public Object actOnRoot(Object obj) {
                db().activate(obj, Integer.MAX_VALUE);
                return obj;
            }
            public void assertOnLeaves(Object obj) {
                Assert.isNotNull(obj);
                Assert.isTrue(db().isStored(obj));
            }
        });
    }
    
    public void testRefresh(){
        run(new ActAndAssert(){
            public Object actOnRoot(Object obj) {
                db().activate(obj, Integer.MAX_VALUE);
                Iterator4 i = iterateGraphStringFields(obj);
                while(i.moveNext()){
                    FieldOnObject fieldOnObject = (FieldOnObject) i.current();
                    fieldOnObject._field.set(fieldOnObject._object, "modified");
                }
                db().refresh(obj, Integer.MAX_VALUE);
                return obj;
            }
            public void assertOnLeaves(Object obj) {
                Iterator4 i = iterateStringFieldsOnObject(obj);
                while(i.moveNext()){
                    FieldOnObject fieldOnObject = (FieldOnObject) i.current();
                    Assert.areNotEqual("modified", fieldOnObject._field.get(fieldOnObject._object));
                }
            }
        });
    }
    
    public void testDeactivate(){
        run(new ActAndAssert(){
            public Object actOnRoot(Object obj) {
                db().activate(obj, Integer.MAX_VALUE);
                Iterator4 graph = iterateGraph(obj);
                db().deactivate(obj, Integer.MAX_VALUE);
                return graph;
            }
            public void assertOnLeaves(Object obj) {
                assertIsDeactivated(obj);
            }
        });
    }
    
    public void testGetById(){
        assertIsDeactivated(countryByID());
        assertIsDeactivated(customerByID());
    }
    
    public void testGetByUUID(){
        assertIsDeactivated(db().getByUUID(countryUUID));
        assertIsDeactivated(db().getByUUID(customerUUID));
    }
    
    public Iterator4 iterateGraphStringFields(Object obj){
        return Iterators.concat(Iterators.map(iterateGraph(obj), new Function4() {
            public Object apply(Object current) {
                return iterateStringFieldsOnObject(current);
            }
        }));
    }

    Customer customerByID() {
        return (Customer) db().getByID(customerID);
    }
    
    Country countryByID() {
        return (Country) db().getByID(countryID);
    }
    
    // A small evil multimethod hack to have "Do What I mean" behaviour. 
    Iterator4 iterateGraph(Object obj){
        if(obj instanceof Iterator4){
            return (Iterator4)obj;
        }
        if(obj instanceof Customer){
            return iterateGraph((Customer)obj);
        }
        return iterateGraph((Country)obj);
    }
    
    private Iterator4 iterateGraph(Customer customer){
        return new CompositeIterator4(new Iterator4[]{
            iterateGraph(customer._addresses[0]._country),
            new ArrayIterator4(new Object[]{
                customer._addresses[0]._country,
                customer._addresses[0],
                customer
            })
        });
    }

    private Iterator4 iterateGraph(Country country){
        return new ArrayIterator4(new Object[]{
            country._states[0]._cities[0],
            country._states[0],
            country
        });
    }
    
    private void checkGraphActivation(Customer customer) throws Exception {
        assertIsActivated(customer);
        assertIsNotNull(customer, "_name");
        assertIsNotNull(customer, "_addresses");
        Address address = customer._addresses[0];
        assertIsActivated(address);
        assertIsNotNull(address, "_firstLine");
        assertIsNotNull(address, "_country");
        checkGraphActivation(address._country);
    }
    
    private void checkGraphActivation(Country country) throws Exception {
        assertIsDeactivated(country);
        assertIsNull(country, "_states");
        State state = country.getState("94403");
        assertIsActivated(state);
        assertIsNotNull(country, "_states");
        assertIsNotNull(state, "_name");
        assertIsNotNull(state, "_cities");
        City city = state._cities[0];
        Assert.isNotNull(city);
        assertIsDeactivated(city);
        assertIsNull(city, "_name");
    }
    
    void assertIsDeactivated(Object obj){
        Iterator4 i = iterateFieldValues(obj);
        while(i.moveNext()){
            Assert.isNull(i.current());
        }
        Assert.isFalse(db().isActive(obj));
        Assert.isTrue(db().isStored(obj));
    }
    
    private void assertIsActivated(Object obj){
        Iterator4 i = iterateFieldValues(obj);
        while(i.moveNext()){
            Assert.isNotNull(i.current());
        }
        Assert.isTrue(db().isActive(obj));
        Assert.isTrue(db().isStored(obj));
    }
    
    Iterator4 iterateStringFieldsOnObject(final Object obj){
        final ReflectClass stringClass = reflector().forClass(String.class);
        return mapPersistentFields(obj, new Function4(){
            public Object apply(Object current) {
                ReflectField field = (ReflectField) current;
                if(field.getFieldType() != stringClass){
                    return Iterators.SKIP;
                }
                return new FieldOnObject(field, obj);
            }
        });
    }

    private Iterator4 iterateFieldValues(final Object obj){
        return mapPersistentFields(obj, new Function4() {
            public Object apply(Object current) {
                ReflectField field = (ReflectField) current;
                try {
                    return field.get(obj);
                } catch (Exception e) {
                    throw new Db4oException(e);
                }
            }
        });
    }
    
	private Iterator4 mapPersistentFields(final Object obj,
			final Function4 function) {
		return Iterators.map(iteratePersistentFields(obj), function);
	}
    
    private Iterator4 iteratePersistentFields(final Object obj){
        return Iterators.filter(declaredFields(obj), new Predicate4() {
        	public boolean match(Object candidate) {
                ReflectField field = (ReflectField) candidate;
                return !field.isTransient() && !field.isStatic();
            }        
        });
    }

	private ReflectField[] declaredFields(final Object obj) {
		ReflectClass claxx = reflector().forObject(obj);
        return claxx.getDeclaredFields();
	}
    
    private void assertIsNull(Object obj, String fieldName) throws Exception{
        Assert.isTrue(fieldIsNull(obj, fieldName));
    }
    
    private void assertIsNotNull(Object obj, String fieldName) throws Exception{
        Assert.isFalse(fieldIsNull(obj, fieldName));
    }
    
    private boolean fieldIsNull(Object obj, String fieldName) throws Exception {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        Assert.isNotNull(field);
        return field.get(obj) == null;
    }
    
    private void run(ActAndAssert actAndAssert){
        run(actAndAssert, customerByID());
        run(actAndAssert, countryByID());
    }
    
    private void run(ActAndAssert actAndAssert, Object obj){
        Iterator4 i = iterateGraph(actAndAssert.actOnRoot(obj));
        while(i.moveNext()){
            actAndAssert.assertOnLeaves(i.current());
        }
    }
    
    public static class FieldOnObject {
        
        public final ReflectField _field;
        
        public final Object _object;
        
        public FieldOnObject(ReflectField field, Object obj){
            _field = field;
            _object = obj;
        }
    }
    
    public interface ActAndAssert {
        
        public Object actOnRoot(Object obj);
        
        public void assertOnLeaves(Object obj);
        
    }

}

