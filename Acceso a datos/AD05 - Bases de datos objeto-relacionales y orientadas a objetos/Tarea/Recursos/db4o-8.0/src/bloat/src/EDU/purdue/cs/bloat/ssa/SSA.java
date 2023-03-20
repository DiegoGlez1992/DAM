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

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Compute the SSA form of the control flow graph and build FUD chains.
 * <p>
 * The SSA algorithm is from:
 * 
 * <pre>
 *     R. Cytron, J. Ferrante J, B. K. Rosen, M. N. Wegman, and F. K. Zadeck,
 *     &quot;Efficiently Computing Static Single Assignment Form and the Control
 *     Dependence Graph&quot;, TOPLAS, 13(4): 451-490, October 1991.
 * </pre>
 * 
 * <p>
 * I made modifications to the algorithm to compute FUD chains and to run the
 * algorithm separately for each variable similar to the SSAPRE algorithm.
 * Making a separate pass for each variable allows variables to be added
 * incrementally.
 */
public class SSA {
	public static boolean DEBUG = false;

	/**
	 * Transforms a control flow graph into Single Static Assignment (SSA) form.
	 * First, the CFG is traversed and a list of all variables (both local and
	 * stack) eligible for SSA renaming is compiled. Variables are represented
	 * by instances of <tt>SSAConstructionInfo</tt>. Each of these variables
	 * is then transformed.
	 * 
	 * @see #transform(FlowGraph)
	 * @see SSAConstructionInfo
	 */
	public static void transform(final FlowGraph cfg) {
		final Iterator e = SSA.collectVars(cfg);

		while (e.hasNext()) {
			final SSAConstructionInfo info = (SSAConstructionInfo) e.next();
			SSA.transform(cfg, info);
		}
	}

	/**
	 * Performs the actions necessary to convert a CFG into SSA form with
	 * respect to one variable. The variable's information is stored in the
	 * <tt>SSAConstructionInfo</tt>.
	 */
	public static void transform(final FlowGraph cfg,
			final SSAConstructionInfo info) {
		if (SSA.DEBUG) {
			System.out.println("transforming " + info.prototype + " ("
					+ info.prototype.type() + ")");
		}

		SSA.placePhiFunctions(cfg, info);
		SSA.rename(cfg, info);
		SSA.insertCode(cfg, info);
	}

	/**
	 * Visits the nodes in a control flow graph and constructs
	 * <tt>SSAConstructionInfo</tt> objects for each variable in the CFG.
	 * Returns the <tt>SSAConstructionInfo</tt>s for the variables in the
	 * CFG.
	 */
	private static Iterator collectVars(final FlowGraph cfg) {
		// SSAConstructionInfo objects for cfg
		final Map infos = new HashMap();

		cfg.visit(new TreeVisitor() {
			// Visit all statements in the CFG. Remove any pre-existing
			// PhiStmts.
			public void visitTree(final Tree tree) {
				final Iterator iter = tree.stmts().iterator();

				while (iter.hasNext()) {
					final Stmt stmt = (Stmt) iter.next();

					if (stmt instanceof PhiStmt) {
						iter.remove();

					} else {
						stmt.visit(this);
					}
				}
			}

			// Recall that VarExprs represent variables. If we have not
			// already created a SSAConstructionInfo for a variable
			// (VarExpr), do so. Make note of the fact that this is a real
			// occurrence of the variable.
			public void visitVarExpr(final VarExpr expr) {
				expr.visitChildren(this);

				expr.setDef(null);

				final Object key = expr.comparator();

				SSAConstructionInfo info = (SSAConstructionInfo) infos.get(key);

				if (info == null) {
					info = new SSAConstructionInfo(cfg, expr);
					infos.put(key, info);
				}

				info.addReal(expr);
			}
		});

		return infos.values().iterator();
	}

	/**
	 * Places phi statements at the appropriate locations in the CFG. This
	 * implementation only places phi functions for variables that are live on
	 * entry to at least one block. That is, if a variable is only used within
	 * one block, we don't bother searching for a place to put phi functions for
	 * it.
	 * 
	 * @param cfg
	 *            The CFG in which phi functions are placed.
	 * @param info
	 *            The variable for which phi functions will be placed.
	 */
	private static void placePhiFunctions(final FlowGraph cfg,
			final SSAConstructionInfo info) {
		if (SSA.DEBUG) {
			System.out.println("Placing phi-functions for " + info);
		}

		// Phis are only placed for variables which are live on entry to
		// at least one block.
		//
		// This is the semi-pruned form described in "Practical
		// Improvements to the Construction and Destruction of Static
		// Single Assignment Form" by Briggs, Cooper, Harvey, Simpson
		//

		// Blocks in which the variable in the SSAConstructionInfo is
		// defined. That is, variables that are defined in this block.
		final BitSet killed = new BitSet(cfg.size());

		// Is the variable used in more than one block?
		boolean nonLocal = false;

		final Iterator reals = info.reals().iterator();

		// Look at all real (not in phi statement) occurrences of the
		// variable in the SSAConstructionInfo. Determine which variables
		// are live on entry to some basic block (i.e. "non-local"). If
		// a variable is not live on entry to some basic block, it is only
		// used within the block in which it is defined, so don't bother
		// adding a phi statement for it.
		while (reals.hasNext()) {
			final VarExpr real = (VarExpr) reals.next();

			final Block block = real.block(); // Block in which variable
												// occurs

			if (real.isDef()) {
				killed.set(cfg.preOrderIndex(block));

			} else if (!killed.get(cfg.preOrderIndex(block))) {
				// There is a use of the variable as an operand that is not
				// defined in the block in which it occurs. Therefore, the
				// variable is non-local.
				nonLocal = true;
				break;
			}
		}

		if (!nonLocal) {
			return;
		}

		// We've decided that this variable is used in multiple blocks,
		// so go ahead and place phi functions for it.

		// Iterate over all of the catch blocks (blocks that begin an
		// exception handler) in the CFG and instert PhiCatchStmts where
		// appropriate.
		final Iterator catchBlocks = cfg.catchBlocks().iterator();

		while (catchBlocks.hasNext()) {
			final Block block = (Block) catchBlocks.next();
			info.addCatchPhi(block);
			info.addDefBlock(block);
		}

		// Iterate over all of the subroutines (finally blocks) and insert
		// PhiReturnStmts where appropriate.
		final Iterator subs = cfg.subroutines().iterator();

		while (subs.hasNext()) {
			final Subroutine sub = (Subroutine) subs.next();
			info.addRetPhis(sub);

			final Iterator paths = sub.paths().iterator();

			while (paths.hasNext()) {
				final Block[] path = (Block[]) paths.next();
				info.addDefBlock(path[1]);
			}
		}

		// Now we add real phi functions to the CFG. Phi functions are
		// placed at the (blocks in the) iterated dominance fontier of each
		// of the blocks containing a definition of the variable.
		final Iterator df = cfg.iteratedDomFrontier(info.defBlocks())
				.iterator();

		while (df.hasNext()) {
			final Block block = (Block) df.next();

			// Don't place phi-statements in the exit block because one of
			// the operands will always have a null definition.
			if (block != cfg.sink()) {
				info.addPhi(block);
			}
		}
	}

	/**
	 * If the block resides in a protected region and there is a
	 * <tt>PhiCatchStmt</tt> for the variable in question in the handler of
	 * the exception thrown by the protected region (meaning that the variable
	 * is used in the protected region), the variable becomes an operand to the
	 * <tt>PhiCatchStmt</tt>.
	 * 
	 * @param info
	 *            The variable (LocalExpr) that we're dealing with
	 * @param block
	 *            The block in a potentially protected region. If the block is
	 *            indeed in a protected region, the occurrence of the the
	 *            variable represented by info becomes an operand to the
	 *            PhiCatchStmt at the beginning of the protected region's
	 *            handler.
	 * @param def
	 *            The defining occurrence of the variable stored in info.
	 */
	private static void addCatchPhiOperands(final SSAConstructionInfo info,
			final Block block, final LocalExpr def) {
		final Iterator handlers = block.graph().handlers().iterator();

		// Iterate over all of the exception handlers in the CFG. If
		// the block we are dealing with is a protected block (that is,
		// is inside a try block), then the variable represented by info
		// becomes an operand to the PhiCatchStmt residing at the
		// beginning of the protected block's handler.
		while (handlers.hasNext()) {
			final Handler handler = (Handler) handlers.next();

			if (handler.protectedBlocks().contains(block)) {
				final PhiCatchStmt phi = (PhiCatchStmt) info.phiAtBlock(handler
						.catchBlock());

				if ((phi != null) && !phi.hasOperandDef(def)) {
					final LocalExpr operand = (LocalExpr) info.prototype
							.clone();
					operand.setDef(def); // ???
					phi.addOperand(operand);
				}
			}
		}
	}

	/**
	 * The actual renamining is done by the search method. This method just
	 * takes care of <Tt>PhiReturnStmts</tt>.
	 */
	private static void rename(final FlowGraph cfg,
			final SSAConstructionInfo info) {
		SSA.search(cfg, info, null, cfg.source());

		// Eliminate PhiReturns by replacing their uses with the defs live
		// at the end of the returning sub or live on the same path on entry
		// to the sub (if the variable did not occur in the subroutine).

		// Examine each PhiReturnStmt in the CFG. Recall that
		// PhiReturnStmts are "inserted" at blocks that begin exceptions
		boolean changed = true;

		while (changed) {
			changed = false;

			final Iterator subs = cfg.subroutines().iterator();

			while (subs.hasNext()) {
				final Subroutine sub = (Subroutine) subs.next();
				final Iterator paths = sub.paths().iterator();

				final PhiJoinStmt entry = (PhiJoinStmt) info.phiAtBlock(sub
						.entry());

				if (entry == null) {
					// If there was no PhiJoinStmt for the variable in the
					// subroutine, who cares? We don't.
					continue;
				}

				while (paths.hasNext()) {
					final Block[] path = (Block[]) paths.next();

					final PhiReturnStmt ret = (PhiReturnStmt) info
							.phiAtBlock(path[1]);

					if (ret != null) {
						DefExpr def = ret.operand().def();

						if (def != entry.target()) {
							// If the operand of the PhiReturnStmt is different
							// from
							// the new SSA variable defined by the PhiCatchStmt
							// at
							// the beginning of the subroutine, then the
							// variable
							// was defined in the subroutine, so the operand to
							// the
							// PhiReturnStmt is the correct SSA variable. This
							// is
							// like the variable "b" in figure 3.5 in Nate's
							// Thesis.
							continue;
						}

						// Replace all uses of the target of the PhiReturnStmt
						// with the SSA variable corresponding to the block in
						// which the jsr occured. This is like variable "a" in
						// figure 3.5 in Nate's Thesis.
						def = ((VarExpr) entry.operandAt(path[0])).def();

						final Iterator uses = ret.target().uses().iterator();

						while (uses.hasNext()) {
							final VarExpr use = (VarExpr) uses.next();
							use.setDef(def);
						}

						// The PhiReturnStmt is no longer needed
						info.removePhiAtBlock(path[1]);
						changed = true;
					}
				}
			}
		}

		final Iterator subs = cfg.subroutines().iterator();

		// Examine any remaining PhiReturnStmts. Replace all uses of the
		// target of the PhiReturnStmt with its operand.
		while (subs.hasNext()) {
			final Subroutine sub = (Subroutine) subs.next();

			final Iterator paths = sub.paths().iterator();

			while (paths.hasNext()) {
				final Block[] path = (Block[]) paths.next();

				final PhiReturnStmt ret = (PhiReturnStmt) info
						.phiAtBlock(path[1]);

				if (ret != null) {
					final DefExpr def = ret.operand().def();

					final Iterator uses = ret.target().uses().iterator();

					while (uses.hasNext()) {
						final VarExpr use = (VarExpr) uses.next();
						use.setDef(def);
					}

					info.removePhiAtBlock(path[1]);
				}
			}
		}
	}

	/**
	 * Does the actual renaming. It keeps track of the most recent occurrence of
	 * an (SSA numbered) variable and recalculates the definitions of variables
	 * appropriately.
	 * 
	 * @param info
	 *            SSAConstructionInfo representing the variable being converted
	 *            into SSA form.
	 * @param top
	 *            "Top" of the variable stack for the variable in question. Each
	 *            variable has a "stack" associated with it. The top of the
	 *            stack contains the current SSA name of the variable. It can
	 *            also be thought of as the "most recent definition" of the
	 *            variable.
	 * @param block
	 *            Basic block in which the variable is being renamed.
	 */
	private static void search(final FlowGraph cfg,
			final SSAConstructionInfo info, VarExpr top, final Block block) {
		if (SSA.DEBUG) {
			System.out.println("  renaming " + info.prototype + " in " + block);
		}

		// If appropriate, add top as an operand of a PhiCatchStmt
		if (top instanceof LocalExpr) {
			SSA.addCatchPhiOperands(info, block, (LocalExpr) top);
		}

		// First handle any phi in the block.
		final PhiStmt phi = info.phiAtBlock(block);

		if (phi != null) {
			top = phi.target();

			if (top instanceof LocalExpr) {
				SSA.addCatchPhiOperands(info, block, (LocalExpr) top);
			}
		}

		// If the block in which the variable is being renamed begins an
		// exception handler and we're dealing with a stack variable, then
		// there is no most recent definition of the variable because the
		// stack is cleared when an exception is handled. I dunno.
		if (cfg.catchBlocks().contains(block)
				&& (info.prototype instanceof StackExpr)) {

			if (SSA.DEBUG) {
				System.out.println("  Killing TOS at " + block);
			}

			// The operand stack is popped down to 0 at catch blocks.
			top = null;
		}

		final Iterator e = info.realsAtBlock(block).iterator();

		// Examine each occurrence of the variable in the block of
		// interest. When we encounter a definition of the variable, make
		// that definition to the most recent SSA variable (top). For
		// each use, make this most recent SSA variable be its defining
		// expression.
		while (e.hasNext()) {
			final VarExpr real = (VarExpr) e.next();

			if (real.isDef()) {
				real.setDef(null);

				top = real; // A definition means a new SSA variable

				if (top instanceof LocalExpr) {
					SSA.addCatchPhiOperands(info, block, (LocalExpr) top);
				}

				if (SSA.DEBUG) {
					System.out.println("  TOS = " + top);
				}

			} else {
				// Make sure that the variable is defined somewhere else
				// (somewhere that we have already seen).
				Assert.isTrue(top != null, "Null def for " + real);
				real.setDef(top);
			}
		}

		final Iterator succs = cfg.succs(block).iterator();

		// Examine all successors of the block in question. If the
		// successor contains a PhiJoinStmt for the variable, then set the
		// operand corresponding to the block to be defined by the most
		// recent SSA variable. Similarly for a PhiReturnStmt.
		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final PhiStmt succPhi = info.phiAtBlock(succ);

			if (succPhi instanceof PhiJoinStmt) {
				final PhiJoinStmt f = (PhiJoinStmt) succPhi;
				final VarExpr operand = (VarExpr) f.operandAt(block);
				operand.setDef(top);

			} else if (succPhi instanceof PhiReturnStmt) {
				final PhiReturnStmt f = (PhiReturnStmt) succPhi;
				final VarExpr operand = (VarExpr) f.operand();
				operand.setDef(top);
			}

			// Adjust the operands of any PhiCatchStmts if the sucessor node
			// is protected.
			if (top instanceof LocalExpr) {
				SSA.addCatchPhiOperands(info, succ, (LocalExpr) top);
			}
		}

		final Iterator children = cfg.domChildren(block).iterator();

		// Visit the children in the dominator tree. Keep the same most
		// recent SSA variable (top).
		while (children.hasNext()) {
			final Block child = (Block) children.next();
			SSA.search(cfg, info, top, child);
		}
	}

	/**
	 * Iterates over the blocks in the CFG and inserts the phi statement
	 * associated with that block. Up until this point, the phi statement is
	 * only maintained in SSAConstructionInfo. Note that the phi statement
	 * cannot be a return phi.
	 * 
	 * @param cfg
	 *            The CFG into which to insert phi statements.
	 * @param info
	 *            Represents the variable whose phi statements we are inserting.
	 * 
	 * @see PhiReturnStmt
	 */
	private static void insertCode(final FlowGraph cfg,
			final SSAConstructionInfo info) {
		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final PhiStmt phi = info.phiAtBlock(block);

			if (phi != null) {
				Assert.isFalse(phi instanceof PhiReturnStmt);
				block.tree().prependStmt(phi);
			}
		}
	}
}
