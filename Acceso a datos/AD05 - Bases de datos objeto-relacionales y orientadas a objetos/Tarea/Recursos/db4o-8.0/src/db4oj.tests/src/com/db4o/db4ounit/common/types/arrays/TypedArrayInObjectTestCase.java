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

import com.db4o.db4ounit.common.sampledata.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TypedArrayInObjectTestCase extends AbstractDb4oTestCase {
	
	private final static AtomData[] ARRAY = {new AtomData("TypedArrayInObject")};

	public static class Data {
		public Object _obj;
		public Object[] _objArr;

		public Data(Object obj, Object[] obj2) {
			this._obj = obj;
			this._objArr = obj2;
		}
	}
	
	protected void store(){
		Data data = new Data(ARRAY,ARRAY);
		db().store(data);
	}
	
	public void testRetrieve(){
		Data data=(Data)retrieveOnlyInstance(Data.class);
		Assert.isTrue(data._obj instanceof AtomData[],"Expected instance of "+AtomData[].class+", but got "+data._obj);
		Assert.isTrue(data._objArr instanceof AtomData[],"Expected instance of "+AtomData[].class+", but got "+data._objArr);
		ArrayAssert.areEqual(ARRAY,data._objArr);
		ArrayAssert.areEqual(ARRAY,(AtomData[])data._obj);
	}
}
