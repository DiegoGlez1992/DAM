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

public class HidingField {

    public String name;

    public void store() {
        ExtendHidingField ehf = new ExtendHidingField();
        ehf.name = "child";
        ehf.setParentName("parent");
        Test.store(ehf);
    }

    public void test() {
        Query q = Test.query();
        q.constrain(ExtendHidingField.class);
        q.descend("name").constrain("child");
        ObjectSet objectSet = q.execute();
        System.out.println(objectSet.size());
        while (objectSet.hasNext()) {
            System.out.println(objectSet.next());
        }

        q = Test.query();
        q.constrain(ExtendHidingField.class);
        q.constrain(new Evaluation() {

            public void evaluate(Candidate candidate) {
                ExtendHidingField ehf = (ExtendHidingField) candidate
                        .getObject();
                candidate.include("child".equals(ehf.name));
            }
        });
        objectSet = q.execute();
        System.out.println(objectSet.size());
        while (objectSet.hasNext()) {
            System.out.println(objectSet.next());
        }
    }

    public void setParentName(String name) {
        this.name = name;
    }

    public String toString() {
        return "HidingField " + name;
    }

    public static class ExtendHidingField extends HidingField {

        public String name;

        public String toString() {
            return super.toString() + " ExtendHidingField " + name;
        }
    }
}
