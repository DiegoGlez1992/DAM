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

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;


public class Backup {
    
    static int allAtomCount;
    static int specialAtomCount;
    
    static final String FILE = "backuptest.db4o";
    static final String NAME = "backuptest";
    
    public void store(){
        if(! Test.isClientServer()){
	        new File(FILE).delete();
	        Test.store(new Atom(NAME));
	        Test.commit();
	        
	        Query q = Test.query();
	        q.constrain(Atom.class);
	        allAtomCount = q.execute().size();
	        q = Test.query();
	        q.constrain(Atom.class);
	        q.descend("name").constrain(NAME);
	        specialAtomCount = q.execute().size();
	        Test.objectContainer().ext().backup(FILE);
        }
    }
    
    public void test(){
        if(! Test.isClientServer()){
	        ObjectContainer objectContainer = Db4o.openFile(FILE);
	        Query q = objectContainer.query();
	        q.constrain(Atom.class);
	        Test.ensure(allAtomCount == q.execute().size());
	        q = Test.query();
	        q.constrain(Atom.class);
	        q.descend("name").constrain(NAME);
	        ObjectSet objectSet = q.execute();
	        Test.ensure(objectSet.size() == specialAtomCount);
	        Atom atom = (Atom)objectSet.next();
	        Test.ensure(atom.name.equals(NAME));
	        objectContainer.close();
        }
    }
    
    
}
