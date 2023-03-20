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

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

/**
 * 
 */
public class ParameterizedEvaluation implements Serializable {
    
    public String str;
    
    public void store(){
        Test.deleteAllInstances(this);
        store("one");
        store("fun");
        store("ton");
        store("sun");
    }
    
    private void store(String str){
        ParameterizedEvaluation pe = new ParameterizedEvaluation();
        pe.str = str;
        Test.store(pe);
    }
    
    public void test(){
        Test.ensure(queryContains("un").size() == 2);
    }
    
    private ObjectSet queryContains(final String str){
        Query q = Test.query();
        q.constrain(ParameterizedEvaluation.class);
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                ParameterizedEvaluation pe = (ParameterizedEvaluation)candidate.getObject();
                boolean inc = pe.str.indexOf(str) != -1;
                candidate.include(inc);
            }
        });
        
        return q.execute();
    }

}
