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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class IndexCreateDrop {
    
    public int myInt;
    public String myString;
    public Date myDate;
    
    public void store(){
        Test.deleteAllInstances(this);
        store(4);
        store(7);
        store(6);
        store(6);
        store(5);
        store(4);
        store(0);
        store(0);
    }
    
    public void test(){
        queries();
        indexed(true);
        Test.reOpenServer();
        queries();
        indexed(false);
        Test.reOpenServer();
        queries();
        indexed(true);
        Test.reOpenServer();
        queries();
    }
    
    private void indexed(boolean flag){
        ObjectClass oc = Db4o.configure().objectClass(this.getClass());
        oc.objectField("myInt").indexed(flag);
        oc.objectField("myString").indexed(flag);
        oc.objectField("myDate").indexed(flag);
    }
    
    private void store(int val){
        IndexCreateDrop icd = new IndexCreateDrop();
        icd.myInt = val;
        if(val != 0){
            icd.myString = "" + val;
            icd.myDate = new Date(val);
        }
        
        Test.store(icd);
    }
    
    private void queries(){
        Query q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myInt").constrain(new Integer(6));
        Test.ensure(q.execute().size() == 2);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myInt").constrain(new Integer(4)).greater();
        Test.ensure(q.execute().size() == 4);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myInt").constrain(new Integer(4)).greater().equal();
        Test.ensure(q.execute().size() == 6);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myInt").constrain(new Integer(7)).smaller().equal();
        Test.ensure(q.execute().size() == 8);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myInt").constrain(new Integer(7)).smaller();
        Test.ensure(q.execute().size() == 7);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myString").constrain("6");
        Test.ensure(q.execute().size() == 2);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myString").constrain("7");
        Test.ensure(q.execute().size() == 1);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myString").constrain("4");
        Test.ensure(q.execute().size() == 2);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myString").constrain(null);
        Test.ensure(q.execute().size() == 2);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myDate").constrain(new Date(4)).greater();
        Test.ensure(q.execute().size() == 4);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myDate").constrain(new Date(4)).greater().equal();
        Test.ensure(q.execute().size() == 6);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myDate").constrain(new Date(7)).smaller().equal();
        
        Test.ensure(q.execute().size() == 6);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myDate").constrain(new Date(7)).smaller();
        
        Test.ensure(q.execute().size() == 5);
        
        q = Test.query();
        q.constrain(IndexCreateDrop.class);
        q.descend("myDate").constrain(null);
        Test.ensureEquals(2,  q.execute().size());
    }

}
