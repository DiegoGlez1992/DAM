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
package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public abstract class ActivatableCollectionTestCaseBase<C extends Collection<CollectionElement>> extends AbstractDb4oTestCase {

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}

	@Override
	protected void store() throws Exception {
		C collection = newActivatableCollection();
		CollectionHolder<C> item = new CollectionHolder<C>();
		item._collection = collection;
		store(item);
	}

	protected void assertAreEqual(C elements, C singleList) {
		IteratorAssert.sameContent(elements.iterator(), singleList.iterator());
	}

	protected CollectionHolder<C> singleHolder() {
		return retrieveOnlyInstance(CollectionHolder.class);
	}

	protected C singleCollection() {
		return singleHolder()._collection;
	}

	protected abstract C newActivatableCollection();
}