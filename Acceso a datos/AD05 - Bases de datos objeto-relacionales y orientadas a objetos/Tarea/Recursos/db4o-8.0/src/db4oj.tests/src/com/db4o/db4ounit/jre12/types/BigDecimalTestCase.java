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
package com.db4o.db4ounit.jre12.types;

import java.math.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class BigDecimalTestCase extends AbstractDb4oTestCase implements OptOutBigMathIssue {
	
	public static void main(String[] args) {
		new BigDecimalTestCase().runAll();
	}
	
	static String DATA = "123456789.1011121314151617181920";
	
	public static class Item {
		public BigDecimal _bigDecimal;
	}
	
	@Override
	protected void store() throws Exception {
		Item item = new Item();
		item._bigDecimal = new BigDecimal(DATA);
		store(item);
	}
	
	public void test(){
		Item item = retrieveOnlyInstance(Item.class);
		Assert.areEqual(DATA, item._bigDecimal.toString());
	}


}
