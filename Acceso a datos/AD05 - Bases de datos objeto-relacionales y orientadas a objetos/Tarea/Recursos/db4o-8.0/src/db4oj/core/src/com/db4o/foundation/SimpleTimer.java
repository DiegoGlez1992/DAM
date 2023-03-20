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
package com.db4o.foundation;

/**
 * @exclude
 */
public final class SimpleTimer implements Runnable {

	private final Runnable _runnable;

	private final long _interval;

	private Lock4 _lock;

	public volatile boolean stopped = false;

	public SimpleTimer(Runnable runnable, long interval) {
		_runnable = runnable;
		_interval = interval;
		_lock = new Lock4();
	}

	public void stop() {
		stopped = true;
		
		_lock.run(new Closure4() { 
			public Object run() {
				_lock.awake();
				return null;
			}
		});
	}

	public void run() {
		while (!stopped) {
			_lock.run(new Closure4() { 
				public Object run() {
					_lock.snooze(_interval);
					return null;
				}
			});
		
			if (!stopped) {
				_runnable.run();
			}
		}
	}
}
