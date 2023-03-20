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
package com.db4o.db4ounit.common.io;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SaveAsStorageTestCase extends AbstractDb4oTestCase implements OptOutMultiSession, OptOutInMemory, OptOutNoFileSystemData, OptOutSilverlight {
 
	public static void main(String[] args) {
		new SaveAsStorageTestCase().runSolo();
	}
	
	private final SaveAsStorage _storage = new SaveAsStorage(new CachingStorage(new FileStorage()));
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.storage(_storage);
	}
	
	public void testExistingFileWillNotBeOverWritten(){
		db().store(new Item(1));
		final String oldFileName = fileSession().fileName();
		final ByRef<String> newPath = new ByRef();
		try{
			newPath.value = Path4.getTempFileName();
			Assert.isTrue(File4.exists(newPath.value));
			Assert.expect(IllegalStateException.class, new CodeBlock() {
				public void run() throws Throwable {
					_storage.saveAs(oldFileName, newPath.value);
				}
			});
			assertItems(db(), 1);
		} finally{
			File4.delete(newPath.value);
		}
	}
	
	private void assertItems(String fileName, int count) {
		EmbeddedObjectContainer objectContainer = Db4oEmbedded.openFile(fileName);
		assertItems(objectContainer, count);
		objectContainer.close();
	}

	private void assertItems(ObjectContainer objectContainer, int count) {
		ObjectSet<Item> items = objectContainer.query(Item.class);
		Assert.areEqual(count, items.size());
		Assert.areEqual(count, items.size());
		int countCheck = 0;
		for(Item item: items){
			Assert.isGreater(0, item._id);
			countCheck++;
		}
		Assert.areEqual(count, countCheck);
	}
	
	public void testUnknownBin(){
		db().store(new Item(1));
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				_storage.saveAs("unknown", "unknown");
			}
		});
		assertItems(db(), 1);
	}
	
	public void testSaveAsTwice(){
		db().store(new Item(1));
		db().commit();
		String oldFileName = fileSession().fileName();
		
		String firstNewFileName = saveOldAs(oldFileName);
		assertItems(oldFileName, 1);
		db().store(new Item(2));
		db().commit();
		String secondNewFileName = saveOldAs(firstNewFileName);
		assertItems(firstNewFileName, 2);
		db().store(new Item(3));
		assertItems(db(), 3);
		db().commit();
		db().close();
		assertItems(secondNewFileName, 3);
	}
	
	public void testPartialPersistence(){
		String oldFileName = fileSession().fileName(); 
		db().store(new Item(1));
		db().commit();
		db().store(new Item(2));
		String newPath = null;
		try{
			newPath = saveOldAs(oldFileName);
			ObjectSet<Item> items = db().query(Item.class);
			Assert.areEqual(2, items.size());
			db().store(new Item(3));
			db().close();
			assertItems(oldFileName, 1);
			assertItems(newPath, 3);
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			File4.delete(newPath);
		}
	}

	private String saveOldAs(String oldFileName) {
		String newPath;
		newPath = Path4.getTempFileName();
		File4.delete(newPath);
		_storage.saveAs(oldFileName, newPath);
		return newPath;
	}
	
	public static class Item{
		
		public int _id;

		public Item(int id){
			_id = id;
		}
		
	}

}
