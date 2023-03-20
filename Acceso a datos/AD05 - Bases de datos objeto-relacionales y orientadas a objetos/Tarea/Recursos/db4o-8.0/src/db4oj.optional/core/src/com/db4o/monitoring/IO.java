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
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.*;
import com.db4o.monitoring.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
class IO extends MBeanRegistrationSupport implements IOMBean {

	private TimedReading _numBytesReadPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numBytesWrittenPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numReadsPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numWritesPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numSyncsPerSec = TimedReading.newPerSecond();
	
	public IO(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public double getBytesReadPerSecond() {
		return _numBytesReadPerSec.read();
	}

	public double getBytesWrittenPerSecond() {
		return _numBytesWrittenPerSec.read();
	}

	public double getReadsPerSecond() {
		return _numReadsPerSec.read();
	}

	public double getWritesPerSecond() {
		return _numWritesPerSec.read();
	}

	public double getSyncsPerSecond() {
		return _numSyncsPerSec.read();
	}

	public void notifyBytesRead(int numBytesRead) {
		_numBytesReadPerSec.incrementBy(numBytesRead);
		_numReadsPerSec.increment();
	}

	public void notifyBytesWritten(int numBytesWritten) {
		_numBytesWrittenPerSec.incrementBy(numBytesWritten);
		_numWritesPerSec.increment();
	}

	public void notifySync() {
		_numSyncsPerSec.increment();
	}

}
