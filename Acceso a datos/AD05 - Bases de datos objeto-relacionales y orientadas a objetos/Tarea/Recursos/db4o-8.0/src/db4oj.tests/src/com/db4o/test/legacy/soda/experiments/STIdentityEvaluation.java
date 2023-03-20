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
package com.db4o.test.legacy.soda.experiments;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;


public class STIdentityEvaluation implements STClass1{
    
    public static transient SodaTest st;
    
    public Object[] store() {
        
        Helper helperA = new Helper("aaa");
        
        return new Object[] {
            new STIdentityEvaluation(null),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(helperA),
            new STIdentityEvaluation(new HelperDerivate("bbb")),
            new STIdentityEvaluation(new Helper("dod"))
            };
    }
    
    public Helper helper;
    
    public STIdentityEvaluation(){
    }
    
    public STIdentityEvaluation(Helper h){
        this.helper = h;
    }
    
    public void test(){
        Query q = st.query();
        Object[] r = store();
        q.constrain(new Helper("aaa"));
        ObjectSet os = q.execute();
        Helper helperA = (Helper)os.next();
        q = st.query();
        q.constrain(STIdentityEvaluation.class);
        q.descend("helper").constrain(helperA).identity();
        q.constrain(new IncludeAllEvaluation());
        st.expect(q,new Object[]{r[1], r[2], r[3]});
    }
    
    // FIXME: the SodaQueryComparator changes seem to have broken this
    public void _testMemberClassConstraint(){
        Query q = st.query();
        Object[] r = store();
        q.constrain(STIdentityEvaluation.class);
        q.descend("helper").constrain(HelperDerivate.class);
        st.expect(q,new Object[]{r[4]});
    }
    
    public static class Helper{
        
        public String hString;
        
        public Helper(){
        }
        
        public Helper(String str){
            hString = str;
        }
    }
    
    public static class HelperDerivate extends Helper{
        public HelperDerivate(){
        }
        
        public HelperDerivate(String str){
            super(str);
        }
        
    }
    
    public static class IncludeAllEvaluation implements Evaluation {
        public void evaluate(Candidate candidate) {
            candidate.include(true);
        }
    } 

    
}
