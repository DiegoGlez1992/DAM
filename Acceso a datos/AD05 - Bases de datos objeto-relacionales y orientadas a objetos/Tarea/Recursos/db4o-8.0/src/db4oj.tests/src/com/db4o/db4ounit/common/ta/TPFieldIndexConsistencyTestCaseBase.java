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
package com.db4o.db4ounit.common.ta;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public abstract class TPFieldIndexConsistencyTestCaseBase extends AbstractDb4oTestCase {

	protected static final String ID_FIELD_NAME = "_id";

	public static class Item implements Activatable {
		public int _id;
		private transient Activator _activator;

		public Item(int id) {
			_id = id;
		}
		
		public int id() {
			activate(ActivationPurpose.READ);
			return _id;
		}
		
		public void id(int id) {
			activate(ActivationPurpose.WRITE);
			_id = id;
		}

		public void activate(ActivationPurpose purpose) {
			if(_activator != null) {
				_activator.activate(purpose);
			}
		}

		public void bind(Activator activator) {
			if(_activator != null && activator != null && _activator != activator) {
				throw new IllegalStateException();
			}
			_activator = activator;
		}
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
		config.objectClass(Item.class).objectField(ID_FIELD_NAME).indexed(true);
	}
	
	protected void assertFieldIndex(int id) {
		ReflectClass claxx = reflector().forClass(Item.class);
		ClassMetadata classMetadata = fileSession().classMetadataForReflectClass(claxx);
		FieldMetadata field = classMetadata.fieldMetadataForName(ID_FIELD_NAME);
		BTreeRange indexRange = field.search(trans(), id);
		Assert.areEqual(1, indexRange.size());
	}

	protected void assertItemQuery(int id) {
		Query query = newQuery(Item.class);
		query.descend(ID_FIELD_NAME).constrain(id);
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(1, result.size());
		Assert.areEqual(id, result.next().id());
	}

}
