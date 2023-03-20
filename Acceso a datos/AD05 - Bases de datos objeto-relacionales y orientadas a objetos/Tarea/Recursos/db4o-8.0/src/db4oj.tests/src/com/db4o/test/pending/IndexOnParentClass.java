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
package com.db4o.test.pending;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class IndexOnParentClass {
    
    public String name;
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("name").indexed(true);
    }
    
    public void store(){
        IndexOnParentClass p = new IndexOnParentClass();
        p.name = "all";
        Test.store(p);
        p = new ChildClass();
        p.name = "all";
        Test.store(p);
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(ChildClass.class);
        q.descend("name").constrain("all");
        int size = q.execute().size();
        System.out.println(size);
        Test.ensure(size == 1);
    }
    
    public static class ChildClass extends IndexOnParentClass{
        
    }
}
