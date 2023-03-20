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
package com.db4o.db4ounit.common;

import db4ounit.extensions.*;

/**
 * 
 */
public class AllTests extends ComposibleTestSuite {
	
	/**
	 * @sharpen.ignore test suited is executed differently under .net
	 */
	public static void main(String[] args) {
		System.exit(new AllTests().runSolo());
	}

	protected Class[] testCases() {
		return composeTests(
				new Class[] {
						com.db4o.db4ounit.common.acid.AllTests.class,
						com.db4o.db4ounit.common.activation.AllTests.class,
						com.db4o.db4ounit.common.api.AllTests.class,
						com.db4o.db4ounit.common.assorted.AllTests.class,
						com.db4o.db4ounit.common.backup.AllTests.class,
						com.db4o.db4ounit.common.btree.AllTests.class,
						com.db4o.db4ounit.common.classindex.AllTests.class,
						com.db4o.db4ounit.common.caching.AllTests.class,
						com.db4o.db4ounit.common.config.AllTests.class,
						com.db4o.db4ounit.common.constraints.AllTests.class,
						com.db4o.db4ounit.common.defragment.AllTests.class,
						com.db4o.db4ounit.common.diagnostics.AllTests.class,
						com.db4o.db4ounit.common.events.AllTests.class,
						com.db4o.db4ounit.common.exceptions.AllTests.class,
						com.db4o.db4ounit.common.ext.AllTests.class,
						com.db4o.db4ounit.common.fatalerror.AllTests.class,
						com.db4o.db4ounit.common.fieldindex.AllTests.class,
						com.db4o.db4ounit.common.filelock.AllTests.class,
						com.db4o.db4ounit.common.foundation.AllTests.class,
						com.db4o.db4ounit.common.freespace.AllTests.class,
						com.db4o.db4ounit.common.handlers.AllTests.class,
						com.db4o.db4ounit.common.header.AllTests.class,
						com.db4o.db4ounit.common.interfaces.AllTests.class,
						com.db4o.db4ounit.common.internal.AllTests.class,
						com.db4o.db4ounit.common.ids.AllTests.class,
						com.db4o.db4ounit.common.io.AllTests.class,
						com.db4o.db4ounit.common.querying.AllTests.class,
						com.db4o.db4ounit.common.refactor.AllTests.class,
						com.db4o.db4ounit.common.references.AllTests.class,
						com.db4o.db4ounit.common.reflect.AllTests.class,
						com.db4o.db4ounit.common.regression.AllTests.class,
						com.db4o.db4ounit.common.sessions.AllTests.class,
						com.db4o.db4ounit.common.store.AllTests.class,
						com.db4o.db4ounit.common.soda.AllTests.class,
						com.db4o.db4ounit.common.stored.AllTests.class,
						com.db4o.db4ounit.common.ta.AllCommonTATests.class,
						com.db4o.db4ounit.common.tp.AllTests.class,
						com.db4o.db4ounit.common.types.AllTests.class,
						com.db4o.db4ounit.common.updatedepth.AllTests.class,
						com.db4o.db4ounit.common.uuid.AllTests.class,
						com.db4o.db4ounit.optional.AllTests.class,
						com.db4o.db4ounit.util.test.AllTests.class,
						});
	}
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] {
				com.db4o.db4ounit.common.cs.AllTests.class, 
				com.db4o.db4ounit.common.qlin.AllTests.class,
				};
	}
}
