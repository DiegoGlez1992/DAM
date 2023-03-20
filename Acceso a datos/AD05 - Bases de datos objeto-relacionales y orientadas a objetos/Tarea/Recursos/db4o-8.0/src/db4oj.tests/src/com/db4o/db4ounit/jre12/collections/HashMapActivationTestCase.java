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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class HashMapActivationTestCase extends AbstractDb4oTestCase{
	
	public static class Holder{
		 public HashMap _hashmap;
	}
	
	protected void store() throws Exception {
		Holder holder = new Holder();
		holder._hashmap = new HashMap();
		holder._hashmap.put("key", "value");
		store(holder);
	}
	
	protected void configure(Configuration config) throws Exception {
		config.activationDepth(1);
	}
	
	public void test(){
		Holder holder = (Holder) retrieveOnlyInstance(Holder.class);
		db().activate(holder,2);
		db().activate(holder._hashmap, Integer.MAX_VALUE);
		Assert.areEqual(1, holder._hashmap.size());
	}

}
