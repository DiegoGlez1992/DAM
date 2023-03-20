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

public class GreaterOrEqual {

    public int val;

    public GreaterOrEqual() {

    }

    public GreaterOrEqual(int val) {
        this.val = val;
    }

    public void store() {
        Test.store(new GreaterOrEqual(1));
        Test.store(new GreaterOrEqual(2));
        Test.store(new GreaterOrEqual(3));
        Test.store(new GreaterOrEqual(4));
        Test.store(new GreaterOrEqual(5));
    }

    public void test() {
        int[] expect = {3,4,5};
		Query q = Test.query();
		q.constrain(GreaterOrEqual.class);
		q.descend("val").constrain(new Integer(3)).greater().equal();
		ObjectSet res = q.execute();
		while(res.hasNext()){
		    GreaterOrEqual r = (GreaterOrEqual)res.next();
		    for (int i = 0; i < expect.length; i++) {
		        if(expect[i] == r.val){
		            expect[i] = 0;
		        }
            }
		}
		for (int i = 0; i < expect.length; i++) {
		    Test.ensure(expect[i] == 0);
		}
    }

}
