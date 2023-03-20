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

import java.io.*;

import com.db4o.cs.foundation.*;

/**
 * @exclude
 */
@decaf.Ignore
public class MonitoredSocket4Factory implements Socket4Factory {
	
	private final Socket4Factory _socketFactory;
	
	public MonitoredSocket4Factory(Socket4Factory socketFactory) {
		_socketFactory = socketFactory;
	}

	public ServerSocket4 createServerSocket(final int port) throws IOException {
		return new MonitoredServerSocket4(_socketFactory.createServerSocket(port));
	}
	
	public Socket4 createSocket(String hostName, int port) throws IOException {
		return new MonitoredClientSocket4(_socketFactory.createSocket(hostName, port));
	}

}
