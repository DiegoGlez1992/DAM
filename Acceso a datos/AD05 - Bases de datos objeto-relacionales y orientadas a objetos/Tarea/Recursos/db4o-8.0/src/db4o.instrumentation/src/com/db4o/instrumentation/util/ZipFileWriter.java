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
package com.db4o.instrumentation.util;

import java.io.*;
import java.util.zip.*;

/**
 * Simpler facade on top of the {@link ZipOutputStream} api.
 */
public class ZipFileWriter {

	private final ZipOutputStream _zipWriter;

	public ZipFileWriter(File file) throws IOException {
		_zipWriter = new ZipOutputStream(new FileOutputStream(file));
	}

	public void writeResourceString(String fileName, String contents) throws IOException {
		writeEntry(fileName, contents.getBytes());
	}	
	
	public void close() throws IOException {
		_zipWriter.close();
	}
	
	public void writeEntry(String entryName, final byte[] bytes)	throws IOException {
		beginEntry(entryName, bytes.length);
		try {
			_zipWriter.write(bytes);
		} finally {
			endEntry();
		}
	}
	
	private void beginEntry(final String entryName, int length) throws IOException {
		final ZipEntry entry = new ZipEntry(entryName);
		entry.setSize(length);
		_zipWriter.putNextEntry(entry);
	}

	private void endEntry() throws IOException {
		_zipWriter.closeEntry();
	}
}
