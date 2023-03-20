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

import com.db4o.internal.caching.*;

import db4ounit.fixtures.*;

public class CacheTestSuite extends FixtureTestSuiteDescription {
	
	// initializer
	{
		fixtureProviders(new SubjectFixtureProvider(
			new Deferred4() {
				public Object value() {
					return CacheFactory.newLRUCache(10);
				}
			},
			new Deferred4() {
				public Object value() {
					return CacheFactory.new2QCache(10);
				}
			},
			new Deferred4() {
				public Object value() {
					return CacheFactory.new2QXCache(10);
				}
			},
			new Deferred4() {
				public Object value() {
					return CacheFactory.newLRUIntCache(10);
				}
			}
			
			// The following doesn' sharpen. Ignore for now.
			
//			,new Deferred4() {
//				public Object value() {
//					return new Cache4() {
//						
//						private final Cache4 _delegate = CacheFactory.newLRULongCache(10); 
//	
//						public Object produce(Object key, final Function4 producer, Procedure4 finalizer) {
//							Function4 delegateProducer = new Function4<Long, Object>() {
//								public Object apply(Long arg) {
//									return producer.apply(arg.intValue());
//								}
//							};
//							return _delegate.produce(((Integer)key).longValue(), delegateProducer, finalizer);
//						}
//	
//						public Iterator iterator() {
//							return _delegate.iterator();
//						}
//					};
//				}
//			}
		));
		
		testUnits(CacheTestUnit.class);
	}
	
}
