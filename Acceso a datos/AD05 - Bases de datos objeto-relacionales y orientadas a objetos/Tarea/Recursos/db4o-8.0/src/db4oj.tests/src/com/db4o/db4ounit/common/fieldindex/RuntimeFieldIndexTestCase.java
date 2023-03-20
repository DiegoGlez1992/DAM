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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class RuntimeFieldIndexTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	private static final String FIELDNAME = "_id";

	public static class Data {
		public int _id;

		public Data(int id) {
			_id = id;
		}		
	}
	
	protected void store() throws Exception {
		for(int i=1; i <= 3; i++) {
			store(new Data(i));
		}
	}
	
	public void testCreateIndexAtRuntime() {
		StoredField field = storedField();
		Assert.isFalse(field.hasIndex());
		field.createIndex();
		Assert.isTrue(field.hasIndex());
		assertQuery();
		field.createIndex(); // ensure that second call is ignored
	}

	private void assertQuery() {
		Query query = newQuery(Data.class);
		query.descend(FIELDNAME).constrain(new Integer(2));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
	}
	
	public void testDropIndex(){
		StoredField field = storedField();
		field.createIndex();
		assertQuery();
		field.dropIndex();
		Assert.isFalse(field.hasIndex());
		assertQuery();
	}

	private StoredField storedField() {
		return db().storedClass(Data.class).storedField(FIELDNAME,null);
	}

}
