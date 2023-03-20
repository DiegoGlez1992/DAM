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
import com.db4o.query.*;
import com.db4o.test.*;

public class CreateIndex {

    public String i_name;
    
    public int i_int;
    
    public Integer i_intWrapper;

    public CreateIndex() {
    }

    public CreateIndex(String name) {
        this.i_name = name;
    }

    public CreateIndex(int a_int) {
        i_int = a_int;
        i_intWrapper = new Integer(a_int);
    }

    public void configure() {
        Db4o.configure().objectClass(this).objectField("i_name").indexed(true);
        Db4o.configure().objectClass(this).objectField("i_int").indexed(true);
        Db4o.configure().objectClass(this).objectField("i_intWrapper").indexed(true);
    }

    public void store() {
        Test.deleteAllInstances(this);

        Test.store(new CreateIndex("a"));
        Test.store(new CreateIndex("c"));
        Test.store(new CreateIndex("b"));
        Test.store(new CreateIndex("f"));
        Test.store(new CreateIndex("e"));

        Test.store(new CreateIndex(1));
        Test.store(new CreateIndex(5));
        Test.store(new CreateIndex(7));
        Test.store(new CreateIndex(3));
        Test.store(new CreateIndex(2));
        Test.store(new CreateIndex(3));

        tQueryB();
        tQueryInts(5);
        tQueryIntWrapper();
    }

    public void test() {
        
        tQueryNull(6);
        
        Test.store(new CreateIndex("d"));
        tQueryB();
        tUpdateB();
        Test.store(new CreateIndex("z"));
        Test.store(new CreateIndex("y"));
        Test.reOpen();
        tQueryB();

        tQueryInts(8);
    }
    
    private void tQueryIntWrapper(){
        
        Query q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_intWrapper").constrain(new Integer(4)).greater().equal();
        tExpectInts(q, new int[] { 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_intWrapper").constrain(new Integer(4)).smaller();
        tExpectInts(q, new int[] { 1, 2, 3, 3 });
        
    }

    private void tQueryInts(int expectedZeroSize) {
        
        Query q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(0));
        int zeroSize = q.execute().size();
        Test.ensure(zeroSize == expectedZeroSize);
        
        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(4)).greater().equal();
        tExpectInts(q, new int[] { 5, 7 });
         
        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(4)).greater();
        tExpectInts(q, new int[] { 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(3)).greater();
        tExpectInts(q, new int[] { 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(3)).greater().equal();
        tExpectInts(q, new int[] { 3, 3, 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(2)).greater().equal();
        tExpectInts(q, new int[] { 2, 3, 3, 5, 7 });
        q = Test.query();

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(2)).greater();
        tExpectInts(q, new int[] { 3, 3, 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(1)).greater().equal();
        tExpectInts(q, new int[] { 1, 2, 3, 3, 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(1)).greater();
        tExpectInts(q, new int[] { 2, 3, 3, 5, 7 });

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(4)).smaller();
        tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(4)).smaller().equal();
        tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(3)).smaller();
        tExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(3)).smaller().equal();
        tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(2)).smaller().equal();
        tExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);
        q = Test.query();

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(2)).smaller();
        tExpectInts(q, new int[] { 1 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(1)).smaller().equal();
        tExpectInts(q, new int[] { 1 }, expectedZeroSize);

        q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_int").constrain(new Integer(1)).smaller();
        tExpectInts(q, new int[] {
        }, expectedZeroSize);

    }

    private void tExpectInts(Query q, int[] ints, int zeroSize) {
        ObjectSet res = q.execute();
        Test.ensure(res.size() == (ints.length + zeroSize));
        while (res.hasNext()) {
            CreateIndex ci = (CreateIndex)res.next();
            for (int i = 0; i < ints.length; i++) {
                if (ints[i] == ci.i_int) {
                    ints[i] = 0;
                    break;
                }
            }
        }
        for (int i = 0; i < ints.length; i++) {
            Test.ensure(ints[i] == 0);
        }
    }

    private void tExpectInts(Query q, int[] ints) {
        tExpectInts(q, ints, 0);
    }

    private void tQueryB() {
        ObjectSet res = query("b");
        Test.ensure(res.size() == 1);
        CreateIndex ci = (CreateIndex)res.next();
        Test.ensure(ci.i_name.equals("b"));
    }
    
    private void tQueryNull(int expect) {
        ObjectSet res = query(null);
        Test.ensure(res.size() == expect);
        while(res.hasNext()){
            CreateIndex ci = (CreateIndex)res.next();
            Test.ensure(ci.i_name == null);
        }
    }

    private void tUpdateB() {
        ObjectSet res = query("b");
        CreateIndex ci = (CreateIndex)res.next();
        ci.i_name = "j";
        Test.objectContainer().store(ci);
        res = query("b");
        Test.ensure(res.size() == 0);
        res = query("j");
        Test.ensure(res.size() == 1);
        ci.i_name = "b";
        Test.objectContainer().store(ci);
        tQueryB();
    }

    private ObjectSet query(String n) {
        Query q = Test.query();
        q.constrain(CreateIndex.class);
        q.descend("i_name").constrain(n);
        return q.execute();
    }

}
