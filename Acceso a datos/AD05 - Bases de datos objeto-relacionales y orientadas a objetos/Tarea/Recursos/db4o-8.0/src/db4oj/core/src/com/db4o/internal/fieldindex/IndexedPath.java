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
import com.db4o.internal.query.processor.*;

public class IndexedPath extends IndexedNodeBase {
	
	public static IndexedNode newParentPath(IndexedNode next, QCon constraint) {
		if (!canFollowParent(constraint)) {
			return null;
		}
		return new IndexedPath((QConObject) constraint.parent(), next);
	}	
	
	private static boolean canFollowParent(QCon con) {
		final QCon parent = con.parent();
		final FieldMetadata parentField = getYapField(parent);
		if (null == parentField) return false;
		final FieldMetadata conField = getYapField(con);
		if (null == conField) return false;
		return parentField.hasIndex() &&
		    parentField.fieldType().isAssignableFrom(conField.containingClass());
	}
	
	private static FieldMetadata getYapField(QCon con) {
		QField field = con.getField();
		if (null == field) return null;
		return field.getFieldMetadata();
	}
	
	private IndexedNode _next;

	public IndexedPath(QConObject parent, IndexedNode next) {
		super(parent);
		_next = next;
	}
	
	public Iterator4 iterator() {		
		return new IndexedPathIterator(this, _next.iterator());
	}

	public int resultSize() {
		throw new NotSupportedException();
	}
	
	public void markAsBestIndex() {
		_constraint.setProcessedByIndex();
		_next.markAsBestIndex();
	}

}
