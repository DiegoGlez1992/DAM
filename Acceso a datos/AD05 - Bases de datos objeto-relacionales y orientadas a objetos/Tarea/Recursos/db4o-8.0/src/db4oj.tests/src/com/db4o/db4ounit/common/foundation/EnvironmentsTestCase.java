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
package com.db4o.db4ounit.common.foundation;

import static com.db4o.foundation.Environments.my;
import static com.db4o.foundation.Environments.runWith;

import com.db4o.foundation.*;

import db4ounit.*;

public class EnvironmentsTestCase implements TestCase {
	
	public interface Whatever {
	}
	
	// FIXME: db4ounit tests always run in an environment now (required to keep the test executor)
	public void _testNoEnvironment() {
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				my(Whatever.class);
			}
		});
	}
	
	public void testRunWith() {
		final Whatever whatever = new Whatever() {};
		final Environment environment = new Environment() {
			public <T> T provide(Class<T> service) {
				return service.cast(whatever);
			}
		};
		final ByRef<Boolean> ran = ByRef.newInstance();
		runWith(environment, new Runnable() {
			public void run() {
				ran.value = true;
				Assert.areSame(whatever, my(Whatever.class));
			}
		});
		Assert.isTrue(ran.value);
	}
	
	public void testNestedEnvironments() {
		final Whatever whatever = new Whatever() {};
		
		final Environment environment1 = new Environment() {
			public <T> T provide(Class<T> service) {
				return service.cast(whatever);
			}
		};
		
		final Environment environment2 = new Environment() {
			public <T> T provide(Class<T> service) {
				return null;
			}
		};
		
		runWith(environment1, new Runnable() {
			public void run() {
				Assert.areSame(whatever, my(Whatever.class));
				runWith(environment2, new Runnable() {
					public void run() {
						Assert.isNull(my(Whatever.class));
					}
				});
				Assert.areSame(whatever, my(Whatever.class));
			}
		});
	}
}
