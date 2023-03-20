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

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class StringHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] arguments) {
        new StringHandlerTestCase().runSolo();
    }
    
    public static class Item  {
    	public String _string;
    	public Item(String s) {
    		_string = s;
		}
    	public boolean equals(Object obj) {
        	if(obj == this){
        		return true;
        	}
        	if (!(obj instanceof Item)) {
        		return false;
			}
        	Item other = (Item)obj;
        	return this._string.equals(other._string);
    	}
    	
    	public int hashCode() {
        	int hash = 7;
        	hash = 31 * hash + (null == _string ? 0 : _string.hashCode());
        	return hash;
    	}
    	
    	public String toString() {
    		return "[" + _string + "]";
    	}
    }
    

	public void testIndexMarshalling() {
		ByteArrayBuffer reader=new ByteArrayBuffer(2*Const4.INT_LENGTH);
		final Slot original = new Slot(0xdb,0x40);
		stringHandler().writeIndexEntry(context(),reader, original);
		reader._offset=0;
		Slot retrieved = (Slot) stringHandler().readIndexEntry(context(), reader);
		Assert.areEqual(original.address(), retrieved.address());
		Assert.areEqual(original.length(), retrieved.length());
	}

    private StringHandler stringHandler() {
        return new StringHandler();
    }
	
	public void testReadWrite(){
	    MockWriteContext writeContext = new MockWriteContext(db());
	    stringHandler().write(writeContext, "one");
	    MockReadContext readContext = new MockReadContext(writeContext);
	    String str = (String)stringHandler().read(readContext);
	    Assert.areEqual("one", str);
	}
	
    public void testStoreObject() throws Exception{
        doTestStoreObject(new Item("one"));
    }
	
}
