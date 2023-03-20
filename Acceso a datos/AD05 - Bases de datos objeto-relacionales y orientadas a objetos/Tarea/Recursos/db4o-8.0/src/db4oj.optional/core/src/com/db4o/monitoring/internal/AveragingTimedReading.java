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
package com.db4o.monitoring.internal;

import static com.db4o.foundation.Environments.my;

@decaf.Remove
public class AveragingTimedReading {
	
	private final Clock _clock = my(Clock.class);
	
	private long _lastStart;
	private long _aggregateTime;
	private int _eventCount;
	
	public synchronized void eventStarted() {
		_lastStart = currentTime();
	}

	public synchronized void eventFinished() {
		if (-1 == _lastStart) {
			throw new IllegalStateException();
		}
		
		_aggregateTime += currentTime() - _lastStart;
		_eventCount++;
		_lastStart = -1;
	}
	
	public synchronized double read() {
		if (_eventCount == 0) {
			return 0;
		}
		final long value = _aggregateTime / _eventCount;
		_eventCount = 0;
		_aggregateTime = 0;
		_lastStart = currentTime();
		return value;
	}
	
	private long currentTime() {
		return _clock.currentTimeMillis();
	}

}
