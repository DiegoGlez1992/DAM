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
package com.db4o.db4ounit.common.soda.experiments;
// JDK 1.4.x only
// import java.util.regex.*;

import com.db4o.db4ounit.common.soda.*;
import com.db4o.db4ounit.common.soda.classes.simple.*;
import com.db4o.db4ounit.common.soda.classes.typedhierarchy.*;
import com.db4o.db4ounit.common.soda.wrapper.untyped.*;
import com.db4o.query.*;







// dependant on the previous run of some other test classes
public class STMagicTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase implements STInterface {

	public String str;

	public STMagicTestCase() {
	}

	private STMagicTestCase(String str) {
		this.str = str;
	}

	public String toString() {
		return "STMagicTestCase: " + str;
	}

	/** needed for STInterface test */
	public Object returnSomething() {
		return str;
	}

	public Object[] createData() {
		return new Object[] { new STMagicTestCase("aaa"), new STMagicTestCase("aaax")};
	}

	/** 
	 * Magic: 
	 * Query for all objects with a known attribute, 
	 * independant of the class or even if you don't
	 * know the class.
	 */
	public void testUnconstrainedClass() {
		Query q = newQuery();
		q.descend("str").constrain("aaa");
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(
			q,
			new Object[] { new STMagicTestCase("aaa"), new STStringTestCase("aaa"), new STStringUTestCase("aaa")});
	}

	/**
	 * Magic:
	 * Query for multiple classes.
	 * Every class gets it's own slot in the query graph.
	 */
	public void testMultiClass() {
		Query q = newQuery();
		q.constrain(STDoubleTestCase.class).or(q.constrain(STStringTestCase.class));
		Object[] stDoubles = new STDoubleTestCase().createData();
		Object[] stStrings = new STStringTestCase().createData();
		Object[] res = new Object[stDoubles.length + stStrings.length];
		System.arraycopy(stDoubles, 0, res, 0, stDoubles.length);
		System.arraycopy(stStrings, 0, res, stDoubles.length, stStrings.length);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(q, res);
	}

	/**
	 * Magic:
	 * Execute any node in the query graph.
	 * The data for this example can be found in STTH1.java.
	 */
	public void testExecuteAnyNode() {
		Query q = newQuery();
		q.constrain(new STTH1TestCase().createData()[5]);
		q = q.descend("h2").descend("h3");
		//	We only get one STTH3 here, because the query is
		//	constrained by the STTH2 with the "str2" member.
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STTH3("str3"));
	}

	/**
	 * Magic:
	 * Querying with regular expression by using an Evaluation callback.
	 * 
	 * This test needs JDK 1.4.x java.util.regex.*;
	 * It's uncommented to allow compilation on JDKs 1.2.x and 1.3.x
	 */
//	public void testRegularExpression() {
//		Query q = newQuery();
//		q.constrain(STMagicTestCase.class);
//		Query qStr = q.descend("str");
//		final Pattern pattern = Pattern.compile("a*x");
//		qStr.constrain(new Evaluation() {
//			public void evaluate(Candidate candidate) {
//				candidate.include(pattern.matcher(((String) candidate.getObject())).matches());
//			}
//		});
//		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[1]);
//	}

	/**
	 * Magic:
	 * Querying for an implemented Interface.
	 * Using an Evaluation allows calls to the interface methods
	 * during the run of the query.s
	 */
	public void testInterface() {
		Query q = newQuery();
		q.constrain(STInterface.class);
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STInterface sti = (STInterface) candidate.getObject();
				candidate.include(sti.returnSomething().equals("aaa"));
			}
		});
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(q, new Object[] { new STMagicTestCase("aaa"), new STStringTestCase("aaa")});
	}
	

}
