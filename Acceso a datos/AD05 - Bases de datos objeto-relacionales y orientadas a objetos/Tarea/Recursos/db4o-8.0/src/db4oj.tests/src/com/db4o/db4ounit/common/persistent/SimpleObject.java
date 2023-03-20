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
package com.db4o.db4ounit.common.persistent;

/*
 * Simple class for test
 */
public class SimpleObject {

	public String _s;

	public int _i;
	
	public SimpleObject(String s, int i) {
		_s = s;
		_i = i;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleObject)) {
			return false;
		}
		SimpleObject another = (SimpleObject) obj;
		return _s.equals(another._s) && (_i == another._i);

	}

	public int getI() {
		return _i;
	}

	public void setI(int i) {
		_i = i;
	}


	public String getS() {
		return _s;
	}

	public void setS(String s) {
		_s = s;
	}
	
	public String toString() {
		return _s+":"+_i;
	}
}
