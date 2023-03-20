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
package com.db4o.db4ounit.common.migration;

import java.io.*;

import com.db4o.db4ounit.util.*;
import com.db4o.foundation.io.*;

import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Db4oLibrarian {
	
	private final Db4oLibraryEnvironmentProvider _provider;

	public Db4oLibrarian(Db4oLibraryEnvironmentProvider provider) {
		_provider = provider;
	}

	public Db4oLibrary[] libraries() throws Exception {
		final File[] libFiles = libFiles(archivesPath());
		Db4oLibrary[] libraries = new Db4oLibrary[libFiles.length];
		for (int i = 0; i < libFiles.length; i++) {
			libraries[i] = forFile(libFiles[i].getCanonicalPath());
		}
		return libraries;
	}
	
	public Db4oLibrary forVersion(String version) throws IOException {
		return forFile(fileForVersion(version));
	}

	private String fileForVersion(String version) {
		return Path4.combine(archivesPath(), "db4o-" + version + "-java1.2.jar");
	}

	public Db4oLibrary forFile(final String fname) throws IOException {
		if (!File4.exists(fname)) {
			throw new FileNotFoundException(fname);
		}
		return new Db4oLibrary(fname, environmentFor(fname));
	}

	private Db4oLibraryEnvironment environmentFor(String fname)
			throws IOException {
		return _provider.environmentFor(fname);
	}
	
	private File[] libFiles(String libDir) {
        return new File(libDir).listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
    }
	
	private String archivesPath() {
		return IOServices.safeCanonicalPath(
					System.getProperty(
						"db4o.archives.path",
						WorkspaceServices.workspacePath("db4o.archives/java1.2")));
	}
}
