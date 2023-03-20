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

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TestHashMap {
	
	HashMap hm;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}
	
	public void store(){
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		Test.deleteAllInstances(new com.db4o.config.Entry());
		TestHashMap thm = new TestHashMap();
		thm.hm = new HashMap();
		thm.hm.put("t1", new Atom("t1"));
		thm.hm.put("t2", new Atom("t2"));
		Test.store(thm);
	}
	
	public void test(){
		com.db4o.config.Entry checkEntries = new com.db4o.config.Entry();
		TestHashMap thm = (TestHashMap)Test.getOne(this);
		Test.ensure(thm.hm.size() == 2);
		Test.ensure(thm.hm.get("t1").equals(new Atom("t1")));
		Test.ensure(thm.hm.get("t2").equals(new Atom("t2")));
		thm.hm.put("t2", new Atom("t3"));
		Test.store(thm);
		// System.out.println(Test.occurrences(checkEntries));
		if(Test.COMPARE_INTERNAL_OK){
			Test.ensureOccurrences(checkEntries, 2);
			Test.commit();
			Test.ensureOccurrences(checkEntries, 2);
			Test.deleteAllInstances(this);
			Test.ensureOccurrences(checkEntries, 0);
			Test.rollBack();
			Test.ensureOccurrences(checkEntries, 2);
			Test.deleteAllInstances(this);
			Test.ensureOccurrences(checkEntries, 0);
			Test.commit();
			Test.ensureOccurrences(checkEntries, 0);
		}
	}
}
