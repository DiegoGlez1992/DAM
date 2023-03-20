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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class BitMap4TestCase implements TestCase {
    
    public void test() {
        
        byte[] buffer = new byte[100];
        
        for (int i = 0; i < 17; i++) {
            BitMap4 map = new BitMap4(i);            
            map.writeTo(buffer, 11);
            
            BitMap4 reReadMap = new BitMap4(buffer,11, i);
            
            for (int j = 0; j < i; j++) {
                tBit(map, j);
                tBit(reReadMap, j);
            }
        }
        
    }
    
    private void tBit(BitMap4 map, int bit) {
        map.setTrue(bit);
        Assert.isTrue(map.isTrue(bit));
        map.setFalse(bit);
        Assert.isFalse(map.isTrue(bit));
        map.setTrue(bit);
        Assert.isTrue(map.isTrue(bit));
        
    }

}
