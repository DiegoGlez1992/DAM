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
package com.db4o.internal.encoding;

import com.db4o.config.encoding.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class DelegatingStringIO extends LatinStringIO{
	
	private final StringEncoding _encoding;
	
	public DelegatingStringIO(StringEncoding encoding){
		_encoding = encoding;
	}
	
 	private String decode(byte[] bytes, int start ,int length){
 		return _encoding.decode(bytes, start, length);
 	}
 	
 	private byte[] encode(String str){
 		return _encoding.encode(str);
 	}
 	
    public byte encodingByte(){
    	if(_encoding instanceof BuiltInStringEncoding){
    		return BuiltInStringEncoding.encodingByteForEncoding(_encoding); 
    	}
		return 0;
	}
    
 	public int length(String str){
 		return encode(str).length + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
 	}
 	
 	public String read(ReadBuffer buffer, int length){
 		byte[] bytes = new byte[length];
 		buffer.readBytes(bytes);
 		return decode(bytes, 0, bytes.length);
 	}
 	
 	public String read(byte[] bytes){
 		return decode(bytes, 0, bytes.length);
 	}
 	
 	public int shortLength(String str){
 		return encode(str).length + Const4.INT_LENGTH;
 	}
 	
 	public void write(WriteBuffer buffer, String str) {
 		buffer.writeBytes(encode(str));
 	}
 	
 	public byte[] write(String str){
 		return encode(str);
 	}
 	
	/**
	 * Note the different implementation when compared to LatinStringIO and UnicodeStringIO:
	 * Instead of writing the length of the string, UTF8StringIO writes the length of the 
	 * byte array.
	 */
 	public void writeLengthAndString(WriteBuffer buffer, String str){
	    if (str == null) {
	        buffer.writeInt(0);
	        return;
	    }
	    byte[] bytes = encode(str);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
	}

}
