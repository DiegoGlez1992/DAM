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
package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public abstract class Config4Abstract {
    
	protected KeySpecHashtable4 _config;

	private final static KeySpec CASCADE_ON_ACTIVATE_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
    
	private final static KeySpec CASCADE_ON_DELETE_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
    
	private final static KeySpec CASCADE_ON_UPDATE_KEY=new KeySpec(TernaryBool.UNSPECIFIED);

    private final static KeySpec NAME_KEY=new KeySpec(null);

	public Config4Abstract() {
		this(new KeySpecHashtable4(10));
	}
	
	protected Config4Abstract(KeySpecHashtable4 config) {
		_config=(KeySpecHashtable4)config.deepClone(this);
	}
	
	public void cascadeOnActivate(boolean flag){
		putThreeValued(CASCADE_ON_ACTIVATE_KEY,flag);
	}
	
	public void cascadeOnDelete(boolean flag){
		putThreeValued(CASCADE_ON_DELETE_KEY,flag);
	}
	
	public void cascadeOnUpdate(boolean flag){
		putThreeValued(CASCADE_ON_UPDATE_KEY,flag);
	}

	protected void putThreeValued(KeySpec spec,boolean flag) {
		_config.put(spec, TernaryBool.forBoolean(flag));
	}

	protected void putThreeValuedInt(KeySpec spec,boolean flag) {
		_config.put(spec, flag ? 1 : -1);
	}

	public TernaryBool cascadeOnActivate(){
		return cascade(CASCADE_ON_ACTIVATE_KEY);
	}
	
	public TernaryBool cascadeOnDelete(){
		return cascade(CASCADE_ON_DELETE_KEY);
	}
	
	public TernaryBool cascadeOnUpdate(){
		return cascade(CASCADE_ON_UPDATE_KEY);
	}

	private TernaryBool cascade(KeySpec spec) {
		return _config.getAsTernaryBool(spec);
	}
	
	abstract String className();

	/**
	 * Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
	 */
	public boolean equals(Object obj){
		if(this==obj) {
			return true;
		}
		if(null==obj) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			Exceptions4.shouldNeverHappen();
		}
		return getName().equals(((Config4Abstract)obj).getName());
	}

	public int hashCode() {
		return getName().hashCode();
	}
	
	public String getName(){
		return _config.getAsString(NAME_KEY);
	}
	
	protected void setName(String name) {
		_config.put(NAME_KEY,name);
	}
}
