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
package com.db4o.db4ounit.common.soda;

import java.util.*;

import com.db4o.query.*;

import db4ounit.extensions.*;

/**
 * @exclude
 */
public class CollectIdTestCase extends AbstractDb4oTestCase{
	
	public static class ListHolder {
		
		public List _list;
		
	}
	
	public static class Parent {
		
		public Child _child;
		
	}
	
	public static class Child {
		
		public String _name;
		
	}
	
	
	protected void store() throws Exception {
		ListHolder holder = new ListHolder();
		holder._list = new ArrayList();
		Parent parent = new Parent();
		holder._list.add(parent);
		parent._child = new Child();
		parent._child._name = "child";
		store(holder);
	}
	
	public void test(){
		Query query = newQuery(ListHolder.class);
		Query qList = query.descend("_list");
		// qList.execute();
		Query qChild = qList.descend("_child");
		qChild.execute();
		// Query qName = qList.descend("_name");
		
	}

}
