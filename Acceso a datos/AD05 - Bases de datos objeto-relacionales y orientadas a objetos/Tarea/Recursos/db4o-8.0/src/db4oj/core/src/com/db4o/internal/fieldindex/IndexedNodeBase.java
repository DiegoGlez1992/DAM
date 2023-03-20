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

public abstract class IndexedNodeBase  implements IndexedNode {
	
	protected final QConObject _constraint;

	public IndexedNodeBase(QConObject qcon) {
		if (null == qcon) {
			throw new ArgumentNullException();
		}
		if (null == qcon.getField()) {
			throw new IllegalArgumentException();
		}
        _constraint = qcon;
	}

    public TreeInt toTreeInt() {
    	return addToTree(null, this);
    }
	
	public final BTree getIndex() {
	    return getYapField().getIndex(transaction());
	}

	private FieldMetadata getYapField() {
	    return _constraint.getField().getFieldMetadata();
	}

	public QCon constraint() {
	    return _constraint;
	}

	public boolean isResolved() {
		final QCon parent = constraint().parent();
		return null == parent || !parent.hasParent();
	}

	public BTreeRange search(final Object value) {
		return getYapField().search(transaction(), value);
	}

	public static TreeInt addToTree(TreeInt tree, final IndexedNode node) {
	    Iterator4 i = node.iterator();
		while (i.moveNext()) {
		    FieldIndexKey composite = (FieldIndexKey)i.current();
		    tree = (TreeInt) Tree.add(tree, new TreeInt(composite.parentID()));
		}
		return tree;
	}

	public IndexedNode resolve() {
		if (isResolved()) {
			return null;
		}
		return IndexedPath.newParentPath(this, constraint());
	}

	private Transaction transaction() {
		return constraint().transaction();
	}

}