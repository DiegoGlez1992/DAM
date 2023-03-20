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
package com.db4o.db4ounit.optional.monitoring.cs;

import java.io.*;
import java.util.*;

import com.db4o.cs.foundation.*;

public class CountingSocket4Factory implements Socket4Factory {

	public CountingSocket4Factory(Socket4Factory socketFactory) {		
		_socketFactory = socketFactory;
	}

	public ServerSocket4 createServerSocket(int port) throws IOException {
		_serverSocket = new CountingServerSocket4(_socketFactory.createServerSocket(port));
		return _serverSocket;
	}

	public Socket4 createSocket(String hostName, int port) throws IOException {
		CountingSocket4 socket = new CountingSocket4(_socketFactory.createSocket(hostName, port));
		_sockets.add(socket);
		return socket;
	}

	public List<CountingSocket4> countingSockets() {
		return _sockets;
	}
	
	public List<CountingSocket4> connectedClients() {
		return _serverSocket.connectedClients();
	}
	
	public void resetCounters() {
		for (CountingSocket4 socket : connectedClients()) {
			socket.resetCount();
		}
	}
	
	private CountingServerSocket4 _serverSocket;
	private Socket4Factory _socketFactory;
	private List<CountingSocket4> _sockets = new ArrayList<CountingSocket4>();
}
