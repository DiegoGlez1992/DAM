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
package com.db4o.cs.foundation;

import java.io.IOException;

/**
 * @since 7.12
 */
public class ServerSocket4Decorator implements ServerSocket4 {

	public ServerSocket4Decorator(ServerSocket4 serverSocket) {
		_serverSocket = serverSocket;
	}
	
	public Socket4 accept() throws IOException {
		return _serverSocket.accept();
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
	
	protected ServerSocket4 _serverSocket;
}
