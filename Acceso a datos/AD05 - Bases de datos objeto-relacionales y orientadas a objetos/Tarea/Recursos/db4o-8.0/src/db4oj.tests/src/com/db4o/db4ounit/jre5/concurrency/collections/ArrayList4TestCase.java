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
package com.db4o.db4ounit.jre5.concurrency.collections;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre5.collections.*;
import com.db4o.ext.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */

/**
 */
@decaf.Ignore
public class ArrayList4TestCase extends Db4oConcurrencyTestCase {
	public static void main(String[] args) {
		new ArrayList4TestCase().runEmbeddedConcurrency();
	}

	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ArrayList4Asserter.createList(list);
		store(list);
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		super.configure(config);
	}
	
	public void conc(ExtObjectContainer oc) throws Exception {
		retrieveAndAssertNullArrayList4(oc);
	}
	
	public void concAdd(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertAdd(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkAdd(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkAdd(list);
	}
	
	public void concAdd_LObject(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertAdd_LObject(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}

	public void checkAdd_LObject(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkAdd_LObject(list);
	}
	
	public void concAddAll_LCollection(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertAddAll_LCollection(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}

	public void checkAddAll_LCollection(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkAddAll_LCollection(list);
	}
	
	public void concClear(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertClear(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkClear(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkClear(list);
	}

	public void concContains(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertContains(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concContainsAll(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertContainsAll(retrieveAndAssertNullArrayList4(oc));
	}

	public void concIndexOf(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIndexOf(retrieveAndAssertNullArrayList4(oc));
	}

	public void concIsEmpty(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIsEmpty(retrieveAndAssertNullArrayList4(oc));
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
	}

	public void concIterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIterator(retrieveAndAssertNullArrayList4(oc));
	}

	public void concLastIndexOf(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertLastIndexOf(retrieveAndAssertNullArrayList4(oc));
	}

	public void concRemove_LObject(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertRemove_LObject(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkRemove_LObject(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkRemove_LObject(list);
	}
	

	public void concRemoveAll(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertRemoveAll(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkRemoveAll(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkRemoveAll(list);
	}

	public void concSet(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertSet(retrieveAndAssertNullArrayList4(oc));
	}

	public void concSize(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertSize(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToArray(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertToArray(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToArray_LObject(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertToArray_LObject(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToString(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertToString(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concTrimToSize_EnsureCapacity(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkTrimToSize_EnsureCapacity(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.checkTrimToSize_EnsureCapacity(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concExtOTrimToSize_Remove(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertTrimToSize_Remove(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concTrimToSize_Iterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concEnsureCapacity_Iterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concClear_Iterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertClear_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concClone(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertClone(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concEquals(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertEquals(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext_NoSuchElementException(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIteratorNext_NoSuchElementException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext_ConcurrentModificationException(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIteratorNext_ConcurrentModificationException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIteratorNext(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorRemove(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIteratorRemove(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concRemove_IllegalStateException(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertRemove_IllegalStateException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorRemove_ConcurrentModificationException(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertIteratorRemove_ConcurrentModificationException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concSubList(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertSubList(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concSubList_ConcurrentModification(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertSubList_ConcurrentModification(retrieveAndAssertNullArrayList4(oc));
	}
	
	private ArrayList4<Integer> retrieveAndAssertNullArrayList4(ExtObjectContainer oc) throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(oc, reflector());
	}
}
