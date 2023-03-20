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

import java.lang.reflect.*;
import java.net.*;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.MockActivator;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.internal.Reflection4;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.ta.instrumentation.test.data.*;

import db4ounit.*;

public class TransparentPersistenceClassLoaderTestCase implements TestLifeCycle {

	private static final Class ORIG_CLASS = ToBeInstrumented.class;	
	private static final String CLASS_NAME = ORIG_CLASS.getName();
	private static final Class SUB_CLASS = ToBeInstrumentedSub.class;
	private static final String SUB_CLASS_NAME = SUB_CLASS.getName();
	private static final Class FA_CLASS = ToBeInstrumentedWithFieldAccess.class;
	private static final String FA_CLASS_NAME = FA_CLASS.getName();
	private static final Class NI_CLASS = NotToBeInstrumented.class;
	private static final String NI_CLASS_NAME = NI_CLASS.getName();
	private static final Class CNI_CLASS = CanNotBeInstrumented.class;
	private static final String CNI_CLASS_NAME = CNI_CLASS.getName();
	private static final Class AI_CLASS = AlreadyInstrumentedSuper.class;
	private static final String AI_CLASS_NAME = AI_CLASS.getName();
	private static final Class AI_SUB_CLASS = SubOfAlreadyInstrumented.class;
	private static final String AI_SUB_CLASS_NAME = AI_SUB_CLASS.getName();

	private ClassLoader _loader;

	public void testSelectedClassIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(CLASS_NAME);
		Assert.areEqual(CLASS_NAME, clazz.getName());
		Assert.areNotSame(ORIG_CLASS, clazz);
		assertActivatableInterface(clazz);
		assertActivatorField(clazz);		
		assertBindMethod(clazz);
		assertActivateMethod(clazz);
		assertMethodInstrumentation(clazz, "foo", true);
		assertMethodInstrumentation(clazz, "bar", true);
		assertMethodInstrumentation(clazz, "baz", true);
		assertMethodInstrumentation(clazz, "boo", true);
		assertMethodInstrumentation(clazz, "fooTransient", false);
	}

	public void testSubClassIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(SUB_CLASS_NAME);
		Assert.areEqual(SUB_CLASS_NAME, clazz.getName());
		Assert.areNotSame(SUB_CLASS, clazz);
		assertNoActivatorField(clazz);
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{ ObjectContainer.class });
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{});
		assertMethodInstrumentation(clazz, "fooSub", true);
		assertMethodInstrumentation(clazz, "barSub", true);
		assertMethodInstrumentation(clazz, "bazSub", true);
		assertMethodInstrumentation(clazz, "booSub", true);
	}

	public void testSubOfAlreadyInstrumentedIsInstrumented() throws Exception {
		Class clazz = _loader.loadClass(AI_SUB_CLASS_NAME);
		Assert.areEqual(AI_SUB_CLASS_NAME, clazz.getName());
		Assert.areNotSame(AI_SUB_CLASS, clazz);
		assertNoActivatorField(clazz);
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{ ObjectContainer.class });
		assertNoMethod(clazz, TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{});
		assertMethodInstrumentation(clazz, "toString", true);
	}

	public void testFieldAccessIsInstrumented() throws Exception {
		final Activatable objOne = newToBeInstrumentedInstance();
		final Activatable objTwo = newToBeInstrumentedInstance();
		MockActivator ocOne = MockActivator.activatorFor(objOne);
		MockActivator ocTwo = MockActivator.activatorFor(objTwo);
		Reflection4.invoke(objOne, "compareID", new Class[]{ objTwo.getClass() }, new Object[]{ objTwo });
		Assert.areEqual(1, ocOne.count());
		Assert.areEqual(1, ocTwo.count());
	}
	
	public void  testMixedMethod() throws Exception {
		final Activatable obj = newToBeInstrumentedInstance();
		final MockActivator activator = MockActivator.activatorFor(obj);
		
		Object returnValue = Reflection4.invoke(obj, "setDoubledAndGetInt", Integer.TYPE, new Integer(42));
		assertActivateCalls(activator, 1, 1);
		Assert.areEqual(new Integer(42*2), returnValue);
	}

	public void testFieldSetterIsInstrumented() throws Exception {
		final Activatable obj = newToBeInstrumentedInstance();
		final MockActivator activator = MockActivator.activatorFor(obj);
		
		int expectedWrites = 0;
		Reflection4.invoke(obj, "setInt", Integer.TYPE, new Integer(42));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setChar", Character.TYPE, new Character('a'));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setByte", Byte.TYPE, new Byte((byte)42));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setVolatileByte", Byte.TYPE, new Byte((byte)42));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setLong", Long.TYPE, new Long(42L));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setFloat", Float.TYPE, new Float(42));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setDouble", Double.TYPE, new Double(42));
		assertActivateCalls(activator, 0, ++expectedWrites);
		
		Reflection4.invoke(obj, "setIntArray", int[].class, null);
		assertActivateCalls(activator, 0, ++expectedWrites);
	}

	private void assertActivateCalls(final MockActivator activator,
			int readCount, int writeCount) {
		Assert.areEqual(readCount, activator.readCount());
		Assert.areEqual(writeCount, activator.writeCount());
	}

	public void testInterObjectFieldAccessIsInstrumented() throws Exception {
		Class iClazz = _loader.loadClass(CLASS_NAME);
		Class niClazz = _loader.loadClass(NI_CLASS_NAME);
		final Activatable iObj = (Activatable) iClazz.newInstance();
		final Object niObj = niClazz.newInstance();
		MockActivator act = MockActivator.activatorFor(iObj);
		Reflection4.invoke(niObj, "accessToBeInstrumented", new Class[]{ iClazz }, new Object[]{ iObj });
		Assert.areEqual(1, act.count());
	}

	public void testBindCallOnInstrumentedObject() throws Exception {
		Class activatableClazz = _loader.loadClass(CLASS_NAME);
		final Activatable activatable = (Activatable) activatableClazz.newInstance();
		MockActivator activator = new MockActivator();
		activatable.bind(activator);
		activatable.bind(activator);
		activatable.bind(null);
		activatable.bind(null);
		activatable.bind(new MockActivator());
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				activatable.bind(new MockActivator());
			}
		});
	}

	private Activatable newToBeInstrumentedInstance()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = _loader.loadClass(FA_CLASS_NAME);
		return (Activatable) clazz.newInstance();
	}
	
	private void assertActivatableInterface(Class clazz) {
		Assert.isTrue(Activatable.class.isAssignableFrom(clazz));
	}

	private void assertActivatorField(Class clazz) throws NoSuchFieldException {
		Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		Assert.areEqual(Activator.class, activatorField.getType());
		assertFieldModifier(activatorField, Modifier.PRIVATE);
		assertFieldModifier(activatorField, Modifier.TRANSIENT);
	}

	private void assertNoActivatorField(final Class clazz) {
		Assert.expect(NoSuchFieldException.class, new CodeBlock() {
			public void run() throws Throwable {
				clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
			}
		});
	}

	private void assertBindMethod(Class clazz) throws Exception {
		final Field activatorField = clazz.getDeclaredField(TransparentActivationInstrumentationConstants.ACTIVATOR_FIELD_NAME);
		final Method bindMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Class[]{Activator.class});
		Assert.isTrue((bindMethod.getModifiers() & Modifier.PUBLIC) > 0);
		
		activatorField.setAccessible(true);
		final Object obj = clazz.newInstance();
		Assert.isNull(activatorField.get(obj));
		
		MockActivator oc = new MockActivator(); 
		Assert.areEqual(0, oc.count());
		
		bindMethod.invoke(obj, new Object[]{ oc });
		Object activator = activatorField.get(obj);
		Assert.isNotNull(activator);

		// same activator, ok
		bindMethod.invoke(obj, new Object[]{ oc });

		// null activator, ok
		bindMethod.invoke(obj, new Object[]{ null });

		// reset activator after null, ok
		bindMethod.invoke(obj, new Object[]{ oc });

		MockActivator otherOc = new MockActivator();
		try {
			bindMethod.invoke(obj, new Object[]{ otherOc });
			Assert.fail();
		}
		catch(InvocationTargetException exc) {
			Assert.isInstanceOf(IllegalStateException.class, exc.getTargetException());
		}
		Assert.areEqual(0, oc.count());
		Assert.areEqual(0, otherOc.count());
	}

	private void assertNoMethod(final Class clazz,final String methodName,final Class[] paramTypes) throws Exception {
		Assert.expect(NoSuchMethodException.class, new CodeBlock() {
			public void run() throws Throwable {
				clazz.getDeclaredMethod(methodName, paramTypes);
			}
		});
	}
	
	private void assertActivateMethod(Class clazz) throws Exception {
		final Method activateMethod = clazz.getDeclaredMethod(TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Class[]{ActivationPurpose.class});
		activateMethod.setAccessible(true);
		Assert.isTrue((activateMethod.getModifiers() & Modifier.PUBLIC) > 0);
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator activator = MockActivator.activatorFor(obj);
		activateMethod.invoke(obj, new Object[]{ActivationPurpose.READ});
		activateMethod.invoke(obj, new Object[]{ActivationPurpose.READ});
		Assert.areEqual(2, activator.count());
	}

	private void assertMethodInstrumentation(Class clazz,String methodName,boolean expectInstrumentation) throws Exception {
		final Activatable obj = (Activatable) clazz.newInstance();
		MockActivator oc = MockActivator.activatorFor(obj);
		Reflection4.invoke(obj, methodName, new Class[]{}, new Object[]{});
		if (expectInstrumentation) {
			Assert.areEqual(1, oc.count());
		} else {
			Assert.areEqual(0, oc.count());
		}
	}

	private void assertFieldModifier(Field activatorField, int modifier) {
		Assert.isTrue((activatorField.getModifiers()&modifier)>0);
	}
	
	public void testOtherClassIsNotInstrumented() throws Exception {
		Class clazz = _loader.loadClass(NI_CLASS_NAME);
		Assert.areEqual(NI_CLASS_NAME, clazz.getName());
		Assert.areNotSame(NI_CLASS, clazz);
		Assert.isFalse(Activatable.class.isAssignableFrom(clazz));
	}

	public void testCanNotBeInstrumented() throws Exception {
		Class clazz = _loader.loadClass(CNI_CLASS_NAME);
		Assert.isFalse(Activatable.class.isAssignableFrom(clazz));
	}
	
	public void setUp() throws Exception {
		ClassLoader baseLoader = ORIG_CLASS.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[]{ CLASS_NAME, SUB_CLASS_NAME, FA_CLASS_NAME, CNI_CLASS_NAME, AI_CLASS_NAME, AI_SUB_CLASS_NAME });
		_loader = new BloatInstrumentingClassLoader(urls, baseLoader, new AcceptAllClassesFilter(), new InjectTransparentActivationEdit(filter));
	}

	public void tearDown() throws Exception {
	}
	
	public static void main(String[] args) {
		new ConsoleTestRunner(TransparentPersistenceClassLoaderTestCase.class).run();
	}
}
