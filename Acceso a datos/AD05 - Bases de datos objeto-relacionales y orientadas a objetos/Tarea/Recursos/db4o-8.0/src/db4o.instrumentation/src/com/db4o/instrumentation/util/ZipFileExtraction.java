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
import java.util.*;
import java.util.zip.*;

/**
 * Extracts the contents of a zip file to a directory.
 * 
 * The operation is performed as a side effect of the
 * constructor execution.
 */
public class ZipFileExtraction {

	private final ZipFile _zipFile;
	private final File _targetDir;

	public ZipFileExtraction(File file, File targetDir) throws IOException {
		_targetDir = targetDir;
		_zipFile = new ZipFile(file);
		try {
			extractEntries();
		} finally {
			_zipFile.close();
		}
	}
	
	private void extractEntries() throws IOException {
		final Enumeration entries = _zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			extractEntry(entry);
		}
	}

	private void extractEntry(ZipEntry entry) throws IOException {
		if (entry.isDirectory()) {
			targetPathFor(entry).mkdirs();
			return;
		}
		extractFileEntry(entry);
	}

	private void extractFileEntry(ZipEntry entry) throws FileNotFoundException,
			IOException {
		final FileOutputStream fos = new FileOutputStream(targetPathFor(entry));
		try {
			final InputStream is = _zipFile.getInputStream(entry);
			try {
				copy(is, fos);
			} finally {
				is.close();
			}
		} finally {
			fos.close();
		}
	}

	private void copy(InputStream is, OutputStream os) throws IOException{
		byte[] buffer = new byte[64*1024];
		int bytesRead = 0;
		while (-1 != (bytesRead = is.read(buffer))) {
			os.write(buffer, 0, bytesRead);
		}
	}

	private File targetPathFor(ZipEntry entry) {
		final File targetPath = new File(_targetDir, entry.getName());
		targetPath.getParentFile().mkdirs();
		return targetPath;
	}
}
