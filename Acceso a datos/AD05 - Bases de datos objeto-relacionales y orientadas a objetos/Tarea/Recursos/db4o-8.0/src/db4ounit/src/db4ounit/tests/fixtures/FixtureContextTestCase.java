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
package db4ounit.tests.fixtures;

import db4ounit.*;
import db4ounit.fixtures.*;

public class FixtureContextTestCase implements TestCase {
	
	public static final class ContextRef {
		public FixtureContext value;
	}
	
	public void test() {
		final FixtureVariable f1 = new FixtureVariable();
		final FixtureVariable f2 = new FixtureVariable();
		final ContextRef c1 = new ContextRef();
		final ContextRef c2 = new ContextRef();
		new FixtureContext().run(new Runnable() {
			public void run() {
				f1.with("foo", new Runnable() {
					public void run() {
						assertValue("foo", f1);
						assertNoValue(f2);
						c1.value = FixtureContext.current();
						f2.with("bar", new Runnable() {
							public void run() {
								assertValue("foo", f1);
								assertValue("bar", f2);
								c2.value = FixtureContext.current();
							}
						});
					}
				});
				
			}
		});
		assertNoValue(f1);
		assertNoValue(f2);
		
		c1.value.run(new Runnable() {
			public void run() {
				assertValue("foo", f1);
				assertNoValue(f2);
			}
		});
		
		c2.value.run(new Runnable() {
			public void run() {
				assertValue("foo", f1);
				assertValue("bar", f2);
			}
		});
	}

	private void assertNoValue(final FixtureVariable f1) {
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				use(f1.value());
			}

			private void use(Object value) {
			}
		});
	}

	private void assertValue(final String expected, final FixtureVariable fixture) {
		Assert.areEqual(expected, fixture.value());
	}

}
