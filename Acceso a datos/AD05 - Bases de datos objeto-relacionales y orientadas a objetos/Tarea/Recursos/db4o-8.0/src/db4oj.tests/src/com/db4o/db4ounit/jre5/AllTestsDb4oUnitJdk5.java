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
package com.db4o.db4ounit.jre5;

import db4ounit.extensions.*;


/**
 */
@decaf.Ignore
public class AllTestsDb4oUnitJdk5 extends Db4oTestSuite {

	public static void main(String[] args) {
//		System.exit(new AllTestsDb4oUnitJdk5().runSolo());
//		System.exit(new AllTestsDb4oUnitJdk5().runSoloAndEmbeddedClientServer());
		System.exit(new AllTestsDb4oUnitJdk5().runAll());
//		System.exit(new AllTestsDb4oUnitJdk5().runNetworking());
	}

	@Override
	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.common.assorted.AllTestsJdk5.class,
			com.db4o.db4ounit.jre5.annotation.AllTests.class,
			com.db4o.db4ounit.jre5.collections.AllTests.class,
			com.db4o.db4ounit.jre5.enums.AllTests.class,
			com.db4o.db4ounit.jre5.generic.AllTests.class,
			com.db4o.db4ounit.jre5.query.AllTests.class,
			com.db4o.db4ounit.jre12.AllTestsJdk1_2.class,
		};
	}

}
