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
package EDU.purdue.cs.bloat.util;

import java.util.*;

/**
 * Represents the union-find data structure.
 * 
 * <p>
 * 
 * Sometimes we need to group elements into disjoint sets. Two important
 * operations of these sets are finding the set that contains a given element
 * ("find") and uniting two sets ("union"). <tt>UnionFind</tt> provides an
 * efficient implementation of a data structure that support these operations on
 * disjoint sets of integers.
 * 
 * <p>
 * 
 * Each disjoint set is represented by a tree consisting of <tt>Node</tt>s.
 * (This <tt>Node</tt> is a class local to <tt>UnionFind</tt> and should not
 * be confused with <tt>tree.Node</tt>.) Each <tt>Node</tt> knows its
 * parent and child and has a rank associated with it. The parent node is always
 * the root node of the set tree. A <tt>Node</tt>'s rank is essentially the
 * height of the (sub)tree rooted by that node. When the union of two trees is
 * formed, the root with the smaller rank is made to point to the root with the
 * larger rank. Naturally, each <tt>Node</tt> has an integer "value"
 * associated with it.
 * 
 * <p>
 * 
 * A good description of union-find can be found in [Cormen, et. al. 1990].
 */
public class UnionFind {
	// The trees of Nodes that represent the disjoint sets.
	ResizeableArrayList nodes;

	/**
	 * Constructor.
	 */
	public UnionFind() {
		nodes = new ResizeableArrayList();
	}

	/**
	 * Constructor. Make a <tt>UnionFind</tt> with a given number of disjoint
	 * sets.
	 */
	public UnionFind(final int size) {
		nodes = new ResizeableArrayList(size);
	}

	/**
	 * Searches the disjoint sets for a given integer. Returns the set
	 * containing the integer a. Sets are represented by a local class
	 * <tt>Node</tt>.
	 */
	public Node findNode(final int a) {
		nodes.ensureSize(a + 1);

		final Node na = (Node) nodes.get(a);

		if (na == null) {
			// Start a new set with a
			final Node root = new Node(a);

			root.child = new Node(a);
			root.child.parent = root;

			nodes.set(a, root.child);

			return root;
		}

		return findNode(na);
	}

	/**
	 * Returns the integer value associated with the first <tt>Node</tt> in a
	 * set.
	 */
	public int find(final int a) {
		return findNode(a).value;
	}

	/**
	 * Finds the set containing a given Node.
	 */
	private Node findNode(Node node) {
		final Stack stack = new Stack();

		// Find the child of the root element.
		while (node.parent.child == null) {
			stack.push(node);
			node = node.parent;
		}

		// Do path compression on the way back down.
		final Node rootChild = node;

		while (!stack.empty()) {
			node = (Node) stack.pop();
			node.parent = rootChild;
		}

		Assert.isTrue(rootChild.parent.child != null);

		return rootChild.parent;
	}

	/**
	 * Returns true if a and b are in the same set.
	 */
	public boolean isEquiv(final int a, final int b) {
		return findNode(a) == findNode(b);
	}

	/**
	 * Combines the set that contains a with the set that contains b.
	 */
	public void union(final int a, final int b) {
		final Node na = findNode(a);
		final Node nb = findNode(b);

		if (na == nb) {
			return;
		}

		// Link the smaller tree under the larger.
		if (na.rank > nb.rank) {
			// Delete nb.
			nb.child.parent = na.child;
			na.value = b;

		} else {
			// Delete na.
			na.child.parent = nb.child;
			nb.value = b;

			if (na.rank == nb.rank) {
				nb.rank++;
			}
		}
	}

	class Node {
		Node parent; // The root of the tree in which this Node resides

		Node child;

		int value;

		int rank; // This Node's height in the tree

		public Node(final int v) {
			value = v;
			rank = 0;
		}
	}
}
