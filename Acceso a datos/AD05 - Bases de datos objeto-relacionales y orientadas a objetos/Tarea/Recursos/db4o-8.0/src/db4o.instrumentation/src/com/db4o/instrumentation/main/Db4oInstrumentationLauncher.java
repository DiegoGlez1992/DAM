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
package com.db4o.instrumentation.main;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.instrumentation.core.*;

/**
 * Launch an application through an instrumenting classloader.
 */
public class Db4oInstrumentationLauncher {

	public static void launch(BloatClassEdit[] edits, URL[] classPath, String mainClazzName, String[] args) throws Exception {
		ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();
		BloatClassEdit compositeEdit = new CompositeBloatClassEdit(edits);
		ClassLoader loader=new BloatInstrumentingClassLoader(classPath, parentLoader, compositeEdit);
		Thread.currentThread().setContextClassLoader(loader);
		Class mainClass=loader.loadClass(mainClazzName);
		Method mainMethod=mainClass.getMethod("main",new Class[]{String[].class});
		mainMethod.invoke(null,new Object[]{args});
	}

}
