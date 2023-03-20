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
import com.db4o.monitoring.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
class Queries extends NotificationEmitterMBean implements QueriesMBean {

	private final TimedReading _classIndexScans = TimedReading.newPerSecond();
	private final TimedReading _queries = TimedReading.newPerSecond();
	private final AveragingTimedReading _queryExecutionTime = new AveragingTimedReading();

	public Queries(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	private static String classIndexScanNotificationType() {
		return LoadedFromClassIndex.class.getName();
	}
	
	public double getClassIndexScansPerSecond() {
		return _classIndexScans.read();
	}

	public double getAverageQueryExecutionTime() {
		return _queryExecutionTime.read();
	}

	public double getQueriesPerSecond() {
		return _queries.read();
	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return new MBeanNotificationInfo[] {
			new MBeanNotificationInfo(
					new String[] { classIndexScanNotificationType() },
					Notification.class.getName(),
					"Notification about class index scans."),
			
		};
	}
	
	public void notifyClassIndexScan(LoadedFromClassIndex d) {
		
		_classIndexScans.increment();
		
		sendNotification(classIndexScanNotificationType(), d.problem(), d.reason());
	}

	public void notifyQueryStarted() {
		_queries.increment();
		
		_queryExecutionTime.eventStarted();
	}	
	
	public void notifyQueryFinished() {
		
		_queryExecutionTime.eventFinished();
	}
}
