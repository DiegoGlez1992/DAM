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
package com.db4o.db4ounit.common.io;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;


public class RandomAccessFileFactoryTestCase extends TestWithTempFile{
	
	public void testLockDatabaseFileFalse() throws IOException{
		ObjectContainer container = openObjectContainer(false);
		RandomAccessFile raf = RandomAccessFileFactory.newRandomAccessFile(tempFile(), false, false);
        byte[] bytes = new byte[1];
	    raf.read(bytes);
		raf.close();
		container.close();
	}

	public void testLockDatabaseFileTrue() throws IOException{
		ObjectContainer container = openObjectContainer(true);
		if(! Platform4.needsLockFileThread()){
			Assert.expect(DatabaseFileLockedException.class, new CodeBlock() {
				public void run() throws Throwable {
					RandomAccessFileFactory.newRandomAccessFile(tempFile(), false, true);
				}
			});
		}
		container.close();
	}

	public void testReadOnlyLocked() throws IOException{
		final byte[] bytes = new byte[1];
		final RandomAccessFile raf = RandomAccessFileFactory.newRandomAccessFile(tempFile(), true, true);
		Assert.expect(IOException.class, new CodeBlock() {
			public void run() throws Throwable {
				raf.write(bytes);
			}
		});
		raf.close();
	}
	
	public void testReadOnlyUnLocked() throws IOException{
		final byte[] bytes = new byte[1];
		final RandomAccessFile raf = RandomAccessFileFactory.newRandomAccessFile(tempFile(), true, false);
		Assert.expect(IOException.class, new CodeBlock() {
			public void run() throws Throwable {
				raf.write(bytes);
			}
		});
		raf.close();
	}

	
	private ObjectContainer openObjectContainer(boolean lockDatabaseFile) {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().lockDatabaseFile(lockDatabaseFile);
		return Db4oEmbedded.openFile(config, tempFile());
	}
	



}
