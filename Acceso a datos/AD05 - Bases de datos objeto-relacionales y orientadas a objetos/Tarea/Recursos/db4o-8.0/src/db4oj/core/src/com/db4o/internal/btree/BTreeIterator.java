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
package com.db4o.internal.btree;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class BTreeIterator implements Iterator4{
	
	private final Transaction _transaction;
	
	private final BTree _bTree;
	
	private BTreePointer _currentPointer;
	
	private boolean _beyondEnd;
	
	public BTreeIterator(Transaction trans, BTree bTree){
		_transaction = trans;
		_bTree = bTree;
	}

	public Object current() {
		if(_currentPointer == null){
			throw new IllegalStateException();
		}
		return _currentPointer.key();
	}

	public boolean moveNext() {
		if(_beyondEnd){
			return false;
		}
		if(beforeFirst()){
			_currentPointer = _bTree.firstPointer(_transaction);
		} else {
			_currentPointer = _currentPointer.next();	
		}
		_beyondEnd = (_currentPointer == null);
		return ! _beyondEnd;
	}

	private boolean beforeFirst() {
		return _currentPointer == null;
	}

	public void reset() {
		throw new UnsupportedOperationException();
	}

}
