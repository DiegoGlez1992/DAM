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
public class UpdateCollectionTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new UpdateCollectionTestCase().runConcurrency();
	}

	private static String testString = "simple test string";

	public List list = new ArrayList();

	private static int LIST_SIZE = 100;

	protected void store() throws Exception {

		for (int i = 0; i < LIST_SIZE; i++) {
			SimpleObject o = new SimpleObject(testString + i, i);
			list.add(o);
		}
		store(list);
	}

	public void concUpdateSameElement(ExtObjectContainer oc, int seq)
			throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// find the middle element, by comparing SimpleObject.s
			if ((testString + LIST_SIZE / 2).equals(o.getS())) {
				o.setI(LIST_SIZE + seq);
				found = true;
				break;
			}
		}
		Assert.isTrue(found);
		oc.store(l);
	}

	public void checkUpdateSameElement(ExtObjectContainer oc) throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// find the middle element, by comparing SimpleObject.s
			if ((testString + LIST_SIZE / 2).equals(o.getS())) {
				int i = o.getI();
				Assert.isTrue(LIST_SIZE <= i && i < LIST_SIZE + threadCount());
				found = true;
				break;
			}
		}
		Assert.isTrue(found);

	}

	public void concUpdateDifferentElement(ExtObjectContainer oc, int seq)
			throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		boolean found = false;
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			if ((testString + seq).equals(o.getS())) {
				o.setI(LIST_SIZE + seq);
				oc.store(o);
				found = true;
				break;
			}
		}
		Assert.isTrue(found);

	}

	public void checkUpdateDifferentElement(ExtObjectContainer oc)
			throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			int i = o.getI();
			if(i >= LIST_SIZE) {
				i = i - LIST_SIZE;
			}
			Assert.areEqual(testString + i, o.getS());
		}

	}

	public void concUpdateList(ExtObjectContainer oc, int seq) throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			// set all SimpleObject.i as thread sequence.
			o.setI(seq);
		}
		oc.store(l);

	}

	public void checkUpdateList(ExtObjectContainer oc) throws Exception {

		ObjectSet result = oc.queryByExample(List.class);
		Assert.areEqual(1, result.size());
		List l = (ArrayList) result.next();
		Assert.areEqual(LIST_SIZE, l.size());
		Iterator iter = l.iterator();
		SimpleObject firstElement = (SimpleObject) iter.next();
		int expectedI = firstElement.getI();
		// assert all SimpleObject.i have the same value.
		while (iter.hasNext()) {
			SimpleObject o = (SimpleObject) iter.next();
			Assert.areEqual(expectedI, o.getI());
		}

	}

}
