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

import com.db4o.cs.foundation.*;

public class CountingSocket4 extends Socket4Decorator {
	
	private final Object lock = new Object();
	
	public CountingSocket4(Socket4 socket) {	
		super(socket);
	}

	public void write(byte[] bytes, int offset, int count) throws IOException {
		synchronized(lock){
			_bytesSent += count;
			_messagesSent++;
		}
		super.write(bytes, offset, count);
	}

	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		int bytesReceived = super.read(buffer, offset, count);
		synchronized(lock){
			_bytesReceived += bytesReceived;
		}
		return bytesReceived;
	}
	
	public double bytesSent() {
		synchronized(lock){
			return _bytesSent;
		}
	}

	public double bytesReceived() {
		synchronized(lock){
			return _bytesReceived;
		}
	}

	public double messagesSent() {
		synchronized(lock){
			return _messagesSent;
		}
	}
	
	public void resetCount() {
		synchronized(lock){
			_bytesSent = 0.0;
			_bytesReceived = 0.0;
			_messagesSent = 0.0;
		}
	}
	
	private double _bytesSent;
	private double _bytesReceived;
	private double _messagesSent;
}
