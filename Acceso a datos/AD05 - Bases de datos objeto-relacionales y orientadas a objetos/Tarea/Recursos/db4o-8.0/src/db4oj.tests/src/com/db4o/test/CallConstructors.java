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

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

@decaf.Remove
public class CallConstructors {
    
    static Hashtable constructorCalledByClass = new Hashtable();
    
    static void constructorCalled(Object obj){
        constructorCalledByClass.put(obj.getClass(), obj);
    }
    
    static Object[] cases = new Object[]{
        new CallGlobal(),
        new CallLocalYes(),
        new CallLocalNo()
    };
    
    public void configure(){
        Db4o.configure().callConstructors(false);
        Db4o.configure().objectClass(new CallLocalYes()).callConstructor(true);
        Db4o.configure().objectClass(new CallLocalNo()).callConstructor(false);
    }
    
    public void store(){
        for (int i = 0; i < cases.length; i++) {
            Test.store(cases[i]);
        }
    }
    
    public void test(){
        if(! Test.clientServer){
	        check(new CallLocalYes(), true);
	        check(new CallLocalNo(), false);
	        check(new CallGlobal(), false);
        }
        Db4o.configure().callConstructors(true);
        Test.reOpen();
        check(new CallLocalYes(), true);
        check(new CallLocalNo(), false);
        check(new CallGlobal(), true);
        Db4o.configure().callConstructors(false);
        Test.reOpen();
        check(new CallLocalYes(), true);
        check(new CallLocalNo(), false);
        check(new CallGlobal(), false);
        
    }
    
    private void check(Object obj, boolean expected){
        constructorCalledByClass.clear();
        Query q = Test.query();
        q.constrain(obj.getClass());
        ObjectSet os = q.execute();
        Test.ensure(os.hasNext());
        Test.ensureEquals(obj.getClass(), os.next().getClass());
        Test.ensure(!os.hasNext());
        boolean called = constructorCalledByClass.get(obj.getClass()) != null;
        Test.ensure(called == expected);
    }
    
    
    public static class CallCommonBase{
        public CallCommonBase(){
            constructorCalled(this);
        }
    }
    
    public static class CallGlobal extends CallCommonBase{
    }
    
    public static class CallLocalYes extends CallCommonBase{
    }
    
    public static class CallLocalNo extends CallCommonBase{
    }
}
