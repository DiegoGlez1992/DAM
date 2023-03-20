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
package com.db4o.db4ounit.common.threading;

import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.threading.*;

import db4ounit.*;

public class ThreadPoolTestCase implements TestCase {
	
	ThreadPool4 _subject = new ThreadPool4Impl();
	
	public void testFailureEvent() throws Exception {
		
		final ByRef<Boolean> executed = ByRef.newInstance(false);
		final RuntimeException exception = new RuntimeException();
		
		_subject.uncaughtException().addListener(new EventListener4<UncaughtExceptionEventArgs>() {
			public void onEvent(Event4 e, UncaughtExceptionEventArgs args) {
				Assert.areSame(exception, args.exception());
				executed.value = true;
			}
		});
		
		_subject.start(ReflectPlatform.simpleName(getClass())+" throwing exception thread", new Runnable() {
			public void run() {
				throw exception;
			}
		});
		
		_subject.join(1000);
		
		Assert.isTrue(executed.value);
		
	}
	
	/**
	 * @sharpen.ignore
	 */
	public void testPriority() throws Exception {
		
		final ByRef<Integer> actualPriority = ByRef.newInstance();
		
		_subject.startLowPriority("Priority checker", new Runnable() {
			public void run() {
				actualPriority.value = Thread.currentThread().getPriority();
			}
		});
		
		_subject.join(1000);
		Assert.areEqual(Thread.MIN_PRIORITY, (int)actualPriority.value);
	}
	
	public void testDaemon() throws Exception {
		
		final ByRef<Boolean> isDaemon = ByRef.newInstance();
		
		_subject.startLowPriority("Deamon checker", new Runnable() {
			public void run() {
				isDaemon.value = Thread.currentThread().isDaemon();
			}
		});
		
		_subject.join(1000);
		Assert.isTrue(isDaemon.value);
	}

}
