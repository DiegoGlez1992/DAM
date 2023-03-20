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
package com.db4o.db4ounit.common.backup;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public abstract class MemoryBackupTestCaseBase extends TestWithTempFile {

	public static class Item {
		public int _id;
	
		public Item(int id) {
			_id = id;
		}
	}

	private static final String DB_PATH = "database";
	private static final int NUM_ITEMS = 10;
	 
	public void testMemoryBackup() throws Exception {
		LocalObjectContainer origDb = (LocalObjectContainer) Db4oEmbedded.openFile(config(origStorage()), DB_PATH);
		store(origDb);
		backup(origDb, tempFile());
		origDb.close();
	
		ObjectContainer backupDb = Db4oEmbedded.openFile(config(backupStorage()), tempFile());
		ObjectSet<Item> result = backupDb.query(Item.class);
		Assert.areEqual(NUM_ITEMS, result.size());
		backupDb.close();
		backupStorage().delete(tempFile());
	}

	protected abstract void backup(LocalObjectContainer origDb, String backupPath);

	protected abstract Storage backupStorage();

	protected abstract Storage origStorage();

	private void store(LocalObjectContainer origDb) {
		for(int itemId = 0; itemId < NUM_ITEMS; itemId++) {
			origDb.store(new Item(itemId));
		}
		origDb.commit();
	}

	private EmbeddedConfiguration config(Storage storage) {
		EmbeddedConfiguration origConfig = Db4oEmbedded.newConfiguration();
		origConfig.file().storage(storage);
		return origConfig;
	}

}