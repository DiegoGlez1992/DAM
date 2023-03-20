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
package com.db4o.internal.fieldindex;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;

final class IndexedPathIterator extends CompositeIterator4 {
	
	private IndexedPath _path;
	
	public IndexedPathIterator(IndexedPath path, Iterator4 iterator) {
		super(iterator);
		_path = path;
	}

	protected Iterator4 nextIterator(final Object current) {
		final FieldIndexKey key = (FieldIndexKey) current;
		return _path.search(new Integer(key.parentID())).keys();
	}

}