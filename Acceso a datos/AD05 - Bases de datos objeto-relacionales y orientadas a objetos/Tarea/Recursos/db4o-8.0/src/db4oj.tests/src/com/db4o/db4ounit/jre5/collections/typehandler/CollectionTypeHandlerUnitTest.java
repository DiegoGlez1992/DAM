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
package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore
public abstract class CollectionTypeHandlerUnitTest extends TypeHandlerTestUnitBase {

	protected abstract void assertCompareItems(Object element, boolean successful);

	protected void assertQuery(boolean successful, Object element, boolean withContains) {
		Query q = newQuery(itemFactory().itemClass());
		Constraint constraint = q.descend(itemFactory().fieldName()).constrain(element);
		if(withContains) {
			constraint.contains();
		}
		assertQueryResult(q, successful);
	}
	
	public void testRetrieveInstance() {
	    Object item = retrieveItemInstance();
	    assertContent(item);
	}

    protected Object retrieveItemInstance() {
        Class itemClass = itemFactory().itemClass();
	    Object item = retrieveOnlyInstance(itemClass);
        return item;
    }
	
	public void testDefragRetrieveInstance() throws Exception {
		defragment();
	    Object item = retrieveItemInstance();
	    assertContent(item);
	}

	public void testSuccessfulQuery() throws Exception {
		assertQuery(true, elements()[0], false);
	}

	public void testFailingQuery() throws Exception {
		assertQuery(false, notContained(), false);
	}

	public void testSuccessfulContainsQuery() throws Exception {
		assertQuery(true, elements()[0], true);
	}

	public void testFailingContainsQuery() throws Exception {
		assertQuery(false, notContained(), true);
	}

	public void testCompareItems() throws Exception {
		assertCompareItems(elements()[0], true);
	}

	public void testFailingCompareItems() throws Exception {
		assertCompareItems(notContained(), false);
	}

	public void testDeletion() throws Exception {
	    assertReferenceTypeElementCount(elements().length);
	    Object item = retrieveOnlyInstance(itemFactory().itemClass());
	    db().delete(item);
	    db().purge();
	    Db4oAssert.persistedCount(0, itemFactory().itemClass());
	    assertReferenceTypeElementCount(0);
	}

	public void testJoin() {
		Query q = newQuery(itemFactory().itemClass());
		q.descend(itemFactory().fieldName()).constrain(elements()[0])
			.and(q.descend(itemFactory().fieldName()).constrain(elements()[1]));
		assertQueryResult(q, true);
	}

	public void testSubQuery() {
		Query q = newQuery(itemFactory().itemClass());
		Query qq = q.descend(itemFactory().fieldName());
		qq.constrain(elements()[0]);
		ObjectSet set = qq.execute();
    	Assert.areEqual(1, set.size());
    	assertPlainContent(set.next());
	}
	
	protected void assertReferenceTypeElementCount(int expected) {
		if(!isReferenceElement(elementClass())) {
			return;
		}
		Db4oAssert.persistedCount(expected, elementClass());
	}

	private boolean isReferenceElement(Class elementClass) {
		return ListTypeHandlerTestVariables.ReferenceElement.class == elementClass;
	}

}
