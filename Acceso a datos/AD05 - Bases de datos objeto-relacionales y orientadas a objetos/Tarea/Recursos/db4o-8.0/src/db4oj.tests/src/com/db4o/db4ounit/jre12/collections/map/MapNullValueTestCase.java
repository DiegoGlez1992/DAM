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

// COR-404
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class MapNullValueTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public Map _map;

		public Data(Map _map) {
			this._map = _map;
		}
	}

	private static final String KEY = "KEY";
	
	protected void store() throws Exception {
		Map map=new HashMap();
		map.put(KEY,null);
		Data data=new Data(map);
		Assert.isTrue(data._map.containsKey(KEY));
		store(data);
	}
	
	public void testNullValueIsPersisted() {
		Data data=(Data)retrieveOnlyInstance(Data.class);
		Assert.isTrue(data._map.containsKey(KEY));
	}
	
}
