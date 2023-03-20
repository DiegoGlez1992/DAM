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
package com.db4o.db4ounit.util;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;


public class VersionServices {
    
    public static final byte HEADER_30_40 = 123;
    
    public static final byte HEADER_46_57 = 4;
    
    public static final byte HEADER_60 = 100;
    

    public static byte fileHeaderVersion(String testFile) throws IOException{
        RandomAccessFile raf = new RandomAccessFile(testFile, "r");
        byte[] bytes = new byte[1];
        raf.read(bytes);  // readByte() doesn't convert to .NET.
        byte db4oHeaderVersion = bytes[0]; 
        raf.close();
        return db4oHeaderVersion;
    }
    
    public static int slotHandlerVersion(ExtObjectContainer objectContainer, Object obj){
        int id = (int) objectContainer.getID(obj);
        ObjectInfo objectInfo = objectContainer.getObjectInfo(obj);
        ObjectContainerBase container = (ObjectContainerBase) objectContainer;
        Transaction trans = container.transaction();
        ByteArrayBuffer buffer = container.readBufferById(trans, id);
        UnmarshallingContext context = new UnmarshallingContext(trans, (ObjectReference)objectInfo, Const4.TRANSIENT, false);
        context.buffer(buffer);
        context.persistentObject(obj);
        context.activationDepth(new LegacyActivationDepth(0));
        context.read();
        return context.handlerVersion();
    }


}
