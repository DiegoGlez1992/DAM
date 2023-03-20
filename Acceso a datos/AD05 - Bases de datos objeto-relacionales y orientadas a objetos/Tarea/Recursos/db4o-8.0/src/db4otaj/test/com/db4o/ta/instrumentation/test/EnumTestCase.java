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

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;

@decaf.Remove
public class EnumTestCase implements TestLifeCycle{
	
	private BloatInstrumentingClassLoader _loader;
	
	public static enum MyEnum {
		
		FOO("foo");
		
		public String name;

		MyEnum(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static class MyEnumClient {
		
		public static String nameOf(MyEnum e) {
			return e.name;
		}
		
	}
	
	public void setUp() throws Exception {
		ClassLoader baseLoader = MyEnum.class.getClassLoader();
		ClassFilter filter = new ByNameClassFilter(new String[]{ enumClassName(), enumClientClassName(), });
		_loader = new BloatInstrumentingClassLoader(new URL[] {}, baseLoader, new AcceptAllClassesFilter(), new InjectTransparentActivationEdit(filter));
	}

	private String enumClientClassName() {
	    return MyEnumClient.class.getName();
    }

	private String enumClassName() {
		return MyEnum.class.getName();
	}
	
	public void testEnumIsNotActivatable() throws Exception {
		Class<?> enumClass = _loader.loadClass(enumClassName());
		Assert.isFalse(Activatable.class.isAssignableFrom(enumClass));
		Assert.areEqual("foo", fooEnumFrom(enumClass).toString());
	}
	
	public void testEnumFieldAccessIsNotEnhanced() throws Exception {
		final Class<?> enumClientClass = _loader.loadClass(enumClientClassName());
		final Class<?> enumClass = _loader.loadClass(enumClassName());
		final Method nameOfMethod = enumClientClass.getMethod("nameOf", enumClass);
		Assert.areEqual("foo", nameOfMethod.invoke(null, fooEnumFrom(enumClass)));
	}

	private Object fooEnumFrom(Class<?> enumClass) throws IllegalAccessException, NoSuchFieldException {
	    return enumClass.getField("FOO").get(null);
    }

	public void tearDown() throws Exception {
		
	}


}
