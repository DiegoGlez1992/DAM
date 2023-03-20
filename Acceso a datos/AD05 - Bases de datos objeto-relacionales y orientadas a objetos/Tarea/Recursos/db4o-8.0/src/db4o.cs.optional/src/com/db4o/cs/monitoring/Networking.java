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
package com.db4o.cs.monitoring;

import javax.management.*;

import com.db4o.*;
import com.db4o.monitoring.*;
import com.db4o.monitoring.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Networking extends MBeanRegistrationSupport implements NetworkingMBean {

	public Networking(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public double getBytesSentPerSecond() {
		return bytesSent().read();
	}

	public double getBytesReceivedPerSecond() {
		return bytesReceived().read();
	}
	
	public double getMessagesSentPerSecond() {
		return messagesSent().read();
	}
	
	public void notifyWrite(int count) {
		bytesSent().incrementBy(count);
		messagesSent().incrementBy(1);
	}
	
	public void notifyRead(int count) {
		bytesReceived().incrementBy(count);
	}	
	
	private TimedReading messagesSent() {
		if (null == _messagesSent){
			_messagesSent = TimedReading.newPerSecond();
		}
		
		return _messagesSent;
	}
	
	private TimedReading bytesReceived() {
		if (null == _bytesReceived) {
			_bytesReceived = TimedReading.newPerSecond();
		}
		
		return _bytesReceived;
	}

	private TimedReading bytesSent() {
		if (null == _bytesSent) {
			_bytesSent = TimedReading.newPerSecond();
		}
		
		return _bytesSent;
	}
	
	@Override
	public String toString() {
		return objectName().toString();
	}
	
	public void resetCounters() {
		reset(_bytesSent);
		reset(_bytesReceived);
		reset(_messagesSent);
	}

	private void reset(TimedReading counter) {
		if (null != counter) {
			counter.resetCount();
		}
	}
	
	private TimedReading _bytesSent;
	private TimedReading _bytesReceived;
	private TimedReading _messagesSent;
}
