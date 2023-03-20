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
package com.db4o.test.legacy.soda.joins.untyped;

import java.util.*;

import com.db4o.test.legacy.soda.*;

public class STAndBooleanDate {
	
	public static transient SodaTest st;
	
	boolean shipped;
	Date dateOrdered;
	
	public STAndBooleanDate(){
	}
	
	public STAndBooleanDate(boolean shipped, int year, int month, int day){
		this.shipped = shipped;
		this.dateOrdered = new GregorianCalendar(year, month - 1, day).getTime();
	}
	
	public Object[] store() {
		return new Object[] {
			new STAndBooleanDate(false, 2002, 11, 1),
			new STAndBooleanDate(false, 2002, 12, 3),
			new STAndBooleanDate(false, 2002, 12, 5),
			new STAndBooleanDate(true, 2002, 11, 3),
			new STAndBooleanDate(true, 2002, 12, 4),
			new STAndBooleanDate(true, 2002, 12, 6)
			};
	}
	
	
	
	

}
