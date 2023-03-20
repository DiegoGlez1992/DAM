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

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BTreeSearchTestCase extends AbstractDb4oTestCase implements
		OptOutDefragSolo, OptOutMultiSession {

	protected static final int BTREE_NODE_SIZE = 4;

	public static void main(String[] arguments) {
		new BTreeSearchTestCase().runSolo();
	}

	public void test() throws Exception {
		cycleIntKeys(new int[] { 3, 5, 7, 10, 11, 12, 14, 15, 17, 20, 21, 25 });
	}

	private void cycleIntKeys(int[] values) throws Exception {
		BTree btree = BTreeAssert.createIntKeyBTree(container(), 0,
				BTREE_NODE_SIZE);
		for (int i = 0; i < 5; i++) {
			btree = cycleIntKeys(btree, values);
		}
	}

	private BTree cycleIntKeys(BTree btree, int[] values) throws Exception {
		for (int i = 0; i < values.length; i++) {
			btree.add(trans(), new Integer(values[i]));
		}
		expectKeysSearch(trans(), btree, values);

		btree.commit(trans());

		int id = btree.getID();

		container().commit(trans());

		reopen();

		btree = BTreeAssert.createIntKeyBTree(container(), id, BTREE_NODE_SIZE);

		expectKeysSearch(trans(), btree, values);

		for (int i = 0; i < values.length; i++) {
			btree.remove(trans(), new Integer(values[i]));
		}

		BTreeAssert.assertEmpty(trans(), btree);

		btree.commit(trans());

		BTreeAssert.assertEmpty(trans(), btree);

		return btree;
	}

	private void expectKeysSearch(Transaction trans, BTree btree, int[] keys) {
		int lastValue = Integer.MIN_VALUE;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != lastValue) {
				ExpectingVisitor expectingVisitor = ExpectingVisitor
						.createExpectingVisitor(keys[i], IntArrays4.occurences(
								keys, keys[i]));
				BTreeRange range = btree.searchRange(trans, new Integer(keys[i]));
				BTreeAssert.traverseKeys(range, expectingVisitor);
				expectingVisitor.assertExpectations();
				lastValue = keys[i];
			}
		}
	}
}
