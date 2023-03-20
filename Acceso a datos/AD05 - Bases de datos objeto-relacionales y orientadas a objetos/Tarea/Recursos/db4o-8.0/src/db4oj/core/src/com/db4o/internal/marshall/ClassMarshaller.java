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

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.encoding.*;

/**
 * @exclude
 */
public abstract class ClassMarshaller {
    
    public MarshallerFamily _family;
    
    public RawClassSpec readSpec(Transaction trans,ByteArrayBuffer reader) {
		byte[] nameBytes=readName(trans, reader);
		String className=trans.container().stringIO().read(nameBytes);
		readMetaClassID(reader); // skip
		int ancestorID=reader.readInt();
		reader.incrementOffset(Const4.INT_LENGTH); // index ID
		int numFields=reader.readInt();
		return new RawClassSpec(className,ancestorID,numFields);
    }

    public void write(final Transaction trans, final ClassMetadata clazz, final ByteArrayBuffer writer) {
        
        writer.writeShortString(trans, clazz.nameToWrite());
        
        int intFormerlyKnownAsMetaClassID = 0;
        writer.writeInt(intFormerlyKnownAsMetaClassID);
        
        writer.writeIDOf(trans, clazz._ancestor);
        
        writeIndex(trans, clazz, writer);
        
        writer.writeInt(clazz.declaredAspectCount());
        clazz.traverseDeclaredAspects(new Procedure4() {
			public void apply(Object arg) {
				 _family._field.write(trans, clazz, (ClassAspect)arg, writer);
			}
		});
    }

    protected void writeIndex(Transaction trans, ClassMetadata clazz, ByteArrayBuffer writer) {
        int indexID = clazz.index().write(trans);
        writer.writeInt(indexIDForWriting(indexID));
    }
    
    protected abstract int indexIDForWriting(int indexID);

    public final byte[] readName(Transaction trans, ByteArrayBuffer reader) {
        return readName(trans.container().stringIO(), reader);
    }
    
    public final int readMetaClassID(ByteArrayBuffer reader) {
    	return reader.readInt();
    }
    
    private byte[] readName(LatinStringIO sio, ByteArrayBuffer reader) {
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPCLASS);
        }
        byte[] nameBytes = sio.bytes(reader);
        reader.incrementOffset(nameBytes.length);
        nameBytes  = Platform4.updateClassName(nameBytes);
        return nameBytes;
    }

    public final void read(ObjectContainerBase stream, ClassMetadata clazz, ByteArrayBuffer reader) {
        clazz.setAncestor(stream.classMetadataForID(reader.readInt()));
        
//        if(clazz.callConstructor()){
//            // The logic further down checks the ancestor YapClass, whether
//            // or not it is allowed, not to call constructors. The ancestor
//            // YapClass may possibly have not been loaded yet.
//            clazz.createConstructor(true);
//        }
        
        clazz.checkType();
        
        readIndex(stream, clazz, reader);
        
        clazz._aspects = readAspects(stream, reader, clazz);        
    }

    protected abstract void readIndex(ObjectContainerBase stream, ClassMetadata clazz, ByteArrayBuffer reader) ;

	private ClassAspect[] readAspects(ObjectContainerBase stream, ByteArrayBuffer reader, final ClassMetadata clazz) {
		ClassAspect[] aspects = new ClassAspect[reader.readInt()];
		for (int i = 0; i < aspects.length; i++) {
            aspects[i] = _family._field.read(stream, clazz, reader);
            aspects[i].setHandle(i);
        }
		return aspects;
	}

    public int marshalledLength(final ObjectContainerBase stream, final ClassMetadata clazz) {
        final IntByRef len = new IntByRef(
            stream.stringIO().shortLength(clazz.nameToWrite())
                + Const4.OBJECT_LENGTH
                + (Const4.INT_LENGTH * 2)
                + (Const4.ID_LENGTH));       

        len.value += clazz.index().ownLength();
        
        clazz.traverseDeclaredAspects(new Procedure4() {
            public void apply(Object arg) {
                len.value +=  _family._field.marshalledLength(stream, (ClassAspect)arg);
            }
        });
        return len.value;
    }

	public void defrag(final ClassMetadata classMetadata, final LatinStringIO sio, final DefragmentContextImpl context, int classIndexID)  {
		readName(sio, context.sourceBuffer());
		readName(sio, context.targetBuffer());
		
		int metaClassID=0;
		context.writeInt(metaClassID);

		// ancestor ID
		context.copyID();

		context.writeInt((classMetadata.hasClassIndex() ? indexIDForWriting(classIndexID) : 0));
		
		final int aspectCount = context.readInt();
		
		if(aspectCount > classMetadata.declaredAspectCount()) {
			throw new IllegalStateException();
		}
		
		final IntByRef processedAspectCount = new IntByRef(0);
		
		classMetadata.traverseDeclaredAspects(new Procedure4() {
			public void apply(Object arg) {
				if(processedAspectCount.value >= aspectCount){
					return;
				}
				ClassAspect aspect = (ClassAspect) arg;
				_family._field.defrag(classMetadata,aspect,sio,context);
				processedAspectCount.value++;
			}
		});
		
	}
}
