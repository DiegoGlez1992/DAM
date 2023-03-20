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

import db4ounit.*;
import db4ounit.fixtures.*;

public class StorageTest extends TestWithTempFile {
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	public void testInitialLength() {
		
		storage().open(new BinConfiguration(tempFile(), false, 1000, false)).close();
		
		final Bin bin = storage().open(new BinConfiguration(tempFile(), false, 0, false));
		try {
			Assert.areEqual(1000, bin.length());
		} finally {
			bin.close();
		}
		
	}

	private Storage storage() {
    	return SubjectFixtureProvider.value();
    }

}
