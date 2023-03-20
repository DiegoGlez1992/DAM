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
public abstract class Tree<T> implements ShallowClone , DeepClone, Visitable <T> {
    
	public Tree<T> _preceding;
	public int _size = 1;
	public Tree<T> _subsequent;
	
	 public static final <T,V extends Tree<T>> V add(V  oldTree, V newTree){
		if(oldTree == null){
			return newTree;
		}
		return (V)oldTree.add(newTree);
	 }
	
	public final <V extends Tree<T>> V add(final V newNode){
	    return add(newNode, compare(newNode));
	}
	
    /**
     * On adding a node to a tree, if it already exists, and if
     * Tree#duplicates() returns false, #isDuplicateOf() will be
     * called. The added node can then be asked for the node that
     * prevails in the tree using #duplicateOrThis(). This mechanism
     * allows doing find() and add() in one run.
     */
	public <V extends Tree<T>> V add(final V newNode, final int cmp){
	    if(cmp < 0){
	        if(_subsequent == null){
	            _subsequent = newNode;
	            _size ++;
	        }else{
	            _subsequent = _subsequent.add(newNode);
	            if(_preceding == null){
	                return (V)rotateLeft();
	            }
	            return (V)balance();
	        }
	    }else if(cmp > 0 || newNode.duplicates()){
	        if(_preceding == null){
	            _preceding = newNode;
	            _size ++;
	        }else{
	            _preceding = _preceding.add(newNode);
	            if(_subsequent == null){
	                return (V)rotateRight();
	            }
	            return (V)balance();
	        }
	    }else{
	        return (V)newNode.onAttemptToAddDuplicate(this);
	    }
	    return (V)this;
	}
    
    
    /**
     * On adding a node to a tree, if it already exists, and if
     * Tree#duplicates() returns false, #onAttemptToAddDuplicate() 
     * will be called and the existing node will be stored in
     * this._preceding.
     * This node node can then be asked for the node that prevails 
     * in the tree on adding, using the #addedOrExisting() method. 
     * This mechanism allows doing find() and add() in one run.
     */
    public Tree addedOrExisting(){
        if(wasAddedToTree()){
        	return this;
        }
        return _preceding;
    }
    
    public boolean wasAddedToTree(){
    	return _size != 0;
    }
	
	public final Tree balance(){
		int cmp = _subsequent.nodes() - _preceding.nodes(); 
		if(cmp < -2){
			return rotateRight();
		}else if(cmp > 2){
			return rotateLeft();
		}else{
            setSizeOwnPrecedingSubsequent();
		    return this;
		}
	}
	
	public Tree balanceCheckNulls(){
	    if(_subsequent == null){
	        if(_preceding == null){
                setSizeOwn();
	            return this;
	        }
	        return rotateRight();
	    }else if(_preceding == null){
	        return rotateLeft();
	    }
	    return balance();
	}
	
	public void calculateSize(){
		if(_preceding == null){
			if (_subsequent == null){
				setSizeOwn();
			}else{
                setSizeOwnSubsequent();
			}
		}else{
			if(_subsequent == null){
                setSizeOwnPreceding();
			}else{
                setSizeOwnPrecedingSubsequent();
			}
		}
	}
	
	
    /**
     * returns 0, if keys are equal
     * uses this - other  
     * returns positive if this is greater than a_to
     * returns negative if this is smaller than a_to
     */
	public abstract int compare(Tree a_to);
	
	public static Tree deepClone(Tree a_tree, Object a_param){
		if(a_tree == null){
			return null;
		}
		Tree newNode = (Tree)a_tree.deepClone(a_param);
		newNode._size = a_tree._size;
		newNode._preceding = Tree.deepClone(a_tree._preceding, a_param); 
		newNode._subsequent = Tree.deepClone(a_tree._subsequent, a_param); 
		return newNode;
	}
	
	
	public Object deepClone(Object a_param){
        return shallowClone();
	}
	
	public boolean duplicates(){
		return true;
	}
	
	public final Tree filter(final Predicate4 a_filter){
		if(_preceding != null){
			_preceding = _preceding.filter(a_filter);
		}
		if(_subsequent != null){
			_subsequent = _subsequent.filter(a_filter);
		}
		if(! a_filter.match(this)){
			return remove();
		}
		return this;
	}
	
	public static final <T> Tree<T> find(Tree<T> inTree, Tree<T> template){
		if(inTree == null){
			return null;
		}
		return inTree.find(template);
	}
	
	
	public final Tree<T> find(final Tree<T> template){
		Tree current = this;
		while(true){
			int comparisonResult = current.compare(template);
			if(comparisonResult == 0){
				return current;
			}
			if(comparisonResult < 0){
				current = current._subsequent;
			} else {
				current = current._preceding;
			}
			if(current == null){
				return null;
			}
		}
	}
	
	public static final Tree findGreaterOrEqual(Tree a_in, Tree a_finder){
		if(a_in == null){
			return null;
		}
		int cmp = a_in.compare(a_finder);
		if(cmp == 0){
			return a_in; // the highest node in the hierarchy !!!
		}
		if(cmp > 0){
			Tree node = findGreaterOrEqual(a_in._preceding, a_finder);
			if(node != null){
				return node;
			}
			return a_in;
		}
		return findGreaterOrEqual(a_in._subsequent, a_finder);
	}
	
	
	public final static Tree findSmaller(Tree a_in, Tree a_node){
		if(a_in == null){
			return null;
		}
		int cmp = a_in.compare(a_node);
		if(cmp < 0){
			Tree node = findSmaller(a_in._subsequent, a_node);
			if(node != null){
				return node;
			}
			return a_in;
		}
		return findSmaller(a_in._preceding, a_node);
	}
    
    public final Tree<T> first(){
        if(_preceding == null){
            return this;
        }
        return _preceding.first();
    }
    
    public static Tree last(Tree tree){
    	if(tree == null){
    		return null;
    	}
    	return tree.last();
    }
    
    public final Tree last(){
        if(_subsequent == null){
            return this;
        }
        return _subsequent.last();
    }
    
	public Tree onAttemptToAddDuplicate(Tree oldNode){
		_size = 0;
        _preceding = oldNode;
        return oldNode;
	}
	
    /**
     * @return the number of nodes in this tree for balancing
     */
    public int nodes(){
        return _size;
    }
    
    public int ownSize(){
	    return 1;
	}
	
	public Tree remove(){
		if(_subsequent != null && _preceding != null){
			_subsequent = _subsequent.rotateSmallestUp();
			_subsequent._preceding = _preceding;
			_subsequent.calculateSize();
			return _subsequent;
		}
		if(_subsequent != null){
			return _subsequent;
		}
		return _preceding;
	}
	
	public void removeChildren(){
		_preceding = null;
		_subsequent = null;
		setSizeOwn();
	}
    
    public Tree removeFirst(){
        if(_preceding == null){
            return _subsequent;
        }
        _preceding = _preceding.removeFirst();
        calculateSize();
        return this;
    }
	
	public static Tree removeLike(Tree from, Tree a_find){
		if(from == null){
			return null;
		}
		return from.removeLike(a_find);
	}
	
	public final <V extends Tree<T>> V removeLike(final V a_find){
		int cmp = compare(a_find);
		if(cmp == 0){
			return (V)remove();
		}
		if (cmp > 0){
			if(_preceding != null){
				_preceding = _preceding.removeLike(a_find);
			}
		}else{
			if(_subsequent != null){
				_subsequent = _subsequent.removeLike(a_find);
			}
		}
		calculateSize();
		return (V)this;
	}
	
	public final Tree removeNode(final Tree a_tree){
		if (this == a_tree){
			return remove();
		}
		int cmp = compare(a_tree);
		if (cmp >= 0){
			if(_preceding != null){
				_preceding = _preceding.removeNode(a_tree);
			}
		}
		if(cmp <= 0){
			if(_subsequent != null){
				_subsequent = _subsequent.removeNode(a_tree);	
			}
		}
		calculateSize();
		return this;
	}
    
	public final Tree rotateLeft(){
		Tree tree = _subsequent;
		_subsequent = tree._preceding;
		calculateSize();
		tree._preceding = this;
		if(tree._subsequent == null){
            tree.setSizeOwnPlus(this);
		}else{
            tree.setSizeOwnPlus(this, tree._subsequent);
		}
		return tree;
	}

	public final Tree rotateRight(){
		Tree tree = _preceding;
		_preceding = tree._subsequent;
		calculateSize();
		tree._subsequent = this;
		if(tree._preceding == null){
            tree.setSizeOwnPlus(this);
		}else{
            tree.setSizeOwnPlus(this, tree._preceding);
		}
		return tree;
	}
	
	private final Tree rotateSmallestUp(){
		if(_preceding != null){
			_preceding = _preceding.rotateSmallestUp();
			return rotateRight();
		}
		return this;
	}
    
    public final void setSizeOwn(){
        _size = ownSize();
    }
    
    public final void setSizeOwnPrecedingSubsequent(){
        _size = ownSize() + _preceding._size + _subsequent._size;
    }
    
    public final void setSizeOwnPreceding(){
        _size = ownSize() + _preceding._size;
    }
    
    public final void setSizeOwnSubsequent(){
        _size = ownSize() + _subsequent._size;
    }
    
    public final void setSizeOwnPlus(Tree tree){
        _size = ownSize() + tree._size;
    }
    
    public final void setSizeOwnPlus(Tree tree1, Tree tree2){
        _size = ownSize() + tree1._size + tree2._size;
    }
	
	public static int size(Tree a_tree){
		if(a_tree == null){
			return 0;
		}
		return a_tree.size();
	}
	
    /**
     * @return the number of objects represented.
     */
	public int size(){
		return _size;
	}
    
    public static final void traverse(Tree tree, Visitor4 visitor){
        if(tree == null){
            return;
        }
        tree.traverse(visitor);
    }
    
	/**
	 * Traverses a tree with a starting point node.
	 * If there is no exact match for the starting node, the next higher will be taken.
	 */
    public static void traverse(Tree tree, Tree startingNode, CancellableVisitor4 visitor) {
		if(tree == null){
			return;
		}
		tree.traverse(startingNode, visitor);
	}
    
	private boolean traverse(Tree startingNode, CancellableVisitor4 visitor) {
		if(startingNode != null){
			int cmp = compare(startingNode);
			if(cmp < 0){
				if(_subsequent != null){
					return _subsequent.traverse(startingNode, visitor);
				}
				return true;
			} else if (cmp > 0){
				if(_preceding != null){
					if(! _preceding.traverse(startingNode, visitor)){
						return false;
					}
				}
			}
		} else {
			if(_preceding != null){
				if(! _preceding.traverse(null, visitor)){
					return false;
				}
			}
		}
		if(! visitor.visit(this)){
			return false;
		}
		if(_subsequent != null){
			if(! _subsequent.traverse(null, visitor)){
				return false;
			}
		}
		return true;
	}
	

	public final <V extends Tree<T>> void traverse(final Visitor4<V> visitor){
		if(_preceding != null){
			_preceding.traverse(visitor);
		}
		visitor.visit((V)this);
		if(_subsequent != null){
			_subsequent.traverse(visitor);
		}
	}
	
	public final void traverseFromLeaves(Visitor4 a_visitor){
	    if(_preceding != null){
	        _preceding.traverseFromLeaves(a_visitor);
	    }
	    if(_subsequent != null){
	        _subsequent.traverseFromLeaves(a_visitor);
	    }
	    a_visitor.visit(this);
	}	
	
	// Keep the debug method to debug the depth
//	public final void debugLeafDepth(int currentDepth){
//		currentDepth++;
//		if(_preceding == null && _subsequent == null){
//			System.out.println("" + currentDepth + " tree leaf depth.");
//			return;
//		}
//	    if (_preceding != null){
//	    	_preceding.debugLeafDepth(currentDepth);
//	    }
//	    if(_subsequent != null){
//	    	_subsequent.debugLeafDepth(currentDepth);
//	    }
//	}

	protected Tree shallowCloneInternal(Tree tree) {
		tree._preceding=_preceding;
		tree._size=_size;
		tree._subsequent=_subsequent;
		return tree;
	}
	
	public Object shallowClone() {
		throw new com.db4o.foundation.NotImplementedException();
	}
	
	public abstract T key();
	
	public Object root() {
		return this;
	}
	
	public void accept(final Visitor4<T> visitor){
		traverse(new Visitor4() {
				public void visit(Object obj) {
					Tree<T> tree = (Tree)obj;
					visitor.visit(tree.key());
				}
			}
		);
	}


}
