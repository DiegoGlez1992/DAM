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
package com.db4o.db4ounit.common.assorted;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.handlers.versions.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class HandlerRegistryTestCase extends AbstractDb4oTestCase {
	
	public interface FooInterface {
	}
	
	public static class Item{
	    
	}
	
	protected void store() throws Exception {
	    store(new Item());
	}
	
	public void testCorrectHandlerVersion(){
	    OpenTypeHandler openTypeHandler = new OpenTypeHandler(container());
	    
        assertCorrectedHandlerVersion(OpenTypeHandler0.class, openTypeHandler, -1);
        assertCorrectedHandlerVersion(OpenTypeHandler0.class, openTypeHandler, 0);
        assertCorrectedHandlerVersion(OpenTypeHandler2.class, openTypeHandler, 1);
        assertCorrectedHandlerVersion(OpenTypeHandler2.class, openTypeHandler, 2);
        assertCorrectedHandlerVersion(OpenTypeHandler.class, openTypeHandler, HandlerRegistry.HANDLER_VERSION);
        assertCorrectedHandlerVersion(OpenTypeHandler.class, openTypeHandler, HandlerRegistry.HANDLER_VERSION + 1);
        
        StandardReferenceTypeHandler stdReferenceHandler = new StandardReferenceTypeHandler(itemClassMetadata());
        assertCorrectedHandlerVersion(StandardReferenceTypeHandler0.class, stdReferenceHandler, 0);
        assertCorrectedHandlerVersion(StandardReferenceTypeHandler.class, stdReferenceHandler, 2);
        
        PrimitiveTypeMetadata primitiveMetadata = new PrimitiveTypeMetadata(container(), openTypeHandler,0, null );
        assertPrimitiveHandlerDelegate(OpenTypeHandler0.class, primitiveMetadata,0);
        assertPrimitiveHandlerDelegate(OpenTypeHandler2.class, primitiveMetadata,1);
        assertPrimitiveHandlerDelegate(OpenTypeHandler2.class, primitiveMetadata,2);
        assertPrimitiveHandlerDelegate(OpenTypeHandler.class, primitiveMetadata,HandlerRegistry.HANDLER_VERSION);
        
        ArrayHandler arrayHandler = new ArrayHandler(openTypeHandler, false);
        assertCorrectedHandlerVersion(ArrayHandler0.class, arrayHandler, 0);
        assertCorrectedHandlerVersion(ArrayHandler1.class, arrayHandler, 1);
        assertCorrectedHandlerVersion(ArrayHandler3.class, arrayHandler, 2);
        assertCorrectedHandlerVersion(ArrayHandler3.class, arrayHandler, 3);
        
        assertCorrectedHandlerVersion(ArrayHandler.class, arrayHandler, HandlerRegistry.HANDLER_VERSION);
        
        ArrayHandler multidimensionalArrayHandler = new MultidimensionalArrayHandler(openTypeHandler, false);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler0.class, multidimensionalArrayHandler, 0);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler3.class, multidimensionalArrayHandler, 1);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler3.class, multidimensionalArrayHandler, 2);
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler3.class, multidimensionalArrayHandler, 3);
        
        assertCorrectedHandlerVersion(MultidimensionalArrayHandler.class, multidimensionalArrayHandler, HandlerRegistry.HANDLER_VERSION);
	    
	}

    private void assertPrimitiveHandlerDelegate(Class expectedClass,
        PrimitiveTypeMetadata primitiveMetadata, int version) {
        TypeHandler4 correctTypeHandler = (TypeHandler4) correctHandlerVersion(primitiveMetadata.typeHandler(), version);
        Assert.areSame(expectedClass,correctTypeHandler.getClass());
    }

    private ClassMetadata itemClassMetadata() {
        return container().classMetadataForObject(new Item());
    }

    private void assertCorrectedHandlerVersion(Class expectedClass, TypeHandler4 typeHandler, int version) {
        Assert.areSame(expectedClass, correctHandlerVersion(typeHandler, version).getClass());
    }

    private TypeHandler4 correctHandlerVersion(TypeHandler4 typeHandler, int version) {
        return handlers().correctHandlerVersion(typeHandler, version);
    }

	private HandlerRegistry handlers() {
		return stream().handlers();
	}
	
	public void testTypeHandlerForID(){
	    assertTypeHandler(IntHandler.class, Handlers4.INT_ID);
	    assertTypeHandler(OpenTypeHandler.class, Handlers4.UNTYPED_ID);
	    assertTypeHandler(IntHandler.class, Handlers4.INT_ID);
	    assertTypeHandler(ArrayHandler.class, Handlers4.ANY_ARRAY_ID);
	    assertTypeHandler(MultidimensionalArrayHandler.class, Handlers4.ANY_ARRAY_N_ID);
	}

    private void assertTypeHandler(Class expectedHandlerClass, int classMetadataID) {
        TypeHandler4 handler = container().classMetadataForID(classMetadataID).typeHandler();
        Assert.isInstanceOf(expectedHandlerClass, handler);
    }
	
	public void testTypeHandlerForClass(){
	    Assert.isInstanceOf(
	        IntHandler.class, 
	        handlers().typeHandlerForClass(integerClassReflector()));
	    Assert.isInstanceOf(
                OpenTypeHandler.class, 
                handlers().typeHandlerForClass(objectClassReflector()));
	}
	

	public void testClassForID(){
	    ReflectClass byReflector = integerClassReflector();
	    ReflectClass byID = handlers().classForID(Handlers4.INT_ID);
        Assert.isNotNull(byID);
        Assert.areEqual(byReflector, byID);
	}

	public void testClassReflectorForHandler(){
        ReflectClass byReflector = integerClassReflector();
        ReflectClass byID = handlers().classForID(Handlers4.INT_ID);
        Assert.isNotNull(byID);
        Assert.areEqual(byReflector, byID);
    }
	
    private ReflectClass objectClassReflector() {
        return reflectorFor(Object.class);
    }
	
    private ReflectClass integerClassReflector() {
    	return reflectorFor(Platform4.nullableTypeFor(int.class));
    }

    private ReflectClass reflectorFor(Class clazz) {
        return reflector().forClass(clazz);
    }
	
	public static void main(String[] arguments) {
        new HandlerRegistryTestCase().runSolo();
    }
	
}
