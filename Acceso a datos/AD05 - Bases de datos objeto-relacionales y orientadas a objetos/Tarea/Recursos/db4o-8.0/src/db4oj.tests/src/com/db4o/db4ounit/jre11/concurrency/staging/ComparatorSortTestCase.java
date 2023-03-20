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
package com.db4o.db4ounit.jre11.concurrency.staging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ComparatorSortTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new ComparatorSortTestCase().runConcurrency();
	}
	
	public static class AscendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) first)._id - ((ComparatorSortTestCase) second)._id;
		}
	}

	public static class DescendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) second)._id - ((ComparatorSortTestCase) first)._id;
		}
	}

	public static class OddEvenIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			int idA = ((ComparatorSortTestCase) first)._id;
			int idB = ((ComparatorSortTestCase) second)._id;
			int modA = idA % 2;
			int modB = idB % 2;
			if (modA != modB) {
				return modA - modB;
			}
			return idA - idB;
		}
	}

	public static class AscendingNameComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) first)._name
					.compareTo(((ComparatorSortTestCase) second)._name);
		}
	}

	public static class SmallerThanThirtyPredicate extends Predicate {
		public boolean match(Object candidate) {
			return ((ComparatorSortTestCase)candidate)._id < 30;
		}
	}

	public int _id;

	public String _name;

	public ComparatorSortTestCase() {
		this(0, null);
	}

	public ComparatorSortTestCase(int id, String name) {
		this._id = id;
		this._name = name;
	}

	protected void configure(Configuration config) {
		config.exceptionsOnNotStorable(true);
	}

	protected void store() {
		for (int i = 30; i >= 0; --i) {
			String name = i < 10 ? "0" + i : String.valueOf(i);
			store(new ComparatorSortTestCase(i, name));
		}
	}

	public void conc(ExtObjectContainer oc) {
		assertByIdAscending(oc);
		assertByIdAscendingConstrained(oc);
		assertByIdAscendingNQ(oc);
		
		assertByIdDescending(oc);
		asertByIdDescendingConstrained(oc);
		assertByIdDescendingNQ(oc);
		
		assertByIdOddEven(oc);
		assertByIdOddEvenConstrained(oc);
		assertByIdOddEvenNQ(oc);
		
		assertByNameAscending(oc);
		assertByNameAscendingConstrained(oc);
		assertByNameAscendingNQ(oc);
	}
	
	public void assertByIdAscending(ExtObjectContainer oc) {
		assertIdOrder(oc,new AscendingIdComparator(), range(0, 31));
	}

	public void assertByIdAscendingConstrained(ExtObjectContainer oc) {
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new AscendingIdComparator(), range(0, 30));
	}

	public void assertByIdAscendingNQ(ExtObjectContainer oc) {
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),
				new AscendingIdComparator());
		assertIdOrder(result, range(0, 30));
	}

	public void assertByIdDescending(ExtObjectContainer oc) {
		int[] expected = descendingRange(30);
		assertIdOrder(oc, new DescendingIdComparator(), expected);
	}
	
	private int[] range(final int begin, final int end) {
		int[] expected = new int[end];
		for (int i = begin; i < end; ++i) {
			expected[i] = i;
		}
		return expected;
	}

	private int[] descendingRange(int begin) {
		int[] range = new int[begin+1];
		for (int i = 0; i <= begin; ++i) {
			range[i] = begin-i;
		}
		return range;
	}

	public void asertByIdDescendingConstrained(ExtObjectContainer oc) {
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new DescendingIdComparator(), descendingRange(29));
	}

	public void assertByIdDescendingNQ(ExtObjectContainer oc) {
		int[] expected = descendingRange(29);
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),
				new DescendingIdComparator());
		assertIdOrder(result, expected);
	}
	
	public void assertByIdOddEven(ExtObjectContainer oc) {
		int[] expected = new int[31];
		int i = 0;
		for (; i <= 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		assertIdOrder(oc,new OddEvenIdComparator(), expected);
	}

	public void assertByIdOddEvenConstrained(ExtObjectContainer oc) {
		int[] expected = new int[30];
		int i = 0;
		for (; i < 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new OddEvenIdComparator(), expected);
	}

	public void assertByIdOddEvenNQ(ExtObjectContainer oc) {
		int[] expected = new int[30];
		int i = 0;
		for (; i < 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		ObjectSet result = oc.query(
				new SmallerThanThirtyPredicate(), new OddEvenIdComparator());
		assertIdOrder(result, expected);
	}
	
	public void assertByNameAscending(ExtObjectContainer oc) {
		assertIdOrder(oc, new AscendingNameComparator(), range(0, 31));
	}

	public void assertByNameAscendingConstrained(ExtObjectContainer oc) {
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new AscendingNameComparator(),range(0, 30));
	}

	public void assertByNameAscendingNQ(ExtObjectContainer oc) {
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),new AscendingNameComparator());
		assertIdOrder(result, range(0, 30));
	}

	private void assertIdOrder(ExtObjectContainer oc,
			QueryComparator comparator, int[] ids) {
		Query query = oc.query();
		query.constrain(ComparatorSortTestCase.class);
		assertIdOrder(query, comparator, ids);
	}

	private void assertIdOrder(Query query, QueryComparator comparator,
			int[] ids) {
		query.sortBy(comparator);
		ObjectSet result = query.execute();
		assertIdOrder(result, ids);
	}

	private void assertIdOrder(ObjectSet result, int[] ids) {
		Assert.areEqual(ids.length, result.size());
		for (int idx = 0; idx < ids.length; idx++) {
			Assert.areEqual(ids[idx], ((ComparatorSortTestCase) result.next())._id);
		}
	}
}
