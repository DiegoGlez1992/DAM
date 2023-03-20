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

import static com.db4o.foundation.Environments.my;

import java.lang.management.*;

import javax.management.*;

import com.db4o.*;

/**
 * @exclude
 */
@decaf.Ignore
public class MBeanRegistrationSupport implements Db4oMBean {

	private ObjectContainer _db;
	private ObjectName _objectName;
	private Class<?> _type;

	public MBeanRegistrationSupport(ObjectContainer db, Class<?> type) {
		_db = db;
		_type = type;
		beanRegistry().add(this);
	}

	public MBeanRegistrationSupport(ObjectName objectName) throws JMException {
		_objectName = objectName;
	}

	public void unregister() {
		if (objectName() == null) {
			return;
		}
		
		try {
			platformMBeanServer().unregisterMBean(objectName());
		} catch (JMException e) {
			e.printStackTrace();
		} finally {
			_db = null;
			_objectName = null;
		}
	}

	// FIXME
	public void register() throws JMException {
		if(platformMBeanServer().isRegistered(objectName())) {
			return;
		}
		platformMBeanServer().registerMBean(this, objectName());
	}

	private MBeanServer platformMBeanServer() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	protected ObjectName objectName() {
		if(_objectName != null) {
			return _objectName;
		}
		if(_db == null) {
			return null;
		}
		_objectName = Db4oMBeans.mBeanNameFor(_type, Db4oMBeans.mBeanIDForContainer(_db));
		return _objectName;
	}

	private Db4oMBeanRegistry beanRegistry() {
		return my(Db4oMBeanRegistry.class);
	}
}
