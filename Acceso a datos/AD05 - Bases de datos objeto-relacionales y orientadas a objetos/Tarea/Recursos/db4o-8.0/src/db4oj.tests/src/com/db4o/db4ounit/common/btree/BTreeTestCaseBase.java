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
package com.db4o.db4ounit.common.btree;

import com.db4o.internal.*;
import com.db4o.internal.btree.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public abstract class BTreeTestCaseBase extends AbstractDb4oTestCase implements
		OptOutMultiSession {

	protected static final int BTREE_NODE_SIZE = 4;

	protected BTree _btree;

	protected void db4oSetupAfterStore() throws Exception {
		_btree = newBTree();
	}

	protected BTree newBTree() {
		return BTreeAssert.createIntKeyBTree(container(), 0, BTREE_NODE_SIZE);
	}

	protected BTreeRange range(int lower, int upper) {
		final BTreeRange lowerRange = search(lower);
		final BTreeRange upperRange = search(upper);
		return lowerRange.extendToLastOf(upperRange);
	}

	protected BTreeRange search(int key) {
		return search(trans(), key);
	}

	protected void add(int[] keys) {
		for (int i = 0; i < keys.length; ++i) {
			add(keys[i]);
		}
	}

	protected BTreeRange search(Transaction trans, int key) {
		return _btree.searchRange(trans, new Integer(key));
	}

	protected void commit(Transaction trans) {
		_btree.commit(trans);
	}

	protected void commit() {
		commit(trans());
	}

	protected void remove(Transaction transaction, int[] keys) {
		for (int i = 0; i < keys.length; i++) {
			remove(transaction, keys[i]);
		}
	}

	protected void add(Transaction transaction, int[] keys) {
		for (int i = 0; i < keys.length; i++) {
			add(transaction, keys[i]);
		}
	}

	protected void assertEmpty(Transaction transaction) {
		BTreeAssert.assertEmpty(transaction, _btree);
	}

	protected void add(Transaction transaction, int element) {
		_btree.add(transaction, new Integer(element));
	}

	protected void remove(final int element) {
		remove(trans(), element);
	}

	protected void remove(final Transaction trans, final int element) {
		_btree.remove(trans, new Integer(element));
	}

	protected void add(int element) {
		add(trans(), element);
	}

	private int size() {
		return _btree.size(trans());
	}

	protected void assertSize(int expected) {
		Assert.areEqual(expected, size());
	}

	protected void assertSingleElement(final int element) {
		assertSingleElement(trans(), element);
	}

	protected void assertSingleElement(final Transaction trans,
			final int element) {
		BTreeAssert.assertSingleElement(trans, _btree, new Integer(element));
	}

	protected void assertPointerKey(int key, BTreePointer pointer) {
		Assert.areEqual(new Integer(key), pointer.key());
	}
}
