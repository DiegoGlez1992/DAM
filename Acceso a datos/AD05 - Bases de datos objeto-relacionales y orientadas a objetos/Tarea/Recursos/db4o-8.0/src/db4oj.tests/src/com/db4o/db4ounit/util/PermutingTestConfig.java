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
package com.db4o.db4ounit.util;

public class PermutingTestConfig {

	private Object[][] _values;
	private int[] _indices;
	private boolean _started;
	
	public PermutingTestConfig(Object[][] values) {
		_values=values;
		_indices=new int[_values.length];
		_started=false;
	}
	
	public boolean moveNext() {
		if(!_started) {
			_started=true;
			return true;
		}
		for(int groupIdx=_indices.length-1;groupIdx>=0;groupIdx--) {
			if(_indices[groupIdx]<_values[groupIdx].length-1) {
				_indices[groupIdx]++;
				for(int resetGroupIdx=groupIdx+1;resetGroupIdx<_indices.length;resetGroupIdx++) {
					_indices[resetGroupIdx]=0;
				}
				return true;
			}
		}
		return false;
	}
	
	public Object current(int groupIdx) throws IllegalStateException,IllegalArgumentException {
		if(!_started) {
			throw new IllegalStateException();
		}
		if(groupIdx<0||groupIdx>=_indices.length) {
			throw new IllegalArgumentException();
		}
		return _values[groupIdx][_indices[groupIdx]];
	}
}