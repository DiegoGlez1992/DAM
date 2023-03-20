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
package com.db4o.db4ounit.common.ta;

import db4ounit.extensions.*;

public class AllCommonTATests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllCommonTATests().runAll();
	}
	
	protected Class[] testCases() {
		return new Class[] {
				
			ActivatableTestCase.class,
			ActivationBasedOnConcreteTypeTestCase.class,
			TransparentActivationSupportTestCase.class,
			TAWithGCBeforeCommitTestCase.class,
			com.db4o.db4ounit.common.ta.events.AllTests.class,
			com.db4o.db4ounit.common.ta.mixed.AllTests.class,
			com.db4o.db4ounit.common.ta.nonta.AllTests.class,
			com.db4o.db4ounit.common.ta.sample.AllTests.class,
			com.db4o.db4ounit.common.ta.ta.AllTests.class,
		};
	}

}
