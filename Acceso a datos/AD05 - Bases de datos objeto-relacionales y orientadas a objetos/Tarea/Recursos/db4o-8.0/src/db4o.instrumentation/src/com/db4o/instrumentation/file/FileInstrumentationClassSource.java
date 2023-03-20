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
package com.db4o.instrumentation.file;

import java.io.*;

import com.db4o.instrumentation.util.*;

/**
 * @exclude
 */
public class FileInstrumentationClassSource implements Comparable, InstrumentationClassSource {
	private final File _root;
	private final File _file;

	public FileInstrumentationClassSource(File root, File file) {
		this._root = root;
		this._file = file;
	}
	
	public File root() {
		return _root;
	}

	public File file() {
		return _file;
	}
	
	public String className() throws IOException {
		return BloatUtil.classNameForPath(classPath());
	}

	private String classPath() throws IOException {
		return _file.getCanonicalPath().substring(_root.getCanonicalPath().length()+1);
	}

	public File targetPath(File targetBase) throws IOException {
		return new File(targetBase, classPath());
	}
	
	public int hashCode() {
		return 43 * _root.hashCode() + _file.hashCode();
	}

	public InputStream inputStream() throws IOException {
		return new FileInputStream(_file);
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final FileInstrumentationClassSource other = (FileInstrumentationClassSource) obj;
		return _root.equals(other._root) && _file.equals(other._file);
	}

	public int compareTo(Object o) {
		return _file.compareTo(((FileInstrumentationClassSource)o)._file);
	}
	
	public String toString() {
		return _file + " [" + _root + "]";
	}

    public File sourceFile() {
        return _file;
    }
	
}
