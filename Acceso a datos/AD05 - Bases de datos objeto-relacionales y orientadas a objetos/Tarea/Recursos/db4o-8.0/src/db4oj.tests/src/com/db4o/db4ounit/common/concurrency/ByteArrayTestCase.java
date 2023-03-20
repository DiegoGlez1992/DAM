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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ByteArrayTestCase extends Db4oClientServerTestCase {

	static final int ITERATIONS = 15;

	static final int INSTANCES = 2;

	static final int ARRAY_LENGTH = 1024 * 512;

	public static void main(String[] args) {
		new ByteArrayTestCase().runConcurrency();
	}
	
	/**
     * @sharpen.if !CF
     */
	protected void configure(Configuration config) {
		config.objectClass(SerializableByteArrayHolder.class).translate(
				new TSerializable());

	}

	protected void store() {
		for (int i = 0; i < INSTANCES; ++i) {
			store(new ByteArrayHolder(createByteArray()));
			store(new SerializableByteArrayHolder(createByteArray()));
		}
	}

	public void concByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "raw byte array", ByteArrayHolder.class);
	}

	public void concSerializableByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "TSerializable", SerializableByteArrayHolder.class);
	}

	private void timeQueryLoop(ExtObjectContainer oc, String label,
			final Class clazz) {
		for (int i = 0; i < ITERATIONS; ++i) {
			Query query = oc.query();
			query.constrain(clazz);

			ObjectSet os = query.execute();
			Assert.areEqual(INSTANCES, os.size());

			while (os.hasNext()) {
				Assert.areEqual(ARRAY_LENGTH, ((IByteArrayHolder) os.next())
						.getBytes().length);
			}
		}
	}

	byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = (byte) (i % 256);
		}
		return bytes;
	}
}
