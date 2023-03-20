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
package com.db4o.db4ounit.common.internal.query;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;

public class SodaQueryComparatorBenchmark {
	
	private static final int OBJECT_COUNT = 10000;
	private static final int ITERATIONS = 10;
	
	public static class Item {
		
		public Item(int id, String name, ItemChild child) {
				this.id = id;
				this.name = name;
				this.child = child;
			}
			
			public int id;
			public String name;
			public ItemChild child;
	}
	
	public static class ItemChild {
		
		public ItemChild(String name) {
			this.name = name;
		}
		
		public String name;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for (int i=0; i<2; ++i) {
			benchmarkOneField();
			benchmarkTwoFields();
		}
	}

	private static void benchmarkTwoFields() {
		long sqc = time(new Procedure4<ObjectContainer>() { public void apply(final ObjectContainer container) {
			
			final LocalObjectContainer localContainer = (LocalObjectContainer)container;
			final SodaQueryComparator comparator = new SodaQueryComparator(
					localContainer,
					Item.class,
					new SodaQueryComparator.Ordering(SodaQueryComparator.Direction.ASCENDING, "name"),
					new SodaQueryComparator.Ordering(SodaQueryComparator.Direction.DESCENDING, "child", "name"));
			
			final Query query = container.query();
			query.constrain(Item.class);
			
			final List<Integer> sortedIds = comparator.sort(query.execute().ext().getIDs());
			for (Integer id : sortedIds) {
				Assert.isNull(localContainer.getActivatedObjectFromCache(localContainer.transaction(), id.intValue()));
			}
			
		}});
		System.out.println(" SQC(2): " + sqc + "ms");
		
		long soda = time(new Procedure4<ObjectContainer>() { public void apply(ObjectContainer container) {
			
			final Query query = container.query();
			query.constrain(Item.class);
			query.descend("name").orderAscending();
			query.descend("child").descend("name").orderDescending();
			
			consumeAll(query.execute());
			
		}});
		System.out.println("SODA(2): " + soda + "ms");
	}
	
	private static void benchmarkOneField() {
		long sqc = time(new Procedure4<ObjectContainer>() { public void apply(final ObjectContainer container) {
			
			final LocalObjectContainer localContainer = (LocalObjectContainer)container;
			final SodaQueryComparator comparator = new SodaQueryComparator(
					localContainer,
					Item.class,
					new SodaQueryComparator.Ordering(SodaQueryComparator.Direction.ASCENDING, "name"));
			
			final Query query = container.query();
			query.constrain(Item.class);
			
			final List<Integer> sortedIds = comparator.sort(query.execute().ext().getIDs());
			for (Integer id : sortedIds) {
				Assert.isNull(localContainer.getActivatedObjectFromCache(localContainer.transaction(), id.intValue()));
			}
			
		}});
		System.out.println(" SQC(1): " + sqc + "ms");
		
		long soda = time(new Procedure4<ObjectContainer>() { public void apply(ObjectContainer container) {
			
			final Query query = container.query();
			query.constrain(Item.class);
			query.descend("name").orderAscending();
			consumeAll(query.execute());
			
		}});
		System.out.println("SODA(1): " + soda + "ms");
	}

	protected static void consumeAll(Iterable<Object> items) {
		for (Object item : items) {
			Assert.isNotNull(item);
		}
	}

	private static long time(Procedure4<ObjectContainer> procedure4) {
		
		final PagingMemoryStorage storage = new PagingMemoryStorage();
		
		storeItems(storage);
		final StopWatch stopWatch = new AutoStopWatch();
		for (int i=0; i<ITERATIONS; ++i) {
			applyProcedure(storage, procedure4);
		}
		return stopWatch.peek();
		
	}

	private static void applyProcedure(final PagingMemoryStorage storage,
			Procedure4<ObjectContainer> procedure4) {
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(storage);
		final EmbeddedObjectContainer container = Db4oEmbedded.openFile(config, "benchmark.db4o");
		try {
			procedure4.apply(container);
		} finally {
			container.close();
		}
	}

	private static void storeItems(
			final PagingMemoryStorage storage) {
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(storage);
		
		final EmbeddedObjectContainer container = Db4oEmbedded.openFile(config, "benchmark.db4o");
		try {
			for (int i=0; i<OBJECT_COUNT; ++i) {
				container.store(new Item(i, "Item " + i, new ItemChild("Child " + i)));
			}
		} finally {
			container.close();
		}
	}

}
