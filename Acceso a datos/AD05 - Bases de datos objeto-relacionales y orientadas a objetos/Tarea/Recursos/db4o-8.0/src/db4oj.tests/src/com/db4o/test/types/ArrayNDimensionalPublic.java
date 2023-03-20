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
package com.db4o.test.types;

public class ArrayNDimensionalPublic extends RTest
{
	public String[][][] s1;
	public Object[][] o1;
	
	public void set(int ver){
		if(ver == 1){
			s1 = new String[2][2][3];
			s1[0][0][0] = "000";
			s1[0][0][1] = "001";
			s1[0][1][0] = "010";
			s1[0][1][1] = "011";
			s1[1][0][0] = "100";
			s1[1][0][1] = "101";
			s1[1][1][0] = "110";
			s1[1][1][1] = "111";
		
			o1 = new Object[2][2];
			o1[0][0] = new Integer(0);
			o1[0][1] = "01";
			o1[1][0] = new Float(10);
			o1[1][1] = new Double(1.1);
		}else{
			s1 = new String[1][2][2];
			s1[0][0][0] = "2000";
			s1[0][1][0] = "2010";
			s1[0][0][1] = "2001";
			s1[0][1][1] = "2011";
		
			o1 = new Object[2][3];
			o1[0][0] = null;
			o1[0][1] = new Integer(1);
			o1[0][2] = new Integer(2);
			o1[1][0] = new Float(10);
			o1[1][1] = new Double(1.1);
			o1[1][2] = new Double(1.2);
		}
	}
}
