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
package com.db4o.db4ounit.common.soda;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

// COR-18
public class SortMultipleTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] arguments) {
		new SortMultipleTestCase().runSolo();
	}

	public static class IntHolder {
		public int _value;

		public IntHolder(int value) {
			this._value = value;
		}
		
		public boolean equals(Object obj) {
			if(this==obj) {
				return true;
			}
			if(obj==null||getClass()!=obj.getClass()) {
				return false;
			}
			IntHolder intHolder=(IntHolder)obj;
			return _value==intHolder._value;
		}
		
		public int hashCode() {
			return _value;
		}
		
		public String toString() {
			return String.valueOf(_value);
		}
	}
	
	public static class Data {
		public int _first;
		public int _second;
		public IntHolder _third;
		
		public Data(int first, int second,int third) {
			this._first = first;
			this._second = second;
			this._third=new IntHolder(third);
		}

		public boolean equals(Object obj) {
			if(this==obj) {
				return true;
			}
			if(obj==null||getClass()!=obj.getClass()) {
				return false;
			}
			Data data=(Data)obj;
			return _first==data._first&&_second==data._second&&_third.equals(data._third);
		}
		
		public int hashCode() {
			int hc=_first;
			hc*=29+_second;
			hc*=29+_third.hashCode();
			return hc;
		}
		
		public String toString() {
			return _first+"/"+_second+"/"+_third;
		}
	}
	
	private final static Data[] TEST_DATA={
		new Data(1,2,4), // 0
		new Data(1,4,3), // 1
		new Data(2,4,2), // 2
		new Data(3,1,4), // 3
		new Data(4,3,1), // 4
		new Data(4,1,3)  // 5
	};
	
	protected void store() throws Exception {
		for (int dataIdx = 0; dataIdx < TEST_DATA.length; dataIdx++) {
			store(TEST_DATA[dataIdx]);
		}
	}
	
	public void testSortFirstThenSecondAfterOr() {
		Query query=newQuery(Data.class);
		
		query.descend("_first").constrain(2).smaller().or(
				query.descend("_second").constrain(2).greater());
		
		query.descend("_first").orderAscending();
		query.descend("_second").orderAscending();
		
		assertSortOrder(query, new int[]{0,1,2,4});
	}


	public void testSortFirstThenSecond() {
		Query query=newQuery(Data.class);
		query.descend("_first").orderAscending();
		query.descend("_second").orderAscending();
		assertSortOrder(query, new int[]{0,1,2,3,5,4});
	}

	public void testSortSecondThenFirst() {
		Query query=newQuery(Data.class);
		query.descend("_second").orderAscending();
		query.descend("_first").orderAscending();
		assertSortOrder(query, new int[]{3,5,0,4,1,2});
	}

	public void testSortThirdThenFirst() {
		Query query=newQuery(Data.class);
		query.descend("_third").descend("_value").orderAscending();
		query.descend("_first").orderAscending();
		assertSortOrder(query, new int[]{4,2,1,5,0,3});
	}

	public void testSortThirdThenSecond() {
		Query query=newQuery(Data.class);
		query.descend("_third").descend("_value").orderAscending();
		query.descend("_second").orderAscending();
		assertSortOrder(query, new int[]{4,2,5,1,3,0});
	}

	public void testSortSecondThenThird() {
		Query query=newQuery(Data.class);
		query.descend("_second").orderAscending();
		query.descend("_third").descend("_value").orderAscending();
		assertSortOrder(query, new int[]{5,3,0,4,2,1});
	}
	
	private void assertSortOrder(Query query, int[] expectedIndexes) {
		ObjectSet result=query.execute();
		Assert.areEqual(expectedIndexes.length,result.size());
		for (int i = 0; i < expectedIndexes.length; i++) {
			Assert.areEqual(TEST_DATA[expectedIndexes[i]], result.next());
		}
	}
}
