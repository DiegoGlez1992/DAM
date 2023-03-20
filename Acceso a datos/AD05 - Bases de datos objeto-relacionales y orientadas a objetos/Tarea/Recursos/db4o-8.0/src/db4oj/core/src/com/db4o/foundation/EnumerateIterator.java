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
package com.db4o.foundation;


public class EnumerateIterator extends MappingIterator {
	
	public static final class Tuple {

		public final int index;
		public final Object value;

		public Tuple(int index_, Object value_) {
			index = index_;
			value = value_;
		}
		
	}

	private int _index;

	public EnumerateIterator(Iterator4 iterator) {
		super(iterator);
		_index = 0;
	}
	
	public boolean moveNext() {
		if (super.moveNext()) {
			++_index;
			return true;
		}
		return false;
	}

	protected Object map(Object current) {
		return new Tuple(_index, current);
	}

}
