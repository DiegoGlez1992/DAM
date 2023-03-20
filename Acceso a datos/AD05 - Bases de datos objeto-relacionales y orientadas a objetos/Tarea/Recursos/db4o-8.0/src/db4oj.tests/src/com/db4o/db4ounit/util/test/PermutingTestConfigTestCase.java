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
package com.db4o.db4ounit.util.test;

import com.db4o.db4ounit.util.*;

import db4ounit.*;

public class PermutingTestConfigTestCase implements TestCase {

	public void testPermutation() {
		Object[][] data= new Object[][] {
				new Object[] {"A","B"},
				new Object[] {"X","Y","Z"},
		};
		final PermutingTestConfig config=new PermutingTestConfig(data);
		Object[][] expected= new Object[][] {
				new Object[] {"A","X"},	
				new Object[] {"A","Y"},	
				new Object[] {"A","Z"},	
				new Object[] {"B","X"},	
				new Object[] {"B","Y"},	
				new Object[] {"B","Z"},	
		};
		for (int groupIdx = 0; groupIdx < expected.length; groupIdx++) {
			Assert.isTrue(config.moveNext());
			Object[] current={config.current(0),config.current(1)};
			ArrayAssert.areEqual(expected[groupIdx],current);
		}
		Assert.isFalse(config.moveNext());
	}
	
}
