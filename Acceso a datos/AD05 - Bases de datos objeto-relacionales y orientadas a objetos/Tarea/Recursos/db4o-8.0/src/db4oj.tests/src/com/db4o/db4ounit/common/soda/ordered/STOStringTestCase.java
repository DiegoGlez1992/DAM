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
package com.db4o.db4ounit.common.soda.ordered;
import com.db4o.query.*;


public class STOStringTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {

	public String foo;

    public STOStringTestCase() {
    }

    public STOStringTestCase(String str) {
        this.foo = str;
    }
    
    @Override
    public String toString() {
    	return foo;
    }

    public Object[] createData() {
        return new Object[] {
            new STOStringTestCase(null),
            new STOStringTestCase("bbb"),
            new STOStringTestCase("dod"),
            new STOStringTestCase("aaa"),
            new STOStringTestCase("Xbb"),
            new STOStringTestCase("bbq")};
    }

    public void testAscending() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        q.descend("foo").orderAscending();
        
        expectOrdered(q, new int[] { 0, 4, 3, 1, 5, 2 });
    }

    public void testDescending() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        q.descend("foo").orderDescending();
        
        expectOrdered(q, new int[] { 2, 5, 1, 3, 4, 0 });
    }

    public void testAscendingLike() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        Query qStr = q.descend("foo");
        qStr.constrain("b").like();
        qStr.orderAscending();
        
        expectOrdered(q, new int[] { 4, 1, 5 });
    }

    public void testDescendingContains() {
        Query q = newQuery();
        q.constrain(STOStringTestCase.class);
        Query qStr = q.descend("foo");
        qStr.constrain("b").contains();
        qStr.orderDescending();
        
        expectOrdered(q, new int[] { 5, 1, 4 });
    }
}
