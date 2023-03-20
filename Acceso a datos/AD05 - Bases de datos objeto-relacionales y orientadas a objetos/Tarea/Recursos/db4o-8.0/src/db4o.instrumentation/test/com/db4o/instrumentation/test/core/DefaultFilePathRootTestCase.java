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
package com.db4o.instrumentation.test.core;

import java.io.*;
import java.util.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.file.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

public class DefaultFilePathRootTestCase implements TestLifeCycle {

	private static final File BASEDIR = new File(Path4.getTempPath(), "filePathRoot");
	
	public void test() throws IOException {
		File dirA = mkdir(BASEDIR, "a");
		File dirB = mkdir(BASEDIR, "b");
		File dirC = mkdir(dirB, "c");
		File fileA = createFile(dirA, "a.txt");
		File fileB = createFile(dirB, "b.txt");
		File fileC = createFile(dirC, "c.txt");
		File fileD = createFile(dirC, "d.txt");
		createFile(dirA, "a.txt.bkp");
		String[] rootDirs = new String[]{ dirA.getAbsolutePath(), dirB.getAbsolutePath() };
		FilePathRoot root = new DefaultFilePathRoot(rootDirs, ".txt");
		ArrayAssert.areEqual(rootDirs, root.rootDirs());
		List files = new ArrayList();
		for(Iterator fileIter = root.iterator(); fileIter.hasNext(); ) {
			files.add(fileIter.next());
		}
		Collections.sort(files);
		InstrumentationClassSource[] expected = { 
				new FileInstrumentationClassSource(dirA, fileA), 
				new FileInstrumentationClassSource(dirB, fileB), 
				new FileInstrumentationClassSource(dirB, fileC), 
				new FileInstrumentationClassSource(dirB, fileD), 
		};
		InstrumentationClassSource[] actual = (InstrumentationClassSource[]) files.toArray(new InstrumentationClassSource[files.size()]);
		ArrayAssert.areEqual(expected, actual);
	}

	public void setUp() throws Exception {
		IOUtil.deleteDir(BASEDIR.getAbsolutePath());
		BASEDIR.mkdirs();
	}

	public void tearDown() throws Exception {
		IOUtil.deleteDir(BASEDIR.getAbsolutePath());
	}

	private File mkdir(File base, String name) {
		File dir = new File(base, name);
		dir.mkdir();
		return dir;
	}

	private File createFile(File base, String name) throws IOException {
		File file = new File(base, name);
		file.createNewFile();
		return file;
	}
}
