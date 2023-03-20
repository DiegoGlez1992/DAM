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
package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ArrayHandlerTestCase extends AbstractDb4oTestCase {
    
    public static class FloatArrayHolder {
    	public float[] _floats;
    	public float[][] _jaggedFloats;
    	public Float[][] _jaggedFloatWrappers;
    	
    	public FloatArrayHolder() {
			// for jres that require instantiation through the constructor
    	}
    	
    	public FloatArrayHolder(float... floats) {
    		_floats = floats;
    		_jaggedFloats = new float[][] { floats };
    		_jaggedFloatWrappers = new Float[][] { lift(floats) };
    	}
    	
    	public static Float[] lift(float[] floats) {
    		final Float[] wrappers = new Float[floats.length];
    		for (int i=0; i<floats.length; ++i) {
	            wrappers[i] = floats[i];
            }
			return wrappers;
        }

		public float[] floats() {
    		return _floats;
    	}
    	
    	public float[] jaggedFloats() {
    		return _jaggedFloats[0];
    	}
    	
    	public Float[] jaggedWrappers() {
    		return _jaggedFloatWrappers[0];
    	}
    }
    
    public static class IntArrayHolder{
        public int[] _ints;
        public int[][] _jaggedInts;
        public IntArrayHolder(int[] ints){
            _ints = ints;
            _jaggedInts = new int[][] { _ints };
        } 
        
        public int[] jaggedInts() {
        	return _jaggedInts[0];
        }
    }
    
    public static class StringArrayHolder{
        public String[] _strings;
        public StringArrayHolder(String[] strings){
            _strings = strings;
        }
    }
    
    public static void main(String[] args) {
        new ArrayHandlerTestCase().runSolo();
    }
    
    public void testFloatArrayRoundtrip() throws Exception {
    	final float[] expected = new float[] { Float.MIN_VALUE, Float.MIN_VALUE + 1, 0.0f, Float.MAX_VALUE - 1, Float.MAX_VALUE};
		store(new FloatArrayHolder(expected));
		reopen();
		
		final FloatArrayHolder stored = retrieveOnlyInstance(FloatArrayHolder.class);
		ArrayAssert.areEqual(expected, stored.jaggedFloats());
		ArrayAssert.areEqual(expected, stored.floats());
		ArrayAssert.areEqual(FloatArrayHolder.lift(expected), stored.jaggedWrappers());
    }
    
    public void testArraysHaveNoIdentity() throws Exception {
    	
    	final float[] expected = new float[] { Float.MIN_VALUE, Float.MIN_VALUE + 1, 0.0f, Float.MAX_VALUE - 1, Float.MAX_VALUE};
		store(new FloatArrayHolder(expected));
		store(new FloatArrayHolder(expected));
		reopen();
		
		final ObjectSet<FloatArrayHolder> stored = db().query(FloatArrayHolder.class);
		final FloatArrayHolder first = stored.next();
		final FloatArrayHolder second = stored.next();
		Assert.areNotSame(first._floats, second._floats);
    }

    public void testHandlerVersion(){
        IntArrayHolder intArrayHolder = new IntArrayHolder(new int[0]);
        store(intArrayHolder);
        ReflectClass claxx = reflector().forObject(intArrayHolder);
        ClassMetadata classMetadata = (ClassMetadata) container().produceClassMetadata(claxx);
        FieldMetadata fieldMetadata = classMetadata.fieldMetadataForName("_ints");
        TypeHandler4 arrayHandler = fieldMetadata.getHandler();
        Assert.isInstanceOf(ArrayHandler.class, arrayHandler);
        assertCorrectedHandlerVersion(arrayHandler, 0, ArrayHandler0.class);
        assertCorrectedHandlerVersion(arrayHandler, 1, ArrayHandler1.class);
        assertCorrectedHandlerVersion(arrayHandler, 2, ArrayHandler3.class);
        assertCorrectedHandlerVersion(arrayHandler, 3, ArrayHandler3.class);
        assertCorrectedHandlerVersion(arrayHandler, HandlerRegistry.HANDLER_VERSION, ArrayHandler.class);
    }
    
    public void testIntArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        int[] expected = new int[]{7, 8, 9};
        intArrayHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        int[] actual = (int[]) intArrayHandler().read(readContext);
        ArrayAssert.areEqual(expected, actual);
    }
    
    public void testIntArrayStoreObject() throws Exception{
        IntArrayHolder expectedItem = new IntArrayHolder(new int[] {1, 2, 3});
        db().store(expectedItem);
        db().purge(expectedItem);
        IntArrayHolder readItem = (IntArrayHolder) retrieveOnlyInstance(IntArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._ints, readItem._ints);
        ArrayAssert.areEqual(expectedItem._ints, readItem.jaggedInts());
    }
    
    public void testStringArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        String[] expected = new String[]{"one", "two", "three"};
        stringArrayHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        String[] actual = (String[]) stringArrayHandler().read(readContext);
        ArrayAssert.areEqual(expected, actual);
    }

    public void testStringArrayStoreObject() throws Exception{
        StringArrayHolder expectedItem = new StringArrayHolder(new String[] {"one", "two", "three"});
        db().store(expectedItem);
        db().purge(expectedItem);
        StringArrayHolder readItem = (StringArrayHolder) retrieveOnlyInstance(StringArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._strings, readItem._strings);
    }
    
    private ArrayHandler arrayHandler(Class clazz, boolean isPrimitive) {
        ClassMetadata classMetadata = container().produceClassMetadata(reflector().forClass(clazz));
        return new ArrayHandler(classMetadata.typeHandler(), isPrimitive);
    }

    private void assertCorrectedHandlerVersion(TypeHandler4 arrayHandler, int version, Class handlerClass) {
        TypeHandler4 correctedHandlerVersion = container().handlers().correctHandlerVersion(arrayHandler, version);
        Assert.isInstanceOf(handlerClass, correctedHandlerVersion);
    }
    
    private ArrayHandler intArrayHandler(){
        return arrayHandler(int.class, true);
    }
    
    private ArrayHandler stringArrayHandler(){
        return arrayHandler(String.class, false);
    }

}
   
