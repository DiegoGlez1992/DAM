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
package com.db4o.db4ounit.jre12.soda;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ObjectSetListAPITestSuite extends FixtureTestSuiteDescription implements Db4oTestCase {
	
	{
		testUnits(TestUnit.class);
		
		// TODO: Add QueryEvaluationMode.SNAPSHOT to the fixtures
		fixtureProviders(
				new Db4oFixtureProvider(),
				new SubjectFixtureProvider(
					QueryEvaluationMode.LAZY,
					QueryEvaluationMode.IMMEDIATE));
	}

	private static final int NUMDATA = 1000;
	
	private static class Data {
		private int _id;

		public Data(int id) {
			_id = id;
			use(_id);
		}

		private void use(int id) {
		}
	}
	
	public static class TestUnit extends AbstractDb4oTestCase {

		protected void configure(Configuration config) throws Exception {
			final QueryEvaluationMode evaluationMode = SubjectFixtureProvider.value();
			config.queries().evaluationMode(evaluationMode);
		}
		
		protected void store() throws Exception {
			for(int i = 0; i < NUMDATA; i++) {
				store(new Data(i));
			}
		}
		
		public void testOutOfBounds() {
			final ObjectSet result = result();
			Assert.expect(Db4oRecoverableException.class, IndexOutOfBoundsException.class, new CodeBlock() {
				public void run() throws Throwable {
					result.get(NUMDATA);
				}
			});
		}
	
		public void testToArray() {
			Assert.areEqual(NUMDATA, result().toArray().length);
			Assert.areEqual(NUMDATA, result().toArray(new Data[0]).length);
			Assert.areEqual(NUMDATA, result().toArray(new Data[NUMDATA]).length);
		}
	
		private ObjectSet result() {
			Query query = newQuery(Data.class);
			query.descend("_id").constrain(new Integer(Integer.MAX_VALUE)).not();
			final ObjectSet result = query.execute();
			return result;
		}
	}
}
