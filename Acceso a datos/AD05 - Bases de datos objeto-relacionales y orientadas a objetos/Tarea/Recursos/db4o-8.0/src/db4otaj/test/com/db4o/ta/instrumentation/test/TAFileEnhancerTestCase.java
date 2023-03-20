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
package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;
import com.db4o.instrumentation.main.*;
import com.db4o.internal.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.test.collections.*;
import com.db4o.ta.instrumentation.test.data.*;

import db4ounit.*;

public class TAFileEnhancerTestCase extends TAFileEnhancerTestCaseBase {
    
	private final static Class<?> INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	private final static Class<?> NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;
	private final static Class<?> EXTERNAL_INSTRUMENTED_CLAZZ = ToBeInstrumentedWithExternalFieldAccess.class;
	private final static Class<?> INSTRUMENTED_OUTER_CLAZZ = ToBeInstrumentedOuter.class;
	private final static Class<?> INSTRUMENTED_INNER_CLAZZ = getAnonymousInnerClass(INSTRUMENTED_OUTER_CLAZZ);
	private final static Class<?> LIST_CLIENT_CLAZZ = ArrayListClient.class;
	private final static Class<?> MAP_CLIENT_CLAZZ = HashMapClient.class;
	private final static Class<?> CUSTOM_LIST_CLAZZ = CustomArrayList.class;

	final static Class<?>[] INSTRUMENTED_CLASSES = new Class[] { 
		INSTRUMENTED_CLAZZ, 
		EXTERNAL_INSTRUMENTED_CLAZZ, 
		INSTRUMENTED_OUTER_CLAZZ, 
		INSTRUMENTED_INNER_CLAZZ, 
		LIST_CLIENT_CLAZZ,
		MAP_CLIENT_CLAZZ,
		CUSTOM_LIST_CLAZZ,
		MyArrayList.class,
	};

	private final static Class<?>[] NOT_INSTRUMENTED_CLASSES = new Class[] { 
		NOT_INSTRUMENTED_CLAZZ, 
	};

	private final static Class<?>[] INPUT_CLASSES = (Class[])Arrays4.merge(INSTRUMENTED_CLASSES, NOT_INSTRUMENTED_CLASSES, Class.class);
	
	private static Class<?> getAnonymousInnerClass(Class<?> clazz) {
		try {
			return Class.forName(clazz.getName() + "$1");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected Class<?>[] inputClasses() {
		return INPUT_CLASSES;
	}

	protected Class<?>[] instrumentedClasses() {
		return INSTRUMENTED_CLASSES;
	}

	public void test() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		for (int instrumentedIdx = 0; instrumentedIdx < INSTRUMENTED_CLASSES.length; instrumentedIdx++) {
			loader.assertAssignableFrom(Activatable.class, INSTRUMENTED_CLASSES[instrumentedIdx]);
		}
		for (int notInstrumentedIdx = 0; notInstrumentedIdx < NOT_INSTRUMENTED_CLASSES.length; notInstrumentedIdx++) {
			loader.assertNotAssignableFrom(Activatable.class, NOT_INSTRUMENTED_CLASSES[notInstrumentedIdx]);
		}
		instantiateInnerClass(loader);
	}
	
	public void testMethodInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable instrumented = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(instrumented);
		Reflection4.invoke(instrumented, "setInt", Integer.TYPE, new Integer(42));
		assertReadsWrites(0, 1, activator);
	}
	
	public void testExternalFieldAccessInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable server = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		Object client = loader.newInstance(EXTERNAL_INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(server);
		Reflection4.invoke(client, "accessExternalField", server.getClass(), server);
		assertReadsWrites(0, 1, activator);
	}
	
	
	public void testExceptionsAreBubbledUp() throws Exception {
		
		final RuntimeException exception = new RuntimeException();
		
		final Throwable thrown = Assert.expect(RuntimeException.class, new CodeBlock() {
			public void run() throws Exception {
				new Db4oFileInstrumentor(new BloatClassEdit() {
					public InstrumentationStatus enhance(
							ClassEditor ce,
							ClassLoader origLoader,
							BloatLoaderContext loaderContext) {
						
						throw exception;
						
					}
				}).enhance(srcDir, targetDir, new String[] {});
			}
		});
		
		Assert.areSame(exception, thrown);
			
	}

	@SuppressWarnings("unchecked")
	public void testArrayListActivationWithException() throws Exception {
		enhance();
		AssertingClassLoader loader = newAssertingClassLoader( new Class[] { CollectionClient.class });		
		CollectionClient client = (CollectionClient) loader.newInstance(LIST_CLIENT_CLAZZ);
		MockActivator clientActivator = MockActivator.activatorFor((Activatable)client);
		final List<Object> list = (List)client.collectionInstance();
		assertReadsWrites(1, 0, clientActivator);
		MockActivator listActivator = MockActivator.activatorFor((Activatable)list);
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(0);
			}
		});
		assertReadsWrites(1, 0, listActivator);
	}

	// TODO should go into generic instrumentation test rather than TA
	public void testListener() throws Exception {		
		
		class MockListener implements Db4oInstrumentationListener {
			
			private int _count = 0;
			private boolean _gotStart = false;
			private boolean _gotEnd = false;
			
			public void notifyProcessed(InstrumentationClassSource source, InstrumentationStatus status) {
				if(status == InstrumentationStatus.FAILED) {
					Assert.fail();
				}
				Class<?>[] srcArr = ((status == InstrumentationStatus.INSTRUMENTED) ? INSTRUMENTED_CLASSES : NOT_INSTRUMENTED_CLASSES);
				boolean found = false;
				try {
					for (int srcIdx = 0; srcIdx < srcArr.length; srcIdx++) {
						if(srcArr[srcIdx].getName().equals(source.className())) {
							found = true;
							break;
						}
					}
				} 
				catch (IOException exc) {
					Assert.fail("", exc);
				}
				Assert.isTrue(found);
				_count++;
			}
			
			public void validate() {
				assertFinalCount();
				assertStartEnd(true, true);
			}

			public void notifyStartProcessing(FilePathRoot root) {
				Assert.areEqual(0, _count);
				assertStartEnd(false, false);
				_gotStart = true;
			}

			public void notifyEndProcessing(FilePathRoot root) {
				assertFinalCount();
				assertStartEnd(true, false);
				_gotEnd = true;
			}
			
			private void assertFinalCount() {
				Assert.areEqual(INSTRUMENTED_CLASSES.length + NOT_INSTRUMENTED_CLASSES.length, _count);
			}
			
			private void assertStartEnd(boolean gotStart, boolean gotEnd) {
				Assert.areEqual(gotStart, _gotStart);
				Assert.areEqual(gotEnd, _gotEnd);
			}
		}
		
		MockListener listener = new MockListener();
		enhance(listener);
		listener.validate();
	}

	private void instantiateInnerClass(AssertingClassLoader loader) throws Exception {
		Class<?> outerClazz = loader.loadClass(INSTRUMENTED_OUTER_CLAZZ);
		Object outerInst = outerClazz.newInstance();
		outerClazz.getDeclaredMethod("foo", new Class[]{}).invoke(outerInst, new Object[]{});
	}
	
}
