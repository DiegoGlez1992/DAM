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

import com.db4o.ext.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TestListInMap {

	public Map map;
	
	public void storeOne() {
	    ExtObjectContainer db = Test.objectContainer();
		List list = new LinkedList();
		list.add("ListEntry 1");
		db.store(list);
		map = new HashMap(); 			
		map.put("1", list);
	}
	
	public void testOne() {
	    List list = (List) map.get("1");
	    Object obj = list.get(0);
		System.out.println(obj);
	}
}

