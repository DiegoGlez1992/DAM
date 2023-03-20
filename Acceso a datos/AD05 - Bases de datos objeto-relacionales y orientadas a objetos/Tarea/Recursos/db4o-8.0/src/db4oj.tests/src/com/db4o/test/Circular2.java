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

public class Circular2 {
    
    public Hashtable ht;
    
    public void storeOne(){
        Test.objectContainer().configure().updateDepth(Integer.MAX_VALUE);
        ht = new Hashtable();
        C2C c2c = new C2C();
        c2c.parent = this;
        ht.put("test", c2c);
    }
    
    public void testOne(){
        C2C c2c = (C2C)ht.get("test");
        Test.ensure(c2c.parent == this);
        Test.objectContainer().configure().updateDepth(5);
    }
    
    public static class C2C{
        public Circular2 parent;
    }
}
