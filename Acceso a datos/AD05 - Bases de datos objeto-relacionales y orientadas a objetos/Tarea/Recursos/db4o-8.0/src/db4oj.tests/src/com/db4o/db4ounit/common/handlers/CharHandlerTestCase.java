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

public class CharHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] args) {
        new CharHandlerTestCase().runSolo();
    }
    
    public static class Item {
    	public char _char;
    	public Character _charWrapper;
    	public Item(char c, Character wrapper) {
    		_char = c;
    		_charWrapper = wrapper;
		}
    	
    	public boolean equals(Object obj) {
        	if(obj == this){
        		return true;
        	}
        	if (!(obj instanceof Item)) {
        		return false;
			}
        	Item other = (Item)obj;
        	return (other._char == this._char) 
        			&& this._charWrapper.equals(other._charWrapper);
    	}
    	
    	public String toString() {
    		return "[" + _char + "," + _charWrapper + "]";
    	}
    }
    
    private CharHandler charHandler() {
        return new CharHandler();
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Character expected = new Character((char)0x4e2d);
        charHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Character charValue = (Character)charHandler().read(readContext);
        
        Assert.areEqual(expected, charValue);
    }
    
    public void testStoreObject() throws Exception{
        Item storedItem = new Item((char)0x4e2f, new Character((char)0x4e2d));
        doTestStoreObject(storedItem);
    }
    
}
