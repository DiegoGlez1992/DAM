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
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class DatabaseReadonlyExceptionTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA, OptOutInMemory, OptOutDefragSolo {

	public static void main(String[] args) {
		new DatabaseReadonlyExceptionTestCase().runAll();
	}

	public void testRollback() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().rollback();
			}
		});
	}

	public void testCommit() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
	}

	public void testSet() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().store(new Item());
			}
		});
	}

	public void testDelete() {
		configReadOnly();
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(new Item());
			}
		});
	}
	
	public void testNewFile() {
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				fixture().close();
				fixture().clean();
				fixture().config().readOnly(true);
				fixture().open(DatabaseReadonlyExceptionTestCase.this);
			}
		});
	}

	public void testReserveStorage() {
	    configReadOnly();
		Class exceptionType = isMultiSession() && ! isEmbedded() ? NotSupportedException.class
				: DatabaseReadOnlyException.class;
		Assert.expect(exceptionType, new CodeBlock() {
			public void run() throws Throwable {
				db().configure().reserveStorageSpace(1);
			}
		});
	}
	
	public void testStoredClasses() {
	    configReadOnly();
	    db().storedClasses();
	}
	
	private void configReadOnly() {
		db().configure().readOnly(true);
	}
	
}
