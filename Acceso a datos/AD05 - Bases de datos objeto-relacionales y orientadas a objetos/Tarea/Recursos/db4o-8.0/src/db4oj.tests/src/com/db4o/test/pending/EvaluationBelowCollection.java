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
package com.db4o.test.pending;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class EvaluationBelowCollection {	
    public void store(){
	    SimpleNode sub=new SimpleNode("sub",new SimpleNode[0]);
	    SimpleNode sup=new SimpleNode("sup",new SimpleNode[]{sub});
	    Test.store(sup);
	}

	public void test() {
		Query supq=Test.query();
		supq.constrain(SimpleNode.class);
		Query subq=supq.descend("children");
		//subq.constrain(SimpleNode.class);
		subq.descend("name").constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                candidate.include(false);
            }		    
		});
		ObjectSet objectSet = supq.execute();
		Test.ensure(objectSet.size() == 0);
	}
}
