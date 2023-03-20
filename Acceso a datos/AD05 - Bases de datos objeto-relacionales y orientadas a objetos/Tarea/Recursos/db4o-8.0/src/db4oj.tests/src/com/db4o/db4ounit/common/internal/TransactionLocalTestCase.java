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
package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransactionLocalTestCase extends AbstractInMemoryDb4oTestCase {
	
	static class Item {
		
		public final Transaction transaction;

		public Item(Transaction transaction) {
			this.transaction = transaction;
		}
	}
	
	private final TransactionLocal<Item> _subject = new TransactionLocal<Item>() {
		@Override
		public Item initialValueFor(Transaction transaction) {
			return new Item(transaction);
		}
	};
	
	private Transaction t1;
	private Transaction t2;
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		t1 = newTransaction();
		t2 = newTransaction();
	}
	
	public void testValueRemainsTheSame() {
		Assert.areSame(itemFor(t1), itemFor(t1));
		Assert.areSame(itemFor(t2), itemFor(t2));
	}
	
	public void testDifferentValuesForDifferentTransactions() {
		Assert.areNotSame(itemFor(t1), itemFor(t2));
	}
	
	public void testInitialValueTransaction() {
		Assert.areSame(t1, itemFor(t1).transaction);
		Assert.areSame(t2, itemFor(t2).transaction);
	}
	
	public void testValuesAreDisposedOfOnCommit() {
		final Item itemBeforeCommit = itemFor(t1);
		t1.commit();
		final Item itemAfterCommit = itemFor(t1);
		Assert.areNotSame(itemAfterCommit, itemBeforeCommit);
	}
	
	public void testValuesAreDisposedOfOnRollback() {
		final Item itemBeforeRollback = itemFor(t1);
		t1.rollback();
		final Item itemAfterRollback = itemFor(t1);
		Assert.areNotSame(itemAfterRollback, itemBeforeRollback);
	}

	private Item itemFor(final Transaction transaction) {
	    return transaction.get(_subject).value;
    }

}
