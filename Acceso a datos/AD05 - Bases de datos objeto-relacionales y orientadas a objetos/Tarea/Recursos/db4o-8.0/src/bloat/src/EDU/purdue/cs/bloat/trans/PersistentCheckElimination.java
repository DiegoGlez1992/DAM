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

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Attempts to remove residency checks an update checks from a control flow
 * graph.
 */
public class PersistentCheckElimination {
	public static boolean DEBUG = false;

	private static final int RC = 0;

	private static final int AUPDATE = 1;

	private static final int SUPDATE = 2;

	private static final int SIZE = 3; // Number of persistent opcodes

	private EditorContext context;

	/**
	 * Examines each residency check (<Tt>RCExpr</tt>) and update check (<tt>UCExpr</tt>)
	 * and determines whether or it is redundent. If a residency check checks
	 * something that we know is resident (i.e. the this pointer or the result
	 * of an object creation), then the check is redundent. Once an update check
	 * has been performed on a value, all subsequent checks are redundent.
	 * Redundent checks are removed from the control flow graph.
	 * 
	 * @see RCExpr
	 * @see UCExpr
	 */
	public void transform(final FlowGraph cfg) {
		context = cfg.method().declaringClass().context();

		final BitSet[] seen = new BitSet[PersistentCheckElimination.SIZE];

		for (int i = 0; i < PersistentCheckElimination.SIZE; i++) {
			seen[i] = new BitSet();
		}

		search(cfg, cfg.source(), seen);
	}

	/**
	 * Recursively searches the tree for residency statements that can be
	 * removed. The value numbers of expressions that create new objects are
	 * noted. The value number of the "this" pointer is also noted. If a
	 * residency check (RCExpr) is performed on any of these expressions, it is
	 * redundent and can be removed.
	 * 
	 * <p>
	 * 
	 * When an update check (UCExpr) is encountered each of its children is
	 * visited. The value numbers of expressions that are checked (i.e. wrapped
	 * in UCExpr) are noted. If an UCExpr is encountered that checks an
	 * expression with one of these value numbers, the check is redundent and
	 * can be removed.
	 * 
	 * @param seen
	 *            An array containing a BitSet for each type of
	 *            residency-related instruction (RC, AUPDATE, SUPDATE). Each bit
	 *            in the BitSet corresponds to an expression involving residency
	 *            being visited. That is, its value number is "seen".
	 */
	private void search(final FlowGraph cfg, final Block block,
			final BitSet[] seen) {
		// Accumulate the information stored in seen...
		// Save a copy of the contens of seen
		final BitSet[] save = new BitSet[PersistentCheckElimination.SIZE];

		for (int i = 0; i < PersistentCheckElimination.SIZE; i++) {
			save[i] = new BitSet(seen[i].size());
			save[i].or(seen[i]);
		}

		// Visit each expression in the tree.
		block.visit(new TreeVisitor() {
			// When we reach an expression that creates a new object
			// (NewArrayExpr, NewMultiArrayExpr, or NewExpr), set the bit in
			// the RC bit vector corresponding to the expression's value
			// number.
			public void visitNewArrayExpr(final NewArrayExpr expr) {
				expr.visitChildren(this);

				final int v = expr.valueNumber();
				Assert.isTrue(v != -1);
				seen[PersistentCheckElimination.RC].set(v);
			}

			public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
				expr.visitChildren(this);

				final int v = expr.valueNumber();
				Assert.isTrue(v != -1);
				seen[PersistentCheckElimination.RC].set(v);
			}

			public void visitNewExpr(final NewExpr expr) {
				expr.visitChildren(this);

				final int v = expr.valueNumber();
				Assert.isTrue(v != -1);
				seen[PersistentCheckElimination.RC].set(v);
			}

			// Find the value number of the LocalExpr corresponding to the
			// "this" variable. Set the bit in the RC bit vector
			// corresponding to the "this" pointer's value number.
			public void visitInitStmt(final InitStmt stmt) {
				stmt.visitChildren(this);

				final MethodEditor method = stmt.block().graph().method();

				if (!method.isStatic()) {
					Assert.isTrue(stmt.targets().length > 0);
					final int v = stmt.targets()[0].valueNumber();
					Assert.isTrue(v != -1);
					seen[PersistentCheckElimination.RC].set(v);
				}
			}

			// If the expression being checked by the RCExpr is either the
			// result of a "new" expression or it is the "this" pointer, we
			// know that it is resident and thus does not need to be
			// checked. All occurrences of the RCExpr are replaced with the
			// expression being checked. All of this is contingent on the
			// fact that the check does not have an ALIAS side effect.
			public void visitRCExpr(final RCExpr expr) {
				expr.visitChildren(this);

				final int v = expr.expr().valueNumber();
				Assert.isTrue(v != -1);

				final SideEffectChecker sideEffects = new SideEffectChecker(
						context);
				expr.expr().visit(sideEffects);
				final int flag = sideEffects.sideEffects();

				if (seen[PersistentCheckElimination.RC].get(v)
						&& ((flag & SideEffectChecker.ALIAS) == 0)) {
					// rc(x) --> x
					expr.expr().setParent(null);
					expr.replaceWith(expr.expr(), false);
					expr.cleanupOnly();
				}

				seen[PersistentCheckElimination.RC].set(v);
			}

			// If an object has already been updated, it does not need to be
			// updated again. Note that the children of the UCExpr are
			// visited first. There is a seen bit vector for both AUPDATE
			// and SUPDATE UCExpr. If the value number of the expression
			// being checked has already been encountered in a UCExpr, then
			// the UCExpr is redundent and can be replaced with the
			// expression being checked provided that it has no ALIAS side
			// effects.
			public void visitUCExpr(final UCExpr expr) {
				expr.visitChildren(this);

				final int v = expr.expr().valueNumber();
				Assert.isTrue(v != -1);

				final SideEffectChecker sideEffects = new SideEffectChecker(
						context);
				expr.expr().visit(sideEffects);
				final int flag = sideEffects.sideEffects();

				if (expr.kind() == UCExpr.POINTER) {
					if (seen[PersistentCheckElimination.AUPDATE].get(v)
							&& ((flag & SideEffectChecker.ALIAS) == 0)) {
						// aupdate(x) --> x
						expr.expr().setParent(null);
						expr.replaceWith(expr.expr(), false);
						expr.cleanupOnly();
					}

					seen[PersistentCheckElimination.AUPDATE].set(v);

				} else {
					if (seen[PersistentCheckElimination.SUPDATE].get(v)
							&& ((flag & SideEffectChecker.ALIAS) == 0)) {
						// supdate(x) --> x
						expr.expr().setParent(null);
						expr.replaceWith(expr.expr(), false);
						expr.cleanupOnly();
					}

					seen[PersistentCheckElimination.SUPDATE].set(v);
				}
			}
		});

		// Visit the blocks in the dominator tree in pre-order
		final Iterator children = cfg.domChildren(block).iterator();

		while (children.hasNext()) {
			final Block child = (Block) children.next();
			search(cfg, child, seen);
		}

		for (int i = 0; i < PersistentCheckElimination.SIZE; i++) {
			seen[i] = save[i];
		}
	}
}
