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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class TestDescend {
    
    public TestDescend _child;
    
    public String _name;
    
    public TestDescend(){
        
    }
    
    public TestDescend(TestDescend child, String name){
        _child = child;
        _name = name;
    }
    
    public void storeOne(){
        _child = new TestDescend(new TestDescend(new TestDescend(null, "3"), "2"), "1");
        _name = "0";
    }
    
    public void test(){
        if(Test.isClientServer()){
            return;
        }
        Query q = Test.query();
        q.constrain(this.getClass());
        q.descend("_name").constrain("0");
        ObjectSet objectSet = q.execute();
        TestDescend res = (TestDescend)objectSet.next();
        
        ExtObjectContainer oc = Test.objectContainer();
        
        Object obj = oc.descend(res, new String[]{"_name"});
        Test.ensure(obj.equals("0"));
        
        obj = oc.descend(res, new String[]{"_child", "_child", "_name"});
        Test.ensure(obj.equals("2"));
        
        obj = oc.descend(res, new String[]{"_child", "_child", "_child", "_name"});
        Test.ensure(obj.equals("3"));
        
        obj = oc.descend(res, new String[]{"_child", "CRAP", "_child", "_name"});
        Test.ensure(obj == null);
    }

}
