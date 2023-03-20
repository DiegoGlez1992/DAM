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
package com.db4o.db4ounit.optional.monitoring;

import java.lang.management.*;

import javax.management.*;

/**
 * JDK 1.5 compatible MBean "proxy".
 */
@decaf.Remove
public class MBeanProxy {

	private final MBeanServer _platformServer = ManagementFactory.getPlatformMBeanServer();
	private final ObjectName _beanName;

	public MBeanProxy(ObjectName beanName) {
		_beanName = beanName;
	}

	public <T> T getAttribute(final String attribute) {
		try {
			return (T)_platformServer.getAttribute(_beanName, attribute);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void resetCounters() {
		try {
			_platformServer.invoke(_beanName, "resetCounters", new Object[0], new String[0]);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void addNotificationListener(NotificationListener listener, NotificationFilter notificationFilter) throws JMException {
		_platformServer.addNotificationListener(_beanName, listener, notificationFilter, null);
		
	}
}
