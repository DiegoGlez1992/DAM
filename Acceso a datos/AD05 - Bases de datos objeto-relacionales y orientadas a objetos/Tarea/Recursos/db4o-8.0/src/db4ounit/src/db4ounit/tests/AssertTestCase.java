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
import db4ounit.AssertionException;
import db4ounit.CodeBlock;
import db4ounit.TestCase;

public class AssertTestCase implements TestCase {
	public void testAreEqual() {
		Assert.areEqual(true, true);
		Assert.areEqual(42, 42);
		Assert.areEqual(new Integer(42), new Integer(42));
		Assert.areEqual(null, null);
		expectFailure(new CodeBlock() {
			public void run() throws Throwable {
				Assert.areEqual(true, false);
			}
		});
		expectFailure(new CodeBlock() {
			public void run() throws Throwable {
				Assert.areEqual(42, 43);
			}
		});
		expectFailure(new CodeBlock() {
			public void run() throws Throwable {
				Assert.areEqual(new Object(), new Object());
			}
		});
		expectFailure(new CodeBlock() {
			public void run() throws Throwable {
				Assert.areEqual(null, new Object());
			}
		});
	}	
	
	public void testAreSame() {
		expectFailure(new CodeBlock() {
			public void run() throws Throwable {
				Assert.areSame(new Object(), new Object());
			}
		});
		Assert.areSame(this, this);
	}
	
	private void expectFailure(CodeBlock block) {
		Assert.expect(AssertionException.class, block);
	}
}
