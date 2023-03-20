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
package com.db4o.config.encoding;

/**
 * encodes a String to a byte array and decodes a String
 * from a part of a byte array  
 */
public interface StringEncoding {
	
	/**
	 * called when a string is to be encoded to a byte array.
	 * @param str the string to encode
	 * @return the encoded byte array
	 */
	public byte[] encode(String str);
	
	/**
	 * called when a byte array is to be decoded to a string.  
	 * @param bytes the byte array
	 * @param start the start offset in the byte array
	 * @param length the length of the encoded string in the byte array
	 * @return the string
	 */
	public String decode(byte[] bytes, int start, int length);

}
