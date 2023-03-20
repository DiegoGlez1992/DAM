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
package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class BackupDb4oIOExceptionTestCase
	extends Db4oIOExceptionTestCaseBase
	implements OptOutInMemory {
	
	public static void main(String[] args) {
		new BackupDb4oIOExceptionTestCase().runAll();
	}
	
	private static final String BACKUP_FILE = "backup.db4o";

	protected void db4oSetupBeforeStore() throws Exception {
		super.db4oSetupBeforeStore();
		File4.delete(BACKUP_FILE);
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		super.db4oTearDownBeforeClean();
		File4.delete(BACKUP_FILE);
	}
	
	public void testBackup() {
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				triggerException(true);
				db().backup(BACKUP_FILE);
			}
		});
	}
	
}
