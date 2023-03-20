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
 * A dynamic variable is a value associated to a specific thread and scope.
 * 
 * The value is brought into scope with the {@link #with} method.
 * 
 */
public class DynamicVariable<T> {
	
	public static <T> DynamicVariable<T> newInstance() {
		return new DynamicVariable();
	}
	
	private final ThreadLocal<T> _value = new ThreadLocal<T>();
	
	/**
	 * @sharpen.property
	 */
	public T value() {
		final T value = _value.get();
		return value == null
			? defaultValue()
			: value;
	}
	
	/**
	 * @sharpen.property
	 */
	public void value(T value){
		_value.set(value);
	}
	
	protected T defaultValue() {
		return null;
	}
	
	public Object with(T value, Closure4 block) {
		T previous = _value.get();
		_value.set(value);
		try {
			return block.run();
		} finally {
			_value.set(previous);
		}
	}
	
	public void with(T value, Runnable block) {
		T previous = _value.get();
		_value.set(value);
		try {
			block.run();
		} finally {
			_value.set(previous);
		}
	}
}
