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

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseClosedExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new DatabaseClosedExceptionTestCase().runAll();
	}

	public void testRollback() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().rollback();
			}
		});
	}

	public void testCommit() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
	}

	public void testSet() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().store(new Item());
			}
		});
	}

	public void testDelete() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(new Item());
			}
		});
	}

	public void testQueryClass() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().query(this.getClass());
			}
		});
	}

	public void testQuery() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().query();
			}
		});
	}

	public void testDeactivate() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().deactivate(new Item(), 1);
			}
		});
	}

	public void testActivate() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().activate(new Item(), 1);
			}
		});
	}

	public void testGet() {
		db().close();
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().queryByExample(new Item());
			}
		});
	}

}
