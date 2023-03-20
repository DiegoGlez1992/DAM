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
package com.db4o.db4ounit.common.internal;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.partial 
 */
public class Comparable4TestCase extends AbstractDb4oTestCase implements OptOutMultiSession{

	public static void main(String[] args) {
		new Comparable4TestCase().runAll();
	}
	
	public static class Item {
		
	}
	
	public void testHandlers(){
		assertHandlerComparison(BooleanHandler.class, new Boolean(false), new Boolean(true));
		assertHandlerComparison(ByteHandler.class, new Byte((byte)1), new Byte((byte)2));
		assertHandlerComparison(ByteHandler.class, new Byte(Byte.MIN_VALUE), new Byte(Byte.MAX_VALUE));
		assertHandlerComparison(CharHandler.class, new Character((char)1), new Character((char)2));
		assertHandlerComparison(CharHandler.class, new Character(Character.MIN_VALUE), new Character(Character.MAX_VALUE));
		
		assertHandlerComparison(DoubleHandler.class, new Double(1), new Double(2));
		assertHandlerComparison(DoubleHandler.class, new Double(0.1), new Double(0.2));
		assertHandlerComparison(DoubleHandler.class, new Double(Double.MIN_VALUE), new Double(Double.MAX_VALUE));
		assertHandlerComparison(FloatHandler.class, new Float(1), new Float(2));
		assertHandlerComparison(FloatHandler.class, new Float(0.1), new Float(0.2));
		assertHandlerComparison(FloatHandler.class, new Float(Float.MIN_VALUE), new Float(Float.MAX_VALUE));
		assertHandlerComparison(IntHandler.class, new Integer(2), new Integer(4));
		assertHandlerComparison(IntHandler.class, new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE));
		assertHandlerComparison(LongHandler.class, new Long(2), new Long(4));
		assertHandlerComparison(LongHandler.class, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE));
		assertHandlerComparison(ShortHandler.class, new Short((short)2), new Short((short)4));
		assertHandlerComparison(ShortHandler.class, new Short(Short.MIN_VALUE), new Short(Short.MAX_VALUE));
		
		assertDateHandler();
		
		assertHandlerComparison(StringHandler.class, "a", "b");
		assertHandlerComparison(StringHandler.class, "Hello", "Hello_");
		
		assertClassHandler();
	}

	private void assertClassHandler() {
		int id1 = storeItem();
		int id2 = storeItem();
		int smallerID = Math.min(id1, id2);
		int biggerID = Math.max(id1, id2);
		ClassMetadata classMetadata = new ClassMetadata(container(), reflector().forClass(Item.class));
		assertHandlerComparison((Comparable4)classMetadata.typeHandler(), new Integer(smallerID), new Integer(biggerID));
	}

	private int storeItem() {
		Item item = new Item();
		db().store(item);
		return (int) db().getID(item);
	}
	
	


	/**
	 * @sharpen.remove
	 */
	private void assertDateHandler() {
		assertHandlerComparison(DateHandler.class, new Date(1), new Date(2));
		assertHandlerComparison(DateHandler.class, new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
	}
	
	private void assertHandlerComparison(Class handlerClass, Object smaller, Object greater) {
		Comparable4 handler = (Comparable4) newInstance(handlerClass);
		assertHandlerComparison(handler, smaller, greater);
	}

    private void assertHandlerComparison(Comparable4 handler, Object smaller, Object greater) {
        PreparedComparison comparable = handler.prepareComparison(context(), smaller);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(smaller));
		Assert.isSmaller(0, comparable.compareTo(greater));
		Assert.isGreater(0, comparable.compareTo(null));
		
		comparable = handler.prepareComparison(context(), greater);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(greater));
		Assert.isGreater(0, comparable.compareTo(smaller));
		Assert.isGreater(0, comparable.compareTo(null));
		
		comparable = handler.prepareComparison(context(), null);
		Assert.isNotNull(comparable);
		Assert.areEqual(0, comparable.compareTo(null));
		Assert.isSmaller(0, comparable.compareTo(smaller));
    }

    private Object newInstance (Class clazz){
		ReflectClass classReflector = reflector().forClass(clazz);
		Object obj = classReflector.newInstance();
		if(obj == null) {
			throw new IllegalArgumentException("No usable constructor for Class " + clazz);
		}
		return obj;
	}

}
