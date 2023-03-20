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
package db4ounit.fixtures;

import com.db4o.foundation.*;

import db4ounit.fixtures.FixtureContext.*;

public class FixtureVariable<T> {
	
	public static <T> FixtureVariable<T> newInstance(String label) {
		return new FixtureVariable<T>(label);
    }
	
	private final String _label;
	
	public FixtureVariable() {
		this("");
	}

	public FixtureVariable(String label) {
		_label = label;
	}
	
	/**
	 * @sharpen.property
	 */
	public String label() {
		return _label;
	}
	
	public String toString() {
		return _label;
	}
	
	public Object with(T value, Closure4 closure) {
		return inject(value).run(closure);
	}

	public void with(T value, Runnable runnable) {
		inject(value).run(runnable);
	}

	private FixtureContext inject(T value) {
		return currentContext().add(this, value);
	} 
	
	/**
	 * @sharpen.property
	 */
	public T value() {
		final Found found = currentContext().get(this);
		if (null == found) throw new IllegalStateException();
		return (T)found.value;
	}

	private FixtureContext currentContext() {
		return FixtureContext.current();
	}
}
