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
package com.db4o.internal.marshall;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.convert.conversions.*;

/**
 * Represents a db4o file format version, assembles all the marshallers
 * needed to read/write this specific version.
 * 
 * A marshaller knows how to read/write certain types of values from/to its
 * representation on disk for a given db4o file format version.
 * 
 * Responsibilities are somewhat overlapping with TypeHandler's.
 * 
 * @exclude
 */
public class MarshallerFamily {
    
    
    public static class FamilyVersion{
        
        public static final int PRE_MARSHALLER = 0;
        
        public static final int MARSHALLER = 1;
        
        public static final int BTREE_FIELD_INDEXES = 2;
        
        public static final int CLASS_ASPECTS = 3;
        
    }
   
    private static int CURRENT_VERSION = FamilyVersion.CLASS_ASPECTS;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final PrimitiveMarshaller _primitive;

    private final int _converterVersion;
    
    private final int _handlerVersion;

    private final static MarshallerFamily[] allVersions;
    static {
    	
    	allVersions = new MarshallerFamily[HandlerRegistry.HANDLER_VERSION + 1];
    	allVersions[0] =
	        // LEGACY => before 5.4
	        new MarshallerFamily(
	            0,
	            0,
	            new ClassMarshaller0(),
	            new FieldMarshaller0(),
	            new PrimitiveMarshaller0());
    	
        allVersions[1] =
            new MarshallerFamily(
                ClassIndexesToBTrees_5_5.VERSION,
                1,
                new ClassMarshaller1(),
                new FieldMarshaller0(),
                new PrimitiveMarshaller1());
        allVersions[2] =
            new MarshallerFamily(
                FieldIndexesToBTrees_5_7.VERSION,
                2,
                new ClassMarshaller2(),
                new FieldMarshaller1(),
                new PrimitiveMarshaller1());
    	
    	
    	for (int i = 3; i < allVersions.length; i++) {
    	    allVersions[i] = latestFamily(i);
        }
    }

    public MarshallerFamily(
            int converterVersion,
            int handlerVersion,
            ClassMarshaller classMarshaller,
            FieldMarshaller fieldMarshaller,
            PrimitiveMarshaller primitiveMarshaller) {
        _converterVersion = converterVersion;
        _handlerVersion = handlerVersion;
        _class = classMarshaller;
        _class._family = this;
        _field = fieldMarshaller;
        _primitive = primitiveMarshaller;
        _primitive._family = this;
    }
    
    public static MarshallerFamily latestFamily(int version){
        return new MarshallerFamily(
            ClassAspects_7_4.VERSION,
            version,
            new ClassMarshaller2(),
            new FieldMarshaller2(),
            new PrimitiveMarshaller1());
    }

    public static MarshallerFamily version(int n) {
    	checkIfVersionIsTooNew(n);
        return allVersions[n];
    }

    private static void checkIfVersionIsTooNew(int n) {
    	if(n > allVersions.length){
    		throw new IncompatibleFileFormatException("Databasefile was created with a newer db4o version. Marshaller version: " + n);
    	}
	}

	public static MarshallerFamily current() {
        if(CURRENT_VERSION < FamilyVersion.BTREE_FIELD_INDEXES){
            throw new IllegalStateException("Using old marshaller versions to write database files is not supported, source code has been removed.");
        }
        return version(CURRENT_VERSION);
    }
    
    public static MarshallerFamily forConverterVersion(int n){
        MarshallerFamily result = allVersions[0];
        for (int i = 1; i < allVersions.length; i++) {
            if(allVersions[i]._converterVersion > n){
                return result;
            }
            result = allVersions[i]; 
        }
        return result;
    }
    
    public int handlerVersion(){
    	return _handlerVersion;
    }
    
}
