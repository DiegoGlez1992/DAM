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

import static com.db4o.foundation.Environments.*;

import java.io.*;

import javax.management.*;

import com.db4o.*;
import com.db4o.cs.foundation.*;
import com.db4o.events.*;

/**
 * @exclude
 */
@decaf.Ignore
class MonitoredServerSocket4 extends ServerSocket4Decorator {
	public MonitoredServerSocket4(ServerSocket4 serverSocket) {
		super(serverSocket);
	}

	public Socket4 accept() throws IOException {
		return new MonitoredServerSideClientSocket4(_serverSocket.accept(), bean());
	}
	
	Networking bean() {
		// FIXME
		if (_bean == null) {
			_bean = Db4oClientServerMBeans.newServerNetworkingStatsMBean(my(ObjectContainer.class));		
			try {
				_bean.register();
			} catch (JMException exc) {
				exc.printStackTrace();
			}
			unregisterBeanOnServerClose();			
		}
		return _bean;
	}

	private void unregisterBeanOnServerClose() {
		EventRegistry events = EventRegistryFactory.forObjectContainer(my(ObjectContainer.class));
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() { public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
			_bean.unregister();
			_bean = null;
		}});
	}
	
	private Networking _bean;	
}