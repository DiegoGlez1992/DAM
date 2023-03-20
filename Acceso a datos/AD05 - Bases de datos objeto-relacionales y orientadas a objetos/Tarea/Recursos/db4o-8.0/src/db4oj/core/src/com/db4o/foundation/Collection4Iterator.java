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
public class Collection4Iterator extends Iterator4Impl {

	private final Collection4 _collection;
	private final int _initialVersion;

	public Collection4Iterator(Collection4 collection, List4 first) {
		super(first);
		_collection = collection;
		_initialVersion = currentVersion();
	}
	
	public boolean moveNext() {
		validate();
		return super.moveNext();
	}
	
	public Object current() {
		validate();
		return super.current();
	}

	private void validate() {
		if (_initialVersion != currentVersion()) {
			// FIXME: change to ConcurrentModificationException
			throw new InvalidIteratorException();
		}
	}
	
	private int currentVersion() {
		return _collection.version();
	}
}
