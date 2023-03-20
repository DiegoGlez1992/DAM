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
package com.db4o.db4ounit.common.stored;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

public class ArrayStoredTypeTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public boolean[] _primitiveBoolean;
		public Boolean[] _wrapperBoolean;
		public int[] _primitiveInt;
		public Integer[] _wrapperInteger;

		public Data(boolean[] primitiveBoolean, Boolean[] wrapperBoolean, int[] primitiveInteger, Integer[] wrapperInteger) {
			this._primitiveBoolean = primitiveBoolean;
			this._wrapperBoolean = wrapperBoolean;
			this._primitiveInt = primitiveInteger;
			this._wrapperInteger = wrapperInteger;
		}
	}
	
	protected void store() throws Exception {
		Data data=new Data(
				new boolean[] { true, false },
				new Boolean[] { Boolean.TRUE, Boolean.FALSE },
				new int[] { 0, 1, 2 },
				new Integer[] { new Integer(4),new Integer(5), new Integer(6)}
		);
		store(data);
	}
	
	public void testArrayStoredTypes() {
		StoredClass clazz = db().storedClass(Data.class);
		assertStoredType(clazz, "_primitiveBoolean", boolean.class);
		assertStoredType(clazz, "_wrapperBoolean", Boolean.class);
		assertStoredType(clazz, "_primitiveInt", int.class);
		assertStoredType(clazz, "_wrapperInteger", Integer.class);
	}

	private void assertStoredType(StoredClass clazz, String fieldName,
			Class type) {
		StoredField field = clazz.storedField(fieldName,null);
		
		Assert.areEqual(
				type.getName(),
				// getName() also contains the assembly name in .net
				// so we better remove it for comparison
				CrossPlatformServices.simpleName(field.getStoredType().getName()));
	}
}
