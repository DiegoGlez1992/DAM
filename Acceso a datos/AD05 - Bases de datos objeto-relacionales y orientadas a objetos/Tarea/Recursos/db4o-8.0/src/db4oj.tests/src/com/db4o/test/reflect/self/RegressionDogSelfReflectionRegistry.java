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
package com.db4o.test.reflect.self;

import java.util.*;

import com.db4o.reflect.self.*;

/* GENERATE */
public class RegressionDogSelfReflectionRegistry extends SelfReflectionRegistry {
	private final static Hashtable CLASSINFO;

	static {
		CLASSINFO = new Hashtable(2);
		CLASSINFO.put(Animal.class, new ClassInfo(true, Object.class,
				new FieldInfo[] { new FieldInfo("_name", String.class, true,
						false, false) }));
		CLASSINFO.put(Dog.class,
				new ClassInfo(false, Animal.class,
						new FieldInfo[] {
								new FieldInfo("_age", Integer.class, true,
										false, false),
								new FieldInfo("_parents", Dog[].class, true,
										false, false), 
								new FieldInfo("_prices", int[].class, true,
										false, false),
				}));
	}

	public ClassInfo infoFor(Class clazz) {
		return (ClassInfo) CLASSINFO.get(clazz);
	}

	public Object arrayFor(Class clazz, int length) {
		if (Dog.class.isAssignableFrom(clazz)) {
			return new Dog[length];
		}
		if (Animal.class.isAssignableFrom(clazz)) {
			return new Animal[length];
		}
		return super.arrayFor(clazz, length);
	}

	public Class componentType(Class clazz) {
		if (Dog[].class.isAssignableFrom(clazz)) {
			return Dog.class;
		}
		if (Animal[].class.isAssignableFrom(clazz)) {
			return Animal.class;
		}
		return super.componentType(clazz);
	}
}
