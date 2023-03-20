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

import java.util.*;

@SuppressWarnings("unused")
public class ArrayMixedTypedPrivate extends RTest
{
	private Object[] o1;
	private Object[] o2;
	private Object[] o3;	
	private Object[] o4;
	private Object[] o5;
	private Object[] o6;	

	public void set(int ver){
		if(ver == 1){
			o1 = new Boolean[]{new Boolean(true), new Boolean(false), null };
			o2 = null;
			o3 = new Byte[]{ new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0), null};
			o4 = new Float[] {new Float(Float.MAX_VALUE - 1), new Float(Float.MIN_VALUE), new Float(0), null};
			o5 = new String[] {"db4o rules", "cool", "supergreat"};
			o6 = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
		}else{
			o1 = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
			o2 = null;
			o3 = new String[] {};
			o4 = new Boolean[]{new Boolean(false), new Boolean(true), new Boolean(true)};
			o5 = new Double[]{new Double(Double.MIN_VALUE), new Double(0)};
			o6 = new Object[]{"ohje", new Double(Double.MIN_VALUE), new Float(4), null};
		}
	}
	
	public boolean jdk2(){
		return true;
	}
}
