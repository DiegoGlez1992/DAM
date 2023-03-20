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

import com.db4o.foundation.*;
import com.db4o.test.*;

public class TypedArrayInObject {
	
	public Object obj;
	public Object[] obj2;
	
	public void store(){
		Test.deleteAllInstances(this);
		TypedArrayInObject taio = new TypedArrayInObject();
		Atom[] mols = new Atom[1];
		mols[0] = new Atom("TypedArrayInObject"); 
		taio.obj = mols;
		taio.obj2 = mols;
		Test.store(taio);
	}
	
	public void test(){
		Test.forEach(new TypedArrayInObject(), new Visitor4() {
            public void visit(Object a_obj) {
            	TypedArrayInObject taio = (TypedArrayInObject)a_obj;
            	Test.ensure(taio.obj instanceof Atom[]);
            	Test.ensure(taio.obj2 instanceof Atom[]);
            	
            }
        });
	}
}
