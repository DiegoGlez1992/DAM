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
package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
@decaf.Ignore
public class ArrayList4TAMultiClientsTestCase extends ArrayList4TATestCaseBase implements OptOutSolo {
	public static void main(String[] args) {
		new ArrayList4TAMultiClientsTestCase().runEmbedded();
	}
	
	
	private static final ArrayList4Operation <Integer> _addOneElement = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.add(new Integer(ArrayList4Asserter.CAPACITY));
		}
	};
	
	private static final ArrayList4Operation<Integer> _removeFirstElement = new ArrayList4Operation<Integer>() {
		
		public void operate(ArrayList4<Integer> list) {
			list.remove(0);
		}
	};	
	
	private static final ArrayList4Operation<Integer> _setFirstElementTo1 = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.set(0, new Integer(1));
		}
	};	
	
	private static final ArrayList4Operation<Integer> _clearOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.clear();
		}
	};	

	private static final ArrayList4Operation<Integer> _containsOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			Assert.isFalse(list.contains(new Integer(ArrayList4Asserter.CAPACITY)));
		}
	};
	
	private static final ArrayList4Operation<Integer> _addAllOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			final Vector<Integer> v = new Vector<Integer>();
			for (int i = 0; i < ArrayList4Asserter.CAPACITY; ++i) {
				v.add(new Integer(ArrayList4Asserter.CAPACITY + i));
			}
			list.addAll(v);
		}
	};
	
	private static final ArrayList4Operation<Integer> _removeRangeOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.subList(ArrayList4Asserter.CAPACITY-10, ArrayList4Asserter.CAPACITY).clear();
		}
	};
	
	public void testAddAdd() throws Exception {
		ArrayList4Operation<Integer> anotherAddOp = new ArrayList4Operation<Integer>() {
			public void operate(ArrayList4<Integer> list) {
				list.add(new Integer(ArrayList4Asserter.CAPACITY + 42));
			}	
		};	
		operateOnClient1And2(anotherAddOp, _addOneElement);
		checkAdd();
	}

	public void testSetAdd() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _addOneElement);
		checkAdd();
	}
	
	public void testRemoveAdd() throws Exception {
		operateOnClient1And2(_removeFirstElement, _addOneElement);
		checkAdd();
	}
	
	private void checkAdd() throws Exception {
		checkListSizeAndContents(ArrayList4Asserter.CAPACITY+1);
	}
	
	private void checkNotModified() throws Exception {
		checkListSizeAndContents(ArrayList4Asserter.CAPACITY);
	}
	
	private void checkListSizeAndContents(int expectedSize) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(expectedSize, list.size());
		for (int i = 0; i < expectedSize; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAddRemove() throws Exception {
		operateOnClient1And2(_addOneElement, _removeFirstElement);
		checkRemove();
	}
	
	public void testsetRemove() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _removeFirstElement);
		checkRemove();
	}
	
	public void testRemoveRemove() throws Exception {
		ArrayList4Operation<Integer> anotherRemoveOp = new ArrayList4Operation<Integer>() {
			public void operate(ArrayList4<Integer> list) {
				list.remove(1);
			}	
		};	
		operateOnClient1And2(anotherRemoveOp, _removeFirstElement);
		checkRemove();
	}
	
	private void checkRemove() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(ArrayList4Asserter.CAPACITY - 1, list.size());
		for (int i = 0; i < ArrayList4Asserter.CAPACITY - 1; ++i) {
			Assert.areEqual(new Integer(i + 1), list.get(i));
		}
	}

	public void testAddSet() throws Exception {
		operateOnClient1And2(_addOneElement, _setFirstElementTo1);
		checkSet();
	}
	
	public void testRemoveSet() throws Exception {
		operateOnClient1And2(_removeFirstElement, _setFirstElementTo1);
		checkSet();
	}
	
	public void testSetSet() throws Exception {
		ArrayList4Operation<Integer>  anotherSetOp = new ArrayList4Operation<Integer>() {
			public void operate(ArrayList4<Integer> list) {
				list.set(0, new Integer(2));
			}	
		};
		operateOnClient1And2(anotherSetOp, _setFirstElementTo1);
		checkSet();
	}
	
	public void testClearSet() throws Exception {
		operateOnClient1And2(_clearOp, _setFirstElementTo1);
		checkSet();
	}
	
	public void testSetClear() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _clearOp);
		checkClear();
	}
	
	public void testClearRemove() throws Exception {
		operateOnClient1And2(_clearOp, _removeFirstElement);
		checkRemove();
	}
	
	public void testRemoveClear() throws Exception {
		operateOnClient1And2(_removeFirstElement, _clearOp);
		checkClear();
	}
	
	public void testContainsClear() throws Exception {
		operateOnClient1And2(_containsOp, _clearOp);
		checkClear();
	}
	
	public void testContainsSet() throws Exception {
		operateOnClient1And2(_containsOp, _setFirstElementTo1);
		checkSet();
	}
	
	public void testContainsRemove() throws Exception {
		operateOnClient1And2(_containsOp, _removeFirstElement);
		checkRemove();
	}
	
	public void testContainsAdd() throws Exception {
		operateOnClient1And2(_containsOp, _addOneElement);
		checkAdd();
	}
	
	public void testContainsRemoveRange() throws Exception {
		operateOnClient1And2(_containsOp, _removeRangeOp);
		checkRemoveRange();
	}
	
	public void testAddContains() throws Exception {
		operateOnClient1And2(_addOneElement, _containsOp);
		checkNotModified();
	}
	
	public void testSetContains() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _containsOp);
		checkNotModified();
	}
	
	public void testRemoveContains() throws Exception {
		operateOnClient1And2(_removeFirstElement, _containsOp);
		checkNotModified();
	}
	
	public void testClearContains() throws Exception {
		operateOnClient1And2(_clearOp, _containsOp);
		checkNotModified();
	}
	
	public void testRemoveRangeContains() throws Exception {
		operateOnClient1And2(_removeRangeOp, _containsOp);
		checkNotModified();
	}
	
	public void testAddAllSet() throws Exception {
		operateOnClient1And2(_addAllOp, _setFirstElementTo1);
		checkSet();		
	}
	
	public void testAddAllClear() throws Exception {
		operateOnClient1And2(_addAllOp, _clearOp);
		checkClear();		
	}
	
	public void testAddAllRemove() throws Exception {
		operateOnClient1And2(_addAllOp, _removeFirstElement);
		checkRemove();		
	}
	
	public void testAddAllAdd() throws Exception {
		operateOnClient1And2(_addAllOp, _addOneElement);
		checkAdd();		
	}

	public void testSetAddAll() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _addAllOp);
		checkAddAll();		
	}
	
	public void testClearAddAll() throws Exception {
		operateOnClient1And2(_clearOp, _addAllOp);
		checkAddAll();		
	}
	
	public void testRemoveAddAll() throws Exception {
		operateOnClient1And2(_removeFirstElement, _addAllOp);
		checkAddAll();		
	}
	
	public void testAddAddAll() throws Exception {
		operateOnClient1And2(_addOneElement, _addAllOp);
		checkAddAll();		
	}
	
	public void testRemoveRangeSet() throws Exception {
		operateOnClient1And2(_removeRangeOp, _setFirstElementTo1);
		checkSet();		
	}

	public void testRemoveRangeAdd() throws Exception {
		operateOnClient1And2(_removeRangeOp, _addOneElement);
		checkAdd();		
	}

	public void testRemoveRangeClear() throws Exception {
		operateOnClient1And2(_removeRangeOp, _clearOp);
		checkClear();		
	}

	public void testRemoveRangeAddAll() throws Exception {
		operateOnClient1And2(_removeRangeOp, _addAllOp);
		checkAddAll();		
	}

	public void testRemoveRangeRemove() throws Exception {
		operateOnClient1And2(_removeRangeOp, _removeFirstElement);
		checkRemove();		
	}
	
	public void testSetRemoveRange() throws Exception {
		operateOnClient1And2(_setFirstElementTo1, _removeRangeOp);
		checkRemoveRange();		
	}
	
	public void testAddRemoveRange() throws Exception {
		operateOnClient1And2(_addOneElement, _removeRangeOp);
		checkRemoveRange();		
	}

	public void testClearRemoveRange() throws Exception {
		operateOnClient1And2(_clearOp, _removeRangeOp);
		checkRemoveRange();		
	}
	
	public void testAddAllRemoveRange() throws Exception {
		operateOnClient1And2(_addAllOp, _removeRangeOp);
		checkRemoveRange();		
	}
	
	public void testRemoveRemoveRange() throws Exception {
		operateOnClient1And2(_removeFirstElement, _removeRangeOp);
		checkRemoveRange();		
	}
	
	private void checkRemoveRange() throws Exception {
		checkListSizeAndContents(ArrayList4Asserter.CAPACITY-10);
	}
	
	private void checkAddAll() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		for (int i = 0; i < ArrayList4Asserter.CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	private void checkClear() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(0, list.size());
	}

	private void checkSet() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(ArrayList4Asserter.CAPACITY, list.size());
		Assert.areEqual(new Integer(1), list.get(0));
		for (int i = 1; i < ArrayList4Asserter.CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}
	
	private void operateOnClient1And2(ArrayList4Operation <Integer> op1, ArrayList4Operation<Integer> op2) throws Exception {
		ExtObjectContainer client1 = openNewSession();
		ExtObjectContainer client2 = openNewSession();
		ArrayList4<Integer> list1 = retrieveAndAssertNullArrayList4(client1);
		ArrayList4<Integer> list2 = retrieveAndAssertNullArrayList4(client2);
		op1.operate(list1);
		op2.operate(list2);
		client1.store(list1);
		client2.store(list2);
		client1.commit();
		client2.commit();
		client1.close();
		client2.close();
	}

}
