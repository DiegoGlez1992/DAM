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
package com.db4o.db4ounit.common.querying;

import com.db4o.config.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnUpdateTestCase extends AbstractDb4oTestCase {
    
    public static class Holder {
    	public Object child;
    	
    	public Holder(Object child) {
    		this.child = child;
    	}
    }
    
	public static class Atom {
		
		public Atom child;
		public String name;
		
		public Atom(){
		}
		
		public Atom(Atom child){
			this.child = child;
		}
		
		public Atom(String name){
			this.name = name;
		}
		
		public Atom(Atom child, String name){
			this(child);
			this.name = name;
		}
	}

	public Object child;

	protected void configure(Configuration conf) {
		conf.objectClass(Holder.class).cascadeOnUpdate(true);
	}

	protected void store() {
		Holder cou = new Holder(new Atom(new Atom("storedChild"), "stored"));
		db().store(cou);
	}

	public void test() throws Exception {
		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				Holder cou = (Holder) obj;
				((Atom)cou.child).name = "updated";
				((Atom)cou.child).child.name = "updated";
				db().store(cou);
			}
		});
		
		reopen();
		
		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				Holder cou = (Holder) obj;
				Atom atom = (Atom)cou.child;
				Assert.areEqual("updated", atom.name);
				Assert.areNotEqual("updated", atom.child.name);
			}
		});
	}
}
