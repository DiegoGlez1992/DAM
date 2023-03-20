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
package com.db4o.db4ounit.common.concurrency;

import db4ounit.extensions.*;

public class AllTests extends Db4oConcurrencyTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runConcurrency();
	}

	protected Class[] testCases() {
		return new Class[] { 
				ArrayNOrderTestCase.class, 
				ByteArrayTestCase.class,
				CascadeDeleteDeletedTestCase.class,
				CascadeDeleteFalseTestCase.class,
				CascadeOnActivateTestCase.class,
				CascadeOnUpdateTestCase.class,
				CascadeOnUpdate2TestCase.class,
				CascadeToVectorTestCase.class,
				CaseInsensitiveTestCase.class,
				Circular1TestCase.class,
				ClientDisconnectTestCase.class,
				CreateIndexInheritedTestCase.class,
				DeepSetTestCase.class,
				DeleteDeepTestCase.class,
				DifferentAccessPathsTestCase.class,
				ExtMethodsTestCase.class,
				GetAllTestCase.class,
				GreaterOrEqualTestCase.class,
				IndexedByIdentityTestCase.class,
				IndexedUpdatesWithNullTestCase.class,
				InternStringsTestCase.class,
				InvalidUUIDTestCase.class,
				IsStoredTestCase.class,
				MessagingTestCase.class,
				MultiDeleteTestCase.class,
				MultiLevelIndexTestCase.class,
				NestedArraysTestCase.class,
				ObjectSetIDsTestCase.class,
				ParameterizedEvaluationTestCase.class,
				PeekPersistedTestCase.class,
				PersistStaticFieldValuesTestCase.class,
				QueryForUnknownFieldTestCase.class,
				QueryNonExistantTestCase.class,
				ReadObjectNQTestCase.class,
				ReadObjectQBETestCase.class,
				ReadObjectSODATestCase.class,
				RefreshTestCase.class,
				UpdateObjectTestCase.class,
		};
	}

}
