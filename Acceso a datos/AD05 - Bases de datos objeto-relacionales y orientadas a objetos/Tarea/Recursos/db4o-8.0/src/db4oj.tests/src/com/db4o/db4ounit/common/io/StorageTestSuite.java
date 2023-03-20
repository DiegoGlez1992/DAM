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

import com.db4o.io.*;

import db4ounit.extensions.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.partial
 */
@SuppressWarnings("deprecation")
public class StorageTestSuite extends FixtureTestSuiteDescription implements OptOutVerySlow {

	/**
	 * @sharpen.ignore
	 */
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new EnvironmentProvider(),
				new SubjectFixtureProvider(new Object[] {
						Db4oUnitPlatform.newPersistentStorage(),
						new MemoryStorage(),
						new PagingMemoryStorage(63),
						new CachingStorage(Db4oUnitPlatform.newPersistentStorage()),
						new IoAdapterStorage(new RandomAccessFileAdapter()),
				})			
		};
	}
	
	
	@Override
	public Class[] testUnits() {
		return new Class[] {
				BinTest.class,
				ReadOnlyBinTest.class,
				StorageTest.class				
		};
	}
		
//	combinationToRun(2);
}
