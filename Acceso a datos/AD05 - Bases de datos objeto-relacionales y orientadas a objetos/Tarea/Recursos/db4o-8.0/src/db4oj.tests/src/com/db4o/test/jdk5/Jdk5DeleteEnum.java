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
package com.db4o.test.jdk5;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


/**
 */
@decaf.Ignore
public class Jdk5DeleteEnum {
    
    Jdk5Enum a;
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
    }
    
    public void store(){
        for (int i = 0; i < 2; i++) {
            Jdk5DeleteEnum jde = new Jdk5DeleteEnum();
            jde.a = Jdk5Enum.A;
            Test.store(jde);
        }
    }
    
    public void test(){
        Jdk5DeleteEnum jde = queryOne(); 
        Test.delete(jde);
        Test.reOpen();
        jde = queryOne();
        Test.ensure(jde.a == Jdk5Enum.A);
    }
    
    private Jdk5DeleteEnum queryOne(){
        Query q = Test.query();
        q.constrain(Jdk5DeleteEnum.class);
        ObjectSet objectSet = q.execute();
        return (Jdk5DeleteEnum)objectSet.next();
    }
    
}
