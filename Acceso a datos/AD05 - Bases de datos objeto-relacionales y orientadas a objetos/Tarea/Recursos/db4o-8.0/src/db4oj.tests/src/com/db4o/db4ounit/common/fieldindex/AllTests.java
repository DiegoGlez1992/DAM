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
package com.db4o.db4ounit.common.fieldindex;


import db4ounit.extensions.*;

public class AllTests extends ComposibleTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
		return composeTests(new Class[] {
				IndexedNodeTestCase.class,
	            FieldIndexTestCase.class,
	            FieldIndexProcessorTestCase.class,
	            StringFieldIndexTestCase.class,
				DoubleFieldIndexTestCase.class,
				RuntimeFieldIndexTestCase.class,
				SecondLevelIndexTestCase.class,
				StringFieldIndexDefragmentTestCase.class,
	            StringIndexTestCase.class,
	            StringIndexCorruptionTestCase.class,
	            StringIndexWithSuperClassTestCase.class,
		});
    }
	
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	@Override
	protected Class[] composeWith() {
		return new Class[] {
				CommitAfterDroppedFieldIndexTestCase.class,
				};
	}

}
