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

import java.util.*;

/**
 * 
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
class JdkCollectionIterator4 implements Iterator4{
    
    private static final Object INVALID = new Object();
    
    private final Collection _collection;
    
    private Iterator _iterator;
    
    private Object _current;
    
    public JdkCollectionIterator4(Collection collection) {
        _collection = collection;
        reset();
    }

    public Object current() {
        if(_current == INVALID){
            throw new IllegalStateException();
        }
        return _current;
    }

    public boolean moveNext() {
        if(_iterator.hasNext()){
            _current = _iterator.next();
            return true;
        }
        _current = INVALID;
        return false;
    }

    public void reset() {
        _iterator = _collection.iterator();
        _current = INVALID; 
    }

}