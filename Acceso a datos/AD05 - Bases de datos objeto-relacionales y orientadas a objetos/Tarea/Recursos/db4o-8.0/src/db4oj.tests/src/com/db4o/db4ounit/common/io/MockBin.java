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
package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.mocking.*;

public class MockBin extends MethodCallRecorder implements Bin {

	private int _returnValue;

	public void close() {
		record("close");
	}

	public long length() {
		record("length");
		return _returnValue;
	}

	private void record(final String methodName) {
	    record(new MethodCall(methodName));
    }

	public int read(long position, byte[] buffer, int bytesToRead) {
		record(new MethodCall("read", position, buffer, bytesToRead));
		return _returnValue;
	}

	public void sync() {
		record("sync");
	}

	public int syncRead(long position, byte[] buffer, int bytesToRead) {
		record(new MethodCall("syncRead", position, buffer, bytesToRead));
		return _returnValue;
	}
	
	public void write(long position, byte[] bytes, int bytesToWrite) {
		record(new MethodCall("write", position, bytes, bytesToWrite));
	}

	public void returnValueForNextCall(int value) {
		_returnValue = value;
    }
	
	public void sync(Runnable runnable) {
		sync();
		runnable.run();
		sync();
	}



}
