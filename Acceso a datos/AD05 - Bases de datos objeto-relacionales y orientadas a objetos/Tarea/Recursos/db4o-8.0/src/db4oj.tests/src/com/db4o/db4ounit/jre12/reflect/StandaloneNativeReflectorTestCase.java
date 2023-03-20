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
package com.db4o.db4ounit.jre12.reflect;

import java.util.*;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class StandaloneNativeReflectorTestCase implements TestCase, TestLifeCycle {
	
	private Reflector _reflector;
	
	private static class ItemThrowingConstructors {
		public ItemThrowingConstructors() {
			throw new RuntimeException();
		}
		
		public ItemThrowingConstructors(int value) {
			throw new RuntimeException();
		}
	} 
	
	public static class ItemNoDefaultConstructor {
		public ItemNoDefaultConstructor(int value) {
		}
	}
	
	public static class ItemParent {
		public ItemChild _child;
		
		public ItemParent(ItemChild child) {
			_child = child;
		}
		
	}
	
	public static class ItemChild {
		public String _name;
		
		public ItemChild(String name) {
			_name = name;
		}
	}
	
		
	public static class MockReflectorConfiguration implements ReflectorConfiguration {

		private List _classNames;
		
		private boolean _testConstructor;
		
		public MockReflectorConfiguration(String[] classNames) {
			this(classNames, true);
		}
		
		public MockReflectorConfiguration(String[] classNames, boolean testConstructor) {
			_classNames = Arrays.asList(classNames);
			_testConstructor = testConstructor;
		}
		
		public boolean callConstructor(ReflectClass clazz) {
			return _classNames.contains(clazz.getName());
		}

		public boolean testConstructors() {
			return _testConstructor;
		}
		
	}
	
	public void testComplexItem() throws Exception {
		ReflectClass parentClazz = _reflector.forObject(new ItemParent(null));
		ReflectField[] fields = parentClazz.getDeclaredFields();
		Assert.areEqual(1, fields.length);

		ReflectClass fieldClazz = fields[0].getFieldType();
		ReflectClass childClazz = _reflector.forClass(ItemChild.class);
		Assert.areEqual(childClazz.getName(), fieldClazz.getName());
		
	}
	
	public void testNotStorable() throws Exception {
		assertCannotBeInstantiated(List.class);
		if(!Deploy.csharp){
			assertCannotBeInstantiated(Dictionary.class);
		}
	}
	
	public void testForNullClass() {
		Assert.isNull(_reflector.forClass(null));
	}
	
	public void testPlatformDependentInstantiation() throws Exception {
		ConstructorAwareReflectClass reflectClass = (ConstructorAwareReflectClass)_reflector.forClass(ItemThrowingConstructors.class);
		if(reflectClass.getSerializableConstructor() != null){
			Assert.isTrue(reflectClass.ensureCanBeInstantiated());
			Assert.isNotNull(reflectClass.newInstance());
		}else{
			Assert.isFalse(reflectClass.ensureCanBeInstantiated());
			Assert.isNull(reflectClass.newInstance());
		}
	}
	
	public void testNoDefaultConstructor() throws Exception {
		Assert.isNotNull(createInstanceOf(ItemNoDefaultConstructor.class));		
	}
	
	public void assertCannotBeInstantiated(Class clazz) {
		ReflectClass reflectClass = _reflector.forClass(clazz);
		Assert.isFalse(reflectClass.ensureCanBeInstantiated());
		Assert.isNull(reflectClass.newInstance());
	}
	
	public void testHashTable() throws Exception {
		Hashtable hashTable = (Hashtable)createInstanceOf(Hashtable.class);
		assertIsUsable(hashTable);
	}
	
	public void testHashMap() throws Exception {
		HashMap hashMap = (HashMap)createInstanceOf(HashMap.class);
		assertIsUsable(hashMap);
	}

	public void testList() throws Exception {
		List list = (List)createInstanceOf(ArrayList.class);
		assertIsUsable(list);
	}
	
	public void testFloat() throws Exception {
		Float f = (Float)createInstanceOf(Float.class);
		assertIsUsable(f);
	}
	
	public void testString() throws Exception {
		String s = (String)createInstanceOf(String.class);
		assertIsUsable(s);
	}
	
	private void assertIsUsable(Float f) {
		Assert.areEqual(0.0, f.floatValue());
	}
	
	private void assertIsUsable(String s) {
		Assert.areEqual(0, s.length());
	}

	private void assertIsUsable(Collection collection) {
		if(!Deploy.csharp) {
			Assert.isTrue(collection.isEmpty());
			
			collection.add(new Integer(1));
			Assert.areEqual(1, collection.size());
			
			Assert.isTrue(collection.contains(new Integer(1)));
			
			collection.clear();
		}
		Assert.areEqual(0, collection.size());
		
	}
	
	private void assertIsUsable(Map map) {
		if(!Deploy.csharp) {
			Assert.isTrue(map.isEmpty());
		}
		
		map.put(new Integer(1), "one");
		Assert.areEqual(1, map.size());
		
		Assert.areEqual("one", map.get(new Integer(1)));
		
		map.remove(new Integer(1));
		Assert.areEqual(0, map.size());
	}
	

	private Object createInstanceOf(Class clazz) {
		return _reflector.forClass(clazz).newInstance();
	}
	
	public void setUp() throws Exception {
		_reflector = Platform4.reflectorForType(this.getClass());
		String[] clazzs = new String[]{
				_reflector.forClass(String.class).getName(),
				_reflector.forClass(Hashtable.class).getName(),
				_reflector.forClass(HashMap.class).getName(),
				_reflector.forClass(ArrayList.class).getName(),
		};
		MockReflectorConfiguration config = new MockReflectorConfiguration(clazzs, true);
		_reflector.configuration(config);
	}

	public void tearDown() throws Exception {
	}

}
