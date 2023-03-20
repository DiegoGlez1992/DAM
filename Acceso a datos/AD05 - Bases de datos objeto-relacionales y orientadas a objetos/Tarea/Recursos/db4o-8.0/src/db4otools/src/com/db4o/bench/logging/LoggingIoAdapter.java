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
package com.db4o.bench.logging;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.io.*;
/**
 * 
 * @sharpen.ignore
 * @deprecated use {@link Storage}-equivalent instead.
 *
 */
public class LoggingIoAdapter extends VanillaIoAdapter {
	
	public final static int LOG_READ 	= 1;
	public final static int LOG_WRITE 	= 2;
	public final static int LOG_SYNC 	= 4;
	public final static int LOG_SEEK    = 8;
	
	public final static int LOG_ALL = LOG_READ + LOG_WRITE + LOG_SYNC + LOG_SEEK;
	
	private final String _fileName;
	private final PrintStream _out;
	
	private int _config;
	
    public LoggingIoAdapter(IoAdapter delegateAdapter, String fileName, int config)  {
        super(delegateAdapter);
        _fileName = fileName;
        try {
			_out = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			throw new Db4oIOException(e);
		}
        _config = config;
    }
    
    public LoggingIoAdapter(IoAdapter delegateAdapter, String fileName)  {
    	this(delegateAdapter, fileName, LOG_ALL);
    }

    private LoggingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, String fileName, int config) throws Db4oIOException{
        this(delegateAdapter.open(path, lockFile, initialLength, false), fileName, config);
    }

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new LoggingIoAdapter(_delegate, path, lockFile, initialLength, _fileName, _config);
	}
	
	public void close() throws Db4oIOException {
		_out.flush();
		_out.close();
		super.close();
	}

    public int read(byte[] bytes, int length) throws Db4oIOException {
    	if(config(LOG_READ)) {
    		println(LogConstants.READ_ENTRY + length);
    	}
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	if(config(LOG_SEEK)) {
    		println(LogConstants.SEEK_ENTRY + pos);
    	}
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
    	if(config(LOG_SYNC)) {
    		println(LogConstants.SYNC_ENTRY);
    	}
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
    	if(config(LOG_WRITE)) {
    		println(LogConstants.WRITE_ENTRY + length);
    	}
        _delegate.write(buffer, length);
    }
    
    private void println(String s){
    	_out.println(s);
    }

    private boolean config(int mask) {
    	return (_config & mask) != 0;
    }
    
}
