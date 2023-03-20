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
package com.db4o.internal.cluster;

import com.db4o.*;
import com.db4o.cluster.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.result.*;
import com.db4o.query.*;

/**
 * 
 * @exclude
 */
public class ClusterQueryResult implements QueryResult {
    
	private final Cluster _cluster;
    private final ObjectSet[] _objectSets;
    private final int[] _sizes;
    private final int _size;
    
    public ClusterQueryResult(Cluster cluster, Query[] queries){
        _cluster = cluster;
        _objectSets = new ObjectSet[queries.length]; 
        _sizes = new int[queries.length];
        int size = 0;
        for (int i = 0; i < queries.length; i++) {
            _objectSets[i] = queries[i].execute();
            _sizes[i] = _objectSets[i].size();
            size += _sizes[i];
        }
        _size = size;
    }
    
    private static final class ClusterQueryResultIntIterator implements IntIterator4 {

		private final CompositeIterator4 _delegate;

		public ClusterQueryResultIntIterator(Iterator4[] iterators) {
			_delegate = new CompositeIterator4(iterators);
		}

		public boolean moveNext() {
			return _delegate.moveNext();
		}

		public Object current() {
			return _delegate.current();
		}
		
		public void reset() {
			_delegate.reset();
		}

		public int currentInt() {
			return ((IntIterator4)_delegate.currentIterator()).currentInt();
		}
	}
    
    public IntIterator4 iterateIDs() {
		synchronized(_cluster) {
			final Iterator4[] iterators = new Iterator4[_objectSets.length];
			for (int i = 0; i < _objectSets.length; i++) {
				iterators[i] = ((ObjectSetFacade)_objectSets[i])._delegate.iterateIDs();
			}
			return new ClusterQueryResultIntIterator(iterators); 
		} 
	}
    
	public Iterator4 iterator() {
		synchronized(_cluster) {
			Iterator4[] iterators = new Iterator4[_objectSets.length];
			for (int i = 0; i < _objectSets.length; i++) {
				iterators[i] = ((ObjectSetFacade)_objectSets[i])._delegate.iterator();
			}
			return new CompositeIterator4(iterators);
		} 
	}

    public int size() {
        return _size;
    }
    
    public Object get(int index) {
        synchronized(_cluster){
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            int i = 0;
            while(index >= _sizes[i]){
                index -= _sizes[i];
                i++;
            }
            return ((ObjectSetFacade)_objectSets[i]).get(index); 
        }
    }

    public Object lock() {
        return _cluster;
    }

    public ExtObjectContainer objectContainer() {
        throw new NotSupportedException();
    }

    public int indexOf(int id) {
    	throw new NotSupportedException();
    }

	public void sort(QueryComparator cmp) {
		throw new NotSupportedException();
	}
	
	public void sortIds(IntComparator cmp) {
		throw new NotSupportedException();
	}

    /** @param c */
	public void loadFromClassIndex(ClassMetadata c) {
        throw new NotSupportedException();
	}

    /** @param q */
	public void loadFromQuery(QQuery q) {
		throw new NotSupportedException();
	}

    /** @param i */
	public void loadFromClassIndexes(ClassMetadataIterator i) {
		throw new NotSupportedException();
	}

    /** @param r */
	public void loadFromIdReader(ByteArrayBuffer r) {
		throw new NotSupportedException();
	}
}

