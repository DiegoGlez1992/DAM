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
package com.db4o.internal.caching;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public interface Cache4<K, V> extends Iterable<V> {
	
	/**
	 * Retrieves the value associated to the {@link key} from the cache. If the value is not yet
	 * cached {@link producer} will be called to produce it. If the cache needs to discard a value
	 * {@link finalizer} will be given a chance to process it.
	 * 
	 * @param key the key for the value - must never change - cannot be null
	 * @param producer will be called if value not yet in the cache - can only be null when the value is found in the cache
	 * @param finalizer will be called if a page needs to be discarded - can be null
	 * 
	 * @return the cached value
	 */
	V produce(K key, Function4<K,V> producer, Procedure4<V> finalizer);

}
