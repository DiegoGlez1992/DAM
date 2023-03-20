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

public class FieldIndexProcessorResult {
	
	public static final FieldIndexProcessorResult NO_INDEX_FOUND = new FieldIndexProcessorResult(null);

	public static final FieldIndexProcessorResult FOUND_INDEX_BUT_NO_MATCH = new FieldIndexProcessorResult(null);
	
	private final IndexedNode _indexedNode;
	
	public FieldIndexProcessorResult(IndexedNode indexedNode) {
		_indexedNode = indexedNode;
	}
	
	public Tree toQCandidate(QCandidates candidates){
		return TreeInt.toQCandidate(toTreeInt(), candidates);
	}
	
	public TreeInt toTreeInt(){
		if(foundMatch()){
			return _indexedNode.toTreeInt();
		}
		return null;
	}
	
	public boolean foundMatch(){
		return foundIndex() && ! noMatch();
	}
	
	public boolean foundIndex(){
		return this != NO_INDEX_FOUND;
	}
	
	public boolean noMatch(){
		return this == FOUND_INDEX_BUT_NO_MATCH;
	}
	
	public Iterator4 iterateIDs(){
		return new MappingIterator(_indexedNode.iterator()) {
			protected Object map(Object current) {
			    FieldIndexKey composite = (FieldIndexKey)current;
				return new Integer(composite.parentID());
			}
		};
	}
	
}