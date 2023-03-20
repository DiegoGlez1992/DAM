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
package com.db4o.db4ounit.common.staging;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * COR-1003
 * If the Activator is not declared as transient,
 * it will get restored as null from the database
 * when activation happens.
 */
public class ActivatorIsNotStoredTestCase extends AbstractDb4oTestCase{
	
	public class Item implements Activatable {
		
		public String _name;
		
		public Activator _activator;

		public void activate(ActivationPurpose purpose) {
			if(_activator != null) {
				_activator.activate(purpose);
			}
		}

		public void bind(Activator activator) {
	    	if (_activator == activator) {
	    		return;
	    	}
	    	if (activator != null && _activator != null) {
	            throw new IllegalStateException();
	        }
			_activator = activator;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		config.add(new TransparentActivationSupport());
	}
	
	@Override
	protected void store() throws Exception {
		Item item = new Item();
		item._name = "one";
		store(item);
	}
	
	public void test(){
		Item item = retrieveOnlyInstance(Item.class);
		item.activate(ActivationPurpose.WRITE);
		Assert.isNotNull(item._activator);
		
		store(item);
		Query q = newQuery(Activator.class);
		Assert.areEqual(0, q.execute().size());
	}

}
