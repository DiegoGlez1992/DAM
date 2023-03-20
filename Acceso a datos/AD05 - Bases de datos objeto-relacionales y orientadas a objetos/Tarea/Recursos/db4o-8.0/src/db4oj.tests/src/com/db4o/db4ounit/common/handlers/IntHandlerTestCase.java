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

public class IntHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new IntHandlerTestCase().runSolo();
    }
    
    public static class Item  {
    	public int _int;
    	public Integer _intWrapper;
    	public Item(int i, Integer wrapper) {
    		_int = i;
    		_intWrapper = wrapper;
		}
    	public boolean equals(Object obj) {
        	if(obj == this){
        		return true;
        	}
        	if (!(obj instanceof Item)) {
        		return false;
			}
        	Item other = (Item)obj;
        	return (other._int == this._int) 
        			&& this._intWrapper.equals(other._intWrapper);
    	}
    	
    	public String toString() {
    		return "[" + _int + ","+ _intWrapper + "]";
    	}
    	
    }
    
    private IntHandler intHandler() {
        return new IntHandler();
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Integer expected = new Integer(100);
        intHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Integer intValue = (Integer)intHandler().read(readContext);
        Assert.areEqual(expected, intValue);
    }
    public void testStoreObject() throws Exception{
        Item storedItem = new Item(100, new Integer(200));
        doTestStoreObject(storedItem);
    }
}
