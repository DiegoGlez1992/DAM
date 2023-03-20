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
package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteStringInUntypedFieldTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] args) {
		new DeleteStringInUntypedFieldTestCase().runAll();
	}
	
	public static class Item{
		
		public Item _firstChild;
		
		public String _name;
		
		public Object _untypedName;
		
		public Item _secondChild;
		
		public Item(String name){
			_name = name;
			_untypedName = name;
		}
		
	}
	
	public static class DeleteListener implements DiagnosticListener{
		
		public boolean _called;

		public void onDiagnostic(Diagnostic d) {
			if(d instanceof DeletionFailed){
				_called = true;
			}
		}
	}
	
	private DeleteListener _listener;
	
	@Override
	protected void store() throws Exception {
		Item item = new Item("root");
		item._firstChild = new Item("first");
		item._secondChild = new Item("second");
		store(item);
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnDelete(true);
		_listener = new DeleteListener();
		config.diagnostic().addListener(_listener);
	}
	
	public void test(){
		Query q = itemQuery();
		q.descend("_name").constrain("root");
		ObjectSet<Item> objectSet = q.execute();
		Item item = objectSet.next();
		db().delete(item);
		Assert.isFalse(_listener._called);
		Assert.areEqual(0, itemQuery().execute().size());
	}

	private Query itemQuery() {
		Query q = db().query();
		q.constrain(Item.class);
		return q;
	}
	

	

}
