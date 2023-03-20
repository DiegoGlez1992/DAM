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
package com.db4o.db4ounit.common.caching;

import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

import db4ounit.*;
import db4ounit.fixtures.*;
import db4ounit.mocking.*;

public class CacheTestUnit implements TestCase {
	
	public void testOnDiscard() {
		final TestPuppet puppet = new TestPuppet();
		puppet.fillCache();
		
		final ByRef<String> discarded = new ByRef<String>();
		puppet.produce(42, new Procedure4<String>() {
			public void apply(String discardedValue) {
				discarded.value = discardedValue;
            }
		});
		Assert.areEqual("0", discarded.value);
	}
	
	public void testIterable() {
		final TestPuppet puppet = new TestPuppet();
		Iterator4Assert.sameContent(new Object[] {}, puppet.values());
		puppet.produce(0);
		Iterator4Assert.sameContent(new Object[] { "0" }, puppet.values());
		puppet.fillCache();
		Iterator4Assert.sameContent(new Object[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }, puppet.values());
	}
	
	public void testProduce(){
		final Object obj = new Object();
		Cache4<Integer, Object> cache = SubjectFixtureProvider.value();
		Object value = cache.produce(1, new Function4<Integer, Object> () {
			public Object apply(Integer key) {
				return obj;
			}
		}, null);
		Assert.areSame(obj, value);
		Assert.areSame(obj, cache.produce(1, null, null));
	}
	
	static class TestPuppet {
		final MethodCallRecorder producerCalls = new MethodCallRecorder();
		final Function4<Integer, String> producer = new Function4<Integer, String>() {
			public String apply(Integer key) {
				producerCalls.record(new MethodCall("apply", key));
				return key.toString();
			}
		};
		
		public final Cache4<Integer, String> cache = SubjectFixtureProvider.value();
		
		public void fillCache() {
			fillCache(0, 10);
		}

		public Iterator4 values() {
			final Collection4 values = new Collection4();
			for (String s : cache) {
				values.add(s);
			}
			return values.iterator();
        }

		public void fillCache(final int from, final int to) {
			for (int i=from; i<to; ++i) {
				Assert.areEqual(new Integer(i).toString(), cache.produce(i, producer, null));
			}
		}
		
		public void verify(MethodCall... calls) {
			producerCalls.verify(calls);
		}

		public String produce(int key) {
			return produce(key, null);
		}
		
		public String produce(int key, Procedure4<String> onDiscard) {
			return cache.produce(key, producer, onDiscard);
        }


		public void reset() {
			producerCalls.reset();
		}
		
		public void cacheHit(int key) {
			reset();
			produce(key);
			verify();
		}
		
		public void cacheMiss(int key) {
			reset();
			produce(key);
			verify(applyCall(key));
			reset();
		}

		public void dumpCache() {
//			System.out.println(cache);
		}

		public void cacheMisses(int... keys) {
			for (int key : keys) {
				cacheMiss(key);
			}
		}
		
		public void cacheHits(int... keys) {
			for (int key : keys) {
				cacheHit(key);
			}
		}
	}
	
	public void testProducerIsNotCalledOnCacheHit(){
		final TestPuppet puppet = new TestPuppet();
		puppet.fillCache();
		puppet.verify(applyCalls(10));
		puppet.fillCache();
		puppet.verify(applyCalls(10));
		
		Assert.areEqual("10", puppet.produce(10));
		puppet.verify(applyCalls(11));
		
		puppet.reset();
		
		Assert.areEqual("0", puppet.produce(0));
		puppet.verify(applyCalls(1));
		
		puppet.fillCache(2, 10);
		puppet.verify(applyCalls(1));
	}
	
	public void testHotItemsAreEvictedLast() {
		
		final TestPuppet puppet = new TestPuppet();
		if (puppet.cache.getClass().getName().indexOf("LRU2QXCache") > 0) {
			// LRU2QXCache doesn't meet all the expectations
			return;
		}
		
		puppet.fillCache();
		puppet.fillCache(0, 2); // 0 and 1 are hot now
		
		puppet.cacheMiss(11);
		
		// 2 should have been evicted to make room for 11
		puppet.cacheMiss(2);
		
		puppet.cacheHit(11); // 11 is as hot as 0 and 1 now
		
		puppet.cacheMiss(12);
		
		puppet.cacheHit(0); 
		puppet.cacheHit(1); 
		puppet.cacheHit(2); 
		puppet.cacheMiss(3);
		
		puppet.cacheMiss(4);
		puppet.cacheMiss(5);
		
		puppet.cacheMiss(13);
		puppet.cacheMiss(14);
		
		puppet.cacheMisses(6, 7, 8, 9);
		
		puppet.dumpCache();
		
		puppet.cacheHits(6,7,8,9,13,14);
		
		puppet.cacheMiss(15);
		
		puppet.cacheMiss(11);
		puppet.cacheMiss(0);
		puppet.dumpCache();
		
	}

	private MethodCall[] applyCalls(int count) {
		final MethodCall[] expectations = new MethodCall[count];
		for (int i=0; i<count; ++i) {
			expectations[i] = applyCall(i);
		}
		return expectations;
	}

	private static MethodCall applyCall(int arg) {
		return new MethodCall("apply", arg);
	}

}
