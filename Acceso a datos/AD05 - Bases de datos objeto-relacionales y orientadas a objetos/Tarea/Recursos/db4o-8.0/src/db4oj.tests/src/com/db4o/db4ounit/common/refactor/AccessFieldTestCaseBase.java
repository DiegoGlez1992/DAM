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
package com.db4o.db4ounit.common.refactor;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;

import db4ounit.*;

public abstract class AccessFieldTestCaseBase extends Db4oTestWithTempFile {

	public void setUp() throws Exception {
		withDatabase(new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				db.store(newOriginalData());
			}
		});
	}

	protected void renameClass(Class origClazz, String targetName) {
		EmbeddedConfiguration config = newConfiguration();
		config.common().objectClass(origClazz).rename(targetName);
		withDatabase(config, new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				// do nothing
			}
		});
	}

	protected abstract Object newOriginalData();

	protected <T, F> void assertField(final Class<T> targetClazz, final String fieldName, final Class<F> fieldType,
			final F fieldValue) {
				withDatabase(new DatabaseAction() {
					public void runWith(ObjectContainer db) {
						StoredClass storedClass = db.ext().storedClass(targetClazz);
						StoredField storedField = storedClass.storedField(fieldName, fieldType);
						ObjectSet<T> result = db.query(targetClazz);
						Assert.areEqual(1, result.size());
						T obj = result.next();
						F value = (F)storedField.get(obj);
						Assert.areEqual(fieldValue, value);
					}
				});
			}

	private static interface DatabaseAction {
		void runWith(ObjectContainer db);
	}

	private void withDatabase(DatabaseAction action) {
		withDatabase(newConfiguration(), action);
	}

	private void withDatabase(EmbeddedConfiguration config, DatabaseAction action) {
		ObjectContainer db = Db4oEmbedded.openFile(config, tempFile());
		try {
			action.runWith(db);
		}
		finally {
			db.close();
		}
	}

}