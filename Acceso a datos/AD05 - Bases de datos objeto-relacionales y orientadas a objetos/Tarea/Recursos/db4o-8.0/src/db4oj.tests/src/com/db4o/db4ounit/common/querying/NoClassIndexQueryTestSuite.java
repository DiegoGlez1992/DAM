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

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class NoClassIndexQueryTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {

	public static class NoClassIndexQueryTestUnit extends AbstractDb4oTestCase {

		public static class Item {
		}
	
		@Override
		protected void configure(Configuration config) throws Exception {
			config.objectClass(Item.class).indexed(false);
			config.queries().evaluationMode(queryMode.value().mode());
		}
	
		@Override
		protected void store() throws Exception {
			store(new Item());
		}
	
		public void test() {
			ObjectSet<Item> query = db().query(Item.class);
			Assert.areEqual(0, query.size());
		}
	}

	private final static FixtureVariable<LabeledQueryMode> queryMode = FixtureVariable.newInstance("queryMode");
	
	public static class LabeledQueryMode implements Labeled {

		private final QueryEvaluationMode _mode;
		
		public LabeledQueryMode(QueryEvaluationMode mode) {
			_mode = mode;
		}

		public QueryEvaluationMode mode() {
			return _mode;
		}
		
		public String label() {
			return _mode.toString();
		}
		
	}
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			new SimpleFixtureProvider<LabeledQueryMode>(
					queryMode, 
					new LabeledQueryMode(QueryEvaluationMode.IMMEDIATE), 
					new LabeledQueryMode(QueryEvaluationMode.SNAPSHOT), 
					new LabeledQueryMode(QueryEvaluationMode.LAZY))
		};
	}

	@Override
	public Class<?>[] testUnits() {
		return new Class[] {
				NoClassIndexQueryTestUnit.class
		};
	}
}
