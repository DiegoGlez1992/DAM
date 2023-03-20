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

import java.io.*;
import java.net.*;

import com.db4o.internal.*;

public abstract class NetworkSocketBase implements Socket4 {

	private String _hostName;
	private Socket _socket;
	private InputStream _in;
	private OutputStream _out;

	public NetworkSocketBase(Socket socket) throws IOException {
		this(socket, null);
    }

    public NetworkSocketBase(Socket socket, String hostName) throws IOException {
    	_socket = socket;
        _hostName=hostName;
        _in = _socket.getInputStream();
        _out = _socket.getOutputStream();
    }

	public void close() throws IOException {
		_socket.close();
	}

	public void flush() throws IOException {
		_out.flush();
	}

	public boolean isConnected() {
	    return Platform4.isConnected(_socket);
	}

	public int read(byte[] a_bytes, int a_offset, int a_length) throws IOException {
				int ret = _in.read(a_bytes, a_offset, a_length);
				checkEOF(ret);
				return ret;
			}

	private void checkEOF(int ret) throws IOException {
		if(ret == -1) {
			throw new IOException();
		}
	}

	public void setSoTimeout(int timeout) {
	    try {
	        _socket.setSoTimeout(timeout);
	    } 
	    catch (SocketException e) {
	        e.printStackTrace();
	    }
	}

	public void write(byte[] bytes, int off, int len) throws IOException {
		_out.write(bytes,off,len);
	}

	public Socket4 openParallelSocket() throws IOException {
		if(_hostName==null) {
			throw new IllegalStateException();
		}
		return createParallelSocket(_hostName, _socket.getPort());
	}

	protected abstract Socket4 createParallelSocket(String hostName, int port) throws IOException;

	@Override
	public String toString() {
		return _socket.toString();
	}
}