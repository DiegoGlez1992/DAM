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
package com.db4o.internal.marshall;

/**
 * @exclude
 */
public class RawClassSpec {

	private final String _name;
	private final int _superClassID;
	private final int _numFields;

	public RawClassSpec(final String name, final int superClassID, final int numFields) {
		_name = name;
		_superClassID = superClassID;
		_numFields = numFields;
	}
	
	public String name() {
		return _name;
	}
	
	public int superClassID() {
		return _superClassID;
	}
	
	public int numFields() {
		return _numFields;
	}
}
