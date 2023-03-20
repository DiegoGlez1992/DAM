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
import com.db4o.internal.*;


/**
 * @exclude
 */
public final class ObjectHeader {
    
    private final ClassMetadata _classMetadata;
    
    public final MarshallerFamily _marshallerFamily;
    
    public final ObjectHeaderAttributes _headerAttributes;
    
    private int _handlerVersion;
    
    public ObjectHeader(ObjectContainerBase container, ReadWriteBuffer reader){
    	this(container,null,reader);
    }

    public ObjectHeader(ClassMetadata classMetadata, ReadWriteBuffer reader){
    	this(null,classMetadata,reader);
    }
    
    private ObjectHeader(ObjectContainerBase container, ClassMetadata classMetadata, ReadWriteBuffer reader){
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPOBJECT);
        }
        int classID = reader.readInt();
        _marshallerFamily = readMarshallerFamily(reader, classID);
        
        classID=normalizeID(classID);
        
        _classMetadata=(classMetadata!=null ? classMetadata : container.classMetadataForID(classID));

        if (Deploy.debug) {
        	// This check has been added to cope with defragment in debug mode: SlotDefragment#setIdentity()
        	// will trigger calling this constructor with a source db class metadata and a target db stream,
        	// thus _classMetadata==null. There may be a better solution, since this call is just meant to
        	// skip the object header.
        	if(_classMetadata!=null) {
	        	int ycID = _classMetadata.getID();
		        if (classID != ycID) {
		        	System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + classID);
		        }
        	}
        }
        _headerAttributes = slotFormat().readHeaderAttributes((ByteArrayBuffer)reader);
    }

    public static ObjectHeader defrag(DefragmentContextImpl context) {
    	ByteArrayBuffer source = context.sourceBuffer();
    	ByteArrayBuffer target = context.targetBuffer();
		ObjectHeader header=new ObjectHeader(context.services().systemTrans().container(),null,source);
    	int newID =context.mapping().strictMappedID(header.classMetadata().getID());
        if (Deploy.debug) {
            target.readBegin(Const4.YAPOBJECT);
        }
        SlotFormat slotFormat = header.slotFormat();
        slotFormat.writeObjectClassID(target,newID);
        slotFormat.skipMarshallerInfo(target);
        slotFormat.readHeaderAttributes(target);
    	return header;
    }
    
    private SlotFormat slotFormat(){
        return SlotFormat.forHandlerVersion(handlerVersion());
    }
    		
	private MarshallerFamily readMarshallerFamily(ReadWriteBuffer reader, int classID) {
		boolean marshallerAware=marshallerAware(classID);
		_handlerVersion = 0;
        if(marshallerAware) {
            _handlerVersion = reader.readByte();
        }
        MarshallerFamily marshallerFamily=MarshallerFamily.version(_handlerVersion);
		return marshallerFamily;
	}
    
    private boolean marshallerAware(int id) {
    	return id<0;
    }
    
    private int normalizeID(int id) {
    	return (id<0 ? -id : id);
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }

    public static ObjectHeader scrollBufferToContent(LocalObjectContainer container, ByteArrayBuffer buffer) {
        return new ObjectHeader(container, buffer);
    }
    
}
