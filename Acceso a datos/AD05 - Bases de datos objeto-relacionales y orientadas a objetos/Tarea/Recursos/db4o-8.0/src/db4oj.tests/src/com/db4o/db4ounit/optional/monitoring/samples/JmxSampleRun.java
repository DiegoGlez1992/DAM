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
package com.db4o.db4ounit.optional.monitoring.samples;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.monitoring.*;

@decaf.Ignore
public class JmxSampleRun {

	private static final int OBJECT_COUNT = 300000;
	
	private static final int COMMIT_INTERVAL = 100;

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new Thread(newWorker("jmx1.db4o", 0));
		Thread thread2 = new Thread(newWorker("jmx2.db4o", 10));
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		System.out.println("JmxSampleRun completed.");
	}

	private static Runnable newWorker(final String databaseFileName,
			final int sleepInterval) {
		Runnable runnable = new Runnable(){
			public void run() {
				ObjectContainer container = Db4oEmbedded.openFile(configure(), databaseFileName);
				for (int i = 0; i < OBJECT_COUNT; i++) {
					container.store(new Object());
					if(i % COMMIT_INTERVAL == 0){
						container.commit();
					}
					try {
						Thread.sleep(sleepInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				container.close();
			}
		};
		return runnable;
	}

	private static EmbeddedConfiguration configure() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MonitoredStorage(new FileStorage()));
		return config;
	}

}
