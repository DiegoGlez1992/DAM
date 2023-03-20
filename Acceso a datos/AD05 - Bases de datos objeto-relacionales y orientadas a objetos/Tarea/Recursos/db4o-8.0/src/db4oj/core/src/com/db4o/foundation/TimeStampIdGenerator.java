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
public class TimeStampIdGenerator {
	
	public static final int BITS_RESERVED_FOR_COUNTER = 15;
	
	public static final int COUNTER_LIMIT = 64;
    
	private long _counter;
	
	private long _lastTime;

	public static long idToMilliseconds(long id) {
		return id >> BITS_RESERVED_FOR_COUNTER;
	}

	public static long millisecondsToId(long milliseconds) {
		return milliseconds << BITS_RESERVED_FOR_COUNTER;
	}
	
	public TimeStampIdGenerator(long minimumNext) {
		internalSetMinimumNext(minimumNext);
	}

	public TimeStampIdGenerator() {
		this(0);
	}

	public long generate() {
		long t = now();
		if(t > _lastTime){
			_lastTime = t;
			_counter = 0;
			return millisecondsToId(t);
		}
		updateTimeOnCounterLimitOverflow();
		_counter++;
		updateTimeOnCounterLimitOverflow();
		return last();
	}

	protected long now() {
		return System.currentTimeMillis();
	}

	private final void updateTimeOnCounterLimitOverflow() {
		if(_counter < COUNTER_LIMIT){
			return;
		}
		long timeIncrement = _counter / COUNTER_LIMIT;
		_lastTime += timeIncrement;
		_counter -= (timeIncrement * COUNTER_LIMIT);
	}

	public long last() {
		return millisecondsToId(_lastTime) + _counter;
	}

	public boolean setMinimumNext(long newMinimum) {
        if(newMinimum <= last()){
            return false;
        }
        internalSetMinimumNext(newMinimum);
        return true;
	}

	private void internalSetMinimumNext(long newNext) {
		_lastTime = idToMilliseconds(newNext);
		long timePart = millisecondsToId(_lastTime);
		_counter = newNext - timePart;
		updateTimeOnCounterLimitOverflow();
	}
	
}
