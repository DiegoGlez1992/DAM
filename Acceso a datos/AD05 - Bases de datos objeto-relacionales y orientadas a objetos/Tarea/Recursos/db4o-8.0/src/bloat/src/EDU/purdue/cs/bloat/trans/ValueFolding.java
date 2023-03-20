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
package EDU.purdue.cs.bloat.trans;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * <tt>ValueFolding</tt> uses a <tt>ValueFolder</tt> to determine which
 * nodes in an expression tree can be removed or replaced by common expression
 * elimination and constant propagation.
 * 
 * @see ValueFolder
 */
public class ValueFolding {
	public static boolean DEBUG = false;

	SideEffectChecker sideEffects;

	ValueFolder folder;

	boolean changed;

	public static boolean DUMP = false;

	public static boolean SAVEDUMP = false;

	PrintWriter vn;

	PrintWriter dump;

	/**
	 * Performs value folding (common expression elimination and constant
	 * folding) on a control flow graph.
	 */
	int next = 1;

	public void transform(final FlowGraph cfg) {

		final File dir = new File("ValueFoldingOutdir");
		if (ValueFolding.DUMP) {
			// System.out.println(" Dumping Value Folding in directory: " +
			// dir);

			// try {
			// cfgFile = new File(dir, "cfg.txt");

			// cfg.print(new PrintWriter(new FileWriter(cfgFile), true));

			// dotFile = new File(dir, "cfg.dot");
			// cfg.printGraph(new PrintWriter(new FileWriter(dotFile),
			// true));

			// vnFile = new File(dir, "value.numbers");
			// vn = new PrintWriter(new FileWriter(vnFile), true);

			// dumpFile = new File(dir, "vn.dump");
			// dump = new PrintWriter(new FileWriter(dumpFile), true);

			// } catch (IOException ex) {

			// }

			vn = new PrintWriter(System.out, true);
			dump = new PrintWriter(System.out, true);
		}

		final EditorContext context = cfg.method().declaringClass().context();

		sideEffects = new SideEffectChecker(context);

		folder = new ValueFolder(true, context);

		final SSAGraph ssaGraph = new SSAGraph(cfg);

		ssaGraph.visitComponents(new ComponentVisitor() {
			public void visitComponent(final List scc) {
				// Maps Nodes in the SCC to their folded value
				final HashMap map = new HashMap(scc.size() * 2 + 1);

				boolean changed = true;

				while (changed) {
					changed = false;

					final Iterator iter = scc.iterator();

					int x = 0;
					while (iter.hasNext()) {
						final Node node = (Node) iter.next();

						if (ValueFolding.DUMP) {
							x++;
							dump.println("Folding SCC Node " + node + " (" + x
									+ " of " + scc.size() + ")");
						}

						if (fold(map, node)) {
							changed = true;
						}
					}

					if (ValueFolding.DUMP) {
						dump.println("");
					}

					if (scc.size() == 1) {
						break;
					}
				}
			}
		});

		cfg.removeUnreachable();

		folder = null;
		sideEffects = null;

		// Okay, we've successfully value folded, remove debugging files
		if (ValueFolding.DUMP && !ValueFolding.SAVEDUMP) {
			// cfgFile.delete();
			// dotFile.delete();
			// dumpFile.delete();
			// vnFile.delete();
			// dir.delete();
		}
	}

	/**
	 * Builds a mapping between the nodes in a CFG's SCCs and the expressions
	 * they are equivalent to.
	 * 
	 * @param map
	 *            A mapping between the SCCs of the CFG's SSA Graph
	 *            (definitions) and the expressions they are folded to
	 * @param sccNode
	 *            A Node in the SCC of the SSA Graph
	 * 
	 * @return True, if the value in the mapping was changed.
	 */
	boolean fold(final Map map, final Node sccNode) {
		Node node = (Node) map.get(sccNode);

		if (ValueFolding.DUMP) {
			dump
					.println("  SCC Node " + sccNode + " is mapped to node "
							+ node);
		}

		// The SCC node has not been folded yet, fold it to itself
		if (node == null) {
			node = sccNode;
		}

		if (!node.hasParent()) {
			return false;
		}

		if (ValueFolding.DEBUG) {
			System.out.println("folding --- " + node + " in " + node.parent());
		}

		if (ValueFolding.DUMP) {
			dump.println("  Folding " + node + " (" + "VN="
					+ node.valueNumber() + ") in " + node.parent());
		}

		final int v = node.valueNumber();

		if (v == -1) {
			// Node has not been assigned a value number, can't do anything
			return false;
		}

		folder.values.ensureSize(v + 1);
		final ConstantExpr oldValue = (ConstantExpr) folder.values.get(v);
		ConstantExpr value = null;

		if (ValueFolding.DUMP) {
			dump.println("    Node " + node + " is mapped to constant "
					+ oldValue);
		}

		if (node instanceof ConstantExpr) {
			// If the node that we're dealing with is already a
			// ConstantExpr, change it to the mapped value if it is
			// different.
			value = (ConstantExpr) node;

			if ((oldValue == null) || !oldValue.equalsExpr(value)) {
				// The node was not previously mapped to a constant, or it was
				// mapped to a different constant. Update the mapping to
				// relfect the new constant.
				if (ValueFolding.DEBUG) {
					System.out.println("changed " + oldValue + " to " + value);
				}

				if (ValueFolding.DUMP) {
					dump.println("      Changed " + oldValue + " to " + value);
				}

				folder.values.set(v, value);
				return true;
			}

			// Mapping was already correct, don't do anything.
			return false;
		}

		if ((node instanceof Expr) && (oldValue != null)) {
			// The node is a non-constant Expr that was mapped to a constant

			if (node.parent() instanceof PhiCatchStmt) {
				// Don't fold values inside PhiCatchStmts
				return false;
			}

			sideEffects.reset();
			node.visit(sideEffects);

			if (!sideEffects.hasSideEffects()) {
				// If the expression does not have side effects, then make a
				// clone of the value to which it was mapped and map the clone
				// to the original sccNode (which may or may not be node).
				// Technically, the mapping did not change.

				value = (ConstantExpr) oldValue.clone();
				node.replaceWith(value);
				map.put(sccNode, value);
				return false;
			}
		}

		if (value == null) {
			// The node is mapped to nothing, Use the ValueFolder to
			// determine a expression that node can be folded to.

			folder.node = null;
			node.visit(folder);

			if (ValueFolding.DEBUG) {
				System.out.println("folded " + node + " to " + folder.node);
			}

			if (ValueFolding.DUMP) {
				dump.println("    Using ValueFolder to determine new value");
				dump.println("      Folded " + node + " to " + folder.node);
			}

			if (folder.node != null) {
				// Assert.isTrue(folder.node.hasParent(),
				// "No parent for " + folder.node);
				map.put(sccNode, folder.node);
			}

			if (folder.node instanceof ConstantExpr) {
				// If the node was folded into a ConstantExpr, then fold it in
				// the ValueFolder.
				value = (ConstantExpr) folder.node;
				folder.values.set(v, value);
				return true;
			}
		}

		return false;
	}
}
