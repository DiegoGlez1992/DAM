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

public class DualDelete {
	
	public Atom atom;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
	}
	
	public void store(){
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		DualDelete dd1 = new DualDelete();
		dd1.atom = new Atom("justone");
		Test.store(dd1);
		DualDelete dd2 = new DualDelete();
		dd2.atom = dd1.atom;
		Test.store(dd2);
	}
	
	public void test(){
		Test.deleteAllInstances(this);
		Test.ensureOccurrences(new Atom(), 0);
		Test.rollBack();
		Test.ensureOccurrences(new Atom(), 1);
		Test.deleteAllInstances(this);
		Test.ensureOccurrences(new Atom(), 0);
		Test.commit();
		Test.ensureOccurrences(new Atom(), 0);
		Test.rollBack();
		Test.ensureOccurrences(new Atom(), 0);
	}
	

}
