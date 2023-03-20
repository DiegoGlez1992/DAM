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
package com.db4o.enhance.test;

import java.io.*;

import org.apache.tools.ant.*;

import db4ounit.*;

public class Db4oEnhancerAntTaskTestCaseLauncher implements TestCase {

	private static final String BUILD_XML = "testscript/Db4oEnhancerAntTaskBuild.xml";

	private static String userClassPath = null;
	private static String sourceClassPath = null;
	private static String sourcePath = null;
	private static boolean forceProperties = false;
	
	public static void main(String[] args) {
		if(args != null && args.length > 2) {
			userClassPath = args[0];
			sourceClassPath = args[1];
			sourcePath = args[2];
			forceProperties = true;
		}
		System.exit(new ConsoleTestRunner(Db4oEnhancerAntTaskTestCaseLauncher.class).run());
	}
	
	public void test() throws Exception {
		File buildFile = new File(BUILD_XML);
		Project p = new Project();

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_WARN);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			p.setUserProperty("ant.file", buildFile.getAbsolutePath());
			if(forceProperties) {
				p.setNewProperty("user.classpath", userClassPath);
				p.setNewProperty("source.classpath", sourceClassPath);
				p.setNewProperty("source.path", sourcePath);
			}
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
		}
		catch(Exception exc) {
			p.fireBuildFinished(exc);
			throw exc;
		}
	}
	
}
