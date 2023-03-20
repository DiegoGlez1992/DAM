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
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Performs value numbering analysis on the nodes in a control flow graph. Nodes
 * with identical value numbers are folded into one another so that common
 * (redundent) expressions are eliminated. Note that ValueNumbering works on the
 * SSAGraph for the CFG and not the CFG itself.
 * 
 * @see SSAGraph
 */
// L.T. Simpson 1996. Value-driven redundancy elimination. PhD
// Thesis. Rice University. Look it up. Chapter 4 contains the
// stuff on SCC-based value numbering.
public class ValueNumbering {
	public static boolean DEBUG = false;

	SSAGraph ssaGraph;

	HashMap tuples; // Maps a Node to its Tuple

	ValueFolder folder;

	int next; // The next value number to assign

	public String debugDirName = "debug";

	public File debugDir;

	public boolean DUMP = false;

	private PrintWriter dump = new PrintWriter(System.out);

	/**
	 * Performs value numbering on a control flow graph.
	 * 
	 * @see ComponentVisitor
	 * @see SSAGraph
	 * @see ValueFolder
	 */
	public void transform(final FlowGraph cfg) {
		// Specify directory into which all debugging files should be
		// placed

		if (DUMP || ValueFolding.DUMP) {
			final String className = cfg.method().declaringClass().type()
					.className();
			final String methodName = cfg.method().name();

			final String dirName = debugDirName + File.separator + className
					+ File.separator + methodName;

			debugDir = new File(dirName);
			for (int nextDir = 1; debugDir.exists(); nextDir++) {
				// Multiple directories
				debugDir = new File(dirName + "_" + nextDir);
			}

			if (!debugDir.exists()) {
				debugDir.mkdirs();
			}

			// General dumping file
			try {
				final File dumpFile = new File(debugDir, "vn_dump");
				dump = new PrintWriter(new FileWriter(dumpFile), true);
			} catch (final IOException ex) {
				System.err.println(ex.toString());
			}
		}

		ssaGraph = new SSAGraph(cfg);
		tuples = new HashMap();
		folder = new ValueFolder(false, cfg.method().declaringClass().context());
		next = 0;

		final HashMap valid = new HashMap();
		final HashMap optimistic = new HashMap();

		ssaGraph.visitComponents(new ComponentVisitor() {
			public void visitComponent(final List scc) {
				if (ValueNumbering.DEBUG || DUMP) {
					dump.println("\nNumbering SCC = " + scc);
				}

				final Iterator e = scc.iterator();

				while (e.hasNext()) {
					final Node node = (Node) e.next();
					node.setValueNumber(-1);
				}

				if (scc.size() > 1) {
					if (ValueNumbering.DEBUG || DUMP) {
						dump.println("Optimistic-----------------------");
					}

					boolean changed = true;

					while (changed) {
						changed = false;

						final Iterator iter = scc.iterator();

						while (iter.hasNext()) {
							final Node node = (Node) iter.next();

							if (valnum(node, optimistic)) {
								changed = true;
							}
						}
					}
				}

				if (ValueNumbering.DEBUG || DUMP) {
					dump.println("Valid--------------------------------");
				}

				// The valid table contains the correct value numbers. Run
				// through the each node in the SCC and call valnum.
				// Presumably, the nodes are in reverse postorder.
				final Iterator iter = scc.iterator();

				while (iter.hasNext()) {
					final Node node = (Node) iter.next();
					valnum(node, valid);
				}
			}
		});

		if (ValueNumbering.DEBUG || DUMP) {
			dump.println("Final value numbers--------------------------");
			printValueNumbers(cfg, new PrintWriter(dump));
		}

		if (DUMP) {
			System.out.println("    Dumping to: " + debugDir);

			try {
				final File valueNumbers = new File(debugDir, "scc.txt");
				ssaGraph
						.printSCCs(new PrintWriter(new FileWriter(valueNumbers)));
			} catch (final IOException ex) {
				System.err.println("IOException: " + ex);
			}
		}

		ssaGraph = null;
		tuples = null;

		folder.cleanup();
		folder = null;

		// Make sure each node has a value number
		cfg.visit(new TreeVisitor() {
			public void visitTree(final Tree tree) {
				tree.visitChildren(this);
			}

			public void visitNode(final Node node) {
				node.visitChildren(this);
				Assert.isTrue(node.valueNumber() != -1, "No value number for "
						+ node);
			}
		});
	}

	private void printValueNumbers(final FlowGraph cfg, final PrintWriter pw) {
		cfg.visit(new TreeVisitor() {
			public void visitTree(final Tree tree) {
				tree.visitChildren(this);
			}

			public void visitNode(final Node node) {
				node.visitChildren(this);

				pw.println("VN[" + node + " " + System.identityHashCode(node) +
				// " == " + ssaGraph.equivalent(node) + " " +
						// System.identityHashCode(ssaGraph.equivalent(node)) +
						"] = " + node.valueNumber());
			}
		});
	}

	/**
	 * Simplifies a node by examining its type. A ValueFolder may be used to
	 * perform simplification.
	 * 
	 * @return The folded (simplified) value of the node (which may be the same
	 *         as the node itself)
	 */
	private Node simplify(final Node node) {
		if (ValueNumbering.DEBUG || DUMP) {
			dump.println("folding " + node + " in " + node.parent());
		}

		final int v = node.valueNumber();

		// A value number of -1 (i.e. value number has not yet been
		// assigned) cannot be simplified.
		if (v == -1) {
			return node;
		}

		// A constant expression can't be simplified, set the value of
		// value number v to be node
		if (node instanceof ConstantExpr) {
			folder.values.ensureSize(v + 1);
			folder.values.set(v, node);
			return node;
		}

		// Check for the value number in the folder.
		if (v < folder.values.size()) {
			final ConstantExpr value = (ConstantExpr) folder.values.get(v);

			if (value != null) {
				return value;
			}
		}

		// Else, use a ValueFolder to fold the node
		folder.node = null;
		node.visit(folder);

		if (ValueNumbering.DEBUG || DUMP) {
			dump.println("folded " + node + " to " + folder.node);
		}

		if (folder.node == null) {
			// Nothing changed
			return node;
		}

		// If we folded the node into a constant expression, add it to
		// the values list
		if (folder.node instanceof ConstantExpr) {
			folder.values.ensureSize(v + 1);
			folder.values.set(v, folder.node);
		}

		return folder.node;
	}

	/**
	 * Processes a Node in an SCC.
	 */
	private boolean valnum(final Node node, final HashMap table) {
		boolean changed = false; // Did the table change?

		Tuple tuple = (Tuple) tuples.get(node);

		if (tuple == null) {
			// Make a new Tuple for the node being processed
			final Node s = simplify(node);

			tuple = new Tuple(s);
			tuples.put(node, tuple);

			if (ValueNumbering.DEBUG || DUMP) {
				dump.println("  New tuple " + tuple);
			}

		} else if (DUMP) {
			dump.println("  " + node + " mapped to tuple " + tuple);
		}

		final Node w = (Node) table.get(tuple);

		if (ValueNumbering.DEBUG || DUMP) {
			dump.println("  Looking up " + tuple);
			dump.println("    " + tuple + " mapped to node " + w
					+ (w != null ? " (VN = " + w.valueNumber() + ")" : ""));
		}

		int value = -1;

		if ((w != null) && (w.valueNumber() != -1)) {
			value = w.valueNumber();

		} else {
			if (ValueNumbering.DEBUG || DUMP) {
				dump.println("    New value number " + next);
			}

			value = next++;
		}

		Assert.isTrue(value != -1);

		// Now make sure all equivalent nodes have the same value number.
		final Iterator iter = ssaGraph.equivalent(node).iterator();

		while (iter.hasNext()) {
			final Node v = (Node) iter.next();

			final Tuple t = (Tuple) tuples.get(v);

			if (t == null) {
				// Will get done later.
				continue;
			}

			if (v.valueNumber() != value) {
				v.setValueNumber(value);
				table.put(t, v);

				if (ValueNumbering.DEBUG || DUMP) {
					dump.println("    Assigning value number "
							+ v.valueNumber() + " to " + v);
					dump.println("    Mapping tuple " + t + " to node " + v);
				}

				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Tuple contains a Node and an associated hash value. The Node is the
	 * simplified version of another node. The main purpose of the Tuple class
	 * is to compare two Nodes to determine if they are the same with respect to
	 * their value numbers.
	 */
	class Tuple {
		Node node;

		int hash;

		public Tuple(final Node node) {
			this.node = node;
			final List children = ssaGraph.children(node);
			this.hash = NodeComparator.hashCode(node) + children.size();
		}

		public String toString() {
			final List children = ssaGraph.children(node);

			String s = "<" + node + ", hash=" + hash;

			final Iterator iter = children.iterator();

			while (iter.hasNext()) {
				final Node child = (Node) iter.next();
				s += ", " + child + "{" + child.valueNumber() + "}";
			}

			s += ">";

			return s;
		}

		public int hashCode() {
			return hash;
		}

		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof Tuple) {
				final Tuple t = (Tuple) obj;

				if (node == t.node) {
					return true;
				}

				// All mem refs are unequal.
				if ((node instanceof MemRefExpr)
						|| (t.node instanceof MemRefExpr)) {
					return false;
				}

				if (!NodeComparator.equals(node, t.node)) {
					return false;
				}

				final List children1 = ssaGraph.children(node);
				final List children2 = ssaGraph.children(t.node);

				if (children1.size() != children2.size()) {
					return false;
				}

				if (node instanceof PhiStmt) {
					// The order of the children does not matter
					final int[] used = new int[next];
					int free = 0; // The number of un-numbered children

					Iterator iter = children1.iterator();

					while (iter.hasNext()) {
						final Node child = (Node) iter.next();
						final int v = child.valueNumber();

						if (v != -1) {
							used[v]++;

						} else {
							free++;
						}
					}

					iter = children2.iterator();

					while (iter.hasNext()) {
						final Node child = (Node) iter.next();
						final int v = child.valueNumber();

						if (v != -1) {
							if (used[v] != 0) {
								used[v]--;

							} else {
								free--;
							}

						} else {
							free--;
						}
					}

					if (free < 0) {
						return false;
					}

					return true;

				} else {
					// The children of the nodes in the SSAGraph must have the
					// same value numbers and be in the same order.
					final Iterator iter1 = children1.iterator();
					final Iterator iter2 = children2.iterator();

					while (iter1.hasNext() && iter2.hasNext()) {
						final Node child1 = (Node) iter1.next();
						final Node child2 = (Node) iter2.next();

						final int v1 = child1.valueNumber();
						final int v2 = child2.valueNumber();

						if ((v1 != -1) && (v2 != -1) && (v1 != v2)) {
							return false;
						}
					}

					if (iter1.hasNext() || iter2.hasNext()) {
						// Size mismatch.
						return false;
					}

					return true;
				}
			}

			return false;
		}
	}
}
