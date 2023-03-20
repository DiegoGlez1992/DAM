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
package com.db4o.db4ounit.common.diagnostics;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class DescendIntoTranslatorTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).translate(new TItem());
		config.diagnostic().addListener(_collector);
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item("foo"));
	}
	
	public void testDiagnostic() {
		Query query = newQuery(Item.class);
		query.descend("_name").constrain("foo").startsWith(true);
		query.execute();
		
		List<Diagnostic> diagnostics = NativeCollections.filter(
													_collector.diagnostics(),
													new Predicate4<Diagnostic>() {
														public boolean match(Diagnostic candidate) {
															return candidate instanceof DescendIntoTranslator;
														}
													});
		Assert.areEqual(1, diagnostics.size());
		
		DescendIntoTranslator diagnostic =  (DescendIntoTranslator) diagnostics.get(0);
		Assert.areEqual(ReflectPlatform.fullyQualifiedName(Item.class) + "." + "_name", diagnostic.reason());
	}
	
	public static class Item {	
		public Item(String name) {
			_name = name;
		}

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			_name = name;
		}
		
		private String _name ;
	}

	public static class TItem implements ObjectTranslator {

		public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
			Item item = (Item) applicationObject;
			item.setName((String) storedObject);
		}

		public Object onStore(ObjectContainer container, Object applicationObject) {
			String name = ((Item) applicationObject).getName();
			return name;
		}

		public Class storedClass() {
			return String.class;
		}
	}	
	
	private DiagnosticCollector _collector = new DiagnosticCollector();
}