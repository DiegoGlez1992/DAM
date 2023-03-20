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
import com.db4o.query.*;

/**
 * 
 */
public class IndexedUpdatesWithNull {
    
    public String str;
    
    public IndexedUpdatesWithNull(){
    }
    
    public IndexedUpdatesWithNull(String str){
        this.str = str;
    }
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("str").indexed(true);
    }
    
    public void store(){
        Test.store(new IndexedUpdatesWithNull("one"));
        Test.store(new IndexedUpdatesWithNull("two"));
        Test.store(new IndexedUpdatesWithNull("three"));
        Test.store(new IndexedUpdatesWithNull(null));
        Test.store(new IndexedUpdatesWithNull(null));
        Test.store(new IndexedUpdatesWithNull(null));
        Test.store(new IndexedUpdatesWithNull(null));
        Test.store(new IndexedUpdatesWithNull("four"));
    }
    
    public void test(){
        expectNulls(4, true);
        expectNulls(3, false);
        expectNulls(2, false);
        expectNulls(1, true);
    }
    
    private void expectNulls(int count, boolean commit){
        Query q = Test.query();
        q.constrain(this.getClass());
        q.descend("str").constrain(null);
        ObjectSet objectSet = q.execute();
        Test.ensure(objectSet.size() == count);
        IndexedUpdatesWithNull iuwn = (IndexedUpdatesWithNull)objectSet.next();
        iuwn.str = "hi";
        ObjectContainer oc = Test.objectContainer();
        oc.store(iuwn);
        if(commit){
            oc.commit();
        }
    }
}
