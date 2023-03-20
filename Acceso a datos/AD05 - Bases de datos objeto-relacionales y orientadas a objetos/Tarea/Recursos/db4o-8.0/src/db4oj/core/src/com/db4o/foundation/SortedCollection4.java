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
public class SortedCollection4 {
	
	private final Comparison4 _comparison;
	private Tree _tree;

	public SortedCollection4(Comparison4 comparison) {
		if (null == comparison) {
			throw new ArgumentNullException();
		}
		_comparison = comparison;
		_tree = null;
	}
	
	public Object singleElement() {
		if (1 != size()) {
			throw new IllegalStateException();
		}
		return _tree.key();
	}
	
	public void addAll(Iterator4 iterator) {		
		while (iterator.moveNext()) {
			add(iterator.current());
		}		
	}

	public void add(Object element) {
		_tree = Tree.add(_tree, new TreeObject(element, _comparison));
	}	

	public void remove(Object element) {
		_tree = Tree.removeLike(_tree, new TreeObject(element, _comparison));
	}

	public Object[] toArray(final Object[] array) {
		Tree.traverse(_tree, new Visitor4() {
			int i = 0;
			public void visit(Object obj) {
				array[i++] = ((TreeObject)obj).key();
			}
		});
		return array;
	}
	
	public int size() {
		return Tree.size(_tree);
	}
	
}
