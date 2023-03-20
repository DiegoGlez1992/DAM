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

public class PausableBlockingQueue<T> extends BlockingQueue<T> implements PausableBlockingQueue4<T> {

	private volatile boolean _paused = false;

	public boolean pause() {
		if (_paused) {
			return false;
		}
		_paused = true;
		return true;
	}

	public boolean resume() {
		return _lock.run(new Closure4<Boolean>() {

			public Boolean run() {
				if (!_paused) {
					return false;
				}
				_paused = false;
				_lock.awake();
				return true;
			}
		});
	}
	
	public boolean isPaused() {
		return _paused;
	}
	
	@Override
	protected boolean unsafeWaitForNext(final long timeout) throws BlockingQueueStoppedException {
		boolean hasNext = super.unsafeWaitForNext(timeout);
		while (_paused && !_stopped) {
			_lock.snooze(timeout);
		}
		if (_stopped) {
			throw new BlockingQueueStoppedException();
		}
		return hasNext;
	}

	public T tryNext() {
		return _lock.run(new Closure4<T>() {
			public T run() {
				return isPaused() ? null : hasNext() ? next() : null;
			}
		});
	}

}
