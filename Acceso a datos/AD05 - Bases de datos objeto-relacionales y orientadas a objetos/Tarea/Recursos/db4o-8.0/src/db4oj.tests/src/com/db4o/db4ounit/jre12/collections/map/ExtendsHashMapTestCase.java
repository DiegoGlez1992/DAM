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
package com.db4o.db4ounit.jre12.collections.map;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ExtendsHashMapTestCase extends AbstractDb4oTestCase {

	public static class ExtendsHashMap extends HashMap {
	}

	protected void store() throws Exception {
		Map map = new ExtendsHashMap();
		map.put(new Integer(1), "one");
		map.put(new Integer(2), "two");
		map.put(new Integer(3), "three");
		store(map);
	}
	
	public void test(){
		ExtendsHashMap ehm = (ExtendsHashMap)retrieveOnlyInstance(ExtendsHashMap.class);
		Assert.areEqual("one", ehm.get(new Integer(1)));
		Assert.areEqual("two", ehm.get(new Integer(2)));
		Assert.areEqual("three", ehm.get(new Integer(3)));
	}

}
