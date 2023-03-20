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
package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

public class InternStrings {
	public String _name;
	
	public InternStrings() {
		this(null);
	}

	public InternStrings(String name) {
		_name = name;
	}

	public void configure() {
		Db4o.configure().internStrings(true);
	}
	
	public void store() {
        Test.deleteAllInstances(this);
		String name="Foo";
		Test.store(new InternStrings(name));
		Test.store(new InternStrings(name));
	}
	
	public void test() {
		Query query=Test.query();
		query.constrain(getClass());
		ObjectSet result=query.execute();
		Test.ensureEquals(2, result.size());
		InternStrings first=(InternStrings)result.next();
		InternStrings second=(InternStrings)result.next();
		Test.ensure(first._name==second._name);
	}
}
