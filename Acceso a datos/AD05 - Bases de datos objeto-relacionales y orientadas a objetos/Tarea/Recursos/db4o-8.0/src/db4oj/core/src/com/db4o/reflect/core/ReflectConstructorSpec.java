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
package com.db4o.reflect.core;

import com.db4o.foundation.*;

/**
 * a spec holding a constructor, it's arguments
 * and information, if the constructor can instantiate
 * objects.
 */
public class ReflectConstructorSpec {
	private ReflectConstructor _constructor;
	private Object[] _args;
	private TernaryBool _canBeInstantiated;

	public static final ReflectConstructorSpec UNSPECIFIED_CONSTRUCTOR =
		new ReflectConstructorSpec(TernaryBool.UNSPECIFIED);

	public static final ReflectConstructorSpec INVALID_CONSTRUCTOR =
		new ReflectConstructorSpec(TernaryBool.NO);

	public ReflectConstructorSpec(ReflectConstructor constructor, Object[] args) {
		_constructor = constructor;
		_args = args;
		_canBeInstantiated = TernaryBool.YES; 
	}
	
	private ReflectConstructorSpec(TernaryBool canBeInstantiated) {
		_canBeInstantiated = canBeInstantiated;
		_constructor = null;
	}
	
	/**
	 * creates a new instance.
	 * @return the newly created instance.
	 */
	public Object newInstance() {
		if(_constructor == null) {
			return null;
		}
		return _constructor.newInstance(_args);
	}
	
	/**
	 * returns true if an instance can be instantiated
	 * with the constructor, otherwise false.
	 */
	public TernaryBool canBeInstantiated(){
		return _canBeInstantiated;
	}
}
