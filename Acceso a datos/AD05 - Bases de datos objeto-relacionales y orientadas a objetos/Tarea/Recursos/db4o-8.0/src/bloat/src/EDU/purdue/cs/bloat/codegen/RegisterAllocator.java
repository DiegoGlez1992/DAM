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
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * RegisterAllocator performs analysis on a control flow graph and determines
 * the minimum amount of local variables needed in a method.
 * 
 * @see LocalVariable
 */
// Note that RegisterAllocator uses a different IGNode from Liveness!
public class RegisterAllocator {
	FlowGraph cfg;

	Liveness liveness;

	Map colors;

	int colorsUsed;

	final static float MAX_WEIGHT = Float.MAX_VALUE;

	final static float LOOP_FACTOR = 10.0F;

	final static int MAX_DEPTH = (int) (Math.log(RegisterAllocator.MAX_WEIGHT) / Math
			.log(RegisterAllocator.LOOP_FACTOR));

	/**
	 * Constructor. Builds an interference graph based on the expression nodes
	 * found in liveness. Traverses the graph and determines which nodes needs
	 * to be precolored and which nodes can be coalesced (move statements).
	 * Nodes are coalesced and local variables are assigned to expressions.
	 * 
	 * @see FlowGraph
	 * @see LocalVariable
	 */
	public RegisterAllocator(final FlowGraph cfg, final Liveness liveness) {
		this.cfg = cfg;
		this.liveness = liveness;
		colorsUsed = 0;
		colors = new HashMap();

		// Construct the interference graph.
		final Graph ig = new Graph();

		Iterator iter = liveness.defs().iterator();

		while (iter.hasNext()) {
			final VarExpr def = (VarExpr) iter.next();

			if (!(def instanceof LocalExpr)) {
				// Ignore node in the Liveness IG that are not LocalExprs
				continue;
			}

			// Create a new node in the IG, if one does not already exist
			IGNode defNode = (IGNode) ig.getNode(def);

			if (defNode == null) {
				defNode = new IGNode((LocalExpr) def);
				ig.addNode(def, defNode);
			}

			// Examine each variable that interferes with def
			final Iterator intersections = liveness.intersections(def);

			while (intersections.hasNext()) {
				final VarExpr expr = (VarExpr) intersections.next();

				if (expr == def) {
					// If for some reason, def interferes with itself, ignore it
					continue;
				}

				// Add an edge in RegisterAllocator's IG between the variables
				// that interfere
				if (expr instanceof LocalExpr) {
					IGNode node = (IGNode) ig.getNode(expr);

					if (node == null) {
						node = new IGNode((LocalExpr) expr);
						ig.addNode(expr, node);
					}

					ig.addEdge(defNode, node);
					ig.addEdge(node, defNode);
				}
			}
		}

		// Arrays of expressions that invovle a copy of one local variable
		// to another. Expressions invovled in copies (i.e. "moves") can
		// be coalesced into one expression.
		final ArrayList copies = new ArrayList();

		// Nodes that are the targets of InitStmt are considered to be
		// precolored.
		final ArrayList precolor = new ArrayList();

		cfg.visit(new TreeVisitor() {
			public void visitBlock(final Block block) {
				// Don't visit the sink block. There's nothing interesting
				// there.
				if (block != RegisterAllocator.this.cfg.sink()) {
					block.visitChildren(this);
				}
			}

			public void visitPhiStmt(final PhiStmt stmt) {
				stmt.visitChildren(this);

				if (!(stmt.target() instanceof LocalExpr)) {
					return;
				}

				// A PhiStmt invovles an assignment (copy). So note the copy
				// between the target and all of the PhiStmt's operands in the
				// copies list.

				final IGNode lnode = (IGNode) ig.getNode(stmt.target());

				final HashSet set = new HashSet();

				final Iterator e = stmt.operands().iterator();

				while (e.hasNext()) {
					final Expr op = (Expr) e.next();

					if ((op instanceof LocalExpr) && (op.def() != null)) {
						if (!set.contains(op.def())) {
							set.add(op.def());

							if (op.def() != stmt.target()) {
								final IGNode rnode = (IGNode) ig.getNode(op
										.def());
								copies.add(new IGNode[] { lnode, rnode });
							}
						}
					}
				}
			}

			public void visitStoreExpr(final StoreExpr expr) {
				expr.visitChildren(this);

				if (!(expr.target() instanceof LocalExpr)) {
					return;
				}

				final IGNode lnode = (IGNode) ig.getNode(expr.target());

				if ((expr.expr() instanceof LocalExpr)
						&& (expr.expr().def() != null)) {

					// A store of a variable into another variable is a copy
					final IGNode rnode = (IGNode) ig.getNode(expr.expr().def());
					copies.add(new IGNode[] { lnode, rnode });
					return;
				}

				// Treat L := L + k as a copy so that they get converted
				// back to iincs.
				if (expr.target().type().equals(Type.INTEGER)) {
					if (!(expr.expr() instanceof ArithExpr)) {
						return;
					}

					// We're dealing with integer arithmetic. Remember that an
					// ArithExpr has a left and a right operand. If one of the
					// operands is a variable and if the other is a constant and
					// the operation is addition or substraction, we have an
					// increment.

					final ArithExpr rhs = (ArithExpr) expr.expr();
					LocalExpr var = null;

					Integer value = null;

					if ((rhs.left() instanceof LocalExpr)
							&& (rhs.right() instanceof ConstantExpr)) {

						var = (LocalExpr) rhs.left();

						final ConstantExpr c = (ConstantExpr) rhs.right();

						if (c.value() instanceof Integer) {
							value = (Integer) c.value();
						}

					} else if ((rhs.right() instanceof LocalExpr)
							&& (rhs.left() instanceof ConstantExpr)) {

						var = (LocalExpr) rhs.right();

						final ConstantExpr c = (ConstantExpr) rhs.left();

						if (c.value() instanceof Integer) {
							value = (Integer) c.value();
						}
					}

					if (rhs.operation() == ArithExpr.SUB) {
						if (value != null) {
							value = new Integer(-value.intValue());
						}

					} else if (rhs.operation() != ArithExpr.ADD) {
						value = null;
					}

					if ((value != null) && (var.def() != null)) {
						final int incr = value.intValue();

						if ((short) incr == incr) {
							// Only generate an iinc if the increment
							// fits in a short
							final IGNode rnode = (IGNode) ig.getNode(var.def());
							copies.add(new IGNode[] { lnode, rnode });
						}
					}
				}
			}

			public void visitInitStmt(final InitStmt stmt) {
				stmt.visitChildren(this);

				// The initialized variables are precolored.
				final LocalExpr[] t = stmt.targets();

				for (int i = 0; i < t.length; i++) {
					precolor.add(t[i]);
				}
			}
		});

		// Coalesce move related nodes, maximum weight first.
		while (copies.size() > 0) {
			// We want the copy (v <- w) with the maximum:
			// weight(v) + weight(w)
			// ---------------------
			// size(union)
			// where union is the intersection of the nodes that conflict
			// with v and the nodes that conflict with w. This equation
			// appears to be in conflict with the one given on page 38 of
			// Nate's thesis.

			HashSet union; // The union of neighboring nodes

			int max = 0;

			IGNode[] copy = (IGNode[]) copies.get(max);

			float maxWeight = copy[0].weight + copy[1].weight;
			union = new HashSet();
			union.addAll(ig.succs(copy[0]));
			union.addAll(ig.succs(copy[1]));
			maxWeight /= union.size();

			for (int i = 1; i < copies.size(); i++) {
				copy = (IGNode[]) copies.get(i);

				float weight = copy[0].weight + copy[1].weight;
				union.clear();
				union.addAll(ig.succs(copy[0]));
				union.addAll(ig.succs(copy[1]));
				weight /= union.size();

				if (weight > maxWeight) {
					// The ith copy has the maximum weight
					maxWeight = weight;
					max = i;
				}
			}

			// Remove the copy with the max weight from the copies list. He
			// does it in a rather round-about way.
			copy = (IGNode[]) copies.get(max);
			copies.set(max, copies.get(copies.size() - 1));
			copies.remove(copies.size() - 1);

			if (!ig.hasEdge(copy[0], copy[1])) {
				// If the variables involved in the copy do not interfere with
				// each other, they are coalesced.

				if (CodeGenerator.DEBUG) {
					System.out.println("coalescing " + copy[0] + " " + copy[1]);
					System.out.println("    0 conflicts " + ig.succs(copy[0]));
					System.out.println("    1 conflicts " + ig.succs(copy[1]));
				}

				ig.succs(copy[0]).addAll(ig.succs(copy[1]));
				ig.preds(copy[0]).addAll(ig.preds(copy[1]));

				copy[0].coalesce(copy[1]);

				if (CodeGenerator.DEBUG) {
					System.out.println("    coalesced " + copy[0]);
					System.out.println("    conflicts " + ig.succs(copy[0]));
				}

				// Remove coalesced node from the IG
				ig.removeNode(copy[1].key);

				iter = copies.iterator();

				// Examine all copies. If the copy involves the node that was
				// coalesced, the copy is no longer interesting. Remove it.
				while (iter.hasNext()) {
					final IGNode[] c = (IGNode[]) iter.next();

					if ((c[0] == copy[1]) || (c[1] == copy[1])) {
						iter.remove();
					}
				}
			}
		}

		// Create a list of uncolored nodes.
		final ArrayList uncoloredNodes = new ArrayList();

		Iterator nodes = ig.nodes().iterator();

		while (nodes.hasNext()) {
			final IGNode node = (IGNode) nodes.next();

			final ArrayList p = new ArrayList(precolor);
			p.retainAll(node.defs);

			// See if any node got coalesced with a precolored node.
			if (p.size() == 1) {
				// Precolored
				node.color = ((LocalExpr) p.get(0)).index();

				if (CodeGenerator.DEBUG) {
					System.out.println("precolored " + node + " " + node.color);
				}

			} else if (p.size() == 0) {
				// Uncolored (i.e. not coalesced with any of the pre-colored
				// nodes.
				node.color = -1;
				uncoloredNodes.add(node);

			} else {
				// If two or more pre-colored nodes were coalesced, we have a
				// problem.
				throw new RuntimeException("coalesced pre-colored defs " + p);
			}
		}

		// Sort the uncolored nodes, by decreasing weight. Wide nodes
		// have half their original weight since they take up two indices
		// and we want to put color nodes with the lower indices.

		Collections.sort(uncoloredNodes, new Comparator() {
			public int compare(final Object a, final Object b) {
				final IGNode na = (IGNode) a;
				final IGNode nb = (IGNode) b;

				float wa = na.weight / ig.succs(na).size();
				float wb = nb.weight / ig.succs(nb).size();

				if (na.wide) {
					wa /= 2;
				}

				if (nb.wide) {
					wb /= 2;
				}

				if (wb > wa) {
					return 1;
				}

				if (wb < wa) {
					return -1;
				}

				return 0;
			}
		});

		nodes = uncoloredNodes.iterator();

		while (nodes.hasNext()) {
			final IGNode node = (IGNode) nodes.next();

			if (CodeGenerator.DEBUG) {
				System.out.println("coloring " + node);
				System.out.println("    conflicts " + ig.succs(node));
			}

			// Make sure node has not been colored
			Assert.isTrue(node.color == -1);

			// Determine which colors have been assigned to the nodes
			// conflicting with the node of interest
			final BitSet used = new BitSet();

			final Iterator succs = ig.succs(node).iterator();

			while (succs.hasNext()) {
				final IGNode succ = (IGNode) succs.next();

				if (succ.color != -1) {
					used.set(succ.color);

					if (succ.wide) {
						used.set(succ.color + 1);
					}
				}
			}

			// Find the next available color
			for (int i = 0; node.color == -1; i++) {
				if (!used.get(i)) {
					if (node.wide) {
						// Wide variables need two colors
						if (!used.get(i + 1)) {
							node.color = i;

							if (CodeGenerator.DEBUG) {
								System.out.println("    assigning color " + i
										+ " to " + node);
							}

							if (i + 1 >= colorsUsed) {
								colorsUsed = i + 2;
							}
						}

					} else {
						node.color = i;

						if (CodeGenerator.DEBUG) {
							System.out.println("    assigning color " + i
									+ " to " + node);
						}

						if (i >= colorsUsed) {
							colorsUsed = i + 1;
						}
					}
				}
			}
		}

		nodes = ig.nodes().iterator();

		while (nodes.hasNext()) {
			final IGNode node = (IGNode) nodes.next();

			// Make sure each node has been colored
			Assert.isTrue(node.color != -1, "No color for " + node);

			iter = node.defs.iterator();

			// Set the index of the variable and all of its uses to be the
			// chosen color.
			while (iter.hasNext()) {
				final LocalExpr def = (LocalExpr) iter.next();
				def.setIndex(node.color);

				final Iterator uses = def.uses().iterator();

				while (uses.hasNext()) {
					final LocalExpr use = (LocalExpr) uses.next();
					use.setIndex(node.color);
				}
			}
		}

		if (CodeGenerator.DEBUG) {
			System.out.println("After allocating locals--------------------");
			cfg.print(System.out);
			System.out.println("End print----------------------------------");
		}
	}

	/**
	 * Returns the maximum number of local variables used by the cfg after its
	 * "registers" (local variables) have been allocated.
	 */
	public int maxLocals() {
		return colorsUsed;
	}

	/**
	 * Creates a new local variable in this method (as modeled by the cfg).
	 * Updates the number of local variables appropriately.
	 */
	public LocalVariable newLocal(final Type type) {
		// Why don't we add Type information to the LocalVariable? Are we
		// assuming that type checking has already been done and so its a
		// moot point?

		final LocalVariable var = new LocalVariable(colorsUsed);
		colorsUsed += type.stackHeight();
		return var;
	}

	/**
	 * IGNode is a node in the interference graph. Note that this node is
	 * different from the one in Liveness. For instance, this one stores
	 * information about a node's color, its weight, etc. Because nodes may be
	 * coalesced, an IGNode may represent more than one LocalExpr. That's why
	 * there is a list of definitions.
	 */
	class IGNode extends GraphNode {
		Set defs;

		LocalExpr key;

		int color;

		boolean wide; // Is the variable wide?

		float weight;

		public IGNode(final LocalExpr def) {
			color = -1;
			key = def;
			defs = new HashSet();
			defs.add(def);
			wide = def.type().isWide();
			computeWeight();
		}

		/**
		 * Coalesce two nodes in the interference graph. The weight of the other
		 * node is added to that of this node. This node also inherits all of
		 * the definitions of the other node.
		 */
		void coalesce(final IGNode node) {
			Assert.isTrue(wide == node.wide);

			weight += node.weight;

			final Iterator iter = node.defs.iterator();

			while (iter.hasNext()) {
				final LocalExpr def = (LocalExpr) iter.next();
				defs.add(def);
			}
		}

		public String toString() {
			return "(color=" + color + " weight=" + weight + " "
					+ defs.toString() + ")";
		}

		/**
		 * Calculates the weight of a Block based on its loop depth. If the
		 * block does not exceed the MAX_DEPTH, then the weight is LOOP_FACTOR
		 * raised to the depth.
		 */
		private float blockWeight(final Block block) {
			int depth = cfg.loopDepth(block);

			if (depth > RegisterAllocator.MAX_DEPTH) {
				return RegisterAllocator.MAX_WEIGHT;
			}

			float w = 1.0F;

			while (depth-- > 0) {
				w *= RegisterAllocator.LOOP_FACTOR;
			}

			return w;
		}

		/**
		 * Computes the weight of a node in the interference graph. The weight
		 * is based on where the variable represented by this node is used. The
		 * method blockWeight is used to determine the weight of a variable used
		 * in a block based on the loop depth of the block. Special care must be
		 * taken if the variable is used in a PhiStmt.
		 */
		private void computeWeight() {
			weight = 0.0F;

			final Iterator iter = defs.iterator();

			// Look at all(?) of the definitions of the IGNode
			while (iter.hasNext()) {
				final LocalExpr def = (LocalExpr) iter.next();

				weight += blockWeight(def.block());

				final Iterator uses = def.uses().iterator();

				// If the variable is used as an operand to a PhiJoinStmt,
				// find the predacessor block to the PhiJoinStmt in which the
				// variable occurs and add the weight of that block to the
				// running total weight.
				while (uses.hasNext()) {
					final LocalExpr use = (LocalExpr) uses.next();

					if (use.parent() instanceof PhiJoinStmt) {
						final PhiJoinStmt phi = (PhiJoinStmt) use.parent();

						final Iterator preds = cfg.preds(phi.block())
								.iterator();

						while (preds.hasNext()) {
							final Block pred = (Block) preds.next();
							final Expr op = phi.operandAt(pred);

							if (use == op) {
								weight += blockWeight(pred);
								break;
							}
						}

					} else if (use.parent() instanceof PhiCatchStmt) {
						// If the variable is used in a PhiCatchStmt, add the
						// weight of the block in which the variable is defined
						// to
						// the running total.
						weight += blockWeight(use.def().block());

					} else {
						// Just add in the weight of the block in which the
						// variable is used.
						weight += blockWeight(use.block());
					}
				}
			}
		}
	}
}
