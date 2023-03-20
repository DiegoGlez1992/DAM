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

/**
 * @exclude
 */
public class CacheFactory {

	public static <K, V> Cache4<K, V> new2QCache(int size) {
		return new LRU2QCache<K, V>(size);
	}
	
	public static <V> Cache4<Long, V> new2QLongCache(int size) {
		return new LRU2QLongCache<V>(size);
	}

	public static <K, V> Cache4<K, V> new2QXCache(int size) {
		return new LRU2QXCache<K, V>(size);
	}

	public static <K, V> PurgeableCache4<K, V> newLRUCache(int size) {
		return new LRUCache<K, V>(size);
	}
	
	public static <V> PurgeableCache4<Integer, V> newLRUIntCache(int size) {
		return new LRUIntCache<V>(size);
	}
	
	public static <V> PurgeableCache4<Long, V> newLRULongCache(int size) {
		return new LRULongCache<V>(size);
	}


}
