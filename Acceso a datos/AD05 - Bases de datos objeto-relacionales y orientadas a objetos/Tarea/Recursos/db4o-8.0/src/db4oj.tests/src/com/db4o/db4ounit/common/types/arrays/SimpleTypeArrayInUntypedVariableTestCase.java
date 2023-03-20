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

import db4ounit.*;
import db4ounit.extensions.*;

public class SimpleTypeArrayInUntypedVariableTestCase extends AbstractDb4oTestCase {
    
	private static final int[] ARRAY = {1, 2, 3};

	public static class Data {
		public Object _arr;

		public Data(Object arr) {
			this._arr = arr;
		}
	}
	
    protected void store(){
    	db().store(new Data(ARRAY));
    }
    
    public void testRetrieval(){
    	Data data=(Data)retrieveOnlyInstance(Data.class);
    	Assert.isTrue(data._arr instanceof int[]);
        int[] arri = (int[])data._arr;
        ArrayAssert.areEqual(ARRAY,arri);
    }
}
