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

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class SodaEvaluation {
	
	public SodaEvaluation child;
	public String name;
	
	public void store(){
		Test.deleteAllInstances(this);
		name = "one";
		Test.store(this);
		SodaEvaluation se = new SodaEvaluation();
		se.child= new SodaEvaluation();
		se.child.name = "three";
		se.name = "two";
		Test.store(se);
		
	}
	
	public void test(){
		final String nameConstraint = "three"; 
		Query q = Test.query();
		Query cq = q;
		q.constrain(this.getClass());
		cq = cq.descend("child");
		cq.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
            	candidate.include(((SodaEvaluation)candidate.getObject()).name.equals(nameConstraint));
            }
        });
        ObjectSet os = q.execute();
        Test.ensure(os.size() == 1);
        SodaEvaluation se = (SodaEvaluation)os.next();
        Test.ensure(se.name.equals("two"));
		
	}

}
