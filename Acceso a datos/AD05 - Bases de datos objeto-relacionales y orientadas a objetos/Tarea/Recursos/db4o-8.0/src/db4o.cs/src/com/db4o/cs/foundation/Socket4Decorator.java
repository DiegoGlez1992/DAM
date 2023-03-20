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
public class Socket4Decorator implements Socket4 {
	public Socket4Decorator(Socket4 socket) {	
		_socket = socket;
	}

	public void close() throws IOException {
		_socket.close();
	}

	public void flush() throws IOException {
		_socket.flush();
	}

	public boolean isConnected() {
		return _socket.isConnected();
	}

	public Socket4 openParallelSocket() throws IOException {
		return _socket.openParallelSocket();
	}

	public int read(byte[] buffer, int offset, int count) throws IOException {
		return _socket.read(buffer, offset, count);
	}

	public void setSoTimeout(int timeout) {
		_socket.setSoTimeout(timeout);
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		_socket.write(bytes, offset, count);
	}
	
	@Override
	public String toString() {
		return _socket.toString();
	}
	
	protected Socket4 _socket;
}
