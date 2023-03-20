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

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;

import db4ounit.extensions.*;

public class Db4oInMemory extends AbstractSoloDb4oFixture {
    
	private static final String DB_URI = "test_db";

	public Db4oInMemory() {
		super();
	}
	
	public Db4oInMemory(FixtureConfiguration fc) {
		this();
		fixtureConfiguration(fc);
	}
	
	@Override
	public boolean accept(Class clazz) {
		if (!super.accept(clazz)) {
			return false;
		}
		if (OptOutInMemory.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
	}

	private final PagingMemoryStorage _storage = new PagingMemoryStorage(63);
	
	protected ObjectContainer createDatabase(Configuration config) {
		return Db4o.openFile(config, DB_URI);
	}

	protected Configuration newConfiguration() {
		Configuration config = super.newConfiguration();
		config.storage(_storage);
		return config;
	}

    protected void doClean() {
    	try {
			_storage.delete(DB_URI);
		} 
    	catch (IOException exc) {
			exc.printStackTrace();
		}
    }

	public String label() {
		return buildLabel("IN-MEMORY");
	}

	public void defragment() throws Exception {
		defragment(DB_URI);
	}
	
}
