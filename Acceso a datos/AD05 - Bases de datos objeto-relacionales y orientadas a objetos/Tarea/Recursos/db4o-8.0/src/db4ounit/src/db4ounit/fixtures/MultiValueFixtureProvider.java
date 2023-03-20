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

public class MultiValueFixtureProvider implements FixtureProvider {

	public static Object[] value() {
		return (Object[])_variable.value();
	}
	
	private static final FixtureVariable _variable = new FixtureVariable("data");
	
	private final Object[][] _values;

	public <T> MultiValueFixtureProvider(T[]... values) {
		_values = values;
	}

	public FixtureVariable variable() {
		return _variable;
	}

	public Iterator4 iterator() {
		return Iterators.iterate(_values);
	}
}
