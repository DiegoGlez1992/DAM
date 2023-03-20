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
package com.db4o.db4ounit.common.querying;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll(); //runSoloAndClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
	    	CascadedDeleteUpdate.class,
			CascadeDeleteArray.class,
            CascadeDeleteDeleted.class,
			CascadeDeleteFalse.class,
	    	CascadeOnActivate.class,
	        CascadeOnDeleteTestCase.class,
	        CascadeOnDeleteHierarchyTestCase.class,
	        CascadeOnUpdateTestCase.class,
	        CascadeToArray.class,
	        ConjunctiveQbETestCase.class,
	        DeepMultifieldSortingTestCase.class,
	        DescendIndexQueryTestCase.class,
	        IdentityQueryForNotStoredTestCase.class,
			IdListQueryResultTestCase.class,
            IndexedJoinQueriesTestCase.class,
			IndexOnParentFieldTestCase.class,
            IndexedQueriesTestCase.class,
            InvalidFieldNameConstraintTestCase.class,
            LazyQueryResultTestCase.class,
            MultiFieldIndexQueryTestCase.class,
            NoClassIndexQueryTestSuite.class,
            NullConstraintQueryTestCase.class,
            ObjectSetTestCase.class,
            OrderedQueryTestCase.class,
            QueryByExampleTestCase.class,
            QueryingForAllObjectsTestCase.class,
            QueryingVersionFieldTestCase.class,
            SameChildOnDifferentParentQueryTestCase.class,
            SortingOnUnknownClassTestCase.class,
		};
	}
}
