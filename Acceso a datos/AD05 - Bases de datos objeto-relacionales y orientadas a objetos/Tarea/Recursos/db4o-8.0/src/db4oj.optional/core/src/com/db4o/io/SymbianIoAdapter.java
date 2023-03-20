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

import java.io.*;

import com.db4o.ext.*;

/**
 * Workaround for two I/O bugs in Symbian JDK versions:<br>
 * - seek() cannot move beyond the current file length.<br>
 *   Fix: Write padding bytes up to the seek target if necessary<br>
 * - Under certain (rare) conditions, calls to RAF.length() seems
 *   to garble up following reads.<br>
 *   Fix: Use a second RAF handle to the file for length() calls
 *   only.<br><br>
 *   
 *   <b>Usage:</b><br>
 *   Db4o.configure().io(new com.db4o.io.SymbianIoAdapter())<br><br>
 *   
 * TODO:<br> 
 * - BasicClusterTest C/S fails (in AllTests context only)
 * 
 * @sharpen.ignore
 * @deprecated Symbian is not supported anymore since 8.0
 */
public class SymbianIoAdapter extends RandomAccessFileAdapter {
    private byte[] _seekBytes=new byte[500];
    private String _path;
    private long _pos;
    private long _length;
    
	protected SymbianIoAdapter(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		super(path, lockFile, initialLength, readOnly);		
		_path=path;
		_pos=0;
		setLength();
	}
	
	private void setLength() throws Db4oIOException {
		_length=retrieveLength();
	}

		
	private long retrieveLength() throws Db4oIOException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(_path, "r");
			return file.length();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		} finally {
			try {
				if (file != null) {
					file.close();
				}
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
	}
	
	
	public SymbianIoAdapter() {
		super();
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new SymbianIoAdapter(path,lockFile,initialLength, readOnly);
	}

	public long getLength() throws Db4oIOException {
		setLength();
		return _length;
	}
	
	public int read(byte[] bytes, int length) throws Db4oIOException {
		int ret=super.read(bytes, length);
		_pos+=ret;
		return ret;
	}
	
	public void write(byte[] buffer, int length) throws Db4oIOException {
		super.write(buffer, length);		
		_pos+=length;
		if(_pos>_length) {
			setLength();
		}
	}
	
	public void seek(long pos) throws Db4oIOException {
		if (pos > _length) {
			setLength();
		}
		if (pos > _length) {
			int len = (int) (pos - _length);
			super.seek(_length);
			_pos=_length;
			if (len < 500) {
				write(_seekBytes, len);
			} else {
				write(new byte[len]);
			}
		}
		super.seek(pos);
		_pos=pos;
	}
}