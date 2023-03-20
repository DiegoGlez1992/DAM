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


public class CallbackCanDelete {
    
    public String _name;
    
    public CallbackCanDelete _next;
    
    public CallbackCanDelete() {
    
    }

    public CallbackCanDelete(String name_, CallbackCanDelete next_) {
        _name = name_;
        _next = next_;
    }
    
    public void storeOne(){
        Test.deleteAllInstances(this);
        _name = "p1";
        _next = new CallbackCanDelete("c1", null);
    }
    
    public void test(){
        ObjectContainer oc = Test.objectContainer();
        ObjectSet objectSet = oc.queryByExample(new CallbackCanDelete("p1", null));
        CallbackCanDelete ccd = (CallbackCanDelete) objectSet.next();
        oc.deactivate(ccd, Integer.MAX_VALUE);
        oc.delete(ccd);
    }
    
    
    public boolean objectCanDelete(ObjectContainer container){
        container.activate(this, Integer.MAX_VALUE);
        Test.ensure(_name.equals("p1"));
        Test.ensure(_next != null);
        return true;
    }
    
}
