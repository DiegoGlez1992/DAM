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
package com.db4o.db4ounit.jre11.soda.wrapper.typed;
import java.io.*;

import com.db4o.*;
import com.db4o.query.*;


public class STCharWTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase implements Serializable{
	
	final static String DESCENDANT = "i_char";
	
	public Character i_char;
	
	
	public STCharWTTestCase(){
	}
	
	private STCharWTTestCase(char a_char){
		i_char = new Character(a_char);
	}
	
	public Object[] createData() {
		return new Object[]{
			new STCharWTTestCase((char)0),
			new STCharWTTestCase((char)1),
			new STCharWTTestCase((char)99),
			new STCharWTTestCase((char)909)
		};
	}
	
	public void testEquals(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend(DESCENDANT).constrain(new Character((char)0));
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}
	
	public void testNotEquals(){
		Query q = newQuery();
		
		q.constrain(_array[0]);
		q.descend(DESCENDANT).constraints().not();
		expect(q, new int[] {1, 2, 3});
	}
	
	public void testGreater(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)9));
		q.descend(DESCENDANT).constraints().greater();
		
		expect(q, new int[] {2, 3});
	}
	
	public void testSmaller(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)1));
		q.descend(DESCENDANT).constraints().smaller();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}
	
	public void testIdentity(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)1));
		ObjectSet set = q.execute();
		STCharWTTestCase identityConstraint = (STCharWTTestCase)set.next();
		identityConstraint.i_char = new Character((char)9999);
		q = newQuery();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_char = new Character((char)1);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q,_array[1]);
	}
	
	public void testNotIdentity(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)1));
		ObjectSet set = q.execute();
		STCharWTTestCase identityConstraint = (STCharWTTestCase)set.next();
		identityConstraint.i_char = new Character((char)9080);
		q = newQuery();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_char = new Character((char)1);
		
		expect(q, new int[] {0, 2, 3});
	}
	
	public void testConstraints(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase((char)1));
		q.constrain(new STCharWTTestCase((char)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			db4ounit.Assert.fail("Constraints not returned");
		}
	}
	
	public void testEvaluation(){
		Query q = newQuery();
		q.constrain(new STCharWTTestCase());
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STCharWTTestCase sts = (STCharWTTestCase)candidate.getObject();
				candidate.include((sts.i_char.charValue() + 2) > 100);
			}
		});
		
		expect(q, new int[] {2, 3});
	}
	
}

