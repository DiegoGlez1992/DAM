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
public class ObjectLifecycle extends MBeanRegistrationSupport implements ObjectLifecycleMBean{
	
	private final TimedReading _activated = TimedReading.newPerSecond();
	
	private final TimedReading _deactivated = TimedReading.newPerSecond();
	
	private final TimedReading _stored = TimedReading.newPerSecond();
	
	private final TimedReading _deleted = TimedReading.newPerSecond();

	public ObjectLifecycle(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
	}

	public double getObjectsActivatedPerSec() {
		return _activated.read();
	}

	public double getObjectsDeactivatedPerSec() {
		return _deactivated.read();
	}

	public double getObjectsDeletedPerSec() {
		return _deleted.read();
	}

	public double getObjectsStoredPerSec() {
		return _stored.read();
	}

	public void notifyStored() {
		_stored.increment();
	}

	public void notifyDeleted() {
		_deleted.increment();
	}

	public void notifyActivated() {
		_activated.increment();
	}

	public void notifyDeactivated() {
		_deactivated.increment();
	}

}
