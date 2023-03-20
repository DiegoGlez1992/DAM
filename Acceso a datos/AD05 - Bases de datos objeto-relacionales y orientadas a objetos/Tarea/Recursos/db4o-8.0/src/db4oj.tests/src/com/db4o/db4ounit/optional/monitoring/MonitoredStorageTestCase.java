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

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.monitoring.*;

import db4ounit.*;
import db4ounit.extensions.OptOutNotSupportedJavaxManagement;

@decaf.Remove
public class MonitoredStorageTestCase implements TestLifeCycle, OptOutNotSupportedJavaxManagement {
	
	private CountingStorage _storage = new CountingStorage(new MemoryStorage());
	
	private EmbeddedObjectContainer _container;

	private final MBeanProxy _bean = new MBeanProxy(getIOMBeanName());
	
	public void testNumSyncsPerSecond() {
		Assert.areEqual(_storage.numberOfSyncCalls(), getAttribute("SyncsPerSecond"));		
	}
	
	public void testNumBytesReadPerSecond() {
		Assert.areEqual(_storage.numberOfBytesRead(), getAttribute("BytesReadPerSecond"));		
	}

	public void testNumBytesWrittenPerSecond() {
		Assert.areEqual(_storage.numberOfBytesWritten(), getAttribute("BytesWrittenPerSecond"));		
	}

	public void testNumReadsPerSecond() {
		Assert.areEqual(_storage.numberOfReadCalls(), getAttribute("ReadsPerSecond"));		
	}

	public void testNumWritesPerSecond() {
		Assert.areEqual(_storage.numberOfWriteCalls(), getAttribute("WritesPerSecond"));		
	}

	public void setUp() throws Exception{
		ClockMock clock = new ClockMock();
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(_storage);
		config.common().add(new IOMonitoringSupport());
		config.common().environment().add(clock);
		
		_container = Db4oEmbedded.openFile(config, "");
		_container.store(new Object());
		_container.commit();
		
		clock.advance(1000);
	
	}

	public void tearDown() throws Exception {
		if(null != _container){
			_container.close();
		}
	}

	private double getAttribute(final String attribute) {
		return _bean.<Double>getAttribute(attribute);
	}

	
	private ObjectName getIOMBeanName() {
		return Db4oMBeans.mBeanNameFor(IOMBean.class, "");
	}

}
