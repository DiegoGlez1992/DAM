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


/**
 * @exclude
 */
public class HashtableIterator implements Iterator4 {
	
	private final HashtableIntEntry[] _table;
	
	private HashtableIntEntry _currentEntry;
	
	private int _currentIndex;
	
	public HashtableIterator(HashtableIntEntry[] table) {
		_table = table;
		reset();
	}
	
	private void checkInvalidTable(){
		if(_table == null || _table.length == 0){
			positionBeyondLast();
		}
	}

	public Object current() {
		if (_currentEntry == null) {
			throw new IllegalStateException();
		}
		return _currentEntry;
	}

	public boolean moveNext() {
		if(isBeyondLast()){
			return false;
		}
		if(_currentEntry != null){
			_currentEntry = _currentEntry._next;
		}
		while(_currentEntry == null){
			if(_currentIndex >= _table.length){
				positionBeyondLast();
				return false;
			}
			_currentEntry = _table[_currentIndex++];
		}
		return true;
	}

	public void reset() {
		_currentEntry = null;
		_currentIndex = 0;
		checkInvalidTable();
	}
	
	private boolean isBeyondLast(){
		return _currentIndex == -1;
	}
	
	private void positionBeyondLast(){
		_currentIndex = -1;		
	}

}
