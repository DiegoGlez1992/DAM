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
package com.db4o.db4ounit.common.types.arrays;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ByteArrayTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ByteArrayTestCase().runAll();
	}
	
	public static interface IByteArrayHolder {
		byte[] getBytes();
	}

	public static class SerializableByteArrayHolder implements Serializable, IByteArrayHolder {

		private static final long serialVersionUID = 1L;
		
		byte[] _bytes;
		
		public SerializableByteArrayHolder(byte[] bytes) {
			this._bytes = bytes;
		}
		
		public byte[] getBytes() {
			return _bytes;
		}	
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

	static final int INSTANCES = 2;
	static final int ARRAY_LENGTH = 1024;
	
	/**
	 * @sharpen.if !CF && !SILVERLIGHT
	 */
	protected void configure(Configuration config) {
		config.objectClass(SerializableByteArrayHolder.class).translate(new TSerializable());		
	}
	
	protected void store() {
		for (int i=0; i<INSTANCES; ++i) {
			db().store(new ByteArrayHolder(createByteArray()));
			db().store(new SerializableByteArrayHolder(createByteArray()));
		}
	}
	
	/**
	 * @sharpen.if !CF && !SILVERLIGHT
	 */
	public void testByteArrayHolder() throws Exception {
		timeQueryLoop("raw byte array", ByteArrayHolder.class);
	}
	
	/**
	 * @sharpen.if !CF && !SILVERLIGHT
	 */
	public void testSerializableByteArrayHolder() throws Exception {
		timeQueryLoop("TSerializable", SerializableByteArrayHolder.class);
	}

	private void timeQueryLoop(String label, final Class clazz) throws Exception {
		Query query = newQuery(clazz);
		ObjectSet os = query.execute();
		Assert.areEqual(INSTANCES, os.size());

		while (os.hasNext()) {
			Assert.areEqual(ARRAY_LENGTH, ((IByteArrayHolder) os.next())
					.getBytes().length, label);
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
