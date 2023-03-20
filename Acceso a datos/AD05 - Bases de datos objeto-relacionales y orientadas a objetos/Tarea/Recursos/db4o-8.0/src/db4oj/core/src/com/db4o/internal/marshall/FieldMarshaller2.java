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

import com.db4o.internal.*;
import com.db4o.internal.encoding.*;


/**
 * @exclude
 */
public class FieldMarshaller2 extends FieldMarshaller1 {
    
    private static final int ASPECT_TYPE_TAG_LENGTH = 1;
    
    public int marshalledLength(ObjectContainerBase stream, ClassAspect aspect) {
        return super.marshalledLength(stream, aspect) + ASPECT_TYPE_TAG_LENGTH;
    }
    
    protected RawFieldSpec readSpec(AspectType aspectType, ObjectContainerBase stream, ByteArrayBuffer reader) {
        return super.readSpec(AspectType.forByte(reader.readByte()), stream, reader);
    }
    
    public void write(Transaction trans, ClassMetadata clazz, ClassAspect aspect, ByteArrayBuffer writer) {
        writer.writeByte(aspect.aspectType()._id);
        super.write(trans, clazz, aspect, writer);
    }
    
    public void defrag(ClassMetadata classMetadata, ClassAspect aspect, LatinStringIO sio,
        final DefragmentContextImpl context){
        context.readByte();
        super.defrag(classMetadata, aspect, sio, context);
    }

}
