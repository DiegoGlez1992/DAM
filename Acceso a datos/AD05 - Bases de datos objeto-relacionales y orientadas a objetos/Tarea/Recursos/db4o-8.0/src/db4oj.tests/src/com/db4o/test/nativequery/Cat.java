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
package com.db4o.test.nativequery;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;



/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Cat {
    
    public String name;
    
    public Cat(){
        
    }
    
    public Cat(String name){
        this.name = name;
    }
    
    public void store(){
        Test.store(new Cat("Fritz"));
        Test.store(new Cat("Garfield"));
        Test.store(new Cat("Tom"));
        Test.store(new Cat("Occam"));
        Test.store(new Cat("Zora"));
    }
    
    public void test(){
        ObjectContainer objectContainer = Test.objectContainer();
        List<Cat> list = objectContainer.query(new Predicate <Cat> () {
            public boolean match(Cat cat){
                return cat.name.equals("Occam") || cat.name.equals("Zora"); 
            }
        });
        Test.ensure(list.size() == 2);
        String[] lookingFor = new String[] {"Occam" , "Zora"};
        boolean[] found = new boolean[2];
        for (Cat cat : list){
            for (int i = 0; i < lookingFor.length; i++) {
                if(cat.name.equals(lookingFor[i])){
                    found[i] = true;
                }
            }
        }
        for (int i = 0; i < found.length; i++) {
            Test.ensure(found[i]);
        }
    }
    

}
