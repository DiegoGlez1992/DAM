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
package com.db4o.db4ounit.common.soda.classes.typedhierarchy;
import com.db4o.query.*;


/** ETH: Extends Typed Hierarchy */
public class STETH1TestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public String foo1;
	
	public STETH1TestCase(){
	}
	
	public STETH1TestCase(String str){
		foo1 = str;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STETH1TestCase(),
			new STETH1TestCase("str1"),
			new STETH2(),
			new STETH2("str1", "str2"),
			new STETH3(),
			new STETH3("str1a", "str2", "str3"),
			new STETH3("str1a", "str2a", null),
			new STETH4(),
			new STETH4("str1a", "str2", "str4"),
			new STETH4("str1b", "str2a", "str4")
		};
	}
	
	public void testStrNull(){
		Query q = newQuery();
		q.constrain(new STETH1TestCase());
		q.descend("foo1").constrain(null);
		
		expect(q, new int[] {0, 2, 4, 7});
	}

	public void testTwoNull(){
		Query q = newQuery();
		q.constrain(new STETH1TestCase());
		q.descend("foo1").constrain(null);
		q.descend("foo3").constrain(null);
		
		expect(q, new int[] {0, 2, 4, 7});
	}
	
	public void testClass(){
		Query q = newQuery();
		q.constrain(STETH2.class);
		
		expect(q, new int[] {2, 3, 4, 5, 6, 7, 8, 9});
	}
	
	public void testOrClass(){
		Query q = newQuery();
		q.constrain(STETH3.class).or(q.constrain(STETH4.class));
		
		expect(q, new int[] {4, 5, 6, 7, 8, 9});
	}
	
	public void testAndClass(){
		Query q = newQuery();
		q.constrain(STETH1TestCase.class);
		q.constrain(STETH4.class);
		
		expect(q, new int[] {7, 8, 9});
	}
	
	public void testParalellDescendantPaths(){
		Query q = newQuery();
		q.constrain(STETH3.class).or(
		q.constrain(STETH4.class));
		q.descend("foo3").constrain("str3").or(
		q.descend("foo4").constrain("str4"));
		
		expect(q, new int[] {5, 8, 9});
	}
	
	public void testOrObjects(){
		Query q = newQuery();
		
		q.constrain(_array[3]).or(q.constrain(_array[5]));
		expect(q, new int[] {3, 5});
	}
	
}

