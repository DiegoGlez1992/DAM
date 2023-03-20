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
package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.convert.*;

import db4ounit.*;

public class CommitTimestampMigrationTestCase extends FormatMigrationTestCaseBase {
	
	
	public static class Item {
	}

	@Override
	protected void configureForTest(Configuration config) {
		configureForStore(config);
	}
	
	@Override
	protected void configureForStore(Configuration config) {
		config.generateVersionNumbers(ConfigScope.GLOBALLY);
			
		// This needs to be in a different method for .NET because .NET
		// tries to resolve the complete method body for jitting and will
		// throw without calling the first method. 

		configureForStore8_0AndNewer(config);
	}
	
    protected void configureForStore8_0AndNewer(Configuration config){
    	config.generateCommitTimestamps(true);
    }

	
	@Override
	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		
		if (db4oMajorVersion() <= 6 || (db4oMajorVersion() == 7 && db4oMinorVersion() == 0)) {
			return;
		}			
		
		Item item = objectContainer.query(Item.class).next();
		ObjectInfo objectInfo = objectContainer.getObjectInfo(item);
		long version = objectInfo.getCommitTimestamp();
		Assert.isGreater(0, version);
	}

	@Override
	protected String fileNamePrefix() {
		return "commitTimestamp";
	}

	@Override
	protected void store(ObjectContainerAdapter objectContainer) {
		objectContainer.store(new Item());
	}

}
