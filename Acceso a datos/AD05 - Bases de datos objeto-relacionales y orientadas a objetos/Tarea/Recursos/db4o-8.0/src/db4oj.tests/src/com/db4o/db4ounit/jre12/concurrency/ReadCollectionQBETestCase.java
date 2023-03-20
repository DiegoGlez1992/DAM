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
package com.db4o.db4ounit.jre12.concurrency;

import java.util.*;

import com.db4o.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ReadCollectionQBETestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new ReadCollectionQBETestCase().runConcurrency();
	}
	
	private static String testString = "simple test string";
	
	private static int LIST_SIZE = 100;

	public List list = new ArrayList();

	protected void store() throws Exception {
		for (int i = 0; i < LIST_SIZE; i++) {
			SimpleObject o = new SimpleObject(testString + i, i);
			list.add(o);
		}
		store(list);
	}

	public void concReadCollection(ExtObjectContainer oc) throws Exception {
		ObjectSet result = oc.queryByExample(new ArrayList());
		Assert.areEqual(1, result.size());
		List resultList = (List) result.next();
		Assert.areEqual(list, resultList);
	}
}
