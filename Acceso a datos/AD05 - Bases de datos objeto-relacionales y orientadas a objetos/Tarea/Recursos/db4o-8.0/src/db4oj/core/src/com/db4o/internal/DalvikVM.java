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
package com.db4o.internal;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;

/**
 * @sharpen.ignore
 */
@decaf.Remove
class DalvikVM extends JDK_5 {
	
	@decaf.Remove
	public final static class Factory implements JDKFactory {
		public JDK tryToCreate() {
			if (!"Dalvik".equals(System.getProperty("java.vm.name"))) {
				return null;
			}
			return new DalvikVM();
		}
	};
	
	@Override
	public ReflectConstructor serializableConstructor(Reflector reflector, final Class clazz) {
		
		return new ReflectConstructor() {
			
			private final ObjectFactory factory = factory().newFactory(clazz);
			
			public Object newInstance(Object[] parameters) {
				return factory.newInstance(clazz);
			}
			
			public ReflectClass[] getParameterTypes() {
				return new ReflectClass[0];
			}
		};
	}
	
	public static class SkipConstructorCheck {
		public SkipConstructorCheck() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
	}
	
	private TernaryBool supportSkipConstructorCall = TernaryBool.UNSPECIFIED;
	
	private ObjectFactoryFactory _factory;
	
	@Override
	boolean supportSkipConstructorCall() {
		factory();
		return supportSkipConstructorCall.definiteYes();
	}
	
	private ObjectFactoryFactory factory(){
		if(supportSkipConstructorCall.definiteNo()){
			return null;
		}
		if(_factory != null){
			return _factory;
		}
		try {
			_factory = new Dalvik2ObjectFactoryFactory();
			_factory.newFactory(SkipConstructorCheck.class).newInstance(SkipConstructorCheck.class);
			supportSkipConstructorCall = TernaryBool.YES;
			return _factory;
		} catch (UnsupportedOperationException e){
			// didn't work, let's try Dalvik 3
		}
		try {
			_factory = new Dalvik3ObjectFactoryFactory();
			_factory.newFactory(SkipConstructorCheck.class).newInstance(SkipConstructorCheck.class);
			supportSkipConstructorCall = TernaryBool.YES;
			return _factory;
		} catch (UnsupportedOperationException e){
			e.printStackTrace();
			// didn't work, maybe log that we need to find a new way?
		} 
		supportSkipConstructorCall = TernaryBool.NO;
		return null;
	}
	
	@Override
	public String generateSignature() {
		return DalvikSignatureGenerator.generateSignature();
	}
	
	private static interface ObjectFactory {
		public Object newInstance(Class clazz);
	}
	
	private static interface ObjectFactoryFactory {
		public ObjectFactory newFactory(Class clazz);
	}
	
	private static class Dalvik2ObjectFactoryFactory implements ObjectFactoryFactory{

		private final Dalvik2ObjectFactory factory = new Dalvik2ObjectFactory();
		
		public ObjectFactory newFactory(Class clazz) {
			return factory;
		}
		
	}
	
	private static class Dalvik3ObjectFactoryFactory implements ObjectFactoryFactory{
		
		private int _methodId;

		public Dalvik3ObjectFactoryFactory() {
			try {
				Method constructorIdMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
				constructorIdMethod.setAccessible(true);
				_methodId = (Integer) constructorIdMethod.invoke(null, Object.class);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			} 

		}
		
		public ObjectFactory newFactory(Class clazz) {
			return new Dalvik3ObjectFactory(clazz, _methodId);
		}
	}
	
	private static class Dalvik2ObjectFactory implements ObjectFactory {
		
		private Method _method;
		
		public Dalvik2ObjectFactory(){
			try {
				_method = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
				_method.setAccessible(true);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			} 
		}

		public Object newInstance(Class clazz) {
			try {
				return _method.invoke(null, clazz, Object.class);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			} 
		}
		
	}
	
	private static class Dalvik3ObjectFactory implements ObjectFactory {
		
		private Method _method;
		
		private final Class _clazz;
		
		private int _methodId;
		
		public Dalvik3ObjectFactory(Class clazz, int methodId) {
			_clazz = clazz;
			_methodId = methodId;
			try {
				_method = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
				_method.setAccessible(true);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			} 
		}

		public Object newInstance(Class clazz) {
			if(clazz != _clazz){
				throw new IllegalArgumentException();
			}
			try {
				return _method.invoke(null, _clazz, _methodId);
			} catch (Exception e) {
				throw new UnsupportedOperationException(e);
			} 
		}
		
	}

}
