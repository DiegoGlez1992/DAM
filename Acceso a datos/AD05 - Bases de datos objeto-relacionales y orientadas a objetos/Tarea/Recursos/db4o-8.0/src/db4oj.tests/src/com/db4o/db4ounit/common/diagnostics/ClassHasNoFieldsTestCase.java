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

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClassHasNoFieldsTestCase extends AbstractDb4oTestCase implements CustomClientServerConfiguration {
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.diagnostic().addListener(_collector);
	}
	
	public void configureClient(Configuration config) throws Exception {
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}
	
	public void testDiagnostic() {
		store(new Item());
		
		List<Diagnostic> diagnostics = NativeCollections.filter(
													_collector.diagnostics(),
													new Predicate4<Diagnostic>() {
														public boolean match(Diagnostic candidate) {
															return candidate instanceof ClassHasNoFields;
														}
													});
		Assert.areEqual(1, diagnostics.size());
		
		ClassHasNoFields diagnostic =  (ClassHasNoFields) diagnostics.get(0);
		Assert.areEqual(ReflectPlatform.fullyQualifiedName(Item.class), diagnostic.reason());
	}
	
	private DiagnosticCollector _collector = new DiagnosticCollector();
	
	static public class Item {	
	}
}

