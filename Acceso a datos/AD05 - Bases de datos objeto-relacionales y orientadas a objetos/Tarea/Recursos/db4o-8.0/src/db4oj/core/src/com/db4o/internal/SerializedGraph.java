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


/**
 * @exclude
 */
public class SerializedGraph {
	
	public final int _id;

	public final byte[] _bytes;
	
	public SerializedGraph(int id, byte[] bytes) {
		_id = id;
		_bytes = bytes;
	}
	
	public int length(){
		return _bytes.length;
	}
	
	public int marshalledLength(){
		return (Const4.INT_LENGTH * 2 )+ length();
	}
	
	public void write(ByteArrayBuffer buffer){
		buffer.writeInt(_id);
		buffer.writeInt(length());
		buffer.append(_bytes);
	}
	
	public static SerializedGraph read(ByteArrayBuffer buffer){
		int id = buffer.readInt();
		int length = buffer.readInt();
		return new SerializedGraph(id, buffer.readBytes(length));
	}
	
}
