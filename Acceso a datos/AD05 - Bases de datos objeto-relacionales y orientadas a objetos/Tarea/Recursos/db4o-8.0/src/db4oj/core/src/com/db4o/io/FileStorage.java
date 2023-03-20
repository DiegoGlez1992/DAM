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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

/**
 * Storage adapter to store db4o database data to physical
 * files on hard disc. 
 */
public class FileStorage implements Storage {

	/**
	 * opens a {@link Bin} on the specified URI (file system path).
	 */
	public Bin open(BinConfiguration config) throws Db4oIOException {
		return new FileBin(config);
    }

	/**
	 * returns true if the specified file system path already exists.
	 */
	public boolean exists(String uri) {
		final File file = new File(uri);
		return file.exists() && file.length() > 0;
    }
	
	public static class FileBin implements Bin {

		private final String _path;

		private RandomAccessFile _file;
		
		public FileBin(BinConfiguration config) throws Db4oIOException {
			boolean ok = false;
			try {
				_path = new File(config.uri()).getCanonicalPath();
				_file = RandomAccessFileFactory.newRandomAccessFile(_path, config.readOnly(), config.lockFile());
				if (config.initialLength() > 0) {
					write(config.initialLength() - 1, new byte[] { 0 }, 1);
				}
				ok = true;
			} catch (IOException e) {
				throw new Db4oIOException(e);
			} finally {
				if(!ok) {
					close();
				}
			}
		}

		public void close() throws Db4oIOException {
			Platform4.unlockFile(_path, _file);
			try {
				if (!isClosed()) {
					_file.close();
				}
			} 
			catch (IOException e) {
				throw new Db4oIOException(e);
			}
			finally {
				_file = null;
			}
		}
		
		boolean isClosed() {
			return _file == null;
		}
		
		public long length() throws Db4oIOException {
			try {
				return _file.length();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			try {
				seek(pos);
				if(DTrace.enabled){
					DTrace.FILE_READ.logLength(pos, length);
				}
				return _file.read(bytes, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		void seek(long pos) throws IOException {
			if (DTrace.enabled) {
				DTrace.REGULAR_SEEK.log(pos);
			}
			_file.seek(pos);
		}

		public void sync() throws Db4oIOException {
			try {
				_file.getFD().sync();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
		
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			return read(position, bytes, bytesToRead);
		}
		
		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			checkClosed();
			try {
				seek(pos);
				if(DTrace.enabled){
					DTrace.FILE_WRITE.logLength(pos, length);
				}
				_file.write(buffer, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		private void checkClosed() {
			if (isClosed()) {
				throw new Db4oIOException();
			}
		}

		public void sync(Runnable runnable) {
			sync();
			runnable.run();
			sync();
		}
	}

	public void delete(String uri) throws IOException {
		File4.delete(uri);
	}

	public void rename(String oldUri, String newUri) throws IOException {
		File4.rename(oldUri, newUri);
	}
}
