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
package com.db4o.internal;

import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public final class BlockSizeBlockConverter implements BlockConverter {
	
	private final int _blockSize;

	public BlockSizeBlockConverter(int blockSize) {
		_blockSize = blockSize;
	}
	
    public int bytesToBlocks(long bytes) {
    	return (int) ((bytes + _blockSize -1 )/ _blockSize);
    }
    
    public int blockAlignedBytes(int bytes) {
    	return bytesToBlocks(bytes) * _blockSize;
    }
    
    public int blocksToBytes(int blocks){
    	return blocks * _blockSize;
    }
    
    public Slot toBlockedLength(Slot slot){
    	return new Slot(slot.address(), bytesToBlocks(slot.length()));
    }
    
    public Slot toNonBlockedLength(Slot slot){
    	return new Slot(slot.address(), blocksToBytes(slot.length()));
    }
    

}
