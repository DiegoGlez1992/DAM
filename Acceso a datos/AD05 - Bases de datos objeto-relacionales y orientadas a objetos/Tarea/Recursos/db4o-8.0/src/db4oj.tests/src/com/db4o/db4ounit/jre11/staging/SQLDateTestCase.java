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
package com.db4o.db4ounit.jre11.staging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
public class SQLDateTestCase extends AbstractDb4oTestCase {

	public static class Item {
		public java.sql.Date _date;

		public Item(java.sql.Date date) {
			_date = date;
		}
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(java.util.Date.class).storeTransientFields(true);
		config.objectClass(java.sql.Date.class).storeTransientFields(true);
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(java.sql.Date.class), new DateHandlerBase() {
			@Override
			public Object copyValue(Object from, Object to) {
				((java.sql.Date)to).setTime(((java.sql.Date)from).getTime());
				return to;
			}

			@Override
			public Object defaultValue() {
				return new java.sql.Date(0);
			}

			@Override
			public Object nullRepresentationInUntypedArrays() {
				return new java.sql.Date(0);
			}
			
			@Override
			public Object primitiveNull() {
				return null;
			}
		});
	}
	
	@Override
	protected void store() throws Exception {
		store(new java.sql.Date(1000));
		store(new java.sql.Date(2000));
	}
	
	public void testRetrieveByExactTime() {
		Query query = newQuery(Item.class);
		query.descend("_date").constrain(new java.sql.Date(1000));
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(1, result.size());
		Assert.areEqual(new java.sql.Date(1000), result.next()._date);
	}
}
