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

import com.db4o.internal.*;

import db4ounit.*;


public class MarshallingBufferTestCase implements TestCase {
    
    private static final int DATA_1 = 111;  
    private static final byte DATA_2 = (byte)2; 
    private static final int DATA_3 = 333; 
    private static final int DATA_4 = 444; 
    private static final int DATA_5 = 55; 
    
    public void testWrite(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        ByteArrayBuffer content = inspectContent(buffer);
        
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
    }

    public void testTransferLastWrite(){
        
        MarshallingBuffer buffer = new MarshallingBuffer();
        
        buffer.writeInt(DATA_1);
        int lastOffset = offset(buffer);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer other = new MarshallingBuffer();
        
        buffer.transferLastWriteTo(other, true);
        
        Assert.areEqual(lastOffset, offset(buffer));
        
        ByteArrayBuffer content = inspectContent(other);
        Assert.areEqual(DATA_2, content.readByte());
    }

    private int offset(MarshallingBuffer buffer) {
        return buffer.testDelegate().offset();
    }
    
    private ByteArrayBuffer inspectContent(MarshallingBuffer buffer) {
        ByteArrayBuffer bufferDelegate = buffer.testDelegate();
        bufferDelegate.seek(0);
        return bufferDelegate;
    }
    
    public void testChildren(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild();
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        buffer.mergeChildren(null, 0, 0);
        
        ByteArrayBuffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
        
        int address = content.readInt();
        content.seek(address);
        
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
    }

    
    public void testGrandChildren(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild();
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        MarshallingBuffer grandChild = child.addChild();
        grandChild.writeInt(DATA_5);
        
        buffer.mergeChildren(null, 0, 0);
        
        ByteArrayBuffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        Assert.areEqual(DATA_2, content.readByte());
        
        int address = content.readInt();
        content.seek(address);
        
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
        
        address = content.readInt();
        content.seek(address);
        Assert.areEqual(DATA_5, content.readInt());
        
    }
    
    public void testLinkOffset(){
        
        int linkOffset = 7;
        
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        buffer.writeByte(DATA_2);
        
        MarshallingBuffer child = buffer.addChild();
        child.writeInt(DATA_3);
        child.writeInt(DATA_4);
        
        MarshallingBuffer grandChild = child.addChild();
        grandChild.writeInt(DATA_5);
        
        buffer.mergeChildren(null, 0, linkOffset);
        
        ByteArrayBuffer content = inspectContent(buffer);
        
        ByteArrayBuffer extendedBuffer = new ByteArrayBuffer(content.length() + linkOffset);
        
        content.copyTo(extendedBuffer, 0, linkOffset, content.length());
        
        extendedBuffer.seek(linkOffset);
        
        Assert.areEqual(DATA_1, extendedBuffer.readInt());
        Assert.areEqual(DATA_2, extendedBuffer.readByte());
        
        int address = extendedBuffer.readInt();
        extendedBuffer.seek(address);
        
        Assert.areEqual(DATA_3, extendedBuffer.readInt());
        Assert.areEqual(DATA_4, extendedBuffer.readInt());
        
        address = extendedBuffer.readInt();
        extendedBuffer.seek(address);
        Assert.areEqual(DATA_5, extendedBuffer.readInt());
        
    }

    
    public void testLateChildrenWrite(){
        MarshallingBuffer buffer = new MarshallingBuffer();
        buffer.writeInt(DATA_1);
        MarshallingBuffer child = buffer.addChild(true, true);
        child.writeInt(DATA_3);
        buffer.writeByte(DATA_2);
        child.writeInt(DATA_4);
        buffer.mergeChildren(null, 0, 0);
        
        ByteArrayBuffer content = inspectContent(buffer);
        Assert.areEqual(DATA_1, content.readInt());
        
        int address = content.readInt();
        content.readInt();  // length
        
        Assert.areEqual(DATA_2, content.readByte());
        
        content.seek(address);
        Assert.areEqual(DATA_3, content.readInt());
        Assert.areEqual(DATA_4, content.readInt());
        
    }
    

}
