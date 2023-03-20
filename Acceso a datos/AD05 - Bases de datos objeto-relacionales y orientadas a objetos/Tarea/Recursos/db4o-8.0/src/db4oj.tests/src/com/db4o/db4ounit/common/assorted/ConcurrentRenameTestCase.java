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
package com.db4o.db4ounit.common.assorted;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ConcurrentRenameTestCase implements TestLifeCycle {
		public static void main(String[] args) {
			new ConsoleTestRunner(ConcurrentRenameTestCase.class).run();
		}
	
		private static final String DATABASE_FILE_NAME = "";
		static final int NUM_ITERATIONS = 500;

		public static class QueryItem {
		}

		public static class RenameItem {
		}
		
		public static abstract class RunnerBase implements Runnable {
			private ObjectContainer _db;
			private List<Throwable> _exceptions;

			protected RunnerBase(ObjectContainer db, List<Throwable> exceptions) {
				_db = db;
				_exceptions = exceptions;
			}
			
			protected abstract void exercise(ObjectContainer db);

			public void run() {
				try {
					for (int i = 0; i < NUM_ITERATIONS; i++) {
						exercise(_db);
						Runtime4.sleep(1);
					}
				}
				catch(Throwable ex) {
					synchronized (_exceptions) {
						_exceptions.add(ex);
					}
				}				
			}
		}		
		
		public static class QueryRunner extends RunnerBase {
			
			public QueryRunner(ObjectContainer db, List<Throwable> exceptions) {
				super(db, exceptions);
			}
			
			@Override
			protected void exercise(ObjectContainer db) {
				Assert.areEqual(1, db.query(QueryItem.class).size());
				
				QueryItem newItem = new QueryItem();
				db.store(newItem);
				db.commit();
				db.delete(newItem);
				db.commit();
			}			
		}
		
		public static class RenameRunner extends RunnerBase {

			private static final String ORIGINAL_NAME = ReflectPlatform.fullyQualifiedName(RenameItem.class);
			private static final String NEW_NAME = ORIGINAL_NAME + "X";
			
			public RenameRunner(ObjectContainer db, List<Throwable> exceptions) {
				super(db, exceptions);
			}
			
			@Override
			protected void exercise(ObjectContainer db) {
				renameClass(db, ORIGINAL_NAME, NEW_NAME);
				renameClass(db, NEW_NAME, ORIGINAL_NAME);		
			}

			private void renameClass(ObjectContainer db, String originalName, String newName) {
				StoredClass storedClass = db.ext().storedClass(originalName);
				storedClass.rename(newName);
			}			
		}
		
		public void test() throws Exception {
			EmbeddedObjectContainer db = openDatabase();
			
			List<Throwable> exceptions = new ArrayList<Throwable>();
			Thread []threads = {new Thread(new QueryRunner(db, exceptions), "ConcurrentRenameTestCase.test Thread[0]"), new Thread(new RenameRunner(db, exceptions), "ConcurrentRenameTestCase.test Thread[1]")};
			
			for (Thread thread : threads) {
				thread.start();
			}
			
			for (Thread thread : threads) {
				thread.join();
			}
			
			db.close();			
			
			Assert.areEqual(0, exceptions.size());
		}

		public void setUp() throws Exception {
			EmbeddedObjectContainer db = openDatabase();
			db.store(new QueryItem());
			db.store(new RenameItem());
			
			db.close();
		}

		private EmbeddedObjectContainer openDatabase() {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.file().storage(_storage);
			
			return Db4oEmbedded.openFile(config, DATABASE_FILE_NAME);
		}

		public void tearDown() throws Exception {			
		}
		
		private MemoryStorage _storage = new MemoryStorage();
}
