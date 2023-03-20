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
package com.db4o.monitoring;

import com.db4o.*;
import com.db4o.io.*;

import static com.db4o.foundation.Environments.*;

/**
 * Publishes storage statistics to JMX.
 */
@decaf.Ignore
public class MonitoredStorage extends StorageDecorator {

	public MonitoredStorage(Storage storage) {
		super(storage);
	}
	
	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new MonitoredBin(config.uri(), bin);
	}
	
	private static class MonitoredBin extends BinDecorator {

		private IO _ioMBean;

		public MonitoredBin(String uri, Bin bin) {
			super(bin);
			_ioMBean = Db4oMBeans.newIOStatsMBean(my(ObjectContainer.class));
		}
		
		@Override
		public void sync() {
			super.sync();
			_ioMBean.notifySync();
		}
		
		@Override
		public void sync(Runnable runnable) {
			super.sync(runnable);
			_ioMBean.notifySync();
		}
		
		@Override
		public int read(long position, byte[] bytes, int bytesToRead) {
			int bytesRead = super.read(position, bytes, bytesToRead);
			_ioMBean.notifyBytesRead(bytesRead);
			return bytesRead;
		}
		
		@Override
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			int bytesRead = super.syncRead(position, bytes, bytesToRead);
			_ioMBean.notifyBytesRead(bytesRead);
			return bytesRead;
		}
		
		@Override
		public void write(long position, byte[] bytes, int bytesToWrite) {
			super.write(position, bytes, bytesToWrite);
			_ioMBean.notifyBytesWritten(bytesToWrite);
		}
	}
}
