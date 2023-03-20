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
package com.db4o.db4ounit.common.querying;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeDeleteArray extends AbstractDb4oTestCase {
 
	public static class ArrayElem {

		public String name;
		
		public ArrayElem(String name){
			this.name = name;
		}
	}

	public ArrayElem[] array;
	
	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
	}
	
	protected void store() {
		CascadeDeleteArray cda = new CascadeDeleteArray();
		cda.array = new ArrayElem[] {
			new ArrayElem("one"),
			new ArrayElem("two"),
			new ArrayElem("three"),
		};
		db().store(cda);
	}

	public void test(){
		
		CascadeDeleteArray cda = (CascadeDeleteArray)retrieveOnlyInstance(getClass());
		
		Assert.areEqual(3, countOccurences(ArrayElem.class));
		
		db().delete(cda);
		
		Assert.areEqual(0, countOccurences(ArrayElem.class));
		
		db().rollback();
		
		Assert.areEqual(3, countOccurences(ArrayElem.class));
		
		db().delete(cda);

		Assert.areEqual(0, countOccurences(ArrayElem.class));

		db().commit();

		Assert.areEqual(0, countOccurences(ArrayElem.class));
	}
}
