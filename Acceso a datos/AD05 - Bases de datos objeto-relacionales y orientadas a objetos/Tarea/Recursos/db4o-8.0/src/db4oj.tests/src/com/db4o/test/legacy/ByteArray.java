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
package com.db4o.test.legacy;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class ByteArray {

	public static interface IByteArrayHolder {
		byte[] getBytes();
	}

	public static class ByteArrayHolder implements IByteArrayHolder {
		
		public byte[] _bytes;
		
		public ByteArrayHolder(byte[] bytes) {
			this._bytes = bytes;
		}
		
		public byte[] getBytes() {
			return _bytes;
		}
	}

	public static class SerializableByteArrayHolder implements Serializable, IByteArrayHolder {

		private static final long serialVersionUID = 1L;
		
		public byte[] _bytes;
		
		public SerializableByteArrayHolder(byte[] bytes) {
			this._bytes = bytes;
		}
		
		public byte[] getBytes() {
			return _bytes;
		}	
	}

	static final int INSTANCES = 2;
	
	static final int ARRAY_LENGTH = 1024*512;
	
	public void store() {
		Test.close();
		
		com.db4o.Db4o.configure().objectClass(SerializableByteArrayHolder.class).translate(new TSerializable());		
		Test.open();
		
		for (int i=0; i<INSTANCES; ++i) {
			Test.store(new ByteArrayHolder(createByteArray()));
			Test.store(new SerializableByteArrayHolder(createByteArray()));
		}
	}
	
	public void testByteArrayHolder() {
		timeQueryLoop("raw byte array", ByteArrayHolder.class);
	}
	
	public void testSerializableByteArrayHolder() {
		timeQueryLoop("TSerializable", SerializableByteArrayHolder.class);
	}

	private void timeQueryLoop(String label, final Class clazz) {

		Test.close();
		Test.open();

		Query query = Test.query();
		query.constrain(clazz);

		ObjectSet os = query.execute();
		Test.ensure(INSTANCES == os.size());

		while (os.hasNext()) {
			Test.ensure(ARRAY_LENGTH == ((IByteArrayHolder) os.next())
					.getBytes().length);
		}
	}
	
	byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (int i=0; i<bytes.length; ++i) {
			bytes[i] = (byte)(i % 256);
		}
		return bytes;
	}
}
