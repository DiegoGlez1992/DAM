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
package com.db4o.util.io.win32;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.io.*;

/**
 * An IoAdapter implementation that uses JNI to talk directly with the WIN32 API.
 */
public class Win32IoAdapter extends IoAdapter {
	
	static {
		System.loadLibrary("Win32IoAdapter");
	}
	
	private long _handle;

	public Win32IoAdapter(String path, boolean lockFile, long initialLength, boolean readOnly) {
		try {
			_handle = openFile(path, lockFile, initialLength);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}	

	public Win32IoAdapter() {
	}

	public void close() throws Db4oIOException {
		try {
			closeFile(getHandle());
			_handle = 0;
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

	public void delete(String path) {
		new File(path).delete();
	}

    public boolean exists(String path){
        File existingFile = new File(path);
        return  existingFile.exists() && existingFile.length() > 0;
    }

	public long getLength() throws Db4oIOException {
		try {
			return getLength(getHandle());
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly)
			throws Db4oIOException {
		return new Win32IoAdapter(path, lockFile, initialLength, readOnly);
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
		try {
			return read(getHandle(), bytes, length);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

	public void seek(long pos) throws Db4oIOException {
		try {
			seek(getHandle(), pos);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}
	
	public void sync() throws Db4oIOException {
		try {
			sync(getHandle());
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}
	
	public void write(byte[] bytes, int length) throws Db4oIOException {
		try {
			write(getHandle(), bytes, length);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}
	
	public void copy(long oldAddress, long newAddress, int length)
			throws Db4oIOException {
		try {
			copy(getHandle(), oldAddress, newAddress, length);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

	private long getHandle() {
		if (0 == _handle) {
			throw new IllegalStateException("File is not open.");
		}
		return _handle;
	}
	
	private static native long openFile(String path, boolean lockFile, long initialLength) throws IOException;
	
	private static native void closeFile(long handle) throws IOException;
	
	private static native long getLength(long handle) throws IOException;
	
	private static native int read(long handle, byte[] bytes, int length) throws IOException;
	
	private static native void seek(long handle, long pos) throws IOException;
	
	private static native void sync(long handle) throws IOException;
	
	private static native void write(long handle, byte[] bytes, int length) throws IOException;
	
	private static native void copy(long handle, long oldAddress, long newAddress, int length) throws IOException;
}
