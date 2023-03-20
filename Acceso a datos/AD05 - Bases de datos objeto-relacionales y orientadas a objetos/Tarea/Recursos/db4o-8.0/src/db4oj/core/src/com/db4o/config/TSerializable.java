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
package com.db4o.config;

import java.io.*;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.reflect.jdk.*;

/**
 * @exclude
 * 
 * @sharpen.ignore
 */
public class TSerializable implements ObjectConstructor {

	public Object onStore(ObjectContainer con, Object object) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteStream);
			out.writeObject(object);
			return byteStream.toByteArray();
		} catch (IOException e) {
			throw new ReflectException(e);
		}
	}

	public void onActivate(ObjectContainer con, Object object, Object members) {
		// do nothing
	}

	public Object onInstantiate(final ObjectContainer con, Object storedObject) {
		try {
			ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream((byte[]) storedObject)) {
				protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
					return JdkReflector.toNative(con.ext().reflector().forName(v.getName()));
				}
			};
			Object in = inStream.readObject();
			return in;
		} catch (IOException e) {
			throw new ReflectException(e);
		} catch (ClassNotFoundException e) {
			throw new ReflectException(e);
		}
	}

	public Class storedClass() {
		return byte[].class;
	}
}
