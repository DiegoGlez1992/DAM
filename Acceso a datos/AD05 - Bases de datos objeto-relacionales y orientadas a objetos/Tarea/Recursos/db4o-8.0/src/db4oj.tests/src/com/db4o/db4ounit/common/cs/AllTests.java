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
package com.db4o.db4ounit.common.cs;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll();
    }
	
	protected Class[] testCases() {
		return new Class[] {
				
			com.db4o.db4ounit.common.cs.caching.AllTests.class,
			com.db4o.db4ounit.common.cs.config.AllTests.class,
			com.db4o.db4ounit.common.cs.objectexchange.AllTests.class,
			
			BatchActivationTestCase.class,
	        CallConstructorsConfigTestCase.class,
	        ClientDisconnectTestCase.class,
            ClientTimeOutTestCase.class,
            ClientTransactionHandleTestCase.class,
            ClientTransactionPoolTestCase.class,
            CloseServerBeforeClientTestCase.class,
            CsCascadedDeleteReaddChildReferenceTestCase.class,
            CsDeleteReaddTestCase.class,
            IsAliveConcurrencyTestCase.class,
            IsAliveTestCase.class,
            NoTestConstructorsQEStringCmpTestCase.class,
            ObjectServerTestCase.class,
            PrefetchConfigurationTestCase.class,
            PrefetchIDCountTestCase.class,
            PrefetchObjectCountZeroTestCase.class,
            PrimitiveMessageTestCase.class,
            QueryConsistencyTestCase.class,
            ReferenceSystemIsolationTestCase.class,
            SendMessageToClientTestCase.class,
            ServerClosedTestCase.class,
            ServerObjectContainerIsolationTestCase.class,
            ServerPortUsedTestCase.class,
            ServerQueryEventsTestCase.class,
            ServerRevokeAccessTestCase.class,
            ServerTimeoutTestCase.class,
            ServerToClientTestCase.class,
            ServerTransactionCountTestCase.class,
            SetSemaphoreTestCase.class,
            SSLSocketTestCase.class,
            CsSchemaUpdateTestCase.class,
		};
	}
	
}
