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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InternStringsTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new InternStringsTestCase().runConcurrency();
	}

	public String _name;

	public InternStringsTestCase() {
		this(null);
	}

	public InternStringsTestCase(String name) {
		_name = name;
	}

	protected void configure(Configuration config) {
		config.internStrings(true);
	}

	protected void store() {
		String name = "Foo";
		store(new InternStringsTestCase(name));
		store(new InternStringsTestCase(name));
	}

	public void conc(ExtObjectContainer oc) {
		Query query = oc.query();
		query.constrain(InternStringsTestCase.class);
		ObjectSet result = query.execute();
		Assert.areEqual(2, result.size());
		InternStringsTestCase first = (InternStringsTestCase) result.next();
		InternStringsTestCase second = (InternStringsTestCase) result.next();
		Assert.areSame(first._name, second._name);
	}
}
