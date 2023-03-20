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
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;

public class BlockSizeDefragTestCase extends Db4oTestWithTempFile {


	public static class ItemA {
		public int _id;

		public ItemA(int id) {
			_id = id;
		}
		
		public String toString() {
			return "A" + _id;
		}
	}

	public static class ItemB {
		public String _name;

		public ItemB(String name) {
			_name = name;
		}

		public String toString() {
			return "A" + _name;
		}
	}

	private final static int[] BLOCK_SIZES = { 1, 2, 3, 4, 7, 8, 13, 16 };
	private final static int NUM_ITEMS_PER_CLASS = 10;
	private final static int DELETE_RATIO = 3;
	
	public void test() throws IOException {
		for (int idx = 0; idx < BLOCK_SIZES.length; idx++) {
			assertBlockSizeDefrag(BLOCK_SIZES[idx]);
		}
	}

	private void assertBlockSizeDefrag(int blockSize) throws IOException {
		String fileName = tempFile();
		new File(fileName).delete();
		createDatabase(fileName, blockSize);
		defrag(fileName, blockSize);
		assertCanRead(fileName, blockSize);
		new File(fileName).delete();
	}

	private void createDatabase(String fileName, int blockSize) {
		ObjectContainer db = Db4oEmbedded.openFile(config(blockSize), fileName);
		Collection4 removed = new Collection4();
		for(int idx = 0; idx < NUM_ITEMS_PER_CLASS; idx++) {
			ItemA itemA = new ItemA(idx);
			ItemB itemB = new ItemB(fillStr('x', idx));
			db.store(itemA);
			db.store(itemB);
			if((idx % DELETE_RATIO) == 0) {
				removed.add(itemA);
				removed.add(itemB);
			}
		}
		db.commit();

		deleteAndReadd(db, removed);

		db.close();
	}

	private void deleteAndReadd(ObjectContainer db, Collection4 removed) {
		Iterator4 removeIter = removed.iterator();
		while(removeIter.moveNext()) {
			db.delete(removeIter.current());
		}
		db.commit();

		Iterator4 readdIter = removed.iterator();
		while(readdIter.moveNext()) {
			db.store(readdIter.current());
		}
		db.commit();
	}

	/**
	 * @deprecated using deprecated api
	 */
	private void defrag(String fileName, int blockSize) throws IOException {
		DefragmentConfig config = new DefragmentConfig(fileName);
		config.db4oConfig(config(blockSize));
		config.forceBackupDelete(true);
		Defragment.defrag(config);
	}

	private void assertCanRead(String fileName, int blockSize) {
		ObjectContainer db = Db4oEmbedded.openFile(config(blockSize), fileName);
		assertResult(db, ItemA.class);
		assertResult(db, ItemB.class);
		db.close();
	}

	private void assertResult(ObjectContainer db, Class clazz) {
		ObjectSet result = db.query(clazz);

		Assert.areEqual(NUM_ITEMS_PER_CLASS, result.size());
		while(result.hasNext()) {
			Assert.isInstanceOf(clazz, result.next());
		}
	}

	private EmbeddedConfiguration config(int blockSize) {
		EmbeddedConfiguration config = newConfiguration();
		config.file().blockSize(blockSize);
		config.common().reflectWith(Platform4.reflectorForType(ItemA.class));
		return config;
	}
	
	private String fillStr(char ch, int len) {
		StringBuffer buf = new StringBuffer();
		for(int idx = 0; idx < len; idx++) {
			buf.append(ch);
		}
		return buf.toString();
	}
}
