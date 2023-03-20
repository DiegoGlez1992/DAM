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

import java.math.*;

import com.db4o.*;
import com.db4o.query.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class StoreBigDecimal {
	public BigDecimal _bd;

	public void configure() {
		Db4o.configure().objectClass(BigInteger.class).storeTransientFields(true); // needed for JDK1.3
		Db4o.configure().objectClass(BigDecimal.class).storeTransientFields(true); // needed for JDK5
	}
	
	public void store() {
		StoreBigDecimal stored=new StoreBigDecimal();
		stored._bd=new BigDecimal("111.11");
		Test.store(stored);
	}
	
	public void testOne() {
		Query q=Test.query();
		q.constrain(StoreBigDecimal.class);
		ObjectSet r=q.execute();
		Test.ensureEquals(1, r.size());
		StoreBigDecimal stored=(StoreBigDecimal)r.next();
		Test.ensureEquals(new BigDecimal("111.11"),stored._bd);
	}
}
