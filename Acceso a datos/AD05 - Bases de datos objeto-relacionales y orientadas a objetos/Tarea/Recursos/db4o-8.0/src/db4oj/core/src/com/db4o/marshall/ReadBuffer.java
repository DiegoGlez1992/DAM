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
package com.db4o.marshall;

import com.db4o.foundation.*;

/**
 * a buffer interface with methods to read and to position 
 * the read pointer in the buffer.
 */
public interface ReadBuffer {
    
	/**
	 * returns the current offset in the buffer
	 * @return the offset
	 */
    int offset();
    
    public BitMap4 readBitMap(int bitCount);

    /**
     * reads a byte from the buffer.
     * @return the byte
     */
    byte readByte();
    
    /**
     * reads an array of bytes from the buffer.
     * The length of the array that is passed as a parameter specifies the
     * number of bytes that are to be read. The passed bytes buffer parameter
     * is directly filled.  
     * @param bytes the byte array to read the bytes into.
     */
    void readBytes(byte[] bytes);

    /**
     * reads an int from the buffer.
     * @return the int
     */
    int readInt();
    
    /**
     * reads a long from the buffer.
     * @return the long
     */
    long readLong();
    
    /**
     * positions the read pointer at the specified position
     * @param offset the desired position in the buffer
     */
	void seek(int offset);
}
