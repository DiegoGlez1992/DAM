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
package com.db4o.io;

import com.db4o.foundation.*;
import com.db4o.internal.threading.*;


public class ThreadedSyncBin extends BinDecorator {
	
	private static final int ONE_SECOND = 1000;

	private volatile Runnable _syncRunnable ;
	
	private volatile boolean _closed;
	
	private final Thread _thread;
	
	private final Lock4 _lock = new Lock4();
	
	public ThreadedSyncBin(Bin bin) {
	    super(bin);
	    _thread = new Thread(new Runnable() {
			public void run() {
				Closure4 closure = new Closure4() {
					public Object run() {
						runSyncRunnable();
						_lock.snooze(ONE_SECOND);
						return null;
					}
				};
				while(true){
					_lock.run(closure);
					if(_closed){
						return;
					}
				}
			}
		}, "ThreadedSyncBin");
	    _thread.start();
    }
	
	@Override
	public void close() {
    	waitForPendingSync();
    	_closed = true;
		_lock.run(new Closure4() {
			public Object run() {
				_lock.awake();
				return null;
			}
		});
    	super.close();
	}
	
	private void waitForPendingSync()  {
		while(_syncRunnable != null){
			if(Thread.currentThread() == _thread){
				return;
			}
		}
	}

	@Override
	public long length() {
		waitForPendingSync();
		return super.length();
	}
	
	@Override
	public int read(long position, byte[] buffer, int bytesToRead) {
		waitForPendingSync();
		return super.read(position, buffer, bytesToRead);
	}
	
	@Override
	public void write(long position, byte[] bytes, int bytesToWrite) {
		waitForPendingSync();
		super.write(position, bytes, bytesToWrite);
	}
	
	@Override
	public void sync() {
		waitForPendingSync();
		super.sync();
	}
	
	@Override
	public void sync(final Runnable runnable) {
		waitForPendingSync();
		_lock.run(new Closure4() {
			public Object run() {
				_syncRunnable = runnable;
				_lock.awake();
				return null;
			}
		});
	}
	
	final void runSyncRunnable(){
		Runnable runnable = _syncRunnable;
		if(runnable != null){
			super.sync();
			runnable.run();
			super.sync();
			_syncRunnable = null;
		}
	}

}
