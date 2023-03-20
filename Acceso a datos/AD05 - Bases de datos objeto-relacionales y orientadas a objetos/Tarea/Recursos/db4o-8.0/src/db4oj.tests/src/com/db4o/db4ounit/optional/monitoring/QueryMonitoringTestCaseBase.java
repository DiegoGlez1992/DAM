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
package com.db4o.db4ounit.optional.monitoring;

import com.db4o.query.Predicate;

@decaf.Remove
public abstract class QueryMonitoringTestCaseBase extends MBeanTestCaseBase {

	public static final class OptimizableQuery extends Predicate<Item> {
		@Override public boolean match(Item candidate) {
			return candidate._id.equals("foo");
		}
	}

	public static final class UnoptimizableQuery extends Predicate<Item> {
		@Override
		public boolean match(Item candidate) {
			return candidate._id.toLowerCase().equals("FOO");
		}
	}
	
	protected void triggerOptimizedQuery() {
		db().query(new OptimizableQuery()).toArray();
	}

	protected void triggerUnoptimizedQuery() {
		db().query(unoptimizableQuery()).toArray();
	}

	protected Predicate<Item> unoptimizableQuery() {
		return new UnoptimizableQuery();
	}
}
