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
package com.db4o.internal.query.result;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.query.processor.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class IdListQueryResult extends AbstractQueryResult implements Visitor4{
    
	private Tree _candidates;
	
	private boolean _checkDuplicates;
	
	public IntArrayList _ids;
    
    public IdListQueryResult(Transaction trans, int initialSize){
    	super(trans);
        _ids = new IntArrayList(initialSize);
    }
    
	public IdListQueryResult(Transaction trans) {
		this(trans, 0);
	}
    
    public IntIterator4 iterateIDs() {
    	return _ids.intIterator();
    }

    public Object get(int index) {
        synchronized (lock()) {
            return activatedObject(getId(index));
        }
    }
    
	public int getId(int index) {
        if (index < 0 || index >= size()) {
            throw new Db4oRecoverableException(new IndexOutOfBoundsException());
        }
		return _ids.get(index);
	}

	public final void checkDuplicates(){
		_checkDuplicates = true;
	}

	public void visit(Object a_tree) {
		QCandidate candidate = (QCandidate) a_tree;
		if (candidate.include()) {
		    addKeyCheckDuplicates(candidate._key);
		}
	}
	
	public void addKeyCheckDuplicates(int a_key){
	    if(_checkDuplicates){
	        TreeInt newNode = new TreeInt(a_key);
	        _candidates = Tree.add(_candidates, newNode);
	        if(newNode._size == 0){
	            return;
	        }
	    }
	    add(a_key);
	}
	
	@Override
	public void sort(final QueryComparator cmp) {
		Algorithms4.sort(new Sortable4() {
			public void swap(int leftIndex, int rightIndex) {
				_ids.swap(leftIndex, rightIndex);
			}
			public int size() {
				return IdListQueryResult.this.size();
			}
			public int compare(int leftIndex, int rightIndex) {
				return cmp.compare(get(leftIndex), get(rightIndex));
			}
		});
	}
	
	@Override
	public void sortIds(final IntComparator cmp) {
		Algorithms4.sort(new Sortable4() {
			public void swap(int leftIndex, int rightIndex) {
				_ids.swap(leftIndex, rightIndex);
			}
			public int size() {
				return IdListQueryResult.this.size();
			}
			public int compare(int leftIndex, int rightIndex) {
				return cmp.compare(_ids.get(leftIndex), _ids.get(rightIndex));
			}
		});
	}
	
	public void loadFromClassIndex(final ClassMetadata clazz) {
		final ClassIndexStrategy index = clazz.index();
		if(index instanceof BTreeClassIndexStrategy){
			BTree btree = ((BTreeClassIndexStrategy)index).btree();
			_ids = new IntArrayList(btree.size(transaction()));
		}
		index.traverseAll(_transaction, new Visitor4() {
			public void visit(Object a_object) {
				add(((Integer)a_object).intValue());
			}
		});
	}

	public void loadFromQuery(QQuery query) {
		query.executeLocal(this);
	}
	
	public void loadFromClassIndexes(ClassMetadataIterator iter){
		
        // duplicates because of inheritance hierarchies
        final ByRef<Tree> duplicates = new ByRef<Tree>();

        while (iter.moveNext()) {
			final ClassMetadata classMetadata = iter.currentClass();
			if (classMetadata.getName() != null) {
				ReflectClass claxx = classMetadata.classReflector();
				if (claxx == null
						|| !(stream()._handlers.ICLASS_INTERNAL.isAssignableFrom(claxx))) {
					final ClassIndexStrategy index = classMetadata.index();
					index.traverseAll(_transaction, new Visitor4() {
						public void visit(Object obj) {
							int id = ((Integer)obj).intValue();
							TreeInt newNode = new TreeInt(id);
							duplicates.value = Tree.add(duplicates.value, newNode);
							if (newNode.size() != 0) {
								add(id);
							}
						}
					});
				}
			}
		}
		
	}

	public void loadFromIdReader(Iterator4 ids) {
		while (ids.moveNext()) {
			add((Integer)ids.current());
		}
	}
	
	public void add(int id){
		_ids.add(id);
	}

	public int indexOf(int id) {
		return _ids.indexOf(id);
	}

	public int size() {
		return _ids.size();
	}
	
}
