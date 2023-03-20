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
package EDU.purdue.cs.bloat.cfg;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * <tt>Block</tt> represents a basic block of code used in control flow
 * graphs. A basic block is always entered at its beginning and exits at its
 * end. That is, its first statement is a label and its last statement is a
 * jump. There are no other labels or jumps in between.
 * <p>
 * Each <tt>Block</tt> knows its parent block and its children in the
 * dominator and postdominator trees. It also knows which blocks are in its
 * dominance frontier and its postdominance frontier.
 * 
 * @see FlowGraph
 * @see DominatorTree
 * @see DominanceFrontier
 */
public class Block extends GraphNode {
	// There are several "types" of Blocks. A NON_HEADER block is not the
	// header of a loop. An IRREDUCIBLE block is one of the headers of an
	// irreducible loop. An irriducible loop has more than one entry
	// point. They are very rare and are really ugly. The loop
	// transformer tries to fix up mutiple headers. A REDUCIBLE header is
	// a header for a reducible loop.
	public static final int NON_HEADER = 0;

	public static final int IRREDUCIBLE = 1;

	public static final int REDUCIBLE = 2;

	FlowGraph graph; // CFG to which this Block belongs

	Label label; // This Block's Label

	Tree tree; // Expression tree for this block

	Block domParent; // Block that (immediately) dominates this Block

	Block pdomParent;

	Set domChildren; // Blocks that this Block dominates

	Set pdomChildren; // The postdominator children of this block

	Set domFrontier; // This Block's dominance frontier

	Set pdomFrontier; // This Block's postdominace frontier

	int blockType; // NON_HEADER, IRREDUCIBLE, or REDUCIBLE

	Block header; // The block's loop header

	StackOptimizer stackOptimizer; // Stack Optimizer

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The block's label. The label may be thought of as the line of
	 *            code at which the block begins.
	 * @param graph
	 *            The CFG containing the block.
	 */
	Block(final Label label, final FlowGraph graph) {
		this.label = label;
		this.graph = graph;
		this.tree = null;
		this.header = null;
		this.blockType = Block.NON_HEADER;

		label.setStartsBlock(true);

		domParent = null;
		pdomParent = null;

		domChildren = new HashSet();
		pdomChildren = new HashSet();

		domFrontier = new HashSet();
		pdomFrontier = new HashSet();

		stackOptimizer = new StackOptimizer(this); // make StackOptimizer
													// object

	}

	/**
	 * Returns the stack optimizer for this block.
	 * 
	 * @return The stack optimizer.
	 */
	public StackOptimizer stackOptimizer() {
		return stackOptimizer;
	}

	/**
	 * Returns the expression tree for this block.
	 * 
	 * @return The tree.
	 */
	public Tree tree() {
		return tree;
	}

	/**
	 * Sets the expression tree for this block.
	 */
	public void setTree(final Tree tree) {
		this.tree = tree;
	}

	/**
	 * Returns the CFG containing the block.
	 * 
	 * @return The CFG.
	 */
	public FlowGraph graph() {
		return graph;
	}

	/**
	 * Returns the label associated with this block.
	 */
	public Label label() {
		return label;
	}

	/**
	 * Visits the expression tree contained in this block.
	 */
	public void visitChildren(final TreeVisitor visitor) {
		if (tree != null) {
			tree.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitBlock(this);
	}

	/**
	 * Sets the type of this Block. A Block may have one of three types:
	 * 
	 * <ul>
	 * <li><tt>NON_HEADER</tt>: Not the header of any loop
	 * <li><tt>IRREDUCIBLE</tt>: Header of an irreducible loop
	 * <li><tt>REDUCIBLE</tt>: Header of a reducible loop
	 * </ul>
	 * 
	 * A <i>loop</i> is a strongly connected component of a control flow graph.
	 * A loop's <i>header</i> is the block that dominates all other blocks in
	 * the loop. A loop is <i>reducible</i> if the only way to enter the loop
	 * is through the header.
	 */
	void setBlockType(final int blockType) {
		this.blockType = blockType;

		if (FlowGraph.DEBUG) {
			System.out.println("    Set block type " + this);
		}
	}

	/**
	 * Returns the type of this Block.
	 */
	int blockType() {
		return blockType;
	}

	public void setHeader(final Block header) {
		this.header = header;

		if (FlowGraph.DEBUG) {
			System.out.println("    Set header " + this);
		}
	}

	public Block header() {
		return header;
	}

	/**
	 * Returns a string representation of this block.
	 */
	public String toString() {
		String s = "<block " + label + " hdr=";

		if (header != null) {
			s += header.label();
		} else {
			s += "null";
		}

		switch (blockType) {
		case NON_HEADER:
			break;
		case IRREDUCIBLE:
			s += " irred";
			break;
		case REDUCIBLE:
			s += " red";
			break;
		}

		if (this == graph.source()) {
			return s + " source>";
		} else if (this == graph.init()) {
			return s + " init>";
		} else if (this == graph.sink()) {
			return s + " sink>";
		} else {
			return s + ">";
		}
	}

	/**
	 * Returns the basic blocks that this Block immediately dominates. That is,
	 * it returns this Block's children in the dominator tree for the CFG.
	 */
	Collection domChildren() {
		return domChildren;
	}

	/**
	 * Returns the immediate dominator of this Block. That is, it returns the
	 * Block's parent in the dominator tree, its immediate dominator.
	 */
	Block domParent() {
		return domParent;
	}

	/**
	 * Specifies that Block dominates this Block (parent in the dominator tree,
	 * the immediate dominator).
	 * 
	 * @param block
	 *            Block that dominates this Block.
	 */
	void setDomParent(final Block block) {
		// If this Block already had a dominator specified, remove
		// it from its dominator's children.
		if (domParent != null) {
			domParent.domChildren.remove(this);
		}

		domParent = block;

		// Add this Block to its new dominator's children.
		if (domParent != null) {
			domParent.domChildren.add(this);
		}
	}

	/**
	 * Returns whether or this Block dominates another given Block. A node X
	 * dominates a node Y when every path from the first node in the CFG (Enter)
	 * to Y must pass through X.
	 */
	public boolean dominates(final Block block) {
		Block p = block;

		while (p != null) {
			if (p == this) {
				return true;
			}
			p = p.domParent();
		}

		return false;
	}

	/**
	 * Returns the children of this Block in the CFG's postdominator tree.
	 */
	Collection pdomChildren() {
		return pdomChildren;
	}

	/**
	 * Returns the parent of this Block in the CFG's postdominator tree.
	 */
	Block pdomParent() {
		return pdomParent;
	}

	/**
	 * Sets this Block's parent in the postdominator tree.
	 */
	void setPdomParent(final Block block) {
		if (pdomParent != null) {
			pdomParent.pdomChildren.remove(this);
		}

		pdomParent = block;

		if (pdomParent != null) {
			pdomParent.pdomChildren.add(this);
		}
	}

	/**
	 * Determines whether or not this block postdominates a given block. A block
	 * X is said to postdominate a block Y when every path from Y to the last
	 * node in the CFG (Exit) passes through X. This relationship can be thought
	 * of as the reverse of dominance. That is, X dominates Y in the reverse
	 * CFG.
	 * 
	 * @see DominatorTree
	 */
	public boolean postdominates(final Block block) {
		Block p = block;

		while (p != null) {
			if (p == this) {
				return true;
			}
			p = p.pdomParent();
		}

		return false;
	}

	/**
	 * Returns the blocks that are in this block's dominance frontier. The
	 * dominance frontier of a node X in a CFG is the set of all nodes Y such
	 * that X dominates a predacessor of Y, but does not strictly dominate Y.
	 * Nodes in the dominance frontier always have more than one parent (a
	 * join).
	 * 
	 * @see DominanceFrontier
	 */
	Collection domFrontier() {
		Assert.isTrue(domFrontier != null);
		return domFrontier;
	}

	/**
	 * Returns the postdominance frontier for this node. A postdominace frontier
	 * is essentially the same as a dominace frontier, but the postdominance
	 * relationship is used instead of the dominance relationship.
	 */
	Collection pdomFrontier() {
		Assert.isTrue(pdomFrontier != null);
		return pdomFrontier;
	}
}
