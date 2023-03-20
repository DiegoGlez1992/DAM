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
package com.db4o.test.legacy;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class MultiDelete {
    
    public MultiDelete child;
    public String name;
    public Object forLong;
    public Long myLong;
    public Object[] untypedArr;
    public Long[] typedArr;
    
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
        Db4o.configure().objectClass(this).cascadeOnUpdate(true);
    }
    
    public void store(){
        MultiDelete md = new MultiDelete();
        md.name = "killmefirst";
        md.setMembers();
        md.child = new MultiDelete();
        md.child.setMembers();
        Test.store(md);
    }
    
    private void setMembers(){
        forLong = new Long(100);
        myLong = new Long(100);
        untypedArr = new Object[]{
            new Long(10),
            "hi",
            new MultiDelete()
        };
        typedArr = new Long[]{
            new Long(3),
            new Long(7),
            new Long(9),
        };
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(MultiDelete.class);
        q.descend("name").constrain("killmefirst");
        ObjectSet objectSet = q.execute();
        Test.ensureEquals(1,objectSet.size());
        MultiDelete md = (MultiDelete)objectSet.next();
        ExtObjectContainer oc = Test.objectContainer();
        long id = oc.getID(md);
        oc.delete(md);
        
        MultiDelete afterDelete = (MultiDelete)oc.getByID(id);
        oc.delete(md);
    }
    

}
