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
package com.db4o.test.reflect;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TestReflectClass {
	
	public static final int FIELD_COUNT = 8;
	public static final int METHOD_COUNT = 2;
	
	public String myString;
	
	public int myInt;
	
	public TestReflectClass myTyped;
	
	public Object myUntyped;
	
	public static Object myStatic;
	
	public transient Object myTransient;
	
	public Object foo(TestReflectClass paramTest){
		if(paramTest != null){
			return "OK";
		}
		throw new NullPointerException("No valid Parameter passed to TestReflectClass.foo()");
	}
	
	public void bar(){
		
	}
}

