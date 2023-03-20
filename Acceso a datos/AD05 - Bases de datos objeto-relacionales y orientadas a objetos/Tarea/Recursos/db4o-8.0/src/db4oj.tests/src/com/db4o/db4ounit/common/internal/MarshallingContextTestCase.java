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
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class MarshallingContextTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] arguments) {
        new MarshallingContextTestCase().runSolo();
    }
    
    public static class StringItem{
        public String _name;
        public StringItem(String name){
            _name = name;
        }
    }
    
    public static class StringIntItem{
        public String _name;
        public int _int;
        public StringIntItem(String name, int i){
            _name = name;
            _int = i;
        }
    }
    
    public static class StringIntBooleanItem{
        public String _name;
        public int _int;
        public boolean _bool;
        public StringIntBooleanItem(String name, int i, boolean bool){
            _name = name;
            _int = i;
            _bool = bool;
        }
    }
    
    public void testStringItem() {
        StringItem writtenItem = new StringItem("one");
        StringItem readItem = (StringItem) writeAndRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
    }
    
    public void testStringIntItem() {
        StringIntItem writtenItem = new StringIntItem("one", 777);
        StringIntItem readItem = (StringIntItem) writeAndRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
        Assert.areEqual(writtenItem._int, readItem._int);
    }

    public void testStringIntBooleanItem() {
        StringIntBooleanItem writtenItem = new StringIntBooleanItem("one", 777, true);
        StringIntBooleanItem readItem = (StringIntBooleanItem) writeAndRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
        Assert.areEqual(writtenItem._int, readItem._int);
        Assert.areEqual(writtenItem._bool, readItem._bool);
    }

    private Object writeAndRead(Object obj) {
        int imaginativeID = 500;
        ObjectReference ref = new ObjectReference(classMetadataForObject(obj), imaginativeID);
        ref.setObject(obj);
        MarshallingContext marshallingContext = new MarshallingContext(trans(), ref, container().updateDepthProvider().forDepth(Integer.MAX_VALUE), true);
        Handlers4.write(ref.classMetadata().typeHandler(), marshallingContext, obj);
        
        Pointer4 pointer = marshallingContext.allocateSlot();
        ByteArrayBuffer buffer = marshallingContext.toWriteBuffer(pointer);
        
        
        buffer.seek(0);
        
//        String str = new String(buffer._buffer);
//        System.out.println(str);
        
        UnmarshallingContext unmarshallingContext = new UnmarshallingContext(trans(), ref, Const4.ADD_TO_ID_TREE, false);
        unmarshallingContext.buffer(buffer);
        unmarshallingContext.activationDepth(new LegacyActivationDepth(5));
        return unmarshallingContext.read();
    }

    private ClassMetadata classMetadataForObject(Object obj) {
        return container().produceClassMetadata(reflector().forObject(obj));
    }

}
