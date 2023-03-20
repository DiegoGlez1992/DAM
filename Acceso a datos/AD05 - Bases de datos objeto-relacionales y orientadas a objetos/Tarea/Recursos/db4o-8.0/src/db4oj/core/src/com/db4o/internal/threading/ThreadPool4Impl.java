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
package com.db4o.internal.threading;

import java.util.*;

import com.db4o.events.*;
import com.db4o.internal.events.*;

public class ThreadPool4Impl implements ThreadPool4 {

	private final Event4Impl<UncaughtExceptionEventArgs> _uncaughtException = Event4Impl.newInstance();
	private final List<Thread> _activeThreads = new ArrayList<Thread>();

	public void join(int timeoutMilliseconds) throws InterruptedException {
		for (Thread thread : activeThreads()) {
			thread.join(timeoutMilliseconds);
		}
	}

	public void startLowPriority(String taskName, Runnable task) {
		final Thread thread = threadFor(taskName, task);
		setLowPriorityOn(thread);
		activateThread(thread);
	}

	/**
	 * @sharpen.remove
	 */
	private void setLowPriorityOn(final Thread thread) {
	    thread.setPriority(Thread.MIN_PRIORITY);
    }

	public void start(String taskName, final Runnable task) {
		final Thread thread = threadFor(taskName, task);
		
		activateThread(thread);
    }

	private Thread threadFor(String threadName, final Runnable task) {
	    final Thread thread = new Thread(new Runnable() {
        	public void run() {
        		try {
        			task.run();
        		} catch (Throwable e) {
        			triggerUncaughtExceptionEvent(e);
        		} finally {
        			dispose(Thread.currentThread());
        		}
            }
        }, threadName);
	    thread.setDaemon(true);
		return thread;
    }

	private void activateThread(final Thread thread) {
		addActiveThread(thread);
	    thread.start();
    }

	private Thread[] activeThreads() {
		synchronized (_activeThreads) {
			return _activeThreads.toArray(new Thread[_activeThreads.size()]);
		}
	}
	
	private void addActiveThread(final Thread thread) {
	    synchronized (_activeThreads) {
	    	_activeThreads.add(thread);
        }
    }

	protected void dispose(Thread thread) {
		synchronized (_activeThreads) {
			_activeThreads.remove(thread);
		}
	}

	protected void triggerUncaughtExceptionEvent(Throwable e) {
		_uncaughtException.trigger(new UncaughtExceptionEventArgs(e));
    }

	public Event4<UncaughtExceptionEventArgs> uncaughtException() {
	    return _uncaughtException;
    }
}
