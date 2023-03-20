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
package com.db4o.internal.handlers.array;

import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class MultidimensionalArrayIterator implements Iterator4 {
    
    private final ReflectArray _reflectArray;
    
    private final Object[] _array;
    
    private int _currentElement;
    
    private Iterator4 _delegate;
    
    public MultidimensionalArrayIterator(ReflectArray reflectArray, Object[] array) {
        _reflectArray = reflectArray;
        _array = array;
        reset();
    }

    public Object current() {
        if(_delegate == null){
            return _array[_currentElement];
        }
        return _delegate.current();
    }

    public boolean moveNext() {
        if(_delegate != null){
            if(_delegate.moveNext()){
                return true;
            }
            _delegate = null;
        }
        _currentElement++;
        if(_currentElement >= _array.length){
            return false;
        }
        Object obj = _array[_currentElement];
        Class clazz = obj.getClass();
        if(clazz.isArray()){
            if(clazz.getComponentType().isArray()){
                _delegate = new MultidimensionalArrayIterator(_reflectArray, (Object[]) obj);
            } else {
                _delegate = new ReflectArrayIterator(_reflectArray, obj);
            }
            return moveNext();
        }
        return true;
    }

    public void reset() {
        _currentElement = -1;
        _delegate = null;
    }

}
