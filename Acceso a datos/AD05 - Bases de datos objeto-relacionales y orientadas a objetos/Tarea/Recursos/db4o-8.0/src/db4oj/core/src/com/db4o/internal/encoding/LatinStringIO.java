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

import com.db4o.internal.*;
import com.db4o.marshall.*;



/**
 * @exclude
 */
public class LatinStringIO {
    
	public byte[] bytes(ByteArrayBuffer buffer) {
        int len = buffer.readInt();
        len = bytesPerChar() * len;
        byte[] res = new byte[len];
        System.arraycopy(buffer._buffer, buffer._offset, res, 0, len);
		return res;
	}
	
    protected int bytesPerChar(){
        return 1;
    }
    
    public byte encodingByte(){
    	return BuiltInStringEncoding.encodingByteForEncoding(new LatinStringEncoding());
	}
	
	public int length(String str){
		return str.length() + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
	}
	
	public String read(ReadBuffer buffer, int length){
	    char[] chars = new char[length];
		for(int ii = 0; ii < length; ii++){
			chars[ii] = (char)(buffer.readByte() & 0xff);
		}
		return new String(chars,0,length);
	}
	
	public String read(byte[] bytes){
	    char[] chars = new char[bytes.length];
	    for(int i = 0; i < bytes.length; i++){
	        chars[i] = (char)(bytes[i]& 0xff);
	    }
	    return new String(chars,0,bytes.length);
	}
	
	public String readLengthAndString(ReadBuffer buffer){
		int length = buffer.readInt();
		if (length == 0) {
			return "";
		}
		return read(buffer, length);
	}
	
	public int shortLength(String str){
		return str.length() + Const4.INT_LENGTH;
	}
	
	public void write(WriteBuffer buffer, String str){
	    final int length = str.length();
	    char[] chars = new char[length];
	    str.getChars(0, length, chars, 0);
	    for (int i = 0; i < length; i ++){
			buffer.writeByte((byte) (chars[i] & 0xff));
		}
	}
	
	public byte[] write(String str){
	    final int length = str.length();
        char[] chars = new char[length];
        str.getChars(0, length, chars, 0);
	    byte[] bytes = new byte[length];
	    for (int i = 0; i < length; i ++){
	        bytes[i] = (byte) (chars[i] & 0xff);
	    }
	    return bytes;
	}
	
	public void writeLengthAndString(WriteBuffer buffer, String str){
	    if (str == null) {
	        buffer.writeInt(0);
	        return;
	    }
        buffer.writeInt(str.length());
        write(buffer, str);
	}

	
}
