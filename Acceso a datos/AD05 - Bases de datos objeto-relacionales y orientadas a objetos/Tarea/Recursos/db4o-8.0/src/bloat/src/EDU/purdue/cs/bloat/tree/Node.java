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
package EDU.purdue.cs.bloat.tree;

import java.io.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Node represents a node in an expression tree. Each Node has a value number
 * and a key associated with it. The value number is used to eliminate
 * statements and expressions that have the same value (PRE). Statements and
 * expressions of the same value will be mapped to the same value number.
 * 
 * @see Expr
 * @see Stmt
 * @see Tree
 */
public abstract class Node {
	protected Node parent; // This Node's parent in a Tree.

	int key; // integer used in some analyses. For instance, when

	// dead code elimination is performed, key is set to
	// DEAD or LIVE.
	int valueNumber; // Used in eliminating redundent statements

	/**
	 * Constructor.
	 */
	public Node() {
		// if (Tree.DEBUG) {
		// // We can't print the Node because things aren't initialized yet
		// System.out.println(" new node " +
		// System.identityHashCode(this));
		// }

		parent = null;
		valueNumber = -1;
		key = 0;
	}

	/**
	 * Returns this Node's value number.
	 */
	public int valueNumber() {
		return valueNumber;
	}

	/**
	 * Sets this Node's value number.
	 */
	public void setValueNumber(final int valueNumber) {
		// if (Tree.DEBUG) {
		// System.out.println(" setVN[" + this + "] = " + valueNumber);
		// }
		this.valueNumber = valueNumber;
	}

	/**
	 * A Node's key represents an integer value that can be used by an algorithm
	 * to mark this node. For instance, when dead code elimination is performed
	 * a Node is marked as DEAD or ALIVE.
	 */
	public int key() {
		return key;
	}

	public void setKey(final int key) {
		this.key = key;
	}

	/**
	 * Visit the children of this node. Not all Nodes will have children to
	 * visit.
	 */
	public abstract void visitForceChildren(TreeVisitor visitor);

	public abstract void visit(TreeVisitor visitor);

	public void visitChildren(final TreeVisitor visitor) {
		if (!visitor.prune()) {
			visitForceChildren(visitor);
		}
	}

	public void visitOnly(final TreeVisitor visitor) {
		visitor.setPrune(true);
		visit(visitor);
		visitor.setPrune(false);
	}

	/**
	 * Returns the basic block in which this Node resides.
	 */
	public Block block() {
		Node p = this;

		while (p != null) {
			if (p instanceof Tree) {
				return ((Tree) p).block();
			}

			p = p.parent;
		}

		throw new RuntimeException(this + " is not in a block");
	}

	/**
	 * Sets the parent Node of this Node.
	 */
	public void setParent(final Node parent) {
		// if (Tree.DEBUG) {
		// System.out.println(" setting parent of " + this + " (" +
		// System.identityHashCode(this) + ") to " + parent);
		// }

		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Node parent() {
		// Note that we can't print this Node because of recursion. Sigh.
		Assert.isTrue(parent != null, "Null parent for "
				+ this.getClass().toString() + " node "
				+ System.identityHashCode(this));
		return parent;
	}

	/**
	 * Copies the contents of one Node into another.
	 * 
	 * @param node
	 *            A Node from which to copy.
	 * 
	 * @return node containing the contents of this Node.
	 */
	protected Node copyInto(final Node node) {
		node.setValueNumber(valueNumber);
		return node;
	}

	/**
	 * Clean up this Node only. Does not effect its children nodes.
	 */
	public abstract void cleanupOnly();

	/**
	 * Cleans up this node so that it is independent of the expression tree in
	 * which it resides. This is usually performed before a Node is moved from
	 * one part of an expression tree to another.
	 * <p>
	 * Traverse the Tree starting at this Node. Remove the parent of each node
	 * and perform any Node-specific cleanup (see cleanupOnly). Sets various
	 * pointers to null so that they eventually may be garbage collected.
	 */
	public void cleanup() {
		// if (Tree.DEBUG) {
		// System.out.println(" CLEANING UP " + this + " " +
		// System.identityHashCode(this));
		// }

		visit(new TreeVisitor() {
			public void visitNode(final Node node) {
				node.setParent(null);
				node.cleanupOnly();
				node.visitChildren(this);
			}
		});
	}

	/**
	 * Replaces this node with another and perform cleanup.
	 */
	public void replaceWith(final Node node) {
		replaceWith(node, true);
	}

	/**
	 * Replaces this Node with node in its (this's) tree.
	 * 
	 * @param node
	 *            The Node with which to replace.
	 * @param cleanup
	 *            Do we perform cleanup on the tree?
	 */
	public void replaceWith(final Node node, final boolean cleanup) {
		// Check a couple of things:
		// 1. The node with which we are replace this does not have a parent.
		// 2. This Node does have a parent.
		Assert.isTrue(node.parent == null, node + " already has a parent");
		Assert.isTrue(parent != null, this + " has no parent");

		final Node oldParent = parent;

		if (this instanceof Stmt) {
			Assert.isTrue(node instanceof Stmt, "Attempt to replace " + this
					+ " with " + node);
		}

		if (this instanceof Expr) {
			Assert.isTrue(node instanceof Expr, "Attempt to replace " + this
					+ " with " + node);

			final Expr expr1 = (Expr) this;
			final Expr expr2 = (Expr) node;

			// Make sure the expressions can be interchanged (i.e. their
			// descriptors
			// are compatible).
			Assert.isTrue(expr1.type().simple().equals(expr2.type().simple()),
					"Type mismatch when replacing " + expr1 + " with " + expr2
							+ ": " + expr1.type() + " != " + expr2.type());
		}

		// Iterate over this parent's tree and replace this with node.
		parent.visit(new ReplaceVisitor(this, node));

		Assert.isTrue(node.parent == oldParent, node + " parent == "
				+ node.parent + " != " + oldParent);

		if (cleanup) {
			cleanup();
		}
	}

	/**
	 * @return A textual representation of this Node.
	 */
	public String toString() {
		final StringWriter w = new StringWriter();

		visit(new PrintVisitor(w) {
			protected void println(final Object s) {
				print(s);
			}

			protected void println() {
			}
		});

		w.flush();

		return w.toString();
	}
}
