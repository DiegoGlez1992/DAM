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
package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.*;

import db4ounit.*;

public class ByteHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new ByteHandlerTestCase().runSolo();
    }
    
    public static class Item {
        
        public byte _byte;
        
        public Byte _byteWrapper;
        
        public Item(byte b, Byte wrapper){
            _byte = b;
            _byteWrapper = wrapper;
        }
        
        public boolean equals(Object obj) {
        	if(obj == this){
        		return true;
        	}
        	if (!(obj instanceof Item)) {
        		return false;
			}
        	Item other = (Item)obj;
        	return (other._byte == this._byte) 
        			&& this._byteWrapper.equals(other._byteWrapper);
        	        	
        }
        
        public String toString() {
    		return "[" + _byte + "," + _byteWrapper + "]";
        }
        
    }
    
    private ByteHandler byteHandler() {
        return new ByteHandler();
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Byte expected = new Byte((byte)0x61);
        byteHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Byte byteValue = (Byte)byteHandler().read(readContext);
        Assert.areEqual(expected, byteValue);
    }
    
    public void testStoreObject() throws Exception{
        Item storedItem = new Item((byte)5, new Byte((byte)6));
        doTestStoreObject(storedItem);
    }
}
