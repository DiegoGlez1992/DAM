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
package com.db4o.db4ounit.common.cs.caching;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.caching.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * removed for JDK 1.1 because there is no access to the private field
 * _clientSlotCache in ClientObjectContainer 
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ClientSlotCacheTestCase extends AbstractDb4oTestCase implements OptOutAllButNetworkingCS {
	
	private static final int SLOT_CACHE_SIZE = 5;

	@Override
	protected void configure(Configuration config) throws Exception {
		ClientConfiguration clientConfiguration = Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(config);
		clientConfiguration.prefetchSlotCacheSize(SLOT_CACHE_SIZE);
	}

	public void testSlotCacheIsTransactionBased() {
		withCache(new Procedure4<ClientSlotCache>() {
			public void apply(ClientSlotCache cache) {
				final Transaction t1 = newTransaction();
				final Transaction t2 = newTransaction();
				
				final ByteArrayBuffer slot = new ByteArrayBuffer(0);
				cache.add(t1, 42, slot);
				Assert.areSame(slot, cache.get(t1, 42));
				
				Assert.isNull(cache.get(t2, 42));
				synchronized(t1.container().lock()){
					t1.commit();
				}
				Assert.isNull(cache.get(t1, 42));
			}
		});
	}
	
	public void testCacheIsCleanUponTransactionCommit() {
		assertCacheIsCleanAfterTransactionOperation(new Procedure4<Transaction>() {
			public void apply(Transaction value) {
				value.commit();
            }
		});
	}
	
	public void testCacheIsCleanUponTransactionRollback() {
		assertCacheIsCleanAfterTransactionOperation(new Procedure4<Transaction>() {
			public void apply(Transaction value) {
				value.rollback();
            }
		});
	}

	private void assertCacheIsCleanAfterTransactionOperation(final Procedure4<Transaction> operation) {
		withCache(new Procedure4<ClientSlotCache>() {
			public void apply(ClientSlotCache cache) {
				final ByteArrayBuffer slot = new ByteArrayBuffer(0);
				cache.add(trans(), 42, slot);
				operation.apply(trans());
				Assert.isNull(cache.get(trans(), 42));
			}
		});
    }
	
	public void testSlotCacheEntryIsPurgedUponActivation() {
		
		final Item item = new Item();
		db().store(item);
		final int id = (int)db().getID(item);
		db().purge(item);
		
		db().configure().clientServer().prefetchDepth(1);
		
		withCache(new Procedure4<ClientSlotCache>() {
			public void apply(ClientSlotCache cache) {
				final ObjectSet<Item> items = newQuery(Item.class).execute();
				Assert.isNotNull(cache.get(trans(), id));
				Assert.isNotNull(items.next());
				Assert.isNull(cache.get(trans(), id), "activation should have purged slot from cache");
			}
		});
	}
	
	public void testAddOverridesExistingEntry(){
		withCache(new Procedure4<ClientSlotCache>() {
			public void apply(ClientSlotCache cache) {
				cache.add(trans(), 42, new ByteArrayBuffer(0));
				cache.add(trans(), 42, new ByteArrayBuffer(1));
				Assert.areEqual(1, cache.get(trans(), 42).length());
			}
		});
	}
	
	public void testCacheSizeIsBounded(){
		withCache(new Procedure4<ClientSlotCache>() {
			public void apply(ClientSlotCache cache) {
				for (int i = 0; i < SLOT_CACHE_SIZE + 1; i++) {
					cache.add(trans(), i, new ByteArrayBuffer(i));
				}
				for (int i = 1; i < SLOT_CACHE_SIZE + 1; i++) {
					Assert.areEqual(i, cache.get(trans(), i).length());
				}
				Assert.isNull(cache.get(trans(), 0));
			}
		});
		
	}
	
	private void withCache(final Procedure4<ClientSlotCache> procedure){
		ClientSlotCache clientSlotCache = null;
		try {
			clientSlotCache = (ClientSlotCache) Reflection4.getFieldValue(container(), "_clientSlotCache");
		} catch (ReflectException e) {
			Assert.fail("Can't get field _clientSlotCache on  container. " + e.toString() );
		}
		procedure.apply(clientSlotCache);
	}
	
	public static class Item {
	}
	
}
