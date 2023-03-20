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
public class SynchronizedBin extends BinDecorator {

	public SynchronizedBin(Bin bin) {
	    super(bin);
    }
	
	@Override
	public void close() {
	    synchronized(_bin) {
	    	super.close();
	    }
	}
	
	@Override
	public long length() {
		synchronized(_bin) {
			return super.length();
		}
	}
	
	@Override
	public int read(long position, byte[] buffer, int bytesToRead) {
		synchronized(_bin) {
			return super.read(position, buffer, bytesToRead);
		}
	}
	
	@Override
	public void write(long position, byte[] bytes, int bytesToWrite) {
		synchronized(_bin) {
			super.write(position, bytes, bytesToWrite);
		}
	}
	
	@Override
	public void sync() {
		synchronized(_bin) {
			super.sync();
		}
	}
	
	@Override
	public void sync(Runnable runnable) {
		synchronized(_bin) {
			super.sync(runnable);
		}
	}
}
