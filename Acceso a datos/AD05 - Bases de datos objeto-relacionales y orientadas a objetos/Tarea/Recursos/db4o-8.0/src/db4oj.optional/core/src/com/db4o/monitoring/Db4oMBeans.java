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
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oMBeans {
	
	private static final String MONITORING_DOMAIN_NAME = "com.db4o.monitoring";

	public static String mBeanIDForContainer(ObjectContainer container) {
		return container.toString();
	}

	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String name) {
		name = name.replaceAll("[:\\?\\*=,\"]", " ");
		final String nameSpec = MONITORING_DOMAIN_NAME + ":name=" + name + ",mbean=" + displayName(mbeanInterface);
		try {
			return new ObjectName(nameSpec);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException("'" + nameSpec + "' is not a valid name.", e);
		}
	}

	private static String displayName(Class<?> mbeanInterface) {
		String className = mbeanInterface.getSimpleName();
		if(! className.endsWith("MBean")){
			throw new IllegalArgumentException();
		}
		return className.substring(0, className.length() - "MBean".length());
	}
	
	static IO newIOStatsMBean(ObjectContainer container) {
		try {
			return new IO(container, IOMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}

	public static Queries newQueriesMBean(InternalObjectContainer container) {
		try {
			return new Queries(container, QueriesMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static com.db4o.monitoring.ReferenceSystem newReferenceSystemMBean(InternalObjectContainer container) {
		try {
			return new com.db4o.monitoring.ReferenceSystem(container, ReferenceSystemMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static NativeQueries newNativeQueriesMBean(InternalObjectContainer container) {
		try {
			return new NativeQueries(container, NativeQueriesMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static Freespace newFreespaceMBean(InternalObjectContainer container) {
		try {
			return new Freespace(container, FreespaceMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static ObjectLifecycle newObjectLifecycleMBean(ObjectContainer container) {
		try {
			return new ObjectLifecycle(container, ObjectLifecycleMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}
}
