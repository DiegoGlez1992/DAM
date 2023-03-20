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

import com.db4o.typehandlers.*;

/**
 */
@decaf.Ignore
public class ListTypeHandlerTestUnitBase extends TypeHandlerTestUnitBase {
	
	protected AbstractItemFactory itemFactory() {
		return (AbstractItemFactory) ListTypeHandlerTestVariables.LIST_IMPLEMENTATION.value();
	}
	
	protected TypeHandler4 typeHandler() {
	    return (TypeHandler4) ListTypeHandlerTestVariables.LIST_TYPEHANDER.value();
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

	protected ListTypeHandlerTestElementsSpec elementsSpec() {
		return (ListTypeHandlerTestElementsSpec) ListTypeHandlerTestVariables.ELEMENTS_SPEC.value();
	}    


}
