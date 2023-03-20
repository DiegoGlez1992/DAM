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

import db4ounit.extensions.*;


public class BTreeRollbackTestCase extends BTreeTestCaseBase {

	public static void main(String[] args) {
		new BTreeRollbackTestCase().runSolo();
	}
	
	private static final int[] COMMITTED_VALUES = new int[]{ 6,8,15,45, 43, 9,23, 25,7,3,2};
	
	private static final int[] ROLLED_BACK_VALUES = new int[]{ 16,18,115,19,17,13,12};
	
	public void test(){
		add(COMMITTED_VALUES);
		commitBTree();
		for (int i = 0; i < 5; i++) {
			add(ROLLED_BACK_VALUES);
			rollbackBTree();
		}
		BTreeAssert.assertKeys(trans(), _btree, COMMITTED_VALUES);
	}

	private void commitBTree() {
		_btree.commit(trans());
		trans().commit();
	}

	private void rollbackBTree() {
		_btree.rollback(trans());
		trans().rollback();
	}
	
}
