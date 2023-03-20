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
package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.threading.*;

import db4ounit.*;
import db4ounit.extensions.*;

public abstract class AbstractSoloDb4oFixture extends AbstractDb4oFixture {

	private ExtObjectContainer _db;
	
	protected AbstractSoloDb4oFixture() {
	}
	
	public final void open(Db4oTestCase testInstance) {
		Assert.isNull(_db);
		final Configuration config = cloneConfiguration();
		applyFixtureConfiguration(testInstance, config);
		_db=createDatabase(config).ext();
		listenToUncaughtExceptions(threadPool());
		
		postOpen(testInstance);
	}

	private ThreadPool4 threadPool() {
	    return threadPoolFor(_db);
    }

	public void close() throws Exception {
		try {
			preClose();
		}
		finally {
		
		if (null != _db) {
				Assert.isTrue(_db.close());
				try {
					threadPool().join(3000);
				} finally {
					_db = null;
				}
			}
		}
	}	

	public boolean accept(Class clazz) {
		return !OptOutSolo.class.isAssignableFrom(clazz);
	}

	public ExtObjectContainer db() {
		return _db;
	}
	
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_db;
	}
	
	public void configureAtRuntime(RuntimeConfigureAction action) {
		action.apply(config());
	}
	
	protected void preClose() {
	}
	
	protected void postOpen(Db4oTestCase testInstance) {
	}
	
	protected abstract ObjectContainer createDatabase(Configuration config);
	

}