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
package com.db4o.db4ounit.jre11.types.arrays;

import db4ounit.*;
import db4ounit.extensions.*;

public class PrimitiveWrapperArrayTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new PrimitiveWrapperArrayTestCase().runSolo();
	}
	
	protected void store() throws Exception {
		store(BooleanContainer.testInstance());
		store(IntegerContainer.testInstance());
	}
	
	public void testBooleanArray() {
		assertArrayContainer(BooleanContainer.testInstance());
	}
	
	public void _testIntegerArray() {
		assertArrayContainer(IntegerContainer.testInstance());
	}
	
	private void assertArrayContainer(final ArrayContainer expected) {
		ArrayContainer item = (ArrayContainer) retrieveOnlyInstance(expected.getClass());		
		ArrayAssert.areEqual(expected.getArray(), item.getArray());
	}
	
	public static interface ArrayContainer {
		
		public Object[] getArray();
	}
	
	public static class IntegerContainer implements ArrayContainer {
		public Integer[] _array;
		
		public IntegerContainer() {
		}
		
		public IntegerContainer(Integer[] array) {
			_array = array;
		}
		
		public Object[] getArray() {
			return _array;
		}
		
		public static IntegerContainer testInstance() {
			return new IntegerContainer(new Integer[] {
					new Integer(42), new Integer(0), null, new Integer(1), new Integer(-42) });
		}
	}
	
	public static class BooleanContainer implements ArrayContainer {
		
		public Boolean[] _array;
		
		public BooleanContainer() {
		}
		
		public BooleanContainer(Boolean[] array){
			_array = array;
		}
		
		public Object[] getArray() {
			return _array;
		}
		
		public static BooleanContainer testInstance(){
			return new BooleanContainer(new Boolean[] {
					new Boolean(true), new Boolean(false), null });
		}
	}

}
