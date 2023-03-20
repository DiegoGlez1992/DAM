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
package com.db4o.test.nativequery.analysis;

import java.util.Date;

import com.db4o.activation.*;
import com.db4o.ta.*;

class Data extends Base {
	boolean bool;
	float value;
	float otherValue;
	String name;
	Data next;
	int[] intArray;
	Data[] objArray;
	Boolean boolWrapper;
	Date date;
	
	private int secret;
	
	public boolean getBool() {
		return bool;
	}
	
	public float getValue() {
		return value;
	}
	public float getValue(int times) {
		return otherValue;
	}
	public String getName() {
		return name;
	}
	public Data getNext() {
		return next;
	}
	
	public boolean hasNext() {
		return getNext()!=null;
	}

	public Date getDate() {
		return date;
	}
	
	public void someMethod() {
		System.out.println();
	}

	public boolean sameSecret(Data other) {
		return secret == other.secret;
	}
	
	public void activate(ActivationPurpose purpose) {
	}

	public void activate() {
		activate(ActivationPurpose.READ);
	}
	
	public void activate(String str) {
	}
	
	public static void activate(Activatable act) {
		act.activate(ActivationPurpose.READ);
	}
}
