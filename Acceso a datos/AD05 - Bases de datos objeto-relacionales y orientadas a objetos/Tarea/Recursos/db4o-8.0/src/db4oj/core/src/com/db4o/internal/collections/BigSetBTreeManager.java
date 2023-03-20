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
package com.db4o.internal.collections;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;

/**
 * @exclude
 */
public class BigSetBTreeManager {
	
	private static final TransactionLocal<Map<Integer, BTree>> _bTreesInTransaction = new TransactionLocal() {
		@Override
		public Map<Integer, BTree> initialValueFor(Transaction transaction) {
			return new HashMap();
		}
	};
	
	private final Transaction _transaction;

	BigSetBTreeManager(Transaction transaction) {
		_transaction = transaction;
    }

	BTree produceBTree(int id) {
		assertValidBTreeId(id);
		BTree bTree = existingBTreeInTransactionWith(id);
		if (null == bTree) {
			bTree = newBTreeWithId(id);
			registerBTreeInTransaction(bTree);
		}
		return bTree;
	}

	BTree newBTree() {
		BTree bTree = newBTreeWithId(0);
		bTree.write(systemTransaction());
		registerBTreeInTransaction(bTree);
		return bTree;
	}
	
	void ensureIsManaged(BTree tree) {
		registerBTreeInTransaction(tree);
	}

	private BTree newBTreeWithId(int id) {
		return newBTreeWithId(id, systemTransaction());
	}

	private Transaction systemTransaction() {
		return _transaction.systemTransaction();
	}

	private static BTree newBTreeWithId(int id, final Transaction systemTransaction) {
	    return new BTree(systemTransaction, id, new IntHandler());
    }

	private static void assertValidBTreeId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException();
		}
	}

	private void registerBTreeInTransaction(BTree tree) {
		assertValidBTreeId(tree.getID());
		bTreesIn(_transaction).put(tree.getID(), tree);
	}

	private BTree existingBTreeInTransactionWith(int id) {
		return bTreesIn(_transaction).get(id);
	}

	private static Map<Integer, BTree> bTreesIn(final Transaction transaction) {
	    return transaction.get(_bTreesInTransaction).value;
    }
}
