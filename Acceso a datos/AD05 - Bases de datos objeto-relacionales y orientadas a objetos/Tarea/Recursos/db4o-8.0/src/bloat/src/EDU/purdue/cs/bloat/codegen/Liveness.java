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
package EDU.purdue.cs.bloat.codegen;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Liveness represents the interference graph of the local variables contained
 * in a control flow graph.
 * 
 * When the liveness of two variables overlap each other, the two variables are
 * said to <i>interfere</i> with each other. The interference graph represents
 * this relationship between variables. There is an (un-directed) edge between
 * variables <tt>a</tt> and <tt>b</tt> in the interference graph if variable
 * <tt>a</tt> interferes with variable <tt>b</tt>.
 */
public class Liveness {
	public static boolean DEBUG = false;

	public static boolean UNIQUE = false;

	public static final boolean BEFORE = false;

	public static final boolean AFTER = true;

	FlowGraph cfg;

	Graph ig;

	/**
	 * Constructor.
	 * 
	 * @param cfg
	 *            Control flow graph on which to perform liveness analysis.
	 */
	public Liveness(final FlowGraph cfg) {
		this.cfg = cfg;
		computeIntersections();
	}

	/**
	 * Removes a local expression from the interference graph.
	 */
	public void removeVar(final LocalExpr expr) {
		ig.removeNode(expr);
	}

	/**
	 * Should not be called.
	 */
	public boolean liveAtUse(final VarExpr isLive, final VarExpr at,
			final boolean after) {
		throw new RuntimeException();
	}

	/**
	 * Should not be called.
	 */
	public boolean liveAtStartOfBlock(final VarExpr isLive, final Block block) {
		throw new RuntimeException();
	}

	/**
	 * Should not be called.
	 */
	public boolean liveAtEndOfBlock(final VarExpr isLive, final Block block) {
		throw new RuntimeException();
	}

	/**
	 * Returns the <tt>LocalExpr</tt>s (variables) that occur in the CFG.
	 * They correspond to nodes in the interference graph.
	 */
	public Collection defs() {
		return ig.keySet();
	}

	/**
	 * Returns an <tt>Iterator</tt> of <tt>LocalExpr</tt>s that interfere
	 * with a given <tt>VarExpr</tt>.
	 */
	public Iterator intersections(final VarExpr a) {
		Assert.isTrue(a != null, "Cannot get intersections for null def");
		Assert.isTrue(a.isDef(), "Cannot get intersections for variable uses");

		final GraphNode node = ig.getNode(a);

		Assert.isTrue(node != null, "Cannot find IG node for " + a);

		return new Iterator() {
			Iterator succs = ig.succs(node).iterator();

			public boolean hasNext() {
				return succs.hasNext();
			}

			public Object next() {
				final IGNode next = (IGNode) succs.next();
				return next.def;
			}

			public void remove() {
				throw new RuntimeException();
			}
		};
	}

	/**
	 * Determines whether or not two variables interfere with one another.
	 */
	public boolean liveRangesIntersect(final VarExpr a, final VarExpr b) {
		Assert.isTrue((a != null) && (b != null),
				"Cannot get intersections for null def");
		Assert.isTrue(a.isDef() && b.isDef(),
				"Cannot get intersections for variable uses");

		if (a == b) {
			return false;
		}

		// If all locals should have unique colors, return true.
		if (Liveness.UNIQUE) {
			return true;
		}

		final IGNode na = (IGNode) ig.getNode(a);
		final IGNode nb = (IGNode) ig.getNode(b);

		Assert.isTrue((na != null) && (nb != null));

		return ig.hasEdge(na, nb);
	}

	/**
	 * Constructs the interference graph.
	 */
	private void computeIntersections() {
		ig = new Graph(); // The interference graph

		if (Liveness.DEBUG) {
			System.out.println("-----------Computing live ranges-----------");
		}

		// All of the nodes (IGNodes) in the IG
		final List defNodes = new ArrayList();

		// The IGNodes whose local variable is defined by a PhiCatchStmt
		final List phiCatchNodes = new ArrayList();

		// An array of NodeInfo for each node in the CFG (indexed by the
		// node's pre-order index). Gives information about the local
		// variables (nodes in the IG) that are defined in each block.
		// The NodeInfos are stored in reverse order. That is, the
		// NodeInfo for the final variable occurrence in the block is the
		// first element in the list.
		final List[] nodes = new ArrayList[cfg.size()];

		// We need to keep track of the order of the statements in which
		// variables occur. There is an entry in nodeIndices for each
		// block in the CFG. Each entry consists of a mapping between a
		// statement in which a variable occurs and the number of the
		// statement (with respect to the other statements in which
		// variables occur) of interest. This is hard to explain in
		// words. This numbering comes into play in the liveOut method.
		final Map[] nodeIndices = new HashMap[cfg.size()];

		Iterator iter = cfg.nodes().iterator();

		// Initialize nodes and nodeIndices
		while (iter.hasNext()) {
			final Block block = (Block) iter.next();
			final int blockIndex = cfg.preOrderIndex(block);
			nodes[blockIndex] = new ArrayList();
			nodeIndices[blockIndex] = new HashMap();
		}

		// Go in trace order. Code generation for phis in the presence of
		// critical edges depends on it!

		iter = cfg.trace().iterator();

		// When performing liveness analysis, we traverse the tree from
		// the bottom up. That is, we do a REVERSE traversal.
		while (iter.hasNext()) {
			final Block block = (Block) iter.next();

			block.visit(new TreeVisitor(TreeVisitor.REVERSE) {
				public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
					if (!(stmt.target() instanceof LocalExpr)) {
						return;
					}

					final LocalExpr target = (LocalExpr) stmt.target();

					// Examine each predacessor and maintain some information
					// about the definitions. Remember that we're dealing with
					// a PhiJoinStmt. The predacessors of PhiJoinStmts are
					// statements that define or use the local (SSA) variable.
					final Iterator preds = cfg.preds(block).iterator();

					while (preds.hasNext()) {
						final Block pred = (Block) preds.next();
						final int predIndex = cfg.preOrderIndex(pred);

						final List n = nodes[predIndex];
						final Map indices = nodeIndices[predIndex];

						indices.put(stmt, new Integer(n.size()));
						final NodeInfo info = new NodeInfo(stmt);
						n.add(info);

						// Make a new node in the interference graph for target,
						// if one does not already exists
						IGNode node = (IGNode) ig.getNode(target);

						if (node == null) {
							node = new IGNode(target);
							ig.addNode(target, node);
							defNodes.add(node);
						}

						info.defNodes.add(node);
					}
				}

				public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
				}

				public void visitStmt(final Stmt stmt) {
				}
			});
		}

		iter = cfg.trace().iterator();

		while (iter.hasNext()) {
			final Block block = (Block) iter.next();
			final int blockIndex = cfg.preOrderIndex(block);

			block.visit(new TreeVisitor(TreeVisitor.REVERSE) {
				Node parent = null;

				public void visitNode(final Node node) {
					final Node p = parent;
					parent = node;
					node.visitChildren(this);
					parent = p;
				}

				public void visitLocalExpr(final LocalExpr expr) {
					Assert.isTrue(parent != null);

					// Recall that a LocalExpr represents a use or a definition
					// of a local variable. If the LocalExpr is defined by a
					// PhiJoinStmt, the block in which it resides should already
					// have some information about it.

					NodeInfo info;

					final List n = nodes[blockIndex];
					final Map indices = nodeIndices[blockIndex];

					final Integer i = (Integer) indices.get(parent);

					if (i == null) {
						if (Liveness.DEBUG) {
							System.out.println("adding " + parent + " at "
									+ n.size());
						}

						indices.put(parent, new Integer(n.size()));
						info = new NodeInfo(parent);
						n.add(info);

					} else {
						if (Liveness.DEBUG) {
							System.out.println("found " + parent + " at " + i);
						}

						info = (NodeInfo) n.get(i.intValue());
						Assert.isTrue(info != null);
					}

					if (expr.isDef()) {
						IGNode node = (IGNode) ig.getNode(expr);

						if (node == null) {
							node = new IGNode(expr);
							ig.addNode(expr, node);
							defNodes.add(node);
						}

						info.defNodes.add(node);
					}
				}

				public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
					NodeInfo info;

					final List n = nodes[blockIndex];
					final Map indices = nodeIndices[blockIndex];

					final Integer i = (Integer) indices.get(stmt);

					if (i == null) {
						if (Liveness.DEBUG) {
							System.out.println("adding " + stmt + " at "
									+ n.size());
						}

						indices.put(stmt, new Integer(n.size()));
						info = new NodeInfo(stmt);
						n.add(info);

					} else {
						if (Liveness.DEBUG) {
							System.out.println("found " + parent + " at " + i);
						}

						info = (NodeInfo) n.get(i.intValue());
						Assert.isTrue(info != null);
					}

					final LocalExpr target = (LocalExpr) stmt.target();

					IGNode node = (IGNode) ig.getNode(target);

					if (node == null) {
						node = new IGNode(target);
						ig.addNode(target, node);
						defNodes.add(node);
						phiCatchNodes.add(node);
					}

					info.defNodes.add(node);
				}

				public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
				}
			});
		}

		// Iterate over all of the nodes in the IG
		final int numDefs = defNodes.size();

		for (int i = 0; i < numDefs; i++) {
			final IGNode node = (IGNode) defNodes.get(i);
			final LocalExpr def = node.def;

			// Set of blocks where this variable is live out (i.e. live on
			// any of the block's outgoing edges).
			final BitSet m = new BitSet(cfg.size());

			final Iterator uses = def.uses().iterator();

			// Look at each use of the local variable
			while (uses.hasNext()) {
				final LocalExpr use = (LocalExpr) uses.next();
				Node parent = use.parent();

				if ((parent instanceof MemRefExpr)
						&& ((MemRefExpr) parent).isDef()) {
					parent = parent.parent();
				}

				// Skip catch-phis. We handle this later.
				if (parent instanceof PhiCatchStmt) {
					// If we want to be less conservative:
					// Need to search back from the operand from all
					// points in the protected region where it is live
					// back to the def of the operand. For each block
					// in protected region, if the operand def is closest
					// dominator of the block
					continue;
				}

				if (Liveness.DEBUG) {
					System.out.println("searching for " + def + " from "
							+ parent);
				}

				final Block block = parent.block();

				if (parent instanceof PhiJoinStmt) {
					final PhiJoinStmt phi = (PhiJoinStmt) parent;

					// The local variable (LocalExpr) occurs within a
					// PhiJoinStmt. Look at the predacessors of the
					// PhiJoinStmt. Recall that each predacessor defines one of
					// the operands to the PhiJoinStmt. Locate the block that
					// defines the LocalExpr in question. Call liveOut to
					// determine for which nodes the LocalExpr is live out.

					// Examine the predacessors of the block containing the
					// LocalExpr
					final Iterator preds = cfg.preds(block).iterator();

					while (preds.hasNext()) {
						final Block pred = (Block) preds.next();

						if (phi.operandAt(pred) == use) {
							final Map indices = nodeIndices[cfg
									.preOrderIndex(pred)];
							final Integer index = (Integer) indices.get(parent);
							Assert.isTrue(index != null, "No index for "
									+ parent);

							liveOut(m, nodes, pred, index.intValue(), node,
									phiCatchNodes);
							break;
						}
					}

				} else {
					// The LocalExpr is define in a non-Phi statement. Figure
					// out which number definition define the LocalExpr in quest
					// and call liveOut to compute the set of block in which the
					// LocalExpr is live out.

					final Map indices = nodeIndices[cfg.preOrderIndex(block)];
					final Integer index = (Integer) indices.get(parent);
					Assert.isTrue(index != null, "No index for " + parent);

					liveOut(m, nodes, block, index.intValue(), node,
							phiCatchNodes);
				}
			}
		}

		// Go through all of the variables that are defined by
		// PhiCatchStmts and make them (the variables) conflict with
		// everything that the operands of the PhiCatchStmt conflict
		// with. See liveOut for a discussion.

		final int numPhiCatches = phiCatchNodes.size();

		for (int i = 0; i < numPhiCatches; i++) {
			final IGNode node = (IGNode) phiCatchNodes.get(i);

			final PhiCatchStmt phi = (PhiCatchStmt) node.def.parent();

			final Iterator operands = phi.operands().iterator();

			while (operands.hasNext()) {
				final LocalExpr operand = (LocalExpr) operands.next();
				final LocalExpr def = (LocalExpr) operand.def();

				if (def != null) {
					final IGNode opNode = (IGNode) ig.getNode(def);

					// Conflict with everything the operand conflicts with.
					final Iterator edges = new ImmutableIterator(ig
							.succs(opNode));

					while (edges.hasNext()) {
						final IGNode otherNode = (IGNode) edges.next();

						if (otherNode != node) {
							if (Liveness.DEBUG) {
								System.out.println(otherNode.def
										+ " conflicts with " + opNode.def
										+ " and thus with " + node.def);
							}

							ig.addEdge(otherNode, node);
							ig.addEdge(node, otherNode);
						}
					}
				}
			}
		}

		if (Liveness.DEBUG) {
			System.out.println("Interference graph =");
			System.out.println(ig);
		}
	}

	/**
	 * Computes (a portion of) the "live out" set for a given local variable. If
	 * a variable is live on a block's outgoing edge in the CFG, then it is
	 * "live out" at that block.
	 * 
	 * @param m
	 *            Bit vector that indicates the block for which block the
	 *            defNode is live out
	 * @param nodes
	 *            The NodeInfo for the local variables used or defined in each
	 *            block
	 * @param block
	 *            The block in which the LocalExpr of interest is defined
	 * @param nodeIndex
	 *            Which number definition in the defining block
	 * @param defNode
	 *            The node in the IG whose live out set we are interested in
	 * @param phiCatchNodes
	 *            The nodes in the interference graph that represent local
	 *            variables defined by PhiCatchStmts
	 */
	// Nate sez:
	//
	// In a PhiJoin pred, add
	// ...
	// phi-target := phi-operand
	// jump with throw succs
	//
	// Don't kill Phi targets in protected blocks
	// The phi target and operand don't conflict
	void liveOut(final BitSet m, final List[] nodes, Block block,
			int nodeIndex, final IGNode defNode, final Collection phiCatchNodes) {
		boolean firstNode = true;

		int blockIndex = cfg.preOrderIndex(block);

		final ArrayList stack = new ArrayList();

		Pos pos = new Pos();
		pos.block = block;
		pos.blockIndex = blockIndex;
		pos.nodeIndex = nodeIndex;

		stack.add(pos);

		while (!stack.isEmpty()) {
			pos = (Pos) stack.remove(stack.size() - 1);

			block = pos.block;
			blockIndex = pos.blockIndex;
			nodeIndex = pos.nodeIndex;

			if (Liveness.DEBUG) {
				System.out.println(defNode.def + " is live at position "
						+ nodeIndex + " of " + block);
			}

			boolean stop = false;

			// The nodes are sorted in reverse. So, the below gets all of
			// the nodes defined at this block after nodeIndex. I believe
			// this is an optimization so we don't calculate things twice.
			// Or maybe its how we get things to terminate.
			final ListIterator iter = nodes[blockIndex].listIterator(nodeIndex);

			while (!stop && iter.hasNext()) {
				final NodeInfo info = (NodeInfo) iter.next();

				if (Liveness.DEBUG) {
					System.out
							.println(defNode.def + " is live at " + info.node);
				}

				if (firstNode) {
					// We don't care about the definition in the block that
					// defines the LocalExpr of interest.
					firstNode = false;
					continue;
				}

				// Look at all (?) of the definitions of the LocalExpr
				final Iterator e = info.defNodes.iterator();

				while (e.hasNext()) {
					final IGNode node = (IGNode) e.next();

					final Iterator catchPhis = phiCatchNodes.iterator();

					// Calculating the live region of the target of a phi-catch
					// node is a little tricky. The target (variable) must be
					// live throughout the protected region as well as after the
					// PhiCatchStmt (its definition). However, we do not want
					// the phi-catch target to conflict (interfere) with any of
					// its operands. So, we make the target conflict with all
					// of the variables that its operand conflict with. See
					// page 37 of Nate's Thesis.

					PHIS: while (catchPhis.hasNext()) {
						final IGNode catchNode = (IGNode) catchPhis.next();

						final PhiCatchStmt phi = (PhiCatchStmt) catchNode.def
								.parent();

						final Handler handler = (Handler) cfg.handlersMap()
								.get(phi.block());

						Assert.isTrue(handler != null, "Null handler for "
								+ phi.block());

						if (handler.protectedBlocks().contains(block)) {
							final Iterator operands = phi.operands().iterator();

							// If the block containing the LocalExpr in question
							// resides inside a protected region. Make sure that
							// the LocalExpr is not one of the operands to the
							// PhiCatchStmt associated with the protected
							// region.

							while (operands.hasNext()) {
								final LocalExpr expr = (LocalExpr) operands
										.next();

								if (expr.def() == node.def) {
									continue PHIS;
								}
							}

							if (Liveness.DEBUG) {
								System.out.println(defNode.def
										+ " conflicts with " + node.def);
							}

							// Hey, wow. The variable defined in the phi-catch
							// interferes with the variable from the worklist.
							ig.addEdge(node, catchNode);
							ig.addEdge(catchNode, node);
						}
					}

					if (node != defNode) {
						if (Liveness.DEBUG) {
							System.out.println(defNode.def + " conflicts with "
									+ node.def);
						}

						// If the node in the worklist is not the node we
						// started
						// with, then they conflict.
						ig.addEdge(node, defNode);
						ig.addEdge(defNode, node);

					} else {
						if (Liveness.DEBUG) {
							System.out.println("def found stopping search");
						}

						// We've come across a definition of the LocalExpr in
						// question, so we don't need to do any more.
						stop = true;
					}
				}
			}

			if (!stop) {
				// Propagate the liveness to each of the predacessors of the
				// block in which the variable of interest is defined. This
				// is accomplished by setting the appropriate bit in m. We
				// also add another Pos to the worklist to work on the
				// predacessor block.
				final Iterator preds = cfg.preds(block).iterator();

				while (preds.hasNext()) {
					final Block pred = (Block) preds.next();
					final int predIndex = cfg.preOrderIndex(pred);

					if (Liveness.DEBUG) {
						System.out.println(defNode.def + " is live at end of "
								+ pred);
					}

					if (!m.get(predIndex)) {
						pos = new Pos();
						pos.block = pred;
						pos.blockIndex = predIndex;

						// Look at all of the statements in which a variable
						// occur
						pos.nodeIndex = 0;

						m.set(predIndex);
						stack.add(pos);
					}
				}
			}
		}
	}

	/**
	 * Represents a node in the interference graph. Connected nodes in the
	 * interference graph interfere with each other. That is, their live regions
	 */
	class IGNode extends GraphNode {
		LocalExpr def;

		/**
		 * Constructor.
		 * 
		 * @param def
		 *            The local variable represented by this node.
		 */
		public IGNode(final LocalExpr def) {
			this.def = def;
		}

		public String toString() {
			return def.toString();
		}
	}

	/**
	 * Stores information about each Node in an expression tree (!) that defines
	 * a local variable (i.e. PhiJoinStmt, PhiCatchStmt, and the parent of a
	 * LocalExpr).
	 */
	class NodeInfo {
		Node node; // Node in an expression tree in which a variable occurs

		List defNodes; // node(s) in IG that define above Node

		public NodeInfo(final Node node) {
			this.node = node;
			defNodes = new ArrayList();
		}
	}

	class Key {
		int blockIndex;

		Node node;

		public Key(final Node node, final int blockIndex) {
			this.blockIndex = blockIndex;
			this.node = node;
		}

		public int hashCode() {
			return node.hashCode() ^ blockIndex;
		}

		public boolean equals(final Object obj) {
			if (obj instanceof Key) {
				final Key key = (Key) obj;
				return (key.node == node) && (key.blockIndex == blockIndex);
			}

			return false;
		}
	}

	/**
	 * A Pos is an element in the worklist used to determine the live out set of
	 * a given LocalExpr. It consists of the block in which a local variable
	 * definition occurs, the block's index (i.e. pre-order traversal number) in
	 * the CFG, and the number of the definition in the block that defines the
	 * LocalExpr of interest.
	 */
	class Pos {
		Block block;

		int blockIndex;

		int nodeIndex;
	}
}
