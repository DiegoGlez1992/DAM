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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.classindex.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public abstract class AbstractLateQueryResult extends AbstractQueryResult {
	
	protected Iterable4 _iterable;

	public AbstractLateQueryResult(Transaction transaction) {
		super(transaction);
	}
	
    public AbstractQueryResult supportSize(){
    	return toIdTree();
    }
    
    public AbstractQueryResult supportSort(){
    	return toIdList();
    }
    
    public AbstractQueryResult supportElementAccess(){
    	return toIdList();
    }
    
    protected int knownSize(){
    	return 0;
    }
    
	public IntIterator4 iterateIDs() {
		if(_iterable == null){
			throw new IllegalStateException();
		}
		return new IntIterator4Adaptor(_iterable);
	}
    
    public AbstractQueryResult toIdList(){
    	return toIdTree().toIdList();
    }

	public boolean skipClass(ClassMetadata classMetadata){
		if (classMetadata.getName() == null) {
			return true;
		}
		ReflectClass claxx = classMetadata.classReflector();
		if (stream()._handlers.ICLASS_INTERNAL.isAssignableFrom(claxx)){
			return true; 
		}
		return false;
	}
	
	protected Iterable4 classIndexesIterable(final ClassMetadataIterator classCollectionIterator) {
		return Iterators.concatMap(Iterators.iterable(classCollectionIterator), new Function4() {
			public Object apply(Object current) {
				final ClassMetadata classMetadata = (ClassMetadata)current;
				if(skipClass(classMetadata)){
					return Iterators.SKIP;
				}
				return classIndexIterable(classMetadata);
			}
		});
	}
	
	protected Iterable4 classIndexIterable(final ClassMetadata clazz) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return classIndexIterator(clazz);
			}
		};
	}
	
	public Iterator4 classIndexIterator(ClassMetadata clazz) {
		return BTreeClassIndexStrategy.iterate(clazz, transaction());
	}

}
