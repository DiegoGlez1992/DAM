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
public final class UnicodeStringIO extends LatinStringIO{
	
    protected int bytesPerChar(){
        return 2;
    }
    
    public byte encodingByte(){
    	return BuiltInStringEncoding.encodingByteForEncoding(new UnicodeStringEncoding());
	}
	
	public int length(String str){
		return (str.length() * 2) + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
	}
	
	public String read(ReadBuffer buffer, int length){
	    char[] chars = new char[length];
		for(int ii = 0; ii < length; ii++){
			chars[ii] = (char)((buffer.readByte() & 0xff) | ((buffer.readByte() & 0xff) << 8));
		}
		return new String(chars, 0, length);
	}
	
	public String read(byte[] bytes){
	    int length = bytes.length / 2;
	    char[] chars = new char[length];
	    int j = 0;
	    for(int ii = 0; ii < length; ii++){
	        chars[ii] = (char)((bytes[j++]& 0xff) | ((bytes[j++]& 0xff) << 8));
	    }
	    return new String(chars,0,length);
	}
	
	public int shortLength(String str){
		return (str.length() * 2)  + Const4.INT_LENGTH;
	}
	
	public void write(WriteBuffer buffer, String str){
	    final int length = str.length();
	    char[] chars = new char[length];
	    str.getChars(0, length, chars, 0);
	    for (int i = 0; i < length; i ++){
	        buffer.writeByte((byte) (chars[i] & 0xff));
	        buffer.writeByte((byte) (chars[i] >> 8));
		}
	}
	
	public byte[] write(String str){
	    final int length = str.length();
	    char[] chars = new char[length];
	    str.getChars(0, length, chars, 0);
	    byte[] bytes = new byte[length * 2];
	    int j = 0;
	    for (int i = 0; i < length; i ++){
	        bytes[j++] = (byte) (chars[i] & 0xff);
	        bytes[j++] = (byte) (chars[i] >> 8);
	    }
	    return bytes;
	}
	
}
