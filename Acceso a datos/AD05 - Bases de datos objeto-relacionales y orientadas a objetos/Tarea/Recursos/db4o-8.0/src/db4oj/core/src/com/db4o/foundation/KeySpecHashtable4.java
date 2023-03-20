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
public class KeySpecHashtable4 implements DeepClone {
    
    private SynchronizedHashtable4 _delegate;
    
	private KeySpecHashtable4(SynchronizedHashtable4 delegate_) {
		_delegate = delegate_;
	}
	
	public KeySpecHashtable4(int size) {
	    this(new SynchronizedHashtable4(size));
	}
	
    public void put(KeySpec spec,byte value) {
    	_delegate.put(spec,new Byte(value));
    }

    public void put(KeySpec spec,boolean value) {
    	_delegate.put(spec,new Boolean(value));
    }

    public void put(KeySpec spec,int value) {
    	_delegate.put(spec,new Integer(value));
    }

    public void put(KeySpec spec, Object value) {
    	_delegate.put(spec,value);
    }

    public byte getAsByte(KeySpec spec) {
    	return ((Byte)get(spec)).byteValue();
    }

    public boolean getAsBoolean(KeySpec spec) {
    	return ((Boolean)get(spec)).booleanValue();
    }

    public int getAsInt(KeySpec spec) {
    	return ((Integer)get(spec)).intValue();
    }

    public TernaryBool getAsTernaryBool(KeySpec spec) {
    	return (TernaryBool)get(spec);
    }

    public String getAsString(KeySpec spec) {
    	return (String)get(spec);
    }

    public synchronized Object get(KeySpec spec) {
        Object value=_delegate.get(spec);
        if(value == null){
            value = spec.defaultValue();
            if(value != null){
                _delegate.put(spec, value);
            }
        }
        return value;
    }
    
    public Object deepClone(Object obj) {
    	return new KeySpecHashtable4((SynchronizedHashtable4) _delegate.deepClone(obj));
    }
}
