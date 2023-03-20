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
package com.db4o.cs.internal.caching;

import com.db4o.cs.caching.*;
import com.db4o.cs.internal.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.caching.*;

public class ClientSlotCacheImpl implements ClientSlotCache {
	
	private static final Function4<Integer, ByteArrayBuffer> nullProducer = new Function4<Integer, ByteArrayBuffer>() {
		public ByteArrayBuffer apply(Integer arg) {
			return null;
		}
	};

	private final TransactionLocal<PurgeableCache4<Integer, ByteArrayBuffer>> _cache = new TransactionLocal<PurgeableCache4<Integer, ByteArrayBuffer>>() {
		public PurgeableCache4<Integer, ByteArrayBuffer> initialValueFor(Transaction transaction) {
			Config4Impl config = transaction.container().config();
			return CacheFactory.<ByteArrayBuffer>newLRUIntCache(config.prefetchSlotCacheSize());
		};
	};
	
	public ClientSlotCacheImpl(ClientObjectContainer clientObjectContainer) {
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(clientObjectContainer);
		eventRegistry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				purge((Transaction) args.transaction(), (int)args.info().getInternalID());
            }
		});
	}

	public void add(Transaction provider, int id, final ByteArrayBuffer slot) {
		purge(provider, id);
		cacheOn(provider).produce(id, new Function4<Integer, ByteArrayBuffer>(){
			public ByteArrayBuffer apply(Integer arg) {
				return slot;
			}
		}, null);
    }

	public ByteArrayBuffer get(Transaction provider, int id) {
		final ByteArrayBuffer buffer = cacheOn(provider).produce(id, nullProducer, null);
		if (null == buffer) {
			return null;
		}
		buffer.seek(0);
		return buffer;
    }
	
	private void purge(Transaction provider, int id) {
		cacheOn(provider).purge(id);
	}
	
	private PurgeableCache4<Integer, ByteArrayBuffer> cacheOn(Transaction provider) {
		return provider.get(_cache).value;
	}
}
