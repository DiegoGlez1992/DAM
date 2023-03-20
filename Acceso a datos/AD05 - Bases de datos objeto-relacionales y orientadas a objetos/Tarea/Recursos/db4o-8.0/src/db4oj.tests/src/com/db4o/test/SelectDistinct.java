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

import com.db4o.*;
import com.db4o.query.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class SelectDistinct {

    public String name;

    public SelectDistinct() {
    }

    public SelectDistinct(String name) {
        this.name = name;
    }

    public void store() {
        Test.store(new SelectDistinct("a"));
        Test.store(new SelectDistinct("a"));
        Test.store(new SelectDistinct("a"));
        Test.store(new SelectDistinct("b"));
        Test.store(new SelectDistinct("b"));
        Test.store(new SelectDistinct("c"));
        Test.store(new SelectDistinct("c"));
        Test.store(new SelectDistinct("d"));
        Test.store(new SelectDistinct("e"));
    }

    public void test() {

        String[] expected = new String[] { "a", "b", "c", "d", "e"};

        Query q = Test.query();
        q.constrain(SelectDistinct.class);
        q.constrain(new Evaluation() {

            private Hashtable ht = new Hashtable();

            public void evaluate(Candidate candidate) {
                SelectDistinct sd = (SelectDistinct) candidate.getObject();
                boolean isDistinct = ht.get(sd.name) == null;
                candidate.include(isDistinct);
                if (isDistinct) {
                    ht.put(sd.name, new Object());
                }

            }
        });

        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            SelectDistinct sd = (SelectDistinct) objectSet.next();
            boolean found = false;
            for (int i = 0; i < expected.length; i++) {
                if (sd.name.equals(expected[i])) {
                    expected[i] = null;
                    found = true;
                    break;
                }
            }
            Test.ensure(found);
        }

        for (int i = 0; i < expected.length; i++) {
            Test.ensure(expected[i] == null);
        }
    }
}