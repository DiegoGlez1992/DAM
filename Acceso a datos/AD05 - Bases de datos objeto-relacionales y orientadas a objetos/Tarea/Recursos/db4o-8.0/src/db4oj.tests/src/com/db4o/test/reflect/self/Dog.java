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
package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.self.*;
import com.db4o.test.*;


public class Dog extends Animal {

	private transient static Vector dogs;
	
    // must be public for the time being due to setAccessible() check in Platform4
    public int _age;
    
    public Dog[] _parents;
    
    public int[] _prices;
    
    public Dog() {
    	// require public no-args constructor
    	this(null,0);
    }
    
    public Dog(String name,int age){
    	this(name,age,new Dog[0],new int[0]);
    }

    public Dog(String name,int age,Dog[] parents,int[] prices){
    	super(name);
        _age = age;
        _parents=parents;
        _prices=prices;
    }
    
    public void configure(){
        Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
    }
    
    public void store(){
        dogs=new Vector();
        Dog laika = new Dog("Laika",7);
        Dog lassie = new Dog("Lassie",6);
        dogs.addElement(laika);
        dogs.addElement(lassie);
        dogs.addElement(new Dog("Sharik",100, new Dog[]{laika,lassie},new int[]{3,2,1}));
        for (Enumeration e = dogs.elements(); e.hasMoreElements();) {
            Test.store(e.nextElement());
        }
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(Dog.class);
        ObjectSet res = q.execute();
        Test.ensure(res.size() == dogs.size());
        while(res.hasNext()) {
        	Dog dog=(Dog)res.next();
        	System.out.println(">>>"+dog._name);
        	Test.ensure(dogs.contains(dog));
        }
                
        q = Test.query();
        q.constrain(Dog.class);
        q.descend("_name").constrain("Laika");
        res = q.execute();
        Test.ensure(res.size() == 1);
        Dog laika = (Dog) res.next();
        Test.ensure(laika._name.equals("Laika"));
    }

    /* GENERATE */
	public Object self_get(String fieldName) {
		if(fieldName.equals("_age")) {
			return new Integer(_age);
		}
		if(fieldName.equals("_parents")) {
			return _parents;
		}
		if(fieldName.equals("_prices")) {
			return _prices;
		}
		return super.self_get(fieldName);
	}

    /* GENERATE */
	public void self_set(String fieldName,Object value) {
		if(fieldName.equals("_age")) {
			_age=((Integer)value).intValue();
			return;
		}
		if(fieldName.equals("_parents")) {
			_parents=(Dog[])value;
			return;
		}
		if(fieldName.equals("_prices")) {
			_prices=(int[])value;
			return;
		}
		super.self_set(fieldName,value);
	}
	
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		Dog dog=(Dog)obj;
		boolean sameName=(_name==null ? dog._name==null : _name.equals(dog._name));
		boolean sameAge=_age==dog._age;
		boolean sameParents=_parents.length==dog._parents.length;
		if(sameParents) {
			for (int i = 0; i < _parents.length; i++) {
				if(!_parents[i].equals(dog._parents[i])) {
					sameParents=false;
					break;
				}
			}
		}
		boolean samePrices=_prices.length==dog._prices.length;
		if(samePrices) {
			for (int i = 0; i < _prices.length; i++) {
				if(!(_prices[i]==dog._prices[i])) {
					samePrices=false;
					break;
				}
			}
		}
		
		return sameName&&sameAge&&sameParents&&samePrices;
	}
	
	public int hashCode() {
		int hash=_age;
		hash=hash*29+(_name==null ? 0 : _name.hashCode());
		hash=hash*29+_parents.length;
		return hash;
	}
}
