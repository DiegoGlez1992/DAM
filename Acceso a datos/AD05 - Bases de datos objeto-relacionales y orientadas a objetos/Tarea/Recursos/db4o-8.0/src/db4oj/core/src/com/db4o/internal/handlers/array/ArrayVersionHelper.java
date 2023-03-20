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
package com.db4o.internal.handlers.array;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ArrayVersionHelper {
    
    public int classIDFromInfo(ObjectContainerBase container, ArrayInfo info){
        ClassMetadata classMetadata = container.produceClassMetadata(info.reflectClass());
        if (classMetadata == null) {
            return 0;
        }
        return classMetadata.getID();
        
    }
    
    public int classIdToMarshalledClassId(int classID, boolean primitive){
        return classID;
    }
    
    public ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
        return isPrimitive ?   
            Handlers4.primitiveClassReflector(classMetadata, reflector) : 
            classMetadata.classReflector();
    }
    
    public boolean useJavaHandling() {
       return true;
    }
    
    public boolean hasNullBitmap(ArrayInfo info) {
    	if(info.nullable()){
    		return true;
    	}
        return ! info.primitive();
    }
    
    public boolean isPreVersion0Format(int elementCount) {
        return false;
    }
    
    public boolean isPrimitive(Reflector reflector, ReflectClass claxx, ClassMetadata classMetadata) {
        return claxx.isPrimitive();
    }
    
    public ReflectClass reflectClassFromElementsEntry(ObjectContainerBase container, ArrayInfo info, int classID) {
        if(classID == 0){
            return null;
        }
        ClassMetadata classMetadata = container.classMetadataForID(classID);
        if (classMetadata == null) {
            return null;
        }
        return classReflector(container.reflector(), classMetadata, info.primitive());
    }
    
    public void writeTypeInfo(WriteContext context, ArrayInfo info) {
        BitMap4 typeInfoBitmap = new BitMap4(2);
        typeInfoBitmap.set(0, info.primitive());
        typeInfoBitmap.set(1, info.nullable());
        context.writeByte(typeInfoBitmap.getByte(0));
    }
    
    public void readTypeInfo(Transaction trans, ReadBuffer buffer, ArrayInfo info, int classID) {
        BitMap4 typeInfoBitmap = new BitMap4(buffer.readByte());
        info.primitive(typeInfoBitmap.isTrue(0));
        info.nullable(typeInfoBitmap.isTrue(1));
    }

}
