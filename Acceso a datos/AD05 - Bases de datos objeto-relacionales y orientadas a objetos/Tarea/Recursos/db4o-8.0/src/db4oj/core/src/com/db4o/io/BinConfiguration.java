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
 * @exclude
 */
public class BinConfiguration {
	
	private final String _uri;
	
	private final boolean _lockFile;
	
	private final long _initialLength;
	
	private final boolean _readOnly;
	
	private final int _blockSize;

	public BinConfiguration(String uri, boolean lockFile, long initialLength, boolean readOnly) {
		this(uri, lockFile, initialLength, readOnly, 1);
	}

	public BinConfiguration(String uri, boolean lockFile, long initialLength, boolean readOnly, int blockSize) {
		_uri = uri;
		_lockFile = lockFile;
		_initialLength = initialLength;
		_readOnly = readOnly;
		_blockSize = blockSize;
	}
	
	public String uri(){
		return _uri;
	}
	
	public boolean lockFile(){
		return _lockFile;
	}
	
	public long initialLength(){
		return _initialLength;
	}
	
	public boolean readOnly(){
		return _readOnly;
	}
	
	public int blockSize() {
		return _blockSize;
	}
	
	@Override
	public String toString() {
		return "BinConfiguration(Uri: " + _uri + ", Locked: " + _lockFile + ", ReadOnly: " + _readOnly + ", BlockSize: " + _blockSize + ")";
	}
}
