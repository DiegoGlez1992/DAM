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

public class DifferentAccessPaths {
	
	public String foo;

	public void store(){
		Test.deleteAllInstances(this);
		DifferentAccessPaths dap = new DifferentAccessPaths();
		dap.foo = "hi";
		Test.store(dap);
		dap = new DifferentAccessPaths();
		dap.foo = "hi too";
		Test.store(dap);
	}

	public void test(){
		DifferentAccessPaths dap = query();
		for(int i = 0; i < 10; i ++){
			Test.ensure(dap == query());
		}
		Test.objectContainer().purge(dap);
		Test.ensure(dap != query());
	}

	private DifferentAccessPaths query(){
		Query q = Test.query();
		q.constrain(DifferentAccessPaths.class);
		q.descend("foo").constrain("hi");
		ObjectSet os = q.execute();
		DifferentAccessPaths dap = (DifferentAccessPaths)os.next();
		return dap;
	}

}
