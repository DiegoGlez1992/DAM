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
package com.db4o.db4ounit.common.exceptions.propagation;

import com.db4o.ext.*;

import db4ounit.*;

public abstract class RecurringFatalExceptionPropagationFixtureBase implements ExceptionPropagationFixture {

	protected static final String CLOSE_MESSAGE = "B";
	protected static final String INITIAL_MESSAGE = "A";

	public void throwShutdownException() {
		Assert.fail();
	}

	public void assertExecute(DatabaseContext context, TopLevelOperation op) {
		try {
			op.apply(context);
			Assert.fail();
		}
		catch(CompositeDb4oException exc) {
			Assert.areEqual(2, exc._exceptions.length);
			assertExceptionMessage(exc, INITIAL_MESSAGE, 0);
			assertExceptionMessage(exc, CLOSE_MESSAGE, 1);
		}
	}

	private void assertExceptionMessage(CompositeDb4oException exc, String expected, int idx) {
		Assert.areEqual(expected, exc._exceptions[idx].getMessage());
	}

	protected abstract Class<? extends RuntimeException> exceptionType();

}