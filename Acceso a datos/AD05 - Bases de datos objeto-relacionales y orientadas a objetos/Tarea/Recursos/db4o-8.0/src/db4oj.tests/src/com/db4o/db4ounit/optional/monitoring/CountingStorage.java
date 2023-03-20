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
package com.db4o.db4ounit.optional.monitoring;

import com.db4o.io.*;

class CountingStorage extends StorageDecorator {
	
	private int _numberOfSyncCalls;
	
	private int _numberOfBytesRead;
	
	private int _numberOfBytesWritten;

	private int _numberOfReadCalls;
	
	private int _numberOfWriteCalls;


	public CountingStorage(Storage storage) {
		super(storage);
	}
	
	public int numberOfSyncCalls() {
		return _numberOfSyncCalls;
	}
	
	public int numberOfBytesRead() {
		return _numberOfBytesRead;
	}
	
	public int numberOfBytesWritten() {
		return _numberOfBytesWritten;
	}
	
	public int numberOfReadCalls() {
		return _numberOfReadCalls;
	}
	
	public int numberOfWriteCalls() {
		return _numberOfWriteCalls;
	}
	
	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new BinDecorator(bin) {


			@Override
			public void sync() {
				++_numberOfSyncCalls;
				super.sync();
			}
			
			@Override
			public void sync(Runnable runnable) {
				++_numberOfSyncCalls;
				super.sync(runnable);
			}
			
			@Override
			public int read(long position, byte[] bytes, int bytesToRead) {
				int bytesRead = super.read(position, bytes, bytesToRead);
				_numberOfBytesRead += bytesRead;
				++ _numberOfReadCalls;
				return bytesRead;
			}
			
			@Override
			public void write(long position, byte[] bytes, int bytesToWrite) {
				_numberOfBytesWritten += bytesToWrite;
				++ _numberOfWriteCalls;
				super.write(position, bytes, bytesToWrite);
			}
			
			
		};
	}
	
}