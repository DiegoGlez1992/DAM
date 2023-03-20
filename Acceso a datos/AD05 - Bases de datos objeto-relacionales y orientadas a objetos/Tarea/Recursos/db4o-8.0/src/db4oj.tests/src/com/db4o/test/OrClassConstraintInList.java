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

import java.util.*;

import com.db4o.query.*;


/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class OrClassConstraintInList {
    
    int cnt;
    List list;
    
    public void store(){
        OrClassConstraintInList occ = new OrClassConstraintInList();
        occ.list = newLinkedList();
        occ.cnt = 0;
        occ.list.add(new Atom());
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.list = newLinkedList();
        occ.cnt = 1;
        occ.list.add(new Atom());
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.cnt = 1;
        occ.list = newLinkedList();
        Test.store(occ);
        occ = new OrClassConstraintInList();
        occ.cnt = 2;
        occ.list = newLinkedList();
        occ.list.add(new OrClassConstraintInList());
        Test.store(occ);
    }
    
    private List newLinkedList() {
    	return new LinkedList();
    }

	public void test(){
        Query q = Test.query();
        q.constrain(OrClassConstraintInList.class);
        Constraint c1 = q.descend("list").constrain(Atom.class);
        Constraint c2 = q.descend("cnt").constrain(new Integer(1));
        c1.or(c2);
        Test.ensure(q.execute().size() == 3);
    }
}
