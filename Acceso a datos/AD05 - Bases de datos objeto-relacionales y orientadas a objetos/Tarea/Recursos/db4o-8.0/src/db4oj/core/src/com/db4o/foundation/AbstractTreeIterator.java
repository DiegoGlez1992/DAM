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
public abstract class AbstractTreeIterator implements Iterator4 {
	
	private final Tree	_tree;

	private Stack4 _stack;

	public AbstractTreeIterator(Tree tree) {
		_tree = tree;
	}

	public Object current() {
		if(_stack == null){
			throw new IllegalStateException();
		}
		Tree tree = peek();
		if(tree == null){
			return null;
		}
		return currentValue(tree);
	}
	
	private Tree peek(){
		return (Tree) _stack.peek();
	}

	public void reset() {
		_stack = null;
	}

	public boolean moveNext() {
		if(_stack == null){
			initStack();
			return _stack != null;
		}
		
		Tree current = peek();
		if(current == null){
			return false;
		}
		
		if(pushPreceding(current._subsequent)){
			return true;
		}
		
		while(true){
			_stack.pop();
			Tree parent = peek();
			if(parent == null){
				return false;
			}
			if(current == parent._preceding){
				return true;
			}
			current = parent;
		}
	}

	private void initStack() {
		if(_tree == null){
			return;
		}
		_stack = new Stack4();
		pushPreceding(_tree);
	}

	private boolean pushPreceding(Tree node) {
		if(node == null){
			return false;
		}
		while (node != null) {
			_stack.push(node);
			node = node._preceding;
		}
		return true;
	}

	protected abstract Object currentValue(Tree tree);
}
