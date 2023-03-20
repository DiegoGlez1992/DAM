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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class StringIndexTestCase extends StringIndexTestCaseBase implements OptOutMultiSession {
	
	public static void main(String[] args) {
		new StringIndexTestCase().runSolo();
	}
    
    public void testNotEquals() {
    	add("foo");
    	add("bar");
    	add("baz");
    	add(null);
    	
    	final Query query = newQuery(Item.class);
    	query.descend("name").constrain("bar").not();
		assertItems(new String[] { "foo", "baz", null }, query.execute());
    }

	public void testCancelRemovalRollback() throws Exception {
    	
    	prepareCancelRemoval(trans(), "original");
    	rename("original", "updated");
    	db().rollback();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }
    
    public void testCancelRemovalRollbackForMultipleTransactions() throws Exception {
    	final Transaction trans1 = newTransaction();
    	final Transaction trans2 = newTransaction();
        
        prepareCancelRemoval(trans1, "original");
        assertExists(trans2, "original");
    	
        trans1.rollback();
        assertExists(trans2, "original");
        
        add(trans2, "second");
        assertExists(trans2, "original");
        
        trans2.commit();
        assertExists(trans2, "original");
        
    	grafittiFreeSpace();
        reopen();
        assertExists("original");
    }
    
    public void testCancelRemoval() throws Exception {
    	prepareCancelRemoval(trans(), "original");
    	db().commit();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }

	private void prepareCancelRemoval(Transaction transaction, String itemName) {
		add(itemName);    	
    	db().commit();
    	
    	rename(transaction, itemName, "updated");    	
    	assertExists(transaction, "updated");
    	
    	rename(transaction, "updated", itemName);
    	assertExists(transaction, itemName);
	}
    
    public void testCancelRemovalForMultipleTransactions() throws Exception {
    	final Transaction trans1 = newTransaction();
    	final Transaction trans2 = newTransaction();
    	
    	prepareCancelRemoval(trans1, "original");
    	rename(trans2, "original", "updated");    	
    	trans1.commit();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }
    
    public void testDeletingAndReaddingMember() throws Exception{
		add("original");
    	assertExists("original");
        rename("original", "updated");        
        assertExists("updated");
        Assert.isNull(query("original"));
        reopen();        
        assertExists("updated");
        Assert.isNull(query("original"));
    }
}
