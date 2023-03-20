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
package com.db4o.db4ounit.jre12.soda.collections;
import java.util.*;

import com.db4o.query.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class STTreeSetTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {

	TreeSet col;

	public STTreeSetTTestCase() {
	}

	public STTreeSetTTestCase(Object[] arr) {
		col = new TreeSet();
		for (int i = 0; i < arr.length; i++) {
			col.add(arr[i]);
		}
	}

	public Object[] createData() {
		return new Object[] {
			new STTreeSetTTestCase(),
			new STTreeSetTTestCase(new Object[0]),
			new STTreeSetTTestCase(new Object[] { new Integer(0), new Integer(0)}),
			new STTreeSetTTestCase(
				new Object[] {
					new Integer(1),
					new Integer(17),
					new Integer(Integer.MAX_VALUE - 1)}),
			new STTreeSetTTestCase(
				new Object[] {
					new Integer(3),
					new Integer(17),
					new Integer(25),
					new Integer(Integer.MAX_VALUE - 2)})
		};
	}

	public void testDefaultContainsInteger() {
		Query q = newQuery();
		
		q.constrain(new STTreeSetTTestCase(new Object[] { new Integer(17)}));
		expect(q, new int[] { 3, 4 });
	}

	public void testDefaultContainsTwo() {
		Query q = newQuery();
		
		q.constrain(new STTreeSetTTestCase(new Object[] { new Integer(17), new Integer(25)}));
		expect(q, new int[] { 4 });
	}

	public void testDescendOne() {
		Query q = newQuery();
		
		q.constrain(STTreeSetTTestCase.class);
		q.descend("col").constrain(new Integer(17));
		expect(q, new int[] { 3, 4 });
	}

	public void testDescendTwo() {
		Query q = newQuery();
		
		q.constrain(STTreeSetTTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		expect(q, new int[] { 4 });
	}

	public void testDescendSmaller() {
		Query q = newQuery();
		
		q.constrain(STTreeSetTTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] { 2, 3 });
	}
	

}