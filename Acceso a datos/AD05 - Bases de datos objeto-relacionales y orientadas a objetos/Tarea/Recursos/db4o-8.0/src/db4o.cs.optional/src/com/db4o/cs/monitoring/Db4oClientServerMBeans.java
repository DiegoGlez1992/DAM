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
import com.db4o.ext.*;
import com.db4o.monitoring.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oClientServerMBeans {

	public static Networking newClientNetworkingStatsMBean(ObjectContainer container) {
		try {
			return new Networking(container, NetworkingMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}

	public static Networking newServerNetworkingStatsMBean(ObjectContainer container) {
		try {
			return new SynchronizedNetworking(container, NetworkingMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}

	public static ClientConnections newClientConnectionsMBean(ObjectServer server) {
		try {
			return new ClientConnections(Db4oMBeans.mBeanNameFor(ClientConnectionsMBean.class, Db4oMBeans.mBeanIDForContainer(server.ext().objectContainer())));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

}
