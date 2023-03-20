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
package com.db4o.db4ounit.jre12.assorted;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class FinalFieldTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		public final int fi;
		public final String fs;
		public int i;
		public String s;
		
		public Item() {
			fi = 0;
			fs = "";
		}
		
		public Item(int i, String s) {
			this.i = this.fi = i;
			this.s = this.fs = s;
		}
	}
	
	protected void store() {
		db().store(new Item(42, "jb"));
	}
	
	public void _testFinalField() {
		Item i = (Item)retrieveOnlyInstance(Item.class);
		Assert.areEqual(42, i.i);
		Assert.areEqual(42, i.fi);
		Assert.areEqual("jb", i.s);
		Assert.areEqual("jb", i.fs);
	}
	
	public static void main(String[] args) {
		new FinalFieldTestCase().runSolo();
	}
}
