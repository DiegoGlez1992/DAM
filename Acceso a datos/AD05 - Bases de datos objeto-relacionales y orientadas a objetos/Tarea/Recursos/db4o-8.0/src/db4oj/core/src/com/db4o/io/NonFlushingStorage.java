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
 * Storage adapter that does not pass flush calls 
 * on to its delegate.
 * You can use this {@link Storage} for improved db4o
 * speed at the risk of corrupted database files in 
 * case of system failure.    
 */
public class NonFlushingStorage extends StorageDecorator {

	public NonFlushingStorage(Storage storage) {
		super(storage);
    }

	@Override
	protected Bin decorate(BinConfiguration config, Bin storage) {
		return new NonFlushingBin(storage);
	}
	
	private static class NonFlushingBin extends BinDecorator {

		public NonFlushingBin(Bin storage) {
			super(storage);
	    }
		
		@Override
		public void sync() {
		}
		
		@Override
		public void sync(Runnable runnable) {
			runnable.run();
		}
		
		
	}

}
