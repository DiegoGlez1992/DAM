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

public class BinTest extends StorageTestUnitBase  {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(BinTest.class).run();
	}
	
	public void testReadWrite() throws Exception {
		int count = 1024 * 8 + 10;
        byte[] data = new byte[count];
        for (int i = 0; i < count; ++i) {
        	data[i] = (byte) (i % 256);
        }
        _bin.write(0, data, data.length);
        _bin.sync();
        
        byte[] readBytes = new byte[count];
        _bin.read(0, readBytes, readBytes.length);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
	}
	
	public void testHugeFile() {
		final int dataSize = 1024 * 2;
		final byte[] data = newDataArray(dataSize);
		for (int i=0; i<64; ++i) {
			_bin.write(i * data.length, data, data.length);
		}
		
		final byte[] readBuffer = new byte[dataSize];
		for (int i=0; i<64; ++i) {
			_bin.read(dataSize * (63-i), readBuffer, readBuffer.length);
			ArrayAssert.areEqual(data, readBuffer);
		}
		
	}

	public void testSeek() throws Exception {
		final int count = 1024 * 2 + 10;
        final byte[] data = newDataArray(count);
        _bin.write(0, data, data.length);
        final byte[] readBytes = new byte[count];
        _bin.read(0, readBytes, readBytes.length);
        for (int i = 0; i < count; i++) {
        	Assert.areEqual(data[i], readBytes[i]);
        }
        _bin.read(20, readBytes, readBytes.length);
        for (int i = 0; i < count - 20; i++) {
        	Assert.areEqual(data[i + 20], readBytes[i]);
        }
        
        byte[] writtenData = new byte[10];
        for (int i = 0; i < writtenData.length; ++i) {
        	writtenData[i] = (byte) i;
        }
        _bin.write(1000, writtenData, writtenData.length);
        int readCount = _bin.read(1000, readBytes, 10);
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
				"this is a really long string, just to make sure all Storage implementations work correctly. " };
		for(int j = 0; j < strs.length; j++) {
			assertReadWriteString(_bin, strs[j]);
		}
	}
	
	private void assertReadWriteString(Bin adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		adapter.write(0, data, data.length);
		adapter.read(0, read, read.length);
		Assert.areEqual(str, new String(read, 0, data.length));
	}

	/**
	 * @sharpen.rename _testReadWriteAheadFileEnd
	 */
	public void testReadWriteAheadFileEnd() throws Exception {
		String str = "this is a really long string, just to make sure that all Storage implementations work correctly. ";
		assertReadWriteAheadFileEnd(_bin, str);
	}
	
	private void assertReadWriteAheadFileEnd(Bin adapter, String str) throws Exception {
		byte[] data = str.getBytes();
		byte[] read = new byte[2048];
		int readBytes = adapter.read(10, data, data.length);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.length());
		readBytes = adapter.read(0, data, data.length);
		Assert.areEqual(-1, readBytes);
		Assert.areEqual(0, adapter.length());
		
		adapter.write(10, data, data.length);
		Assert.areEqual(10 + data.length, adapter.length());
		
		
		readBytes = adapter.read(0, read, read.length);
		Assert.areEqual(10 + data.length, readBytes);
		
		readBytes = adapter.read(20 + data.length, read, read.length);
		Assert.areEqual(-1, readBytes);
		
		readBytes = adapter.read(1024 + data.length, read, read.length);
		Assert.areEqual(-1, readBytes);
		
		adapter.write(1200, data, data.length);
		readBytes = adapter.read(0, read, read.length);
		Assert.areEqual(1200 + data.length, readBytes);		
	}

}
