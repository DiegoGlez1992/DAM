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
package com.db4o.cs.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class LazyClientIdIterator implements IntIterator4{
	
	private final LazyClientQueryResult _queryResult;
	
	private int _current;
	
	private int[] _ids;
	
	private final int _batchSize;
	
	private int _available;
	
	public LazyClientIdIterator(LazyClientQueryResult queryResult){
		_queryResult = queryResult;
		_batchSize = queryResult.config().prefetchObjectCount();
		_ids = new int[_batchSize];
		_current = -1;
	}

	public int currentInt() {
		if(_current < 0){
			throw new IllegalStateException();
		}
		return _ids[_current];
	}

	public Object current() {
		return new Integer(currentInt());
	}

	public boolean moveNext() {
		if(_available < 0){
			return false;
		}
		if(_available == 0){
			_queryResult.fetchIDs(_batchSize);
			_available --;
			_current = 0;
			return (_available > 0);
		}
		_current++;
		_available --;
		return true;
	}

	public void reset() {
		_queryResult.reset();
		_available = 0;
		_current = -1;
	}

	public void loadFromIdReader(Iterator4 ids) {
		int count = 0;
		while (ids.moveNext()) {
			_ids[count++] = (Integer) ids.current();
		}
		if(count > 0){
			_available = count;
			return;
		}
		_available = -1;
	}

}
