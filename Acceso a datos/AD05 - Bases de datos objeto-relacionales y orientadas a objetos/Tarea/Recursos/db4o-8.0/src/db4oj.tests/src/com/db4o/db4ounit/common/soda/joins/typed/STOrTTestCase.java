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
package com.db4o.db4ounit.common.soda.joins.typed;
import com.db4o.query.*;


public class STOrTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {

	public int orInt;
	public String orString;

	public STOrTTestCase() {
	}

	private STOrTTestCase(int a_int, String a_string) {
		orInt = a_int;
		orString = a_string;
	}

	public String toString() {
		return "STOr: int:" + orInt + " str:" + orString;
	}

	public Object[] createData() {
		return new Object[] {
			new STOrTTestCase(0, "hi"),
			new STOrTTestCase(5, null),
			new STOrTTestCase(1000, "joho"),
			new STOrTTestCase(30000, "osoo"),
			new STOrTTestCase(Integer.MAX_VALUE - 1, null),
			};
	}
	
	public void testSmallerGreater() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		sub.constrain(new Integer(30000)).greater().or(
			sub.constrain(new Integer(5)).smaller());
		
		expect(q, new int[] { 0, 4 });
	}

	public void testGreaterGreater() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		sub.constrain(new Integer(30000)).greater().or(
			sub.constrain(new Integer(5)).greater());
		
		expect(q, new int[] { 2, 3, 4 });
	}

	public void testGreaterEquals() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		sub.constrain(new Integer(1000)).greater().or(
			sub.constrain(new Integer(0)));
		
		expect(q, new int[] { 0, 3, 4 });
	}

	public void testEqualsNull() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(1000, null));
		q.descend("orInt").constraints().or(
			q.descend("orString").constrain(null));
		
		expect(q, new int[] { 1, 2, 4 });
	}


	public void testAndOrAnd() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(5)).and(
			q.descend("orString").constrain(null))
		).or(
			q.descend("orInt").constrain(new Integer(1000)).and(
			q.descend("orString").constrain("joho"))
		);
		
		expect(q, new int[] { 1, 2});
	}
	
	public void testOrAndOr() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(5)).or(
			q.descend("orString").constrain(null))
		).and(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
			q.descend("orString").constrain("joho"))
		);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[4]);
	}
	
	public void testOrOrAnd() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
			q.descend("orString").constrain("joho"))
		).or(
			q.descend("orInt").constrain(new Integer(5)).and(
			q.descend("orString").constrain(null))
		);
		
		expect(q, new int[] { 1, 2, 4});
	}
	
	public void testMultiOrAnd(){
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			(
				q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
				q.descend("orString").constrain("joho"))
			).or(
				q.descend("orInt").constrain(new Integer(5)).and(
				q.descend("orString").constrain("joho"))
			)
		).or(
			(
				q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
				q.descend("orString").constrain(null))
			).and(
				q.descend("orInt").constrain(new Integer(5)).or(
				q.descend("orString").constrain(null))
			)
		);
		
		expect(q, new int[] {1,  2, 4});
	}
	
	public void testNotSmallerGreater() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		(sub.constrain(new Integer(30000)).greater().or(
			sub.constrain(new Integer(5)).smaller())).not();
		
		expect(q, new int[] { 1, 2, 3 });
	}

	public void testNotGreaterGreater() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		(sub.constrain(new Integer(30000)).greater().or(
			sub.constrain(new Integer(5)).greater())).not();
		
		expect(q, new int[] { 0, 1});
	}

	public void testNotGreaterEquals() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase());
		Query sub = q.descend("orInt");
		(sub.constrain(new Integer(1000)).greater().or(
			sub.constrain(new Integer(0)))).not();
		
		expect(q, new int[] { 1, 2});
	}

	public void testNotEqualsNull() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(1000, null));
		(q.descend("orInt").constraints().or(
			q.descend("orString").constrain(null))).not();
		
		expect(q, new int[] { 0, 3});
	}

	public void testNotAndOrAnd() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(5)).and(
			q.descend("orString").constrain(null))
		).or(
			q.descend("orInt").constrain(new Integer(1000)).and(
			q.descend("orString").constrain("joho"))
		).not();
		
		expect(q, new int[] { 0, 3, 4});
	}
	
	public void testNotOrAndOr() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(5)).or(
			q.descend("orString").constrain(null))
		).and(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
			q.descend("orString").constrain("joho"))
		).not();
		
		expect(q, new int[] { 0, 1, 2, 3});
	}
	
	public void testNotOrOrAnd() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
			q.descend("orString").constrain("joho"))
		).or(
			q.descend("orInt").constrain(new Integer(5)).and(
			q.descend("orString").constrain(null))
		).not();
		
		expect(q, new int[] { 0, 3});
	}
	
	public void testNotMultiOrAnd(){
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			(
				q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
				q.descend("orString").constrain("joho"))
			).or(
				q.descend("orInt").constrain(new Integer(5)).and(
				q.descend("orString").constrain("joho"))
			)
		).or(
			(
				q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
				q.descend("orString").constrain(null))
			).and(
				q.descend("orInt").constrain(new Integer(5)).or(
				q.descend("orString").constrain(null))
			)
		).not();
		
		expect(q, new int[] {0,  3});
	}
	
	public void testOrNotAndOr() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).or(
			q.descend("orString").constrain("joho"))
		).not().and(
			q.descend("orInt").constrain(new Integer(5)).or(
			q.descend("orString").constrain(null))
		);
		
		expect(q, new int[] { 1});
	}
	
	public void testAndNotAndAnd() {
		Query q = newQuery();
		q.constrain(new STOrTTestCase(0, null));
		(
			q.descend("orInt").constrain(new Integer(Integer.MAX_VALUE - 1)).and(
			q.descend("orString").constrain(null))
		).not().and(
			q.descend("orInt").constrain(new Integer(5)).or(
			q.descend("orString").constrain("osoo"))
		);
		
		expect(q, new int[] { 1, 3});
	}
	
}
