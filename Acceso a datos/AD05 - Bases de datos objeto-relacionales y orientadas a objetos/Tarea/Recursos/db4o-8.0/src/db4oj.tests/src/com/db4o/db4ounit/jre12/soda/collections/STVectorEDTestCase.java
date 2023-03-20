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
package com.db4o.db4ounit.jre12.soda.collections;
import java.util.*;

import com.db4o.query.*;



/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class STVectorEDTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {
	
	public static class ExtendVector extends Vector{
	}
	
	protected ExtendVector vec(Object[] objects){
		ExtendVector v = new ExtendVector();
		for (int i = 0; i < objects.length; i++) {
			v.add(objects[i]);
		}
		return v;
	}

	public Object[] createData() {
		return new Object[] {
			vec(new Object[] { new Integer(8778), new Integer(8779)}), 
			vec(new Object[] { new Integer(8778), new Integer(8789)}),
			vec(new Object[] { "foo877", new STElement("bar877", "barbar877")}),
			vec(new Object[] { "foo8772", new STElement("bar877", "barbar2877")})
		};
	}
	
	public void testDefaultContainsInteger() {
		Query q = newQuery();
		
		q.constrain(vec(new Object[] { new Integer(8778)}));
		expect(q, new int[] { 0, 1 });
	}

	public void testDefaultContainsString() {
		Query q = newQuery();
		
		q.constrain(vec(new Object[] { "foo877" }));
		expect(q, new int[] { 2 });
	}

	public void testDefaultContainsTwo() {
		Query q = newQuery();
		
		q.constrain(vec(new Object[] { new Integer(8778), new Integer(8789)}));
		expect(q, new int[] { 1 });
	}

	public void testDefaultContainsObject() {
		Query q = newQuery();
		
		q.constrain(vec(new Object[] { new STElement("bar877", null)}));
		expect(q, new int[] { 2, 3 });
	}
	
}