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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryByInterface extends AbstractDb4oTestCase {
    
    public static void main(String[] args) {
        new QueryByInterface().runSolo();
    }
    
    protected void store() throws Exception {
        
        Ferrari f430 = new Ferrari("F430");
        Ferrari f450 = new Ferrari("F450");
        store(f430);
        store(f450);
        
        Bmw serie5 = new Bmw("Serie 5");
        Bmw serie7 = new Bmw("Serie 7");
        
        store(serie5);
        store(serie7);
    }
    
    public void test() {
        Query q = newQuery();
        q.constrain(Car.class);
        q.descend("name").constrain("F450");
        ObjectSet result = q.execute();
        
        Assert.areEqual(1, result.size());
        
        Ferrari car = (Ferrari) result.next();
        
        Assert.areEqual("F450", car.name);
    }
    
    public interface Car {
    	
    	
    }

    public static class Ferrari implements Car
    {
        public String name;

        public Ferrari(String n)
        {
            name = n;
        }
        
        public String toString() {
            return "Ferrari " + name;
        }
    }

    public static class Bmw implements Car
    {
        public String name;

        public Bmw(String n)
        {
            name = n;
        }
        
        public String toString() {
            return "BMW " + name;
        }
    }

}
