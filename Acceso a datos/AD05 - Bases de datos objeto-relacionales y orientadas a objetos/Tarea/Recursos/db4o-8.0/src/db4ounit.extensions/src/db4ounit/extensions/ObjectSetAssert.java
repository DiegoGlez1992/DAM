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
package db4ounit.extensions;

import com.db4o.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class ObjectSetAssert {

	public static void sameContent(ObjectSet objectSet, Object... expectedItems) {
		Iterator4Assert.sameContent(Iterators.iterate(expectedItems), iterate(objectSet));
	}
	
	public static void areEqual(ObjectSet objectSet, Object... expectedItems) {
		Iterator4Assert.areEqual(expectedItems, iterate(objectSet));
	}

	public static Iterator4 iterate(ObjectSet objectSet) {
		return new ObjectSetIterator4(objectSet);
	}
	
	static class ObjectSetIterator4 implements Iterator4{
	    
	    private static final Object INVALID = new Object();
	    
	    private ObjectSet _objectSet;
	    
	    private Object _current;
	    
	    public ObjectSetIterator4(ObjectSet collection) {
	        _objectSet = collection;
	    }

	    public Object current() {
	        if(_current == INVALID){
	            throw new IllegalStateException();
	        }
	        return _current;
	    }

	    public boolean moveNext() {
	        if(_objectSet.hasNext()){
	            _current = _objectSet.next();
	            return true;
	        }
	        _current = INVALID;
	        return false;
	    }

	    public void reset() {
	        _objectSet.reset();
	        _current = INVALID; 
	    }
	}

}
