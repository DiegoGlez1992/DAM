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
package com.db4o.db4ounit.jre12.collections.transparent.set;

import java.util.*;

import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableSetAPITestSuite extends FixtureBasedTestSuite implements Db4oTestCase {

	private static FixtureVariable<CollectionSpec<Set<CollectionElement>>> SET_SPEC =
		new FixtureVariable<CollectionSpec<Set<CollectionElement>>>("set");

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider(SET_SPEC,
						new CollectionSpec<HashSet<CollectionElement>>(
								HashSet.class, 
								CollectionFactories.activatableHashSetFactory(),
								CollectionFactories.plainHashSetFactory())  ,
						new CollectionSpec<TreeSet<CollectionElement>>(
										TreeSet.class, 
										CollectionFactories.activatableTreeSetFactory(),
										CollectionFactories.plainTreeSetFactory())
				),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				ActivatableSetAPITestUnit.class
		};
	}
	
	public static class ActivatableSetAPITestUnit extends ActivatableCollectionAPITestUnit<Set<CollectionElement>> {
		@Override
		protected CollectionSpec<Set<CollectionElement>> currentCollectionSpec() {
			return SET_SPEC.value();
		}
	}
}
