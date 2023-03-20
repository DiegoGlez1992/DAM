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
package com.db4o.test.legacy;

import com.db4o.test.*;

public class ArrayNOrder {

    public String[][][] s1;
    public Object[][] o1;

    public void store() {
    	Test.deleteAllInstances(this);
        s1 = new String[2][2][3];
        s1[0][0][0] = "000";
		s1[0][0][1] = "001";
		s1[0][0][2] = "002";
        s1[0][1][0] = "010";
		s1[0][1][1] = "011";
		s1[0][1][2] = "012";
        s1[1][0][0] = "100";
		s1[1][0][1] = "101";
		s1[1][0][2] = "102";
        s1[1][1][0] = "110";
		s1[1][1][1] = "111";
		s1[1][1][2] = "112";

        o1 = new Object[2][2];
        o1[0][0] = new Integer(0);
        o1[0][1] = "01";
        o1[1][0] = new Float(10);
        o1[1][1] = new Double(1.1);
        Test.store(this);
    }

    public void test() {
    	ArrayNOrder ano = (ArrayNOrder)Test.getOne(this);
    	ano.check();
    }
    
    public void check(){
		Test.ensure(s1[0][0][0].equals("000"));
		Test.ensure(s1[0][0][1].equals("001"));
		Test.ensure(s1[0][0][2].equals("002"));
		Test.ensure(s1[0][1][0].equals("010"));
		Test.ensure(s1[0][1][1].equals("011"));
		Test.ensure(s1[0][1][2].equals("012"));
		Test.ensure(s1[1][0][0].equals("100"));
		Test.ensure(s1[1][0][1].equals("101"));
		Test.ensure(s1[1][0][2].equals("102"));
		Test.ensure(s1[1][1][0].equals("110"));
		Test.ensure(s1[1][1][1].equals("111"));
		Test.ensure(s1[1][1][2].equals("112"));
		Test.ensure(o1[0][0].equals(new Integer(0)));
		Test.ensure(o1[0][1].equals("01"));
		Test.ensure(o1[1][0].equals(new Float(10)));
		Test.ensure(o1[1][1].equals(new Double(1.1)));
    }
    

}
