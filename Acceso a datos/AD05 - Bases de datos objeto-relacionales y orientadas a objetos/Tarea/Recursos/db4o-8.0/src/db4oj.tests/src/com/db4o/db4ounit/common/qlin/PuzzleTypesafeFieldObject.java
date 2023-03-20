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
package com.db4o.db4ounit.common.qlin;

import java.lang.reflect.*;

import com.db4o.foundation.*;
import com.db4o.qlin.*;

import db4ounit.*;

/**
 * @sharpen.if !SILVERLIGHT
 */
@decaf.Remove(decaf.Platform.JDK11)
public class PuzzleTypesafeFieldObject implements TestCase{
	
	private static Prototypes _prototypes = new Prototypes(); 
	
	public static class Cat {
		
		public String name;

		public Cat(String name){
			this.name = name;
		}
	}
	
	public void testTypeSafeFieldAsObject() {
		Cat cat = prototype(Cat.class);
		Field nameField = field(cat, cat.name);
	}
	
	private <T> T prototype(Class<T> clazz) {
		return _prototypes.prototypeForClass(clazz);
	}
	
	public static Field field(Object onObject, Object expression) {
		Class clazz = onObject.getClass();
		Iterator4<String> path = _prototypes.backingFieldPath(onObject.getClass(), expression);
		path.moveNext();
		System.out.println(path.current());
		return null;
	}
	
	public void setUp() throws Exception {
		_prototypes = new Prototypes(Prototypes.defaultReflector(), RECURSION_DEPTH, IGNORE_TRANSIENT_FIELDS);
	}

	public void tearDown() throws Exception {
		
	}
	
	private static final boolean IGNORE_TRANSIENT_FIELDS = true;
	
	private static final int RECURSION_DEPTH = 10;
	
}
