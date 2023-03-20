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
package com.db4o.osgi.test;

import java.io.*;

import org.osgi.framework.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.osgi.*;

import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

class Db4oOSGiBundleFixture extends AbstractSoloDb4oFixture {

	private final BundleContext _context;
	private final String _fileName;
	private Configuration _config;
	
	public Db4oOSGiBundleFixture(BundleContext context, String fileName) {
		_context = context;
		_fileName = CrossPlatformServices.databasePath(fileName);
	}
	
	protected Configuration newConfiguration() {
		return service(_context).newConfiguration();
	}

	protected ObjectContainer createDatabase(Configuration config) {
		_config = config;
	    return service(_context).openFile(_config,_fileName);
	}

	private static Db4oService service(BundleContext context) {
		ServiceReference sRef = context.getServiceReference(Db4oService.class.getName());
	    Db4oService dbs = (Db4oService)context.getService(sRef);
		return dbs;
	}

	protected void doClean() {
		_config = null;
		new File(_fileName).delete();
	}

	public void defragment() throws Exception {
		defragment(_fileName);
	}

	public String label() {
		return "OSGi/bundle";
	}

	private final static Class[] OPT_OUT = { OptOutNoFileSystemData.class, OptOutCustomContainerInstantiation.class, OptOutNoInheritedClassPath.class };

	public boolean accept(Class clazz) {
		if(!super.accept(clazz)) {
			return false;
		}
		for (int optOutIdx = 0; optOutIdx < OPT_OUT.length; optOutIdx++) {
			if(OPT_OUT[optOutIdx].isAssignableFrom(clazz)) {
				return false;
			}
		}
		return true;
	}
}
