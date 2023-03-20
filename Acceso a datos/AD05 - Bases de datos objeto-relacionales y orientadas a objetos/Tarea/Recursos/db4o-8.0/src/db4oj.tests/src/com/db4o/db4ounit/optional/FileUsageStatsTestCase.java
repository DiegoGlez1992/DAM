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
package com.db4o.db4ounit.optional;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.filestats.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

/** @sharpen.if !SILVERLIGHT */
@decaf.Remove(decaf.Platform.JDK11)
public class FileUsageStatsTestCase extends Db4oTestWithTempFile {

	public static class Child {
	}
	
	public static class Item {
		public int _id;
		public String _name;
		public int[] _arr;
		public List<Child> _list;
		
		public Item(int id, String name, List<Child> list) {
			_id = id;
			_name = name;
			_arr = new int[]{ id };
			_list = list;
		}
	}
	
	public void testFileStats() throws Exception {
		createDatabase(new ArrayList<Slot>());
		assertFileStats();
		defrag();
		assertFileStats();
	}

	private void assertFileStats() {
		FileUsageStats stats = FileUsageStatsCollector.runStats(tempFile(), true, newConfiguration());
		Assert.areEqual(stats.fileSize(), stats.totalUsage(), stats.toString());
	}

	private void defrag() throws IOException {
		String backupPath = Path4.getTempFileName();
		DefragmentConfig config = new DefragmentConfig(tempFile(), backupPath);
		config.forceBackupDelete(true);
		Defragment.defrag(config);
		delete(backupPath);
	}

	private void createDatabase(final List<Slot> gaps) throws IOException {
		delete(tempFile());
		
		EmbeddedConfiguration config = newConfiguration();		
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, tempFile());
		List<Child> list = new ArrayList<Child>();
		list.add(new Child());
		Item item = new Item(0, "#0", list);
		db.store(item);
		db.commit();
		db.close();
	}

	private void delete(String file) throws IOException {
		EmbeddedConfiguration config = newConfiguration();
		config.file().storage().delete(file);
	}

	protected EmbeddedConfiguration newConfiguration() {
		EmbeddedConfiguration config = super.newConfiguration();
		
		config.common().objectClass(Item.class).objectField("_id").indexed(true);
		config.common().objectClass(Item.class).objectField("_name").indexed(true);
		config.file().generateUUIDs(ConfigScope.GLOBALLY);
		config.file().generateCommitTimestamps(true);
		return config;
	}
	
}
