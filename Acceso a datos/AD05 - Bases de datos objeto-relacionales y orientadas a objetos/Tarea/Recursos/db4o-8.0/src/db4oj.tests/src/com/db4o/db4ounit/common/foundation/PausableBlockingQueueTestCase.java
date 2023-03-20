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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class PausableBlockingQueueTestCase extends Queue4TestCaseBase {
	
	public void testTimeoutNext() {
		
		PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();
		
		Assert.isFalse(queue.isPaused());
		
		queue.pause();
		
		Assert.isTrue(queue.isPaused());
		
		Object obj = new Object();
		
		queue.add(obj);
		
		Assert.isTrue(queue.hasNext());
		
		Assert.isNull(queue.tryNext());
		
		queue.resume();
		
		Assert.areSame(obj, queue.next(50));
		
	}
	
	public void testStop() {
		
		final PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();

		queue.pause();

		executeAfter("Pausable queue stopper", 200, new Runnable() {
			public void run() {
				queue.stop();
			}
		});
		
		Assert.expect(BlockingQueueStoppedException.class, new CodeBlock() {
			
			public void run() throws Throwable {
				queue.next();
			}
		});	
		
		
	}
	
	public void testDrainTo() throws InterruptedException {
		final PausableBlockingQueue4<Object> queue = new PausableBlockingQueue<Object>();

		queue.add(new Object());
		queue.add(new Object());
		
		queue.pause();
		
		final Collection4<Object> list = new Collection4<Object>();
		
		Thread t = executeAfter("Pausable queue drainer", 0, new Runnable() {
			public void run() {
				Collection4<Object> l = new Collection4<Object>();
				queue.drainTo(l);
				synchronized (list) {
					list.addAll(l);
				}
			}
		});
		
		Runtime4.sleepThrowsOnInterrupt(200);

		synchronized (list) {
			Assert.areEqual(0, list.size());
		}
		Assert.isTrue(queue.hasNext());

		queue.resume();
		
		t.join();

		synchronized (list) {
			Assert.areEqual(2, list.size());
		}
		Assert.isFalse(queue.hasNext());
	}


	public static Thread executeAfter(String threadName, final long timeInMillis, final Runnable runnable) {
		
		Thread t = new Thread() {
			@Override
			public void run() {
				if (timeInMillis > 0) {
					try {
						Thread.sleep(timeInMillis);
					} catch (InterruptedException e) {
						return;
					}
				}
				runnable.run();
				
			};
		};
		t.setName(threadName);
		t.setDaemon(true);
		t.start();
		
		return t;
	}


}
