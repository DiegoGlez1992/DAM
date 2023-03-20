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
package db4ounit.tests;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestException;

public class TestExceptionWithInnerCause implements TestCase {
	public void testDetailerMessage() {
		final String message = "Detailed message";
		final TestException e = new TestException(message, new Exception("The reason!"));
		Assert.isGreaterOrEqual(0, e.toString().indexOf(message));		
	}
}
