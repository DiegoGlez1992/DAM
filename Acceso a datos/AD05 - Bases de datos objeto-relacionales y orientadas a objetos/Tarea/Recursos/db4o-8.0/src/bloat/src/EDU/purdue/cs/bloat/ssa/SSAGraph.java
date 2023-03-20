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
package EDU.purdue.cs.bloat.ssa;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * The SSA graph (also called the value graph) represents the nesting of
 * expression in a control flow graph. Each node in the SSA graph represents an
 * expression. If the expression is a definition, the it is labeled with the
 * variable it defines. Each node has directed edges to the nodes representing
 * its operands.
 * 
 * <p>
 * 
 * <tt>SSAGraph</tt> is a representation of the definitions found in a CFG in
 * the following form: Each node in the graph is an expression that defines a
 * variable (a <tt>VarExpr</tt>, <tt>PhiStmt</tt>, or a
 * <tt>StackManipStmt</tt>). Edges in the graph point to the nodes whose
 * expressions define the operands of the expression in the source node.
 * 
 * <p>
 * 
 * This class is used primarily get the strongly connected components of the SSA
 * graph in support of value numbering and induction variable analysis.
 * 
 * <p>
 * 
 * Nate warns: Do not modify the CFG while using the SSA graph! The effects of
 * such modification are undefined and will probably lead to nasty things
 * occuring.
 * 
 * @see EDU.purdue.cs.bloat.trans.ValueNumbering ValueNumbering
 */
public class SSAGraph {
	public static boolean DEBUG = false;

	FlowGraph cfg;

	HashMap equiv; // A mapping between a Node and all its equivalent Nodes

	/**
	 * Grumble.
	 */
	public SSAGraph(final FlowGraph cfg, final boolean useless) {
		this(cfg);
	}

	/**
	 * Constructor. Traverse the control flow graph and determines which Nodes
	 * are of an equivalent Type.
	 * 
	 * @param cfg
	 *            The control flow graph to examine
	 */
	public SSAGraph(final FlowGraph cfg) {
		this.cfg = cfg;
		this.equiv = new HashMap();

		cfg.visit(new TreeVisitor() {
			// The CheckExpr and the Expr is checks are equivalent.
			public void visitCheckExpr(final CheckExpr expr) {
				expr.visitChildren(this);
				makeEquiv(expr, expr.expr());
			}

			// The target of the PhiStmt and the PhiStmt are equivalent
			public void visitPhiStmt(final PhiStmt stmt) {
				stmt.visitChildren(this);
				makeEquiv(stmt.target(), stmt);
			}

			// The use of a variable and its defining variable are equivalent
			public void visitVarExpr(final VarExpr expr) {
				if (!expr.isDef()) {
					final VarExpr def = (VarExpr) expr.def();

					if (def != null) {
						makeEquiv(expr, def);
					}
				}
			}

			// With StackManipStmts the stack slot (StackExpr) after the
			// StackManipStmt is equivalent to its corresponding slot before
			// the StackManipStmt.
			public void visitStackManipStmt(final StackManipStmt stmt) {
				final StackExpr[] target = stmt.target();
				final StackExpr[] source = stmt.source();

				switch (stmt.kind()) {
				case StackManipStmt.SWAP:
					// 0 1 -> 1 0
					Assert.isTrue((source.length == 2) && (target.length == 2),
							"Illegal statement: " + stmt);
					manip(source, target, new int[] { 1, 0 });
					break;
				case StackManipStmt.DUP:
					// 0 -> 0 0
					Assert.isTrue((source.length == 1) && (target.length == 2),
							"Illegal statement: " + stmt);
					manip(source, target, new int[] { 0, 0 });
					break;
				case StackManipStmt.DUP_X1:
					// 0 1 -> 1 0 1
					Assert.isTrue((source.length == 2) && (target.length == 3),
							"Illegal statement: " + stmt);
					manip(source, target, new int[] { 1, 0, 1 });
					break;
				case StackManipStmt.DUP_X2:
					if (source.length == 3) {
						// 0 1 2 -> 2 0 1 2
						Assert.isTrue((source.length == 3)
								&& (target.length == 4), "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 2, 0, 1, 2 });
					} else {
						// 0-1 2 -> 2 0-1 2
						Assert.isTrue((source.length == 2)
								&& (target.length == 3), "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 1, 0, 1 });
					}
					break;
				case StackManipStmt.DUP2:
					if (source.length == 2) {
						// 0 1 -> 0 1 0 1
						Assert.isTrue(target.length == 4, "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 0, 1, 0, 1 });
					} else {
						// 0-1 -> 0-1 0-1
						Assert.isTrue((source.length == 1)
								&& (target.length == 2), "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 0, 0 });
					}
					break;
				case StackManipStmt.DUP2_X1:
					if (source.length == 3) {
						// 0 1 2 -> 1 2 0 1 2
						Assert.isTrue(target.length == 5, "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 1, 2, 0, 1, 2 });
					} else {
						// 0 1-2 -> 1-2 0 1-2
						Assert.isTrue((source.length == 2)
								&& (target.length == 3), "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 1, 0, 1 });
					}
					break;
				case StackManipStmt.DUP2_X2:
					if (source.length == 4) {
						// 0 1 2 3 -> 2 3 0 1 2 3
						Assert.isTrue(target.length == 6, "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 2, 3, 0, 1, 2, 3 });
					} else if (source.length == 3) {
						if (target.length == 5) {
							// 0-1 2 3 -> 2 3 0-1 2 3
							manip(source, target, new int[] { 1, 2, 0, 1, 2 });
						} else {
							// 0 1 2-3 -> 2-3 0 1 2-3
							Assert.isTrue(target.length == 4,
									"Illegal statement: " + stmt);
							manip(source, target, new int[] { 2, 0, 1, 2 });
						}
					} else {
						// 0-1 2-3 -> 2-3 0-1 2-3
						Assert.isTrue((source.length == 2)
								&& (target.length == 3), "Illegal statement: "
								+ stmt);
						manip(source, target, new int[] { 1, 0, 1 });
					}
					break;
				}

				stmt.visitChildren(this);
			}

			// Determines equivalence of the StackExprs invovled in a
			// StackManipStmt. Recall that StackManipStmt are things like
			// the dup and swap instructions. So, elements (StackExprs) of
			// the "new" stack will be equivalent to elements of the "old"
			// stack. The s array defines the transformation.
			private void manip(final StackExpr[] source,
					final StackExpr[] target, final int[] s) {
				for (int i = 0; i < s.length; i++) {
					makeEquiv(target[i], source[s[i]]);
				}
			}

			// The StoreExpr is equivalent to the expression being stored.
			public void visitStoreExpr(final StoreExpr expr) {
				expr.visitChildren(this);
				makeEquiv(expr, expr.expr());

				if (expr.target() instanceof VarExpr) {
					makeEquiv(expr.target(), expr.expr());
				}
			}
		});
	}

	/**
	 * Returns the <tt>FlowGraph</tt> that this <tt>SSAGraph</tt> is built
	 * around.
	 */
	public FlowGraph cfg() {
		return (this.cfg);
	}

	/**
	 * Returns a set of nodes whose value is equivalent to a given node. For
	 * example, the LHS and RHS of an assignment are equivalent. As are all
	 * local variables with the same definition.
	 */
	public Set equivalent(final Node node) {
		Set s = (Set) equiv.get(node);

		if (s == null) {
			s = new HashSet(1);
			s.add(node); // A node is equivalent to itself
			equiv.put(node, s);
		}

		return s;
	}

	/**
	 * Makes node1 equivalent to node2 by adding the equivlance Set of node2 to
	 * the equivalance Set of node1, and vice versa.
	 */
	void makeEquiv(final Node node1, final Node node2) {
		final Set s1 = equivalent(node1);
		final Set s2 = equivalent(node2);

		if (s1 != s2) {
			s1.addAll(s2);

			final Iterator iter = s2.iterator();

			while (iter.hasNext()) {
				final Node n = (Node) iter.next();
				equiv.put(n, s1);
			}
		}
	}

	/**
	 * Returns the children (that is, the operands) of a given Node in the SSA
	 * Graph.
	 */
	public List children(final Node node) {
		final ArrayList c = new ArrayList();

		if (node instanceof StoreExpr) {
			final StoreExpr store = (StoreExpr) node;

			// Add the grand children of RHS. The RHS is equivalent to
			// this node.
			store.expr().visitChildren(new TreeVisitor() {
				public void visitNode(final Node node) {
					c.add(node);
				}
			});

			// The LHS is equivalent to this node if it is a VarExpr and not
			// a child.
			if (!(store.target() instanceof VarExpr)) {
				c.add(store.target());
			}

		} else if (node instanceof PhiStmt) {
			final PhiStmt phi = (PhiStmt) node;
			c.addAll(phi.operands());

		} else {
			node.visitChildren(new TreeVisitor() {
				public void visitNode(final Node node) {
					c.add(node);
				}
			});
		}

		return c;
	}

	/**
	 * Returns the Sets of Nodes whose values are equivalent.
	 */
	public Collection equivalences() {
		return equiv.values();
	}

	class Count {
		int value = 0;
	}

	/**
	 * Calculates the strongly connected components (SCC) of the SSA graph. SSCs
	 * are represented by a List of <tt>Node</tt>s. The SCCs are then visited
	 * by the ComponentVistor.
	 */
	public void visitComponents(final ComponentVisitor visitor) {
		// Number the nodes reverse post order (i.e. topological order).

		final Count count = new Count();

		final List postOrder = cfg.postOrder();
		final ListIterator iter = postOrder.listIterator(postOrder.size());

		// Perform a depth-first ordering of the nodes in the CFG to give
		// each node a unique identifier. This is accomplished by
		// visiting the blocks in the CFG in post-order and numbering the
		// Nodes in the block's expression Tree in depth-first order.
		while (iter.hasPrevious()) {
			final Block block = (Block) iter.previous();

			block.visit(new TreeVisitor() {
				public void visitTree(final Tree tree) {
					tree.visitChildren(this);
				}

				public void visitNode(final Node node) {
					node.visitChildren(this);
					node.setKey(count.value++);
				}
			});
		}

		// Build the (strongly connected) components and call
		// visitor.visitComponent for each.
		cfg.visit(new TreeVisitor() {
			ArrayList stack = new ArrayList();

			BitSet onStack = new BitSet(count.value + 1);

			int[] low = new int[count.value + 1];

			int[] dfs = new int[count.value + 1];

			int dfsNumber = 1;

			Node parent; // Parent in the SSA graph

			// Visit the blocks in the CFG in reverse postorder
			public void visitFlowGraph(final FlowGraph cfg) {
				final ListIterator e = postOrder.listIterator(postOrder.size());

				while (e.hasPrevious()) {
					final Block block = (Block) e.previous();
					block.visit(this);
				}
			}

			public void visitTree(final Tree tree) {
				parent = null;
				tree.visitChildren(this);
			}

			// This method is essentially Figure 4.6 in Taylor Simpson's PhD
			// Thesis: www.cs.rice.edu/~lts. The implementation is a little
			// funky, though, because someone wanted to use visitors.
			// Grumble.
			public void visitNode(final Node node) {
				int dfn = dfs[node.key()];
				// System.out.println("visit " + node + " key=" + node.key() +
				// " dfn=" + dfn);

				if (dfn == 0) {
					// The node in question has not yet been visited. Assign it
					// the next dfNumber and add it to the stack. Mark all
					// nodes that are equivalent to the node in question as
					// being visited.

					dfn = dfsNumber++;
					low[dfn] = dfn;

					stack.add(node);
					onStack.set(dfn);

					Iterator equiv = equivalent(node).iterator();

					while (equiv.hasNext()) {
						final Node e = (Node) equiv.next();
						dfs[e.key()] = dfn;
					}

					// Again examine each node, e, equivalent to the node in
					// question. Then recursively visit the children of e in
					// the SSA Graph.
					final Node grandParent = parent;
					parent = node;

					equiv = equivalent(node).iterator();

					while (equiv.hasNext()) {
						final Node e = (Node) equiv.next();

						final Iterator children = children(e).iterator();

						while (children.hasNext()) {
							final Node child = (Node) children.next();
							child.visit(this);
						}
					}

					parent = grandParent; // Restore true parent

					// Now we finally get to the point where we can construct a
					// strongly connected component. Pop all of the nodes off
					// the stack until the node in question is reached.
					if (low[dfn] == dfn) {
						final ArrayList scc = new ArrayList();

						while (!stack.isEmpty()) {
							final Node v = (Node) stack
									.remove(stack.size() - 1);
							onStack.clear(dfs[v.key()]);
							scc.addAll(equivalent(v));

							if (v == node) {
								break;
							}
						}

						// Sort the nodes in the SCC by their reverse
						// post order numbers.
						Collections.sort(scc, new Comparator() {
							public int compare(final Object a, final Object b) {
								final int ka = ((Node) a).key();
								final int kb = ((Node) b).key();
								return ka - kb;
							}
						});

						if (SSAGraph.DEBUG) {
							System.out.print("SCC =");

							final Iterator e = scc.iterator();

							while (e.hasNext()) {
								final Node v = (Node) e.next();
								System.out.print(" " + v + "{" + v.key() + "}");
							}

							System.out.println();
						}

						// Visit the SCC with the visitor that was passed in.
						visitor.visitComponent(scc);
					}

					if (parent != null) {
						final int parentDfn = dfs[parent.key()];
						low[parentDfn] = Math.min(low[parentDfn], low[dfn]);
					}

				} else {
					// We've already visited the node in question
					if (parent != null) {
						final int parentDfn = dfs[parent.key()];

						// (parent, node) is either a cross edge or a back edge.
						if ((dfn < parentDfn) && onStack.get(dfn)) {
							low[parentDfn] = Math.min(low[parentDfn], dfn);
						}
					}
				}
			}
		});
	}

	/**
	 * Visits the strongly connected component that contains a given
	 * <tt>Node</tt>.
	 */
	public void visitComponent(final Node startNode,
			final ComponentVisitor visitor) {
		// Number the nodes reverse post order (i.e. topological order).

		final Count count = new Count();

		final List postOrder = cfg.postOrder();
		final ListIterator iter = postOrder.listIterator(postOrder.size());

		// Perform a depth-first ordering of the nodes in the CFG to give
		// each node a unique identifier. This is accomplished by
		// visiting the blocks in the CFG in post-order and numbering the
		// Nodes in the block's expression Tree in depth-first order.
		while (iter.hasPrevious()) {
			final Block block = (Block) iter.previous();

			block.visit(new TreeVisitor() {
				public void visitTree(final Tree tree) {
					tree.visitChildren(this);
				}

				public void visitNode(final Node node) {
					node.visitChildren(this);
					node.setKey(count.value++);
				}
			});
		}

		// Build the (strongly connected) components and call
		// visitor.visitComponent for each.
		cfg.visit(new TreeVisitor() {
			ArrayList stack = new ArrayList();

			BitSet onStack = new BitSet(count.value + 1);

			int[] low = new int[count.value + 1];

			int[] dfs = new int[count.value + 1];

			int dfsNumber = 1;

			Node parent; // Parent in the SSA graph

			// Visit the blocks in the CFG in reverse postorder
			public void visitFlowGraph(final FlowGraph cfg) {
				final ListIterator e = postOrder.listIterator(postOrder.size());

				while (e.hasPrevious()) {
					final Block block = (Block) e.previous();
					block.visit(this);
				}
			}

			public void visitTree(final Tree tree) {
				parent = null;
				tree.visitChildren(this);
			}

			// This method is essentially Figure 4.6 in Taylor Simpson's PhD
			// Thesis: www.cs.rice.edu/~lts. The implementation is a little
			// funky, though, because someone wanted to use visitors.
			// Grumble.
			public void visitNode(final Node node) {
				int dfn = dfs[node.key()];
				// System.out.println("visit " + node + " key=" + node.key() +
				// " dfn=" + dfn);

				if (dfn == 0) {
					// If this node isn't equivalent to the node the care about,
					// fergit it!
					if (!equivalent(node).contains(startNode)) {
						return;
					}

					// The node in question has not yet been visited. Assign it
					// the next dfNumber and add it to the stack. Mark all
					// nodes that are equivalent to the node in question as
					// being visited.

					dfn = dfsNumber++;
					low[dfn] = dfn;

					stack.add(node);
					onStack.set(dfn);

					Iterator equiv = equivalent(node).iterator();

					while (equiv.hasNext()) {
						final Node e = (Node) equiv.next();
						dfs[e.key()] = dfn;
					}

					// Again examine each node, e, equivalent to the node in
					// question. Then recursively visit the children of e in
					// the SSA Graph.
					final Node grandParent = parent;
					parent = node;

					equiv = equivalent(node).iterator();

					while (equiv.hasNext()) {
						final Node e = (Node) equiv.next();

						final Iterator children = children(e).iterator();

						while (children.hasNext()) {
							final Node child = (Node) children.next();
							child.visit(this);
						}
					}

					parent = grandParent; // Restore true parent

					// Now we finally get to the point where we can construct a
					// strongly connected component. Pop all of the nodes off
					// the stack until the node in question is reached.
					if (low[dfn] == dfn) {
						final ArrayList scc = new ArrayList();

						while (!stack.isEmpty()) {
							final Node v = (Node) stack
									.remove(stack.size() - 1);
							onStack.clear(dfs[v.key()]);
							scc.addAll(equivalent(v));

							if (v == node) {
								break;
							}
						}

						// Sort the nodes in the SCC by their reverse
						// post order numbers.
						Collections.sort(scc, new Comparator() {
							public int compare(final Object a, final Object b) {
								final int ka = ((Node) a).key();
								final int kb = ((Node) b).key();
								return ka - kb;
							}
						});

						if (SSAGraph.DEBUG) {
							System.out.print("SCC =");

							final Iterator e = scc.iterator();

							while (e.hasNext()) {
								final Node v = (Node) e.next();
								System.out.print(" " + v + "{" + v.key() + "}");
							}

							System.out.println();
						}

						// Visit the SCC with the visitor that was passed in.
						visitor.visitComponent(scc);
					}

					if (parent != null) {
						final int parentDfn = dfs[parent.key()];
						low[parentDfn] = Math.min(low[parentDfn], low[dfn]);
					}

				} else {
					// We've already visited the node in question
					if (parent != null) {
						final int parentDfn = dfs[parent.key()];

						// (parent, node) is either a cross edge or a back edge.
						if ((dfn < parentDfn) && onStack.get(dfn)) {
							low[parentDfn] = Math.min(low[parentDfn], dfn);
						}
					}
				}
			}
		});
	}

	/**
	 * Prints a textual representation of the strongly connected components of
	 * the SSAGraph to a PrintWriter.
	 */
	public void printSCCs(final PrintWriter pw) {
		final Collection equivs = this.equivalences(); // A Collection of Sets
		final Iterator iter = equivs.iterator();

		pw.println("Strongly Connected Components of the SSAGraph");

		for (int i = 1; iter.hasNext(); i++) {
			final Set scc = (Set) iter.next();
			final Iterator sccIter = scc.iterator();

			pw.println("  Component " + i);

			while (sccIter.hasNext()) {
				final Node node = (Node) sccIter.next();
				pw.println("    " + node + " [VN = " + node.valueNumber()
						+ ", ID = " + System.identityHashCode(node) + "]");
			}
		}
	}
}
