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
public class TreeObject extends Tree{
	
	private final Object _object;
	
	private final Comparison4 _function;

	public TreeObject(Object object, Comparison4 function) {
		_object = object;
		_function = function;
	}

	public int compare(Tree tree) {
		return _function.compare(_object, tree.key());
	}

	public Object key() {
		return _object;
	}

}
