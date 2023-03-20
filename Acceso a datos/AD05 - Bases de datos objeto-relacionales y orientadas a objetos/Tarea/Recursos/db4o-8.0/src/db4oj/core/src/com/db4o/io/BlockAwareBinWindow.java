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

/**
 * Bounded handle into an IoAdapter: Can only access a restricted area.
 * @exclude
 */
public class BlockAwareBinWindow {

	private BlockAwareBin _bin;
	private int _blockOff;
	private int _len;
	private boolean _disabled;

	/**
	 * @param io The delegate I/O adapter
	 * @param blockOff The block offset address into the I/O adapter that maps to the start index (0) of this window
	 * @param len The size of this window in bytes
	 */
	public BlockAwareBinWindow(BlockAwareBin io,int blockOff,int len) {
		_bin = io;
		_blockOff=blockOff;
		_len=len;
		_disabled=false;
	}

	/**
	 * @return Size of this I/O adapter window in bytes.
	 */
	public int length() {
		return _len;
	}

	/**
	 * @param off Offset in bytes relative to the window start
	 * @param data Data to write into the window starting from the given offset
	 */
	public void write(int off,byte[] data) throws IllegalArgumentException, IllegalStateException{
		checkBounds(off, data);
		_bin.blockWrite(_blockOff+off, data);
	}

	/**
	 * @param off Offset in bytes relative to the window start
	 * @param data Data buffer to read from the window starting from the given offset
	 */
	public int read(int off,byte[] data) throws IllegalArgumentException, IllegalStateException {
		checkBounds(off, data);
		return _bin.blockRead(_blockOff+off, data);
	}

	/**
	 * Disable IO Adapter Window
	 */
	public void disable() {
		_disabled=true;
	}
	
	/**
	 * Flush IO Adapter Window
	 */
	public void flush()  {
		if(!_disabled) {
			_bin.sync();
		}
	}
	
	private void checkBounds(int off, byte[] data) {
		if(_disabled) {
			throw new IllegalStateException();
		}
		if(data==null||off<0||off+data.length>_len) {
			throw new IllegalArgumentException();
		}
	}
}
