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

import java.io.IOException;
import java.util.*;

import com.db4o.cs.foundation.*;

public class CountingServerSocket4 implements ServerSocket4 {

	public CountingServerSocket4(ServerSocket4 serverSocket) {
		_serverSocket = serverSocket;
	}

	public Socket4 accept() throws IOException {
		CountingSocket4 socket = new CountingSocket4(_serverSocket.accept());
		_clients.add(socket);
		
		return socket;
	}

	public void close() throws IOException {
		_serverSocket.close();
	}

	public int getLocalPort() {
		return _serverSocket.getLocalPort();
	}

	public void setSoTimeout(int timeout) {
		_serverSocket.setSoTimeout(timeout);
	}
	
	public List<CountingSocket4> connectedClients() {
		return _clients;
	}

	private ServerSocket4 _serverSocket;
	private List<CountingSocket4> _clients = new ArrayList<CountingSocket4>();
}
