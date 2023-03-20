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
package com.db4o.test.aliases;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;
import com.db4o.test.*;

import db4ounit.extensions.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ClassAliasesTestCase {

	public void testTypeAlias() {
		
		cleanUp();
		
		ObjectContainer container = Test.objectContainer();

		container.store(new Person1("Homer Simpson"));
		container.store(new Person1("John Cleese"));
		
		container = Test.reOpen();
		container.ext().configure().addAlias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias("com.db4o.test.aliases.Person1",
						"com.db4o.test.aliases.Person2"));
		
		assertData(container);
	}
	
	public void testAliasedTypeIsStoredCorrectly() {
		
		cleanUp();
		
		ObjectContainer container = Test.objectContainer();
		container.ext().configure().addAlias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias("com.db4o.test.aliases.Person1",
						"com.db4o.test.aliases.Person2"));
		
		container.store(new Person2("Homer Simpson"));
		container.store(new Person2("John Cleese"));
		
		container = Test.reOpen();
		assertData(container);
		
	}

	private void cleanUp() {
		Test.reOpen();
		Test.deleteAllInstances(Person1.class);
	}
	
	public void testAccessingDotnetFromJava() throws Exception {
		generateDotnetData();
		ObjectContainer container = openDotnetDataFile();
		container.ext().configure().addAlias(
				new WildcardAlias(
						"com.db4o.test.aliases.*, MyAssembly",
						"com.db4o.test.aliases.*"));
//				new TypeAlias(
//						Person2.class.getName() + ", MyAssembly",
//						Person2.class.getName()));
		try {
			assertData(container);
		} finally {
			container.close();
		}
	}
	
	private void assertData(ObjectContainer container) {
		ObjectSet os = container.query(Person2.class);
		
		Test.ensureEquals(2, os.size());
		ensureContains(os, new Person2("Homer Simpson"));
		ensureContains(os, new Person2("John Cleese"));
	}	

	private ObjectContainer openDotnetDataFile() {
		return Db4o.openFile(getDotnetDataFilePath());
	}

	private String getDotnetDataFilePath() {
		return buildTempPath("dotnet.db4o");
	}

	private String buildTempPath(String fname) {
		return IOServices.buildTempPath(fname);
	}	

	private void generateDotnetData() throws IOException {
		new File(getDotnetDataFilePath()).delete();
		executeAssembly(generateAssembly(), getDotnetDataFilePath());
	}

	private String executeAssembly(String assembly, String args) throws IOException {
		String cmdLine = isLinux()
			? "mono " + assembly + " " + args
			: assembly + " " + args;
		return exec(cmdLine);
	}

	private String generateAssembly() throws IOException {
		String code = "namespace com.db4o.test.aliases {" +
			"class Person2 { string _name; public Person2(string name) { _name = name; }}" +
			"class Program {" +
				"static void Main(string[] args) {" +
					"string fname = args[0];" +
					"using (ObjectContainer container = Db4o.OpenFile(fname)) {" +
						"container.Set(new Person2(\"Homer Simpson\"));" +
						"container.Set(new Person2(\"John Cleese\"));" +
					"}" +
					"System.Console.WriteLine(\"success\");" + 
				"}" +
			"}" +
		"}";
		
		String srcFile = buildTempPath("MyAssembly.cs");
		writeFile(srcFile, code);
		String exePath = buildTempPath("MyAssembly.exe");

		File4.copy(db4odllPath(), buildTempPath("db4o.dll"));
		String cmdLine = csharpCompiler() + " /target:exe /r:" + db4odllPath() + " /out:" + exePath + " " + srcFile;
		exec(cmdLine);
		return exePath;
	}

	private String csharpCompiler() {
		return isLinux() ? "mcs" : "csc";
	}

	private boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().indexOf("linux") != -1;
	}

	private String exec(String cmdLine) throws IOException {
		Process p = Runtime.getRuntime().exec(cmdLine);
		return readStdOut(p);
	}

	private String db4odllPath() throws IOException {
		String path = isLinux()
			? "../db4obuild/dist/mono/dll/db4o.dll"
			: "../db4obuild/dist/dll/net/db4o.dll";
		return new File(path).getCanonicalPath();
	}

	private void writeFile(String fname, String contents) throws IOException {
		FileWriter writer = new FileWriter(fname);
		try {
			writer.write(contents);
		} finally {
			writer.close();
		}
	}

	private String readStdOut(Process p) throws IOException {
		
		String lineSeparator = System.getProperty("line.separator");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringWriter writer = new StringWriter();
		String line = null;
		while (null != (line = reader.readLine())) {
			writer.write(line);
			writer.write(lineSeparator);
		}
		return writer.toString();
	}

	private void ensureContains(ObjectSet actual, Object expected) {
		actual.reset();
		while (actual.hasNext()) {
			Object next = actual.next();
			if (next.equals(expected))
				return;
		}
		Test.ensure(false);
	}
	
	public static void main(String[] args) {
		Test.runSolo(ClassAliasesTestCase.class);
	}

}
