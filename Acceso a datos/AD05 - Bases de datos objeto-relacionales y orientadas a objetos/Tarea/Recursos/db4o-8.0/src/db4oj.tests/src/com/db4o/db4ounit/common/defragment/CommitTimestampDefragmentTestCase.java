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
package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;

public class CommitTimestampDefragmentTestCase extends DefragmentTestCaseBase {

	public static class Item {
	}

	public void testKeepingBtrees() throws IOException {

		EmbeddedConfiguration config = newConfiguration();
		config.file().generateCommitTimestamps(true);

		long version = storeItemAndGetCommitTimestamp(config);

		Assert.isGreater(0, version);

		defrag(TernaryBool.UNSPECIFIED);

		assertVersionAfterDefrag(version, null);

	}

	public void testRemovingBtrees() throws IOException {

		EmbeddedConfiguration config = newConfiguration();
		config.file().generateCommitTimestamps(true);

		long version = storeItemAndGetCommitTimestamp(config);

		Assert.isGreater(0, version);

		defrag(TernaryBool.NO);

		EmbeddedConfiguration afterDefragConfig = null;

		assertVersionAfterDefrag(0, afterDefragConfig);

	}

	public void testTurningOnGenerateCommitTimestampInDefrag() throws IOException {

		EmbeddedConfiguration config = newConfiguration();

		long version = storeItemAndGetCommitTimestamp(config);

		Assert.areEqual(0, version);

		defrag(TernaryBool.YES);

		EmbeddedConfiguration afterDefragConfig = null;

		assertVersionAfterDefrag(0, afterDefragConfig);

	}

	private void assertVersionAfterDefrag(long version, EmbeddedConfiguration afterDefragConfig) {
		EmbeddedObjectContainer db = openContainer(afterDefragConfig);

		Item retrievedItem = db.query(Item.class).next();
		long retrievedVersion = db.ext().getObjectInfo(retrievedItem).getCommitTimestamp();

		Assert.areEqual(version, retrievedVersion);

		db.close();
	}

	private long storeItemAndGetCommitTimestamp(EmbeddedConfiguration config) {
		EmbeddedObjectContainer db = openContainer(config);
		Item item = new Item();
		db.store(item);
		db.commit();
		long commitTimestamp = db.ext().getObjectInfo(item).getCommitTimestamp();
		db.close();
		return commitTimestamp;
	}

	private void defrag(TernaryBool generateCommitTimestamp) throws IOException {
		DefragmentConfig config = new DefragmentConfig(sourceFile(), backupFile());
		config.db4oConfig(newConfiguration());
		config.forceBackupDelete(true);

		if (!generateCommitTimestamp.isUnspecified()) {
			config.db4oConfig().generateCommitTimestamps(generateCommitTimestamp.definiteYes());
		}

		Defragment.defrag(config);
	}

	private EmbeddedObjectContainer openContainer(EmbeddedConfiguration config) {
		if(config == null) {
			config = newConfiguration();
		}
		config.common().reflectWith(Platform4.reflectorForType(Item.class));
		return config == null ? Db4oEmbedded.openFile(sourceFile()) : Db4oEmbedded.openFile(config, sourceFile());
	}

}
