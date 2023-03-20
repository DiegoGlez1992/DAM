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

public class CascadeToExistingVectorMember {
	
	public Vector vec;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
	}
	
	public void store(){
		Test.deleteAllInstances(new Atom());
		Test.deleteAllInstances(this);
		CascadeToExistingVectorMember cev = new CascadeToExistingVectorMember();
		cev.vec = new Vector();
		Atom atom = new Atom("one");
		Test.store(atom);
		cev.vec.addElement(atom);
		Test.store(cev);
	}
	
	public void test(){
		Test.forEach(new CascadeToExistingVectorMember(), new Visitor4() {
            public void visit(Object obj) {
            	CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember)obj;
            	Atom atom = (Atom)cev.vec.elementAt(0);
            	atom.name = "two";
            	Test.store(cev);
            	atom.name = "three";
            	Test.store(cev);
            }
        });
        
        Test.reOpen();
        
        Test.forEach(new CascadeToExistingVectorMember(), new Visitor4() {
            public void visit(Object obj) {
            	CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember)obj;
            	Atom atom = (Atom)cev.vec.elementAt(0);
            	Test.ensure(atom.name.equals("three"));
            	Test.ensureOccurrences(atom, 1);
            }
        });
        
        Test.forEach(new CascadeToExistingVectorMember(), new Visitor4() {
            public void visit(Object obj) {
            	CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember)obj;
            	Atom atom = (Atom)cev.vec.elementAt(0);
            	atom.name = "four";
            	Test.store(cev);
            }
        });
        
        
        Test.reOpen();
        
        Test.forEach(new CascadeToExistingVectorMember(), new Visitor4() {
            public void visit(Object obj) {
            	CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember)obj;
            	Atom atom = (Atom)cev.vec.elementAt(0);
            	Test.ensure(atom.name.equals("four"));
            	Test.ensureOccurrences(atom, 1);
            }
        });
        
        
	}
}
