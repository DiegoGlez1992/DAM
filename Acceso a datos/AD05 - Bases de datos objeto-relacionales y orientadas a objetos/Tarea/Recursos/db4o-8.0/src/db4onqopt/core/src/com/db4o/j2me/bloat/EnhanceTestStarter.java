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
package com.db4o.j2me.bloat;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

public class EnhanceTestStarter {
	public static void main(String[] args) throws Exception {
		Generation.main(new String[0]);
		String[] classpath={
				"generated",
				"bin",
				"../db4oj/bin",
				"../db4ojdk1.2/bin",
		};
		URL[] urls=new URL[classpath.length];
		for (int pathIdx = 0; pathIdx < classpath.length; pathIdx++) {
			urls[pathIdx]=new File(classpath[pathIdx]).toURI().toURL();
		}
		// a risky move, but usually this should be the ext classloader
		ClassLoader extCL = ClassLoader.getSystemClassLoader().getParent();
		URLClassLoader urlCL=new URLClassLoader(urls,extCL);
		Class mainClazz=urlCL.loadClass(EnhanceTestMain.class.getName());
		Method mainMethod=mainClazz.getMethod("main",new Class[]{String[].class});
		mainMethod.invoke(null, new Object[]{new String[0]});
	}
}
