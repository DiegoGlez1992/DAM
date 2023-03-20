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


public class UuidAware {
    
    public String name;
    
    private transient long uuidLongPart;

    private transient byte[] uuidSignaturePart;
    
    
    public UuidAware() {
     
    }
    
    public UuidAware(String name) {
        this.name = name;
    }

    public void configure(){
        Db4o.configure().objectClass(this).generateUUIDs(true);
    }
    
    public void store(){
        Test.objectContainer().store(new UuidAware("one"));
        Test.objectContainer().store(new UuidAware("two"));
    }
    
    public void test(){
        ExtObjectContainer oc = Test.objectContainer();

        UuidAware ua = queryName("one");
        ua.checkUUID(oc);
        
        ua = queryName("two");
        ua.checkUUID(oc);
    }
    
    private UuidAware queryName(String name){
        Query q = Test.query();
        q.constrain(getClass());
        q.descend(name);
        return (UuidAware) q.execute().next();
    }
    
    private void checkUUID(ExtObjectContainer oc){
        Db4oUUID uuid = new Db4oUUID(uuidLongPart, uuidSignaturePart);
        Test.ensure(oc.getByUUID(uuid) == this);
    }
    
    public void objectOnActivate(ObjectContainer oc){
        setUuidFields(oc);
    }
    
    public void objectOnNew(ObjectContainer oc){
        setUuidFields(oc);
    }
    
    private void setUuidFields(ObjectContainer oc){
        ObjectInfo info = oc.ext().getObjectInfo(this);
        Db4oUUID uuid = info.getUUID();
        uuidLongPart = uuid.getLongPart();
        uuidSignaturePart = uuid.getSignaturePart();
    }
    

}
