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

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 */
@decaf.Ignore
public class MapTypeHandlerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase  {

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[]{
				new Db4oFixtureProvider(),
				MapTypeHandlerTestVariables.MAP_FIXTURE_PROVIDER,
				MapTypeHandlerTestVariables.MAP_KEYS_PROVIDER,
				MapTypeHandlerTestVariables.MAP_VALUES_PROVIDER,
				MapTypeHandlerTestVariables.TYPEHANDLER_FIXTURE_PROVIDER,
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[]{
			MapTypeHandlerUnitTestCase.class,
		};
	}
	
	public static class MapTypeHandlerUnitTestCase extends CollectionTypeHandlerUnitTest {
		
		protected void fillItem(Object item) {
			fillMapItem(item);
		}

		protected void assertContent(Object item) {
			assertMapContent(item);
		}

		protected void assertPlainContent(Object item) {
			assertPlainMapContent((Map) item);
		}

		protected AbstractItemFactory itemFactory() {
			return (AbstractItemFactory) MapTypeHandlerTestVariables.MAP_IMPLEMENTATION.value();
		}
		
		protected TypeHandler4 typeHandler() {
		    return (TypeHandler4) MapTypeHandlerTestVariables.MAP_TYPEHANDER.value();
		}
		
		protected ListTypeHandlerTestElementsSpec elementsSpec() {
			return (ListTypeHandlerTestElementsSpec) MapTypeHandlerTestVariables.MAP_KEYS_SPEC.value();
		}

		protected void assertCompareItems(Object element, boolean successful) {
			Query q = newQuery();
	    	Object item = itemFactory().newItem();
	    	Map map = mapFromItem(item);
			map.put(element, values()[0]);
	    	q.constrain(item);
			assertQueryResult(q, successful);
		}    
		
	}

}
