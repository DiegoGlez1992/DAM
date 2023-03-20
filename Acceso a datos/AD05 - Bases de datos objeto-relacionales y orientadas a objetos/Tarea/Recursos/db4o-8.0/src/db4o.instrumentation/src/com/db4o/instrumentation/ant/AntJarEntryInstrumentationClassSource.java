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
package com.db4o.instrumentation.ant;

import java.io.*;

import org.apache.tools.ant.types.*;

import com.db4o.instrumentation.file.*;
import com.db4o.instrumentation.util.*;

public class AntJarEntryInstrumentationClassSource implements InstrumentationClassSource {

	private final Resource _resource;
	
	public AntJarEntryInstrumentationClassSource(Resource resource) {
		_resource = resource;
	}

	public String className() throws IOException {
		return BloatUtil.classNameForPath(_resource.getName());
	}

	public InputStream inputStream() throws IOException {
		return _resource.getInputStream();
	}

	public File targetPath(File targetBase) throws IOException {
		return new File(targetBase, _resource.getName());
	}

    public File sourceFile() {
        return null;
    }

}
