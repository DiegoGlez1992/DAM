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
package com.db4o.test.cluster;

import java.io.*;

import com.db4o.*;
import com.db4o.cluster.*;
import com.db4o.query.*;
import com.db4o.test.Test;

import db4ounit.*;


public class BasicClusterTest {
    
    public String _name;
    
    public static final String SECOND_FILE = "second.db4o";
    
    public BasicClusterTest(){
        
    }
    
    public BasicClusterTest(String name){
        _name = name;
    }
    
    public void store(){
        new File(SECOND_FILE).delete();
        Test.store(new BasicClusterTest("inOne"));
        Test.store(new BasicClusterTest("inBoth"));
        ObjectContainer second = Db4o.openFile(SECOND_FILE);
        second.store(new BasicClusterTest("inBoth"));
        second.store(new BasicClusterTest("inTwo"));
        second.close();
    }
    
    public void testConstrained(){
        ObjectContainer second = Db4o.openFile(SECOND_FILE);
        Cluster cluster = new Cluster(new ObjectContainer[]{
            Test.objectContainer(),
            second
        });
        tQuery(cluster, "inOne", 1);
        tQuery(cluster, "inTwo", 1);
        tQuery(cluster, "inBoth", 2);
        second.close();
    }

    public void testPlain(){
        ObjectContainer second = Db4o.openFile(SECOND_FILE);
        Cluster cluster = new Cluster(new ObjectContainer[]{
            Test.objectContainer(),
            second
        });
        Query q = cluster.query();
        q.constrain(this.getClass());
        ObjectSet result=q.execute();
        Test.ensure(result.size() == 4);
        while(result.hasNext()) {
        	Test.ensure(result.next() instanceof BasicClusterTest);
        }
        second.close();
    }

    private void tQuery(Cluster cluster, String name, int expected){
        Query q = cluster.query();
        q.constrain(this.getClass());
        q.descend("_name").constrain(name);
        ObjectSet result=q.execute();
        Assert.areEqual(expected, result.size());
        while(result.hasNext()) {
        	Test.ensure(result.next() instanceof BasicClusterTest);
        }
    }
    
    

}
