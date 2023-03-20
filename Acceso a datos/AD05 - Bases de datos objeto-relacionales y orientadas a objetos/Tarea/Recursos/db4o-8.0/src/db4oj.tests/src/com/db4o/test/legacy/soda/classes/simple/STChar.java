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
package com.db4o.test.legacy.soda.classes.simple;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STChar implements STClass1{
	
	final static String DESCENDANT = "i_char";
	
	public static transient SodaTest st;
	
	public char i_char;
	
	public STChar(){
	}
	
	private STChar(char a_char){
		i_char = a_char;
	}
	
	public Object[] store() {
		return new Object[]{
			new STChar((char)0),
			new STChar((char)1),
			new STChar((char)99),
			new STChar((char)909)
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STChar((char)0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend(DESCENDANT).constrain(new Character((char)0));
		st.expectOne(q, store()[0]);
	}
	
	public void testNotEquals(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(r[0]);
		q.descend(DESCENDANT).constrain(new Character((char)0)).not();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testGreater(){
		Query q = st.query();
		q.constrain(new STChar((char)9));
		q.descend(DESCENDANT).constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		q.constrain(new STChar((char)1));
		q.descend(DESCENDANT).constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testIdentity(){
		Query q = st.query();
		q.constrain(new STChar((char)1));
		ObjectSet set = q.execute();
		STChar identityConstraint = (STChar)set.next();
		identityConstraint.i_char = 9999;
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_char = 1;
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		q.constrain(new STChar((char)1));
		ObjectSet set = q.execute();
		STChar identityConstraint = (STChar)set.next();
		identityConstraint.i_char = 9080;
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_char = 1;
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STChar((char)1));
		q.constrain(new STChar((char)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
}

