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
package com.db4o.test.conjunctions;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class CJSingleField implements CJHasID{
    
    public int _id;
    
    public CJSingleField(){
    }
    
    public CJSingleField(int id){
        _id = id;
    }
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("_id").indexed(true);
    }
    
    public void store(){
        Test.deleteAllInstances(CJSingleField.class);
        store(1);
        store(2);
        store(3);
        store(3);
    }
    
    private void store(int i){
        Test.store(new CJSingleField(i));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(this.getClass());
        Query qId = q.descend("_id");
        qId.constrain(new Integer(1)).greater();
        qId.constrain(new Integer(2)).smaller().equal();
        ConjunctionsTestSuite.expect(q, new int[]{2});
    }
    
    public int getID() {
        return _id;
    }

}
