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
package com.db4o.test.constraints;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class UniqueField {
    
    public int id;
    public String name;
    
    public void store(){
        UniqueField uf = new UniqueField();
        uf.name = "Mark I";
        uf.id = 1;
        Test.store(uf);
        uf = new UniqueField();
        uf.name = "Mark II";
        uf.id = 2;
        Test.store(uf);
        check();
    }
    
    public void test(){
        UniqueField uf = new UniqueField();
        uf.name = "Mark I";
        Test.store(uf);
        check();
        Test.commit();
        check();
        
        Query q = Test.query();
        q.constrain(UniqueField.class);
        q.descend("id").constrain(new Integer(1));
        uf = (UniqueField) q.execute().next();
        uf.name = "Mark II";
        Test.store(uf);
        check();
        Test.commit();
        check();
    }
    
    private void check(){
        ObjectContainer oc = Test.objectContainer();
        Query q = Test.query();
        q.constrain(UniqueField.class);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == 2);
        while(objectSet.hasNext()){
            UniqueField uf = (UniqueField)objectSet.next();
            oc.ext().refresh(uf, Integer.MAX_VALUE);
            if(uf.id == 1){
                Test.ensure(uf.name.equals("Mark I"));
            }else{
                Test.ensure(uf.name.equals("Mark II"));
            }
        }
    }
    
    public boolean objectCanNew(ObjectContainer objectContainer){
        return checkConstraints(objectContainer);
    }

    public boolean objectCanUpdate(ObjectContainer objectContainer){
        return checkConstraints(objectContainer);
    }
    
    private boolean checkConstraints(ObjectContainer objectContainer){
        String semaphoreName ="Unique_User_Name";
        if(! objectContainer.ext().setSemaphore(semaphoreName, 1000)){
            return false;
        }
        Query q = objectContainer.query();
        q.constrain(UniqueField.class);
        q.descend("name").constrain(name);
        ObjectSet objectSet = q.execute(); 
        int size = objectSet.size();
        
        boolean canStore = false;
        if(size == 0){
            canStore = true;
        }
        if(size == 1){
            UniqueField uf = (UniqueField)objectSet.next();
            canStore = (uf == this);
        }
        objectContainer.ext().releaseSemaphore(semaphoreName);
        return canStore;
    }
    
}
