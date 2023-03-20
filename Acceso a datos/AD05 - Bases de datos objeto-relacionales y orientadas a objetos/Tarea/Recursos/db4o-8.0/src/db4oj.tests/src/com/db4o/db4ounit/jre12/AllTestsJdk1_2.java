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
package com.db4o.db4ounit.jre12;

import com.db4o.db4ounit.jre12.foundation.*;
import com.db4o.db4ounit.jre12.reflect.*;

import db4ounit.extensions.*;

/**
 */
@decaf.Remove(decaf.Platform.JDK11)
public class AllTestsJdk1_2 extends ComposibleTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsJdk1_2().runAll());
    }

	protected Class[] testCases() {
		return composeTests(new Class[] {
					// FIXME: solve the workspacePath issue and uncomment migration.AllCommonTests.class below
					//com.db4o.db4ounit.common.migration.AllCommonTests.class,
				
					com.db4o.db4ounit.common.defragment.LegacyDatabaseDefragTestCase.class,
					com.db4o.db4ounit.common.freespace.FreespaceManagerTypeChangeSlotCountTestCase.class,
					com.db4o.db4ounit.common.ta.AllTests.class,
					com.db4o.db4ounit.jre11.AllTests.class,
					com.db4o.db4ounit.jre12.assorted.AllTests.class,
					com.db4o.db4ounit.jre12.blobs.AllTests.class,
					com.db4o.db4ounit.jre12.defragment.AllTests.class,
					com.db4o.db4ounit.jre12.fieldindex.AllTests.class,
					com.db4o.db4ounit.jre12.handlers.AllTests.class,
					com.db4o.db4ounit.jre12.soda.AllTests.class,
					com.db4o.db4ounit.jre12.collections.AllTests.class,
					com.db4o.db4ounit.jre12.collections.facades.AllTests.class,
					com.db4o.db4ounit.jre12.collections.map.AllTests.class,
					com.db4o.db4ounit.jre12.collections.transparent.AllTests.class,
					com.db4o.db4ounit.jre12.querying.AllTests.class,
					com.db4o.db4ounit.jre12.ta.AllTests.class,
					com.db4o.db4ounit.jre12.ta.collections.AllTests.class,
					com.db4o.db4ounit.jre12.types.AllTests.class,
					StandaloneNativeReflectorTestCase.class,
					IterableBaseTestCase.class,
					com.db4o.db4ounit.jre12.tp.AllTests.class,
				});
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] { 
					com.db4o.db4ounit.jre12.defragment.DefragUnknownClassTestCase.class,
					com.db4o.db4ounit.jre12.regression.AllTests.class,
				};
	}
}
