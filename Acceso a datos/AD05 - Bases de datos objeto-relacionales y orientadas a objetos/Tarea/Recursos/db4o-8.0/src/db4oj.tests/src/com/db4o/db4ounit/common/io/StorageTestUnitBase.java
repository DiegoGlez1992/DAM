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

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.fixtures.*;

public class StorageTestUnitBase extends TestWithTempFile {

	protected Bin _bin;

	public StorageTestUnitBase() {
		super();
	}

	@Override
	public void setUp() throws Exception {
    	super.setUp();
    	open(false);
    }

	protected void open(final boolean readOnly) {
		if (null != _bin) {
			throw new IllegalStateException();
		}
	    _bin = storage().open(new BinConfiguration(tempFile(), false, 0, readOnly));
    }

	@Override
	public void tearDown() throws Exception {
    	close();
    	super.tearDown();
    }

	protected void close() {
	    if (null != _bin) {
	    	_bin.sync();
    		_bin.close();
    		_bin = null;
    	}
    }

	private Storage storage() {
    	return SubjectFixtureProvider.value();
    }

}