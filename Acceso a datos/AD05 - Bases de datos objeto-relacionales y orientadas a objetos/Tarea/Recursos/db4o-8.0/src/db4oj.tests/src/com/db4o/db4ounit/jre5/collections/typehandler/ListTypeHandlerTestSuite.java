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

import java.util.*;

import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 */
@decaf.Ignore
@SuppressWarnings("unchecked")
public class ListTypeHandlerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	
	public FixtureProvider[] fixtureProviders() {
		ListTypeHandlerTestElementsSpec[] elementSpecs = {
				ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
				ListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
				ListTypeHandlerTestVariables.OBJECT_ELEMENTS_SPEC,
		};
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			ListTypeHandlerTestVariables.LIST_FIXTURE_PROVIDER,
			new SimpleFixtureProvider(
				ListTypeHandlerTestVariables.ELEMENTS_SPEC,
				(Object[])elementSpecs
			),
			ListTypeHandlerTestVariables.TYPEHANDLER_FIXTURE_PROVIDER, 
		};
	}

	public Class[] testUnits() { 
		return new Class[] {
			ListTypeHandlerTestUnit.class,
		};
	}

	public static class ListTypeHandlerTestUnit extends CollectionTypeHandlerUnitTest {
		
		protected AbstractItemFactory itemFactory() {
			return (AbstractItemFactory) ListTypeHandlerTestVariables.LIST_IMPLEMENTATION.value();
		}
		
		protected TypeHandler4 typeHandler() {
		    return (TypeHandler4) ListTypeHandlerTestVariables.LIST_TYPEHANDER.value();
		}

		protected ListTypeHandlerTestElementsSpec elementsSpec() {
			return (ListTypeHandlerTestElementsSpec) ListTypeHandlerTestVariables.ELEMENTS_SPEC.value();
		}    

		protected void fillItem(Object item) {
			fillListItem(item);
		}
		
		protected void assertContent(Object item) {
			assertListContent(item);
		}

		protected void assertPlainContent(Object item) {
			assertPlainListContent((List) item);
		}
		
	    protected void assertCompareItems(Object element, boolean successful) {
			Query q = newQuery();
	    	Object item = itemFactory().newItem();
	    	List list = listFromItem(item);
			list.add(element);
	    	q.constrain(item);
			assertQueryResult(q, successful);
		}
	    
	    public void testActivation(){
	        Object item = retrieveItemInstance();
	        List list = listFromItem(item);
	        Assert.areEqual(expectedElementCount(), list.size());
	        Object element = list.get(0);
	        if(db().isActive(element)){
	            db().deactivate(item, Integer.MAX_VALUE);
	            Assert.isFalse(db().isActive(element));
	            db().activate(item, Integer.MAX_VALUE);
	            Assert.isTrue(db().isActive(element));
	        }
	    }
		
	}

}
