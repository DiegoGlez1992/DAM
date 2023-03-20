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

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ArrayVersionHelper3 extends ArrayVersionHelper5 {
    
    public int classIDFromInfo(ObjectContainerBase container, ArrayInfo info){
        ClassMetadata classMetadata = container.produceClassMetadata(info.reflectClass());
        if (classMetadata == null) {
            // TODO: This one is a terrible low-frequency blunder !!!
            // If YapClass-ID == 99999 then we will get IGNORE back.
            // Discovered on adding the primitives
            return Const4.IGNORE_ID;
        }
        return classMetadata.getID();
    }
    
    public int classIdToMarshalledClassId(int classID, boolean primitive){
        if(primitive){
            classID -= Const4.PRIMITIVE;
        }
        return - classID;
    }
    
    public ReflectClass classReflector(Reflector reflector, ClassMetadata classMetadata, boolean isPrimitive){
        if(Deploy.csharp){
            ReflectClass primitiveClaxx = Handlers4.primitiveClassReflector(classMetadata, reflector);
            if(primitiveClaxx != null){
                return primitiveClaxx;
            }
        }
        return super.classReflector(reflector, classMetadata, isPrimitive);
    }
    
    public boolean hasNullBitmap(ArrayInfo info) {
        return false;
    }
    
    public boolean isPrimitive(Reflector reflector, ReflectClass claxx, ClassMetadata classMetadata) {
        if(Deploy.csharp){
            return Handlers4.primitiveClassReflector(classMetadata, reflector) != null;
        }
        return claxx.isPrimitive();
    }

	public ReflectClass reflectClassFromElementsEntry(ObjectContainerBase container, ArrayInfo info, int classID) {

        
        if(classID == Const4.IGNORE_ID){
            // TODO: Here is a low-frequency mistake, extremely unlikely.
            // If classID == 99999 by accident then we will get ignore.
            
            return null;
        }
            
        info.primitive(false);
        
        if(useJavaHandling()){
            if(classID < Const4.PRIMITIVE){
                info.primitive(true);
                classID -= Const4.PRIMITIVE;
            }
        }
        classID = - classID;
        
        ClassMetadata classMetadata = container.classMetadataForID(classID);
        if (classMetadata != null) {
            return classReflector(container.reflector(), classMetadata, info.primitive());
        }
            
        return null;
    }
    
    public final boolean useJavaHandling() {
        return ! Deploy.csharp;
    }
    
    public void writeTypeInfo(WriteContext context, ArrayInfo info) {
        // do nothing, the byte for additional type information was added after format 3
    }
    
    public void readTypeInfo(Transaction trans, ReadBuffer buffer, ArrayInfo info, int classID) {
        // do nothing, the byte for additional type information was added after format 3
    }


}
