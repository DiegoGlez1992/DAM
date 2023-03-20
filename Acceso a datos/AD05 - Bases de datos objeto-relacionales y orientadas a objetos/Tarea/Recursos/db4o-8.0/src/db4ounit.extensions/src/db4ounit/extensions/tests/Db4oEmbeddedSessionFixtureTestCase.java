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
package db4ounit.extensions.tests;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class Db4oEmbeddedSessionFixtureTestCase implements TestCase {
	
	final Db4oEmbeddedSessionFixture subject = new Db4oEmbeddedSessionFixture();
	
	public void testDoesNotAcceptRegularTest() {
		Assert.isFalse(subject.accept(RegularTest.class));
	}
	
	public void testAcceptsDb4oTest() {
		Assert.isTrue(subject.accept(Db4oTest.class));
	}
	
	public void testDoesNotAcceptOptOutCS() {
		Assert.isFalse(subject.accept(OptOutTest.class));
	}
	
	public void testDoesNotAcceptOptOutAllButNetworkingCS() {
		Assert.isFalse(subject.accept(OptOutAllButNetworkingCSTest.class));
	}
	
	public void testAcceptsOptOutNetworking() {
		Assert.isTrue(subject.accept(OptOutNetworkingTest.class));
	}
	
	static class RegularTest implements TestCase {
	}
	
	static class Db4oTest implements Db4oTestCase {
	}
	
	static class OptOutTest implements OptOutMultiSession {
	}
	
	static class OptOutNetworkingTest implements OptOutNetworkingCS {
	}
	
	static class OptOutAllButNetworkingCSTest implements OptOutAllButNetworkingCS {
		
	}
	

}
