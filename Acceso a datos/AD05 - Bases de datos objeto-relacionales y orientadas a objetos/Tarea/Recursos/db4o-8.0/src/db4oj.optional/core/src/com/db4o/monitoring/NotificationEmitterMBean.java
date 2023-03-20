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

/**
 * @exclude
 */
@decaf.Ignore
public abstract class NotificationEmitterMBean extends MBeanRegistrationSupport implements NotificationEmitter{

	private final NotificationBroadcasterSupport _notificationSupport = new NotificationBroadcasterSupport();
	
	public NotificationEmitterMBean(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		_notificationSupport.removeNotificationListener(listener, filter, handback);
	}

	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws IllegalArgumentException {
		_notificationSupport.addNotificationListener(listener, filter, handback);
	}
	
	public void removeNotificationListener(NotificationListener listener)
			throws ListenerNotFoundException {
		_notificationSupport.removeNotificationListener(listener);
	}


	protected void sendNotification(final String notificationType,
			final String message, final Object userData) {
		final Notification notification = new Notification(notificationType, objectName(), 0, message);
		notification.setUserData(userData);
		_notificationSupport.sendNotification(notification);
	}


}
