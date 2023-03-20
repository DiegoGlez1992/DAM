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
package com.db4o.db4ounit.common.uuid;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteUUIDTestCase extends AbstractDb4oTestCase {

	private Db4oUUID _uuid;
	
	public static class Item {
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.generateUUIDs(ConfigScope.GLOBALLY);
	}

	@Override
	protected void store() throws Exception {
		Item item = new Item();
		store(item);
		_uuid = db().getObjectInfo(item).getUUID();
	}
	
	public void testDelete() throws Exception {
		Item item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		Assert.isNull(db().getByUUID(_uuid));
	}

	public void testDeleteCommit() throws Exception {
		Item item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().commit();
		Assert.isNull(db().getByUUID(_uuid));
	}

	public void testDeleteRollback() throws Exception {
		Item item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().rollback();
		Assert.isNotNull(db().getByUUID(_uuid));
	}

}
