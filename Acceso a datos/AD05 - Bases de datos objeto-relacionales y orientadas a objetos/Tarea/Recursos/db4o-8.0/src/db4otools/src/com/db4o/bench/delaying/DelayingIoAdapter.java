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
package com.db4o.bench.delaying;

import com.db4o.bench.timing.*;
import com.db4o.ext.*;
import com.db4o.io.*;

/**
 * @deprecated use {@link Storage}-equivalent instead.
 */
public class DelayingIoAdapter extends VanillaIoAdapter {

	private static Delays _delays = new Delays(0,0,0,0);
	
	private NanoTiming _timing;
	
	public DelayingIoAdapter(IoAdapter delegateAdapter) {
		this(delegateAdapter, _delays);
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, Delays delays) {
		super(delegateAdapter);
		_delays = delays;
		_timing = new NanoTiming();
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength)throws Db4oIOException {
		this(delegateAdapter, path, lockFile, initialLength, _delays);
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, Delays delays)throws Db4oIOException {
		this(delegateAdapter.open(path, lockFile, initialLength, false), delays);
	}
	
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new DelayingIoAdapter(_delegate, path, lockFile, initialLength);
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
		delay(_delays.values[Delays.READ]);
		return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	delay(_delays.values[Delays.SEEK]);
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
		delay(_delays.values[Delays.SYNC]);
    	_delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
		delay(_delays.values[Delays.WRITE]);
    	_delegate.write(buffer, length);
    }
	
    private void delay(long time) {
    	_timing.waitNano(time);
    }
    
}
