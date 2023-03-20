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


/**
 * @exclude
 */
public class SynchronizedHashtable4 implements DeepClone {
    
    private final Hashtable4 _delegate;
    
    private SynchronizedHashtable4(Hashtable4 delegate_){
        _delegate = delegate_;
    }

    public SynchronizedHashtable4(int size) {
        this(new Hashtable4(size));
    }

    public synchronized Object deepClone(Object obj) {
        return new SynchronizedHashtable4((Hashtable4)_delegate.deepClone(obj));
    }

    public synchronized void put(Object key, Object value) {
        _delegate.put(key, value);
    }

    public synchronized Object get(Object key) {
        return _delegate.get(key);
    }

}
