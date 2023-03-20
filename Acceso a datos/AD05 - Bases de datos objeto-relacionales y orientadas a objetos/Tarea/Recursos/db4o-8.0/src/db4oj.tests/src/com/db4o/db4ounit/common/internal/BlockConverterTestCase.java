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
import com.db4o.internal.slots.*;

import db4ounit.*;

/**
 * @exclude
 */
public class BlockConverterTestCase implements TestCase {
	
	private final BlockSizeBlockConverter blockConverter = new BlockSizeBlockConverter(8);
	
    public void testBlocksToBytes(){
        int[] blocks = new int[]{0, 1, 8, 9};
        int[] bytes  = new int[]{0, 8, 64, 72};
        
        for (int i = 0; i < blocks.length; i++) {
			Assert.areEqual(bytes[i], blockConverter.blocksToBytes(blocks[i]));
        }
    }
    
    public void testBytesToBlocks(){
        int[] bytes  = new int[]{0, 1, 2, 7, 8, 9, 16, 17, 799, 800, 801};
        int[] blocks = new int[]{0, 1, 1, 1, 1, 2,  2,  3, 100, 100, 101};
        
        for (int i = 0; i < blocks.length; i++) {
            Assert.areEqual(blocks[i], blockConverter.bytesToBlocks(bytes[i]));
        }
    }
    
    public void testBlockAlignedBytes(){
    	int[] bytes    = new int[]{0, 1, 2, 7, 8, 9, 16, 17, 799, 800, 801};
    	int[] aligned  = new int[]{0, 8, 8, 8, 8, 16, 16, 24, 800, 800, 808};
        for (int i = 0; i < bytes.length; i++) {
            Assert.areEqual(aligned[i], blockConverter.blockAlignedBytes(bytes[i]));
        }
    }
    
    public void testToBlockedLength(){
        int[] bytes  = new int[]{0, 1, 2, 7, 8, 9, 16, 17, 799, 800, 801};
        int[] blocks = new int[]{0, 1, 1, 1, 1, 2,  2,  3, 100, 100, 101};
        for (int i = 0; i < bytes.length; i++) {
        	Slot byteSlot = new Slot(0, bytes[i]);
        	Slot blockedSlot = new Slot(0, blocks[i]);
            Assert.areEqual(blockedSlot, blockConverter.toBlockedLength(byteSlot));
        }
    }
    
    public void testToNonBlockedLength(){
        int[] blocks = new int[]{0, 1, 8, 9};
        int[] bytes  = new int[]{0, 8, 64, 72};
        for (int i = 0; i < blocks.length; i++) {
        	Slot blockedSlot = new Slot(0, blocks[i]);
        	Slot byteSlot = new Slot(0, bytes[i]);
            Assert.areEqual(byteSlot, blockConverter.toNonBlockedLength(blockedSlot));
        }
    }

}
