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
import com.db4o.diagnostic.*;
import com.db4o.internal.query.*;
import com.db4o.monitoring.internal.*;
import com.db4o.query.*;

/**
 * @exclude
 */
@decaf.Ignore
public class NativeQueries extends NotificationEmitterMBean implements NativeQueriesMBean {
	
	private final TimedReading _nativeQueries = TimedReading.newPerSecond();
	private final TimedReading _unoptimizedNativeQueries = TimedReading.newPerSecond();

	public NativeQueries(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public double getUnoptimizedNativeQueriesPerSecond() {
		return _unoptimizedNativeQueries.read();
	}
	
	public double getNativeQueriesPerSecond() {
		return _nativeQueries.read();
	}
	
	public MBeanNotificationInfo[] getNotificationInfo() {
		return new MBeanNotificationInfo[] {
			new MBeanNotificationInfo(
					new String[] { unoptimizedQueryNotificationType() },
					Notification.class.getName(),
					"Notification about unoptimized native query execution."),
			
		};
	}

	private String unoptimizedQueryNotificationType() {
		return NativeQueryNotOptimized.class.getName();
	}

	public void notifyNativeQuery(NQOptimizationInfo info) {
		
		if (info.message().equals(NativeQueryHandler.UNOPTIMIZED)) {
			notifyUnoptimized(info.predicate());
		}
		
		_nativeQueries.increment();
	}
	
	private void notifyUnoptimized(Predicate predicate) {
		
		_unoptimizedNativeQueries.increment();
		sendNotification(unoptimizedQueryNotificationType(), "Unoptimized native query.", predicate.getClass().getName());
		
	}

}
