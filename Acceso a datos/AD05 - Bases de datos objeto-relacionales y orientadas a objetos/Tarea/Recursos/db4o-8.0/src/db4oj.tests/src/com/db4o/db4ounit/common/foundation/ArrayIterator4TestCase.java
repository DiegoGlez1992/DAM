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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;


public class ArrayIterator4TestCase implements TestCase {
	
	public void testEmptyArray() {
		assertExhausted(new ArrayIterator4(new Object[0])); 
	}
	
	public void testArray() {
		ArrayIterator4 i = new ArrayIterator4(new Object[] { "foo", "bar" });
		Assert.isTrue(i.moveNext());
		Assert.areEqual("foo", i.current());
		
		Assert.isTrue(i.moveNext());
		Assert.areEqual("bar", i.current());
		
		assertExhausted(i);
	}
	
	private void assertExhausted(final ArrayIterator4 i) {
		Assert.isFalse(i.moveNext());		
		Assert.expect(ArrayIndexOutOfBoundsException.class, new CodeBlock(){
			public void run() throws Throwable {
				System.out.println(i.current());
			}
		});
	}

}
