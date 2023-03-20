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
package com.db4o.test.conjunctions;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class ConjunctionsTestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            CJSingleField.class,
            CJChildField.class
        };
    }
    
    private static final int USED = -9999;

    public static void expect(Query q, int[] vals){
        ObjectSet objectSet = q.execute();
        while(objectSet.hasNext()){
            CJHasID cjs = (CJHasID)objectSet.next();
            boolean found = false;
            for (int i = 0; i < vals.length; i++) {
                if(cjs.getID() == vals[i]){
                    found = true;
                    vals[i] = USED;
                    break;
                }
            }
            Test.ensure(found);
        }
        for (int i = 0; i < vals.length; i++) {
            Test.ensure(vals[i] == USED);
        }
    }
    
}
