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
package com.db4o.test.nativequery;

import java.io.*;
import java.net.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.ant.*;
import com.db4o.instrumentation.main.*;
import com.db4o.nativequery.instrumentation.*;
import com.db4o.nativequery.main.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
public class NQBuildTimeInstrumentationTestCase implements TestLifeCycle {

	private final static String BASE_DIR = Path4.combine(Path4.getTempPath(), "nqfileinstr");
	private final static String SRC_DIR = Path4.combine(BASE_DIR, "source");
	private final static String TARGET_DIR = Path4.combine(BASE_DIR, "target");
	private final static Class[] CLAZZES = { ToBeInstrumented.class, NotToBeInstrumented.class };


	public void testFileEnhancer() throws Exception {		
		Db4oFileInstrumentor enhancer = new Db4oFileInstrumentor(new TranslateNQToSODAEdit());
		enhancer.enhance(new File(SRC_DIR), new File(TARGET_DIR), new String[]{});		
		assertInstrumented();
	}

	public void testAntTask() throws Exception {		
		Db4oFileEnhancerAntTask antTask = new Db4oFileEnhancerAntTask();
		Project project = new Project();
		project.setBaseDir(new File(BASE_DIR));
		FileSet fileSet = new FileSet();
		fileSet.setProject(project);
		fileSet.setDir(new File(SRC_DIR));
		antTask.addSources(fileSet);
		antTask.setClassTargetDir(new File(TARGET_DIR));
		antTask.add(new NQAntClassEditFactory());
		antTask.execute();
		assertInstrumented();
	}

	private void assertInstrumented() throws MalformedURLException,
			ClassNotFoundException, NoSuchMethodException {
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), CLAZZES);
		URLClassLoader loader = new URLClassLoader(new URL[] { new File(TARGET_DIR).toURL() }, excludingLoader);
		
		Class instrumented = loader.loadClass(ToBeInstrumented.class.getName());
		final Class[] queryClassSig = new Class[]{Query.class};
		Assert.isNotNull(instrumented.getDeclaredMethod(SODAMethodBuilder.OPTIMIZE_QUERY_METHOD_NAME, queryClassSig));
		final Class uninstrumented = loader.loadClass(NotToBeInstrumented.class.getName());
		Assert.expect(NoSuchMethodException.class, new CodeBlock() {
			public void run() throws Throwable {
				uninstrumented.getDeclaredMethod(SODAMethodBuilder.OPTIMIZE_QUERY_METHOD_NAME, queryClassSig);
			}
		});
	}

	private void copyClassFile(String srcDir, Class clazz) throws IOException {
		File file = fileForClass(clazz);
		String targetPath = Path4.combine(srcDir, clazz.getName().replace('.', '/') + ".class");
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}

	private File fileForClass(Class clazz) throws IOException {
		String clazzName = clazz.getName();
		int dotIdx = clazzName.lastIndexOf('.');
		String simpleName = clazzName.substring(dotIdx + 1);
		URL url = clazz.getResource(simpleName + ".class");
		return new File(decode(url));
	}

	@SuppressWarnings("deprecation")
    private String decode(URL url) {
	    return URLDecoder.decode(url.getPath());
    }

	public void setUp() throws Exception {
		IOUtil.deleteDir(SRC_DIR);
		IOUtil.deleteDir(TARGET_DIR);
		File4.mkdirs(SRC_DIR);
		File4.mkdirs(TARGET_DIR);		
		for (int clazzIdx = 0; clazzIdx < CLAZZES.length; clazzIdx++) {
			copyClassFile(SRC_DIR, CLAZZES[clazzIdx]);
		}
	}

	public void tearDown() throws Exception {
	}
}
