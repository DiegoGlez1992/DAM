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

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class KeepCollectionContent {
	
	public void store(){
		Test.deleteAllInstances(new ComparableAtom());
		Test.deleteAllInstances(new HashMap());
		Test.deleteAllInstances(new Hashtable());
		Test.deleteAllInstances(new ArrayList());
		Test.deleteAllInstances(new Vector());
		Test.deleteAllInstances(new TreeMap());
		HashMap hm = new HashMap();
		hm.put(new ComparableAtom(), new ComparableAtom());
		Test.store(hm);
		Hashtable ht = new Hashtable();
		ht.put(new ComparableAtom(), new ComparableAtom());
		Test.store(ht);
		ArrayList al = new ArrayList();
		al.add(new ComparableAtom());
		Test.store(al);
		Vector vec = new Vector();
		vec.add(new ComparableAtom());
		Test.store(vec);
		TreeMap tm = new TreeMap();
		tm.put(new ComparableAtom(), new ComparableAtom());
		Test.store(tm);
		Test.commit();
	}
	
	public void test(){
		Test.deleteAllInstances(new HashMap());
		Test.deleteAllInstances(new Hashtable());
		Test.deleteAllInstances(new ArrayList());
		Test.deleteAllInstances(new Vector());
		Test.deleteAllInstances(new TreeMap());
		Test.commit();
		Test.ensureOccurrences(new ComparableAtom(), 8);
	}

}
