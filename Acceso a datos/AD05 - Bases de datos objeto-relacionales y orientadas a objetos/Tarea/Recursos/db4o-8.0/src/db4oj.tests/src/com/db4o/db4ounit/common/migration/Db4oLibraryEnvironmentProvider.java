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
import java.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Db4oLibraryEnvironmentProvider {
	
	private final Map<String, Db4oLibraryEnvironment> _environments = new HashMap();
	private final File _classPath;
	
	public Db4oLibraryEnvironmentProvider(File classPath) {
		_classPath = classPath;
	}
	
	public Db4oLibraryEnvironment environmentFor(final String path)
			throws IOException {
		Db4oLibraryEnvironment existing = existingEnvironment(path);
		if (existing != null) return existing;
		return newEnvironment(path);
	}

	private Db4oLibraryEnvironment existingEnvironment(String path) {
		return _environments.get(path);
	}

	private Db4oLibraryEnvironment newEnvironment(String path)
			throws IOException {
		Db4oLibraryEnvironment env = new Db4oLibraryEnvironment(new File(path), _classPath);
		_environments.put(path, env);
		return env;
	}

	public void disposeAll() {
		for (Db4oLibraryEnvironment e : _environments.values()) {
			e.dispose();
		}
		_environments.clear();
    }

}