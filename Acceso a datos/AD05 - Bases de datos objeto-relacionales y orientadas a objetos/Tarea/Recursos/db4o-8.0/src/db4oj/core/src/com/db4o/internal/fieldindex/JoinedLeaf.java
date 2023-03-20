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
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.query.processor.*;

public abstract class JoinedLeaf implements IndexedNodeWithRange {

	private final QCon _constraint;
	private final IndexedNodeWithRange _leaf1;
	private final BTreeRange _range;
	
	public JoinedLeaf(final QCon constraint, final IndexedNodeWithRange leaf1, final BTreeRange range) {
		if (null == constraint || null == leaf1 || null == range) {
			throw new ArgumentNullException();
		}
		_constraint = constraint;
		_leaf1 = leaf1;
		_range = range;
	}
	
	public QCon getConstraint() {
		return _constraint;
	}
	
	public BTreeRange getRange() {
		return _range;
	}

	public Iterator4 iterator() {
		return _range.keys();
	}

	public TreeInt toTreeInt() {
		return IndexedNodeBase.addToTree(null, this);
	}

	public BTree getIndex() {
		return _leaf1.getIndex();
	}

	public boolean isResolved() {
		return _leaf1.isResolved();
	}

	public IndexedNode resolve() {
		return IndexedPath.newParentPath(this, _constraint);
	}

	public int resultSize() {
		return _range.size();
	}
	
	public void markAsBestIndex() {
		_leaf1.markAsBestIndex();
		_constraint.setProcessedByIndex();
	}
}