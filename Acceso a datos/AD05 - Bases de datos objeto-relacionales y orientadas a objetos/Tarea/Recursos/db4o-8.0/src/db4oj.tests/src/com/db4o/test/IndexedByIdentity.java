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
public class IndexedByIdentity {
    
    public Atom atom;
    
    static final int COUNT = 10;
    
    public void configure(){
        Db4o.configure().objectClass(this).objectField("atom").indexed(true);
    }
    
    public void store(){
        for (int i = 0; i < COUNT; i++) {
            IndexedByIdentity ibi = new IndexedByIdentity();
            ibi.atom = new Atom("ibi" + i);
            Test.store(ibi);
        } 
    }
    
    public void test(){
        readAndUpdate("ibi");
        readAndUpdate("updated");
        
    }
    
    private void readAndUpdate(String atomName){
        for (int i = 0; i < COUNT; i++) {
            Query q = Test.query();
            q.constrain(Atom.class);
            q.descend("name").constrain(atomName + i);
            ObjectSet objectSet = q.execute();
            Atom child = (Atom)objectSet.next();
            // child.name = "rnzelbrnft";
            q = Test.query();
            q.constrain(IndexedByIdentity.class);
            q.descend("atom").constrain(child).identity();
            objectSet = q.execute();
            Test.ensure(objectSet.size() == 1);
            IndexedByIdentity ibi = (IndexedByIdentity)objectSet.next();
            Test.ensure(ibi.atom == child);
            ibi.atom = new Atom("updated" + i);
            Test.store(ibi);
        }
        
        
    }
    
    
    
    
}
