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

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

/**
 * Storage that allows to save an open database file
 * as another file while keeping the reference system
 * in place. If anything goes wrong during copying the 
 * storage tries to reopen the original file, so commit 
 * operations can still take place against the original
 * file.  
 */
public class SaveAsStorage extends StorageDecorator {
	
	private final Hashtable4 _binRecords = new Hashtable4();
	
	public SaveAsStorage(Storage storage) {
		super(storage);
	}
	
	/**
	 * call this method to save the content of a currently 
	 * open ObjectContainer session to a new file location. 
	 * Invocation will close the old file without a commit, 
	 * keep the reference system in place and connect it to
	 * the file in the new location. If anything goes wrong
	 * during the copying operation or while opening it will
	 * be attempted to reopen the old file. In this case a 
	 * Db4oException will be thrown. 
	 * @param oldUri the path to the old open database file
	 * @param newUri the path to the new database file
	 */
	public void saveAs(final String oldUri, final String newUri) {
		if(File4.exists(newUri)){
			throw new IllegalStateException(newUri + " already exists");
		}
		BinRecord binRecord = (BinRecord) _binRecords.get(oldUri);
		if(binRecord == null){
			throw new IllegalStateException(oldUri + " was never opened or was closed.");
		}
		
		final BinConfiguration oldConfiguration = binRecord._binConfiguration;
		final SaveAsBin saveAsBin = binRecord._bin;
		
		Runnable closure = new Runnable() {
			public void run() {
				saveAsBin.sync();
				saveAsBin.close();
				
				try {
					File4.copy(oldUri, newUri);
				} catch (Exception e) {
					reopenOldConfiguration(saveAsBin, oldConfiguration, newUri, e);
				}
				
				BinConfiguration newConfiguration = pointToNewUri(oldConfiguration,newUri);
				
				try{
					Bin newBin = _storage.open(newConfiguration);
					saveAsBin.delegateTo(newBin);
					_binRecords.remove(oldUri);
					_binRecords.put(newUri, new BinRecord(newConfiguration, saveAsBin));
				} catch(Exception e){
					reopenOldConfiguration(saveAsBin, oldConfiguration, newUri, e);
				}				
			}};
			
		saveAsBin.exchangeUnderlyingBin(closure);
	}

	private BinConfiguration pointToNewUri(BinConfiguration oldConfig, String newUri) {
		return new BinConfiguration(
					newUri, 
					oldConfig.lockFile(), 
					oldConfig.initialLength(), 
					oldConfig.readOnly());
	}

	private void reopenOldConfiguration(SaveAsBin saveAsBin, BinConfiguration config, String newUri, Exception e) {
		Bin safeBin = _storage.open(config);
		saveAsBin.delegateTo(safeBin);
		throw new Db4oException("Copying to " + newUri + " failed. Reopened " + config.uri(), e);
	}
	
	@Override
	public Bin open(BinConfiguration config) throws Db4oIOException {
		SaveAsBin openedBin = new SaveAsBin(super.open(config));
		_binRecords.put(config.uri(), new BinRecord(config, openedBin));
		return openedBin;
	}
	
	private static class BinRecord {
		
		final SaveAsBin _bin;
		
		final BinConfiguration _binConfiguration;
		
		BinRecord(BinConfiguration binConfiguration, SaveAsBin bin){
			_binConfiguration = binConfiguration;
			_bin = bin;
		}
		
	}
	
	/**
	 * We could have nicely used BinDecorator here, but 
	 * BinDecorator doesn't allow exchanging the Bin. To
	 * be compatible with released versions we do 
	 */
	private static class SaveAsBin implements Bin{
		
		private Bin _bin;
		
		SaveAsBin(Bin delegate_){
			_bin = delegate_;
		}

		public void exchangeUnderlyingBin(Runnable closure) {
			synchronized (this) {
				closure.run();
			}
		}

		public void close() {
			synchronized (this) {
				_bin.close();
			}
		}

		public long length() {
			synchronized (this) {
				return _bin.length();
			}
		}

		public int read(long position, byte[] bytes, int bytesToRead) {
			synchronized (this) {
				return _bin.read(position, bytes, bytesToRead);
			}
		}

		public void sync() {
			synchronized (this) {
				_bin.sync();
			}
		}

		public void sync(Runnable runnable) {
			synchronized (this) {
				sync();
				runnable.run();
				sync();				
			}
		}
		
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			synchronized (this) {
				return _bin.syncRead(position, bytes, bytesToRead);
			}
		}

		public void write(long position, byte[] bytes, int bytesToWrite) {
			synchronized (this) {
				_bin.write(position, bytes, bytesToWrite);
			}
		}
		
		public void delegateTo(Bin bin){
			_bin = bin;
		}

	}

}
