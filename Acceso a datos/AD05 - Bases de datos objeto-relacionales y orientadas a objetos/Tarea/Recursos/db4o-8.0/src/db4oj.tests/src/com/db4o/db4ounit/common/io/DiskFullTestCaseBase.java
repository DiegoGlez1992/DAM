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
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.io.*;

import db4ounit.*;

/**
 */
@decaf.Ignore
public abstract class DiskFullTestCaseBase extends Db4oTestWithTempFile {
	
	protected abstract ThrowCondition createThrowCondition(Object conditionConfig);

	protected abstract void configureForFailure(ThrowCondition condition);

	private ObjectContainer _db;
	private ThrowCondition _throwCondition;

	public DiskFullTestCaseBase() {
		super();
	}

	public void tearDown() throws Exception {
		if(_db != null) {
			_db.close();
			_db = null;
		}
		super.tearDown();
	}

	protected void storeOneAndFail(Object conditionConfig, boolean doCache) {
		openDatabase(conditionConfig, false, doCache);
		_db.store(new Item(42));
		_db.commit();
		triggerDiskFullAndClose();
	}

	protected void storeNAndFail(Object conditionConfig, int numObjects, int commitInterval, boolean doCache) {
		openDatabase(conditionConfig, false, doCache);
		for(int objIdx = 0; objIdx < numObjects; objIdx++) {
			_db.store(new Item(objIdx));
			if(objIdx % commitInterval == commitInterval - 1) {
				_db.commit();
			}
		}
		triggerDiskFullAndClose();
	}

	protected void assertItemsStored(int numItems, Object conditionConfig, boolean readOnly, boolean withCache) {
		Assert.isNull(_db);
		openDatabase(conditionConfig, readOnly, false);
		int itemCount = _db.query(Item.class).size();
		if(withCache){
			Assert.isTrue(itemCount == numItems || itemCount == numItems + 1);
			
		}else{
			Assert.areEqual(numItems, itemCount);
		}
		closeDb();
	}


	protected void triggerDiskFullAndClose() {
		configureForFailure(_throwCondition);
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				_db.store(new Item(42));
				_db.commit();
			}
		});
		_db = null;
	}

	public void openDatabase(Object conditionConfig, boolean readOnly, boolean doCache) {
		EmbeddedConfiguration config = newConfiguration();
		_throwCondition = createThrowCondition(conditionConfig);
		config.file().freespace().discardSmallerThan(Integer.MAX_VALUE);
		config.file().readOnly(readOnly);
		configureIoAdapter(config, _throwCondition, doCache);
		_db = Db4oEmbedded.openFile(config, tempFile());
	}

	private void configureIoAdapter(EmbeddedConfiguration config, ThrowCondition throwCondition, boolean doCache) {
		Storage storage = new FileStorage();
		storage = new ThrowingStorage(storage, throwCondition);
		if(doCache) {
			storage = new CachingStorage(storage, 256, 2);
		}
		config.file().storage(storage);
	}

	protected void closeDb() {
		try {
			_db.close();
		}
		finally {
			_db = null;
		}
	}

}