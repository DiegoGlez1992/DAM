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
import com.db4o.foundation.*;

public class CascadeToVector {

	public Vector vec;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store() {
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		CascadeToVector ctv = new CascadeToVector();
		ctv.vec = new Vector();
		ctv.vec.addElement(new Atom("stored1"));
		ctv.vec.addElement(new Atom(new Atom("storedChild1"), "stored2"));
		Test.store(ctv);
	}

	public void test() {

		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToVector ctv = (CascadeToVector) obj;
                Enumeration i = ctv.vec.elements();
				while(i.hasMoreElements()){
					Atom atom = (Atom) i.nextElement();
					atom.name = "updated";
					if(atom.child != null){
						// This one should NOT cascade
						atom.child.name = "updated";
					}
				}
				Test.store(ctv);
			}
		});
		Test.reOpen();
		
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToVector ctv = (CascadeToVector) obj;
                Enumeration i = ctv.vec.elements();
                while(i.hasMoreElements()){
                    Atom atom = (Atom) i.nextElement();
					Test.ensure(atom.name.equals("updated"));
					if(atom.child != null){
						Test.ensure( ! atom.child.name.equals("updated"));
					}
				}
			}
		});
		
		
		// Cascade-On-Delete Test: We only want one atom to remain.
		
		Test.reOpen();
		Test.deleteAllInstances(this);
		Test.ensureOccurrences(new Atom(), 1);
	}
}
