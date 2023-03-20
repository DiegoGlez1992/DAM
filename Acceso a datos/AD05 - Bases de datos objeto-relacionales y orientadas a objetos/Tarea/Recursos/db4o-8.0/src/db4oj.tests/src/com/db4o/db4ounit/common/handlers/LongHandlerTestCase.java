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

public class LongHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new LongHandlerTestCase().runSolo();
    }
    
    private LongHandler longHandler() {
        return new LongHandler();
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Long expected = new Long(0x1020304050607080l);
        longHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Long longValue = (Long) longHandler().read(readContext);

        Assert.areEqual(expected, longValue);
    }
    
    public void testStoreObject() {
        Item storedItem = new Item(0x1020304050607080l, new Long(0x1122334455667788l));
        doTestStoreObject(storedItem);
    }
    
    public static class Item  {
        public long _long;
        public Long _longWrapper;
        public Item(long l, Long wrapper) {
            _long = l;
            _longWrapper = wrapper;
        }
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            return (other._long == this._long) 
                    && this._longWrapper.equals(other._longWrapper);
        }
        
        public String toString() {
            return "[" + _long + ","+ _longWrapper + "]";
        }
    }
    
}
