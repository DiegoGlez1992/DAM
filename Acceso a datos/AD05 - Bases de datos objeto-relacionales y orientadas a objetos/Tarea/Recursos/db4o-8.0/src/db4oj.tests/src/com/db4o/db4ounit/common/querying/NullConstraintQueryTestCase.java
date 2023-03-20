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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NullConstraintQueryTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] args) {
		new NullConstraintQueryTestCase().runAll();
	}
	
	private final class LoadedFromClassIndexListener implements DiagnosticListener {
		public void onDiagnostic(Diagnostic d) {
			if(d instanceof LoadedFromClassIndex){
				Assert.fail("Query should not be loaded from class index");
			}
		}
	}

	public static class ObjectItem{
		
		public String _name;
		
		public ObjectItem _child;
		
		public ObjectItem(ObjectItem child, String name){
			_child = child;
			_name = name;
		}
	}
	
	public static class StringItem{
		
		public String _name;
		
		public StringItem(String name){
			_name = name;
		}
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.diagnostic().addListener(new LoadedFromClassIndexListener());
		config.objectClass(ObjectItem.class).objectField("_child").indexed(true);
		config.objectClass(StringItem.class).objectField("_name").indexed(true);
	}
	
	@Override
	protected void store() throws Exception {
		ObjectItem childItem = new ObjectItem(null, "child");
		ObjectItem parentItem = new ObjectItem(childItem, "parent");
		store(parentItem);
		
		store(new StringItem(null));
		store(new StringItem(null));
		store(new StringItem("one"));
		store(new StringItem("two"));
	}
	
	public void testQueryForNullChild(){
		Query q = newQuery(ObjectItem.class);
		q.descend("_child").constrain(null);
		ObjectSet<ObjectItem> objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		ObjectItem item = objectSet.next();
		Assert.areEqual("child", item._name);
	}
	
	public void testQueryForNullString(){
		Query q = newQuery(StringItem.class);
		q.descend("_name").constrain(null);
		ObjectSet<StringItem> objectSet = q.execute();
		Assert.areEqual(2, objectSet.size());
		StringItem item = objectSet.next();
		Assert.isNull(item._name);
		item = objectSet.next();
		Assert.isNull(item._name);
	}
	
}
