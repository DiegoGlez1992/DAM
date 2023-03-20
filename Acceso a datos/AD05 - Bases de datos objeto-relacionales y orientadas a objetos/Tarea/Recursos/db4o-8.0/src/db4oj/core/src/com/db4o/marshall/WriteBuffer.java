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


/**
 * a buffer interface with write methods.
 */
public interface WriteBuffer {

    /**
     * writes a single byte to the buffer.
     * @param b the byte
     */
    void writeByte(byte b);
    
    /**
     * writes an array of bytes to the buffer
     * @param bytes the byte array
     */
    void writeBytes(byte[] bytes);

    /**
     * writes an int to the buffer.
     * @param i the int
     */
    void writeInt(int i);
    
    /**
     * writes a long to the buffer
     * @param l the long
     */
    void writeLong(long l);
    
}
