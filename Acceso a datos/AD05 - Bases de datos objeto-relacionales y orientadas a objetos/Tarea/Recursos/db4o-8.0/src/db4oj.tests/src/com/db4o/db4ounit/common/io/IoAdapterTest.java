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

import db4ounit.*;

public class IoAdapterTest extends IoAdapterTestUnitBase  {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(IoAdapterTest.class).run();
	}
	
	public void testReadWrite() throws Exception {
		_adapter.seek(0);
        int count = 1024 * 8 + 10;
        byte[] data = new byte[count];
        for (int i = 0; i < count; ++i) {
        	data[i] = (byte) (i % 256);
        }
        _adapter.write(data);
        _adapter.seek(0);
        byte[] readBytes = new byte[count];
        _adapter.read(readBytes);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
	}
	
	public void testHugeFile() {
		final int dataSize = 1024 * 2;
		final byte[] data = newDataArray(dataSize);
		for (int i=0; i<64; ++i) {
			_adapter.write(data);
		}
		
		final byte[] readBuffer = new byte[dataSize];
		for (int i=0; i<64; ++i) {
			_adapter.seek(dataSize * (63-i));
			_adapter.read(readBuffer);
			ArrayAssert.areEqual(data, readBuffer);
		}
		
	}

	public void testSeek() throws Exception {
		final int count = 1024 * 2 + 10;
        final byte[] data = newDataArray(count);
        _adapter.write(data);
        final byte[] readBytes = new byte[count];
        _adapter.seek(0);
        _adapter.read(readBytes);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
        _adapter.seek(20);
        _adapter.read(readBytes);
        for (int i = 0; i < count - 20; i++) {
        	Assert.areEqual(data[i + 20], readBytes[i]);
        }
        
        byte[] writtenData = new byte[10];
        for (int i = 0; i < writtenData.length; ++i) {
        	writtenData[i] = (byte) i;
        }
        _adapter.seek(1000);
        _adapter.write(writtenData);
        _adapter.seek(1000);
        int readCount = _adapter.read(readBytes, 10);
        Assert.areEqual(10, readCount);
        for (int i = 0; i < readCount; ++i) {
        	Assert.areEqual(i, readBytes[i]);
        }
	}

	private byte[] newDataArray(final int count) {
	    final byte[] data = new byte[count];
        for (int i = 0; i < data.length; ++i) {
        	data[i] = (byte) (i % 256);
        }
	    return data;
    }

	public void testReadWriteBytes() throws Exception {
		String[] strs = {
				"short string",
				"this is a really long string, just to make sure that all IoAdapters work correctly. " };
		for(int j = 0; j < strs.length; j++) {
			assertReadWriteString(_adapter, strs[j]);
		}
	}
	
	private void assertReadWriteString(IoAdapter adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.seek(0);
		adapter.write(data);
		adapter.seek(0);
		adapter.read(read);
		Assert.areEqual(str, new String(read, 0, data.length));
	}

	/**
	 * @sharpen.rename _testReadWriteAheadFileEnd
	 */
	public void testReadWriteAheadFileEnd() throws Exception {
		String str = "this is a really long string, just to make sure that all IoAdapters work correctly. ";
		assertReadWriteAheadFileEnd(_adapter, str);
	}
	
	private void assertReadWriteAheadFileEnd(IoAdapter adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.seek(10);
		int readBytes = adapter.read(data);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.getLength());
		adapter.seek(0);
		readBytes = adapter.read(data);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.getLength());
		
		adapter.seek(10);
		adapter.write(data);
		Assert.areEqual(10 + data.length, adapter.getLength());
		
		
		adapter.seek(0);
		readBytes = adapter.read(read);
		Assert.areEqual(10 + data.length, readBytes);
		
		adapter.seek(20 + data.length);
		readBytes = adapter.read(read);
		Assert.areEqual(-1, readBytes);
		
		adapter.seek(1024 + data.length);
		readBytes = adapter.read(read);
		Assert.areEqual(-1, readBytes);
		
		adapter.seek(1200);
		adapter.write(data);
		adapter.seek(0);
		readBytes = adapter.read(read);
		Assert.areEqual(1200 + data.length, readBytes);		
	}

}
