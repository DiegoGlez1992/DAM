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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 */
@decaf.Ignore
@SuppressWarnings("unchecked")
public abstract class TypeHandlerTestUnitBase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
	protected abstract AbstractItemFactory itemFactory();
	
	protected abstract TypeHandler4 typeHandler();
	
	protected abstract void fillItem(Object item);
	
	protected abstract void assertContent(Object item);

	protected abstract void assertPlainContent(Object coll);

	protected abstract ListTypeHandlerTestElementsSpec elementsSpec(); 
	
    protected void configure(Configuration config) throws Exception {
    	TypeHandler4 typeHandler = typeHandler();
    	if(typeHandler != null){
	        config.registerTypeHandler(
	            new SingleClassTypeHandlerPredicate(itemFactory().containerClass()),
	            typeHandler);
    	}
        config.objectClass(itemFactory().itemClass()).cascadeOnDelete(true);
    }
    
	protected void store() throws Exception {
		AbstractItemFactory factory = itemFactory();
        Object item = factory.newItem();
        fillItem(item);
        store(item);
    }
	
	protected int expectedElementCount(){
		return elements().length + 1;
	}

	protected Object[] elements() {
		return elementsSpec()._elements;
	}
	
	protected Object[] values() {
		return valuesSpec()._elements;
	}

	protected Object notContained() {
		return elementsSpec()._notContained;
	}

	protected Object largeElement() {
		return elementsSpec()._largeElement;
	}

	protected Class elementClass() {
		return elementsSpec()._notContained.getClass();
	}
	
	private ListTypeHandlerTestElementsSpec valuesSpec() {
		return (ListTypeHandlerTestElementsSpec) MapTypeHandlerTestVariables.MAP_VALUES_SPEC.value();
	}

	protected void assertQueryResult(Query q, boolean successful) {
		if(successful) {
			assertSuccessfulQueryResult(q);
		}
		else {
			assertEmptyQueryResult(q);
		}
	}
	
	protected List listFromItem(Object item) {
		try {
			return (List) item.getClass().getField(AbstractItemFactory.LIST_FIELD_NAME).get(item);
		} 
		catch (Exception exc) {
			throw new RuntimeException("", exc);
		}
	}
	
	protected Map mapFromItem(Object item) {
		try {
			return (Map) item.getClass().getField(AbstractItemFactory.MAP_FIELD_NAME).get(item);
		} 
		catch (Exception exc) {
			throw new RuntimeException("", exc);
		}
	}

	private void assertEmptyQueryResult(Query q) {
		ObjectSet set = q.execute();
		Assert.areEqual(0, set.size());
	}

	private void assertSuccessfulQueryResult(Query q) {
		ObjectSet set = q.execute();
    	Assert.areEqual(1, set.size());
    	Object item = set.next();
        assertContent(item);
	}

	protected void fillListItem(Object item) {
		List list = listFromItem(item);
	    for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
			list.add(elements()[eltIdx]);
		}
	    list.add(null);
	}

	protected void fillMapItem(Object item) {
		Map map = mapFromItem(item);
	    for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
			map.put(elements()[eltIdx], values()[eltIdx]);
		}
	}

	protected void assertListContent(Object item) {
		assertPlainContent(listFromItem(item));
	}

	protected void assertPlainListContent(List list) {
		Assert.areEqual(itemFactory().containerClass(), list.getClass());
		Assert.areEqual(expectedElementCount(), list.size());
		for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
	        Assert.areEqual(elements()[eltIdx], list.get(eltIdx));
		}
		Assert.isNull(list.get(elements().length));
	}
	
	protected void assertMapContent(Object item) {
		assertPlainMapContent(mapFromItem(item));
	}

	protected void assertPlainMapContent(Map map) {
		Assert.areEqual(itemFactory().containerClass(), map.getClass());
		Assert.areEqual(elements().length, map.size());
		for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
	        Assert.areEqual(values()[eltIdx], map.get(elements()[eltIdx]));
		}
	}

}
