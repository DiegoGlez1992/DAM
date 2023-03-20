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
package com.db4o.cs.ssl;

import java.io.*;

import javax.net.ssl.*;

import com.db4o.cs.foundation.*;

@decaf.Ignore
public class SSLSocketFactory implements Socket4Factory {

	private SSLContext _context;

	public SSLSocketFactory(SSLContext context) {
		_context = context;
	}
	
	public ServerSocket4 createServerSocket(int port) throws IOException {
		return new SSLServerSocket4(port, (_context == null ? (SSLServerSocketFactory)SSLServerSocketFactory.getDefault() : _context.getServerSocketFactory()));
	}

	public Socket4 createSocket(String hostName, int port) throws IOException {
		return new SSLSocket4(hostName, port, (_context == null ? (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault() : _context.getSocketFactory()));
	}

}
