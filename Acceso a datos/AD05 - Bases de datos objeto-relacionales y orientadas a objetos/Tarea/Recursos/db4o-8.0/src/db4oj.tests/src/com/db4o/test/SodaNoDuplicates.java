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

public class SodaNoDuplicates {
	
	public Atom atom;
	
	public void store(){
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		Atom m1 = new Atom("One");
		Atom m2 = new Atom("Two");
		SodaNoDuplicates  snd = new SodaNoDuplicates();
		snd.atom = m1;
		Test.store(snd);
		snd = new SodaNoDuplicates();
		snd.atom = m1;
		Test.store(snd);
		snd = new SodaNoDuplicates();
		snd.atom = m2;
		Test.store(snd);
		snd = new SodaNoDuplicates();
		snd.atom = m2;
		Test.store(snd);
	}
	
	public void test(){
		Query q = Test.query();
		q.constrain(SodaNoDuplicates.class);
		Query qAtoms = q.descend("atom");
		ObjectSet set = qAtoms.execute();
		Test.ensure(set.size() == 2);
	}
	

}
