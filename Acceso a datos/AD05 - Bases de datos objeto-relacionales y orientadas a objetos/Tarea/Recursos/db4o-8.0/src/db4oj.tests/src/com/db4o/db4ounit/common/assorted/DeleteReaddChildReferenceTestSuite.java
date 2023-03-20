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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * COR-1539  Readding a deleted object from a different client changes database ID in embedded mode
 */
public class DeleteReaddChildReferenceTestSuite extends FixtureTestSuiteDescription implements Db4oTestCase {
	
	{
		fixtureProviders(new SubjectFixtureProvider(true, false), new Db4oFixtureProvider());
		testUnits(DeleteReaddChildReferenceTestUnit.class);
	}
	
	public static class DeleteReaddChildReferenceTestUnit extends Db4oClientServerTestCase{
		
		
	    private static final String ITEM_NAME = "child";
		private ExtObjectContainer client1;
		private ExtObjectContainer client2;
	
		public static class ItemParent {
	    	
	    	public Item child;
	        
	    }
	    
	    public static class Item {
	        
	        public String name;
	        
	        public Item(String name_){
	            name = name_;
	        }
	    }
	    
	    @Override
	    protected void configure(Configuration config) throws Exception {
	    	if (!useIndices()) {
	    		return;
	    	}
	    	indexField(config, ItemParent.class, ITEM_NAME);
	    	indexField(config, Item.class, "name");
	    }

		private Boolean useIndices() {
			return SubjectFixtureProvider.<Boolean>value();
		}
	    
	    protected void store() throws Exception {
	        Item child = new Item(ITEM_NAME);
	        ItemParent parent = new ItemParent();
	        parent.child = child;
			store(parent);
	    }
	    
	    public void testDeleteReaddFromOtherClient() {
	    	if(!prepareTest()) {
	    		return;
	    	}
			ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
	        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
	        client1.delete(parent1.child);        
	        assertQueries(0, 1);
	        client1.commit();
	        assertQueries(0, 0);
	        client2.store(parent2.child);
	        assertQueries(0, 1);
	        client2.commit();
	        assertQueries(1, 1);
	        client2.close();	
	        assertRestoredState();
	    }

	    public void testDeleteReaddTwiceFromOtherClient() {
	    	if(!prepareTest()) {
	    		return;
	    	}
			ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
	        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
	        client1.delete(parent1.child);        
	        assertQueries(0, 1);
	        client1.commit();
	        assertQueries(0, 0);
	        client2.store(parent2.child);
	        assertQueries(0, 1);
	        client2.store(parent2.child);
	        assertQueries(0, 1);
	        client2.commit();
	        assertQueries(1, 1);
	        client2.close();	
	        assertRestoredState();
	    }

	    public void testDeleteReaddFromBoth() {
	    	if(!prepareTest()) {
	    		return;
	    	}
			ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
	        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
	        client1.delete(parent1.child);        
	        assertQueries(0, 1);
	        client2.delete(parent2.child);        
	        assertQueries(0, 0);
	        client1.store(parent1.child);
	        assertQueries(1, 0);
	        client2.store(parent2.child);
	        assertQueries(1, 1);
	        client1.commit();
	        assertQueries(1, 1);
	        client2.commit();
	        assertQueries(1, 1);
	        client2.close();	
	        assertRestoredState();
	    }

		private void assertRestoredState() {
			ItemParent parent3 = retrieveOnlyInstance(client1, ItemParent.class);
	        db().refresh(parent3, Integer.MAX_VALUE);
	        Assert.isNotNull(parent3);
	        Assert.isNotNull(parent3.child);
		}

		private void assertQueries(int exp1, int exp2) {
			assertQuery(exp1, client1);
	        assertQuery(exp2, client2);
		}

		private boolean prepareTest() {
			if (!isMultiSession()){
	    		return false;
	    	}
	        client1 = db();
	        client2 = openNewSession();
	        return true;
		}
	
	    private void assertQuery(int expectedCount, ExtObjectContainer queryClient) {
	    	assertChildClassOnlyQuery(expectedCount, queryClient);
	    	assertParentChildQuery(expectedCount, queryClient);
	    	assertChildQuery(expectedCount, queryClient);
	    }
	    
		private void assertParentChildQuery(int expectedCount, ExtObjectContainer queryClient) {
			Query query = queryClient.query();
	        query.constrain(ItemParent.class);
	        query.descend("child").descend("name").constrain(ITEM_NAME);
			Assert.areEqual(expectedCount, query.execute().size());
		}
	
		private void assertChildQuery(int expectedCount, ExtObjectContainer queryClient) {
			Query query = queryClient.query();
	        query.constrain(Item.class);
	        query.descend("name").constrain(ITEM_NAME);
			Assert.areEqual(expectedCount, query.execute().size());
		}

		private void assertChildClassOnlyQuery(int expectedCount, ExtObjectContainer queryClient) {
			ObjectSet<Item> result = queryClient.query(Item.class);
			Assert.areEqual(expectedCount, result.size());
		}

	    public static void main(String[] arguments) {
	        new DeleteReaddChildReferenceTestUnit().runAll();
	    }
	
	}
}