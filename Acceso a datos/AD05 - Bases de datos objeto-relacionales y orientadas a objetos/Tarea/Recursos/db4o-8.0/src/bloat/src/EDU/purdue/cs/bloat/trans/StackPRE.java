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
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Eliminate partially redundant local variable loads and stores by replacing
 * them with stack variables and dups.
 * 
 * The algorithm is similar to SSAPRE, except:
 * 
 * We need to place phis for locals at the IDF of the blocks containing defs and
 * uses (not just defs).
 */
public class StackPRE {
	public static boolean DEBUG = false;

	protected FlowGraph cfg;

	protected List[] varphis;

	protected List[] stackvars;

	protected Worklist worklist;

	int next = 0;

	public StackPRE(final FlowGraph cfg) {
		this.cfg = cfg;
	}

	public void transform() {
		stackvars = new ArrayList[cfg.size()];

		for (int i = 0; i < stackvars.length; i++) {
			stackvars[i] = new ArrayList();
		}

		varphis = new ArrayList[cfg.size()];

		for (int i = 0; i < varphis.length; i++) {
			varphis[i] = new ArrayList();
		}

		// Collect local and stack variables into a worklist.
		// Mark the variables that are pushed before any are popped.
		worklist = new Worklist();

		cfg.visit(new TreeVisitor() {
			public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
				worklist.addVarPhi(stmt);
			}

			public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
				worklist.addLocalVar((LocalExpr) stmt.target());
			}

			public void visitLocalExpr(final LocalExpr expr) {
				worklist.addLocalVar(expr);
			}

			public void visitStackExpr(final StackExpr expr) {
				worklist.addStackVar(expr);
			}
		});

		while (!worklist.isEmpty()) {
			final ExprInfo exprInfo = worklist.removeFirst();

			if (StackPRE.DEBUG) {
				System.out.println("PRE for " + exprInfo.def()
						+ " -------------------------");
				System.out.println("Placing Phis for " + exprInfo.def()
						+ " -------------------------");
			}

			placePhiFunctions(exprInfo);

			if (StackPRE.DEBUG) {
				exprInfo.print();
				System.out.println("Renaming for " + exprInfo.def()
						+ " -------------------------");
			}

			rename(exprInfo);

			if (StackPRE.DEBUG) {
				exprInfo.print();
				System.out.println("Down safety for " + exprInfo.def()
						+ " -------------------------");
			}

			downSafety(exprInfo);

			if (StackPRE.DEBUG) {
				System.out.println("Will be available for " + exprInfo.def()
						+ " -------------------------");
			}

			willBeAvail(exprInfo);

			if (StackPRE.DEBUG) {
				System.out.println("Finalize for " + exprInfo.def()
						+ " -------------------------");
			}

			finalize(exprInfo);

			if (StackPRE.DEBUG) {
				System.out.println("Code motion for " + exprInfo.def()
						+ " -------------------------");
			}

			final Type type = exprInfo.def().type();
			final StackExpr tmp = new StackExpr(0, type);
			final SSAConstructionInfo consInfo = new SSAConstructionInfo(cfg,
					tmp);

			codeMotion(exprInfo, tmp, consInfo);

			if (StackPRE.DEBUG) {
				System.out.println("Performing incremental SSA for "
						+ exprInfo.def() + " -------------------------");
			}

			SSA.transform(cfg, consInfo);

			// Get the stack variable phis.
			final Collection defBlocks = consInfo.defBlocks();
			final Iterator e = cfg.iteratedDomFrontier(defBlocks).iterator();

			while (e.hasNext()) {
				final Block block = (Block) e.next();

				final Iterator stmts = block.tree().stmts().iterator();

				while (stmts.hasNext()) {
					final Stmt stmt = (Stmt) stmts.next();
					if (stmt instanceof PhiJoinStmt) {
						worklist.prependVarPhi((PhiJoinStmt) stmt);
					} else if (!(stmt instanceof LabelStmt)) {
						// Only labels occur before phis. If we hit
						// something else, there are no more phis.
						break;
					}
				}
			}

			if (StackPRE.DEBUG) {
				exprInfo.print();
				System.out.println("Done with PRE for " + exprInfo.def()
						+ " -------------------------");
			}

			exprInfo.cleanup();
		}

		varphis = null;
		worklist = null;
	}

	/**
	 * For an local variable, v, insert a Phi at the iterated dominance frontier
	 * of the blocks containing defs and uses of v. This differs from SSA phi
	 * placement in that uses, not just defs are considered in computing the
	 * IDF.
	 */
	private void placePhiFunctions(final ExprInfo exprInfo) {
		final ArrayList w = new ArrayList(cfg.size());

		final Iterator uses = exprInfo.def().uses().iterator();

		w.add(exprInfo.def().block());

		while (uses.hasNext()) {
			final LocalExpr use = (LocalExpr) uses.next();

			if (use.parent() instanceof PhiJoinStmt) {
				final PhiJoinStmt phi = (PhiJoinStmt) use.parent();

				final Iterator preds = cfg.preds(use.block()).iterator();

				while (preds.hasNext()) {
					final Block pred = (Block) preds.next();

					if (phi.operandAt(pred) == use) {
						w.add(pred);
						break;
					}
				}
			} else if (!(use.parent() instanceof PhiCatchStmt)) {
				w.add(use.block());
			}
		}

		final Iterator df = cfg.iteratedDomFrontier(w).iterator();

		while (df.hasNext()) {
			final Block block = (Block) df.next();
			exprInfo.addPhi(block);
		}

		// Don't bother with placing phis for catch blocks, since the
		// operand stack is zeroed at catch blocks.
	}

	/**
	 * Set the definition for the variable occurences. After this step all
	 * occurences of the variable which are at different heights will have
	 * different definitions.
	 */
	private void rename(final ExprInfo exprInfo) {
		search(cfg.source(), exprInfo, null, 0, false);
	}

	private void search(final Block block, final ExprInfo exprInfo, Def top,
			int totalBalance, boolean seenDef) {
		if (StackPRE.DEBUG) {
			System.out.println("    renaming in " + block);
		}

		if (cfg.catchBlocks().contains(block)) {
			if (top != null) {
				top.setDownSafe(false);
			}

			top = null;
		}

		final Phi phi = exprInfo.exprPhiAtBlock(block);

		if (phi != null) {
			if (top != null) {
				top.setDownSafe(false);
			}

			top = phi;

			if (!seenDef) {
				top.setDownSafe(false);
			}
		}

		Node parent = null;
		int balance = 0;

		final Iterator iter = exprInfo.varsAtBlock(block).iterator();

		while (iter.hasNext()) {
			final VarExpr node = (VarExpr) iter.next();

			// Get the parent of the node. If the parent is a putfield
			// or array store, then the node is popped when the grandparent
			// is evaluated, not when the parent is evaluated.
			// We keep track of the parent so that when it changes, we
			// know to update the operand stack balance.

			Node p = node.parent();

			if ((p instanceof MemRefExpr) && ((MemRefExpr) p).isDef()) {
				p = p.parent();
			}

			if (parent != p) {
				parent = p;
				totalBalance += balance;
				balance = 0;

				if ((top != null) && (totalBalance < 0)) {
					top.setDownSafe(false);
				}
			}

			if (node instanceof StackExpr) {
				if (parent instanceof StackManipStmt) {
					switch (((StackManipStmt) parent).kind()) {
					case StackManipStmt.DUP:
					case StackManipStmt.DUP_X1:
					case StackManipStmt.DUP_X2:
						balance += 1;
						break;
					case StackManipStmt.DUP2:
					case StackManipStmt.DUP2_X1:
					case StackManipStmt.DUP2_X2:
						balance += 2;
						break;
					default:
						break;
					}
				} else if (node.isDef()) {
					balance += node.type().stackHeight();
				} else {
					balance -= node.type().stackHeight();
				}
			} else {
				final LocalExpr var = (LocalExpr) node;

				if (var.isDef()) {
					seenDef = true;
				}

				if (StackPRE.DEBUG) {
					System.out.println("node = " + var + " in " + parent);
				}

				if ((totalBalance == 0) && onBottom(var, false)) {
					// Copy the def from the top of the stack and
					// create a new def.
					exprInfo.setDef(var, top);
					top = new RealDef(var);

					if ((balance != 0) || !onBottom(var, true)) {
						top.setDownSafe(false);
					}

					if (StackPRE.DEBUG) {
						System.out.println("New def " + top + " with balance "
								+ totalBalance + " + " + balance);
					}
				} else {
					// The occurence is not on the bottom, so it
					// must be reloaded from a local.
					exprInfo.setDef(var, null);
				}
			}

			if (StackPRE.DEBUG) {
				System.out.println("after " + parent + " top = " + top);
			}
		}

		totalBalance += balance;

		if ((top != null) && (totalBalance < 0)) {
			top.setDownSafe(false);
		}

		// If we hit the sink node, a def at the top of the stack is not
		// down safe.
		if ((block == cfg.sink()) || cfg.succs(block).contains(cfg.sink())) {
			if (top != null) {
				top.setDownSafe(false);
			}
		}

		// First, fill in the operands for the StackPRE phis. Then,
		// handle local variable occurences in successor block variable
		// phis. We do this after the StackPRE phis since they will
		// hoist code above the variable phis.

		Iterator succs = cfg.succs(block).iterator();

		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final Phi succPhi = exprInfo.exprPhiAtBlock(succ);

			if (succPhi != null) {
				succPhi.setOperandAt(block, top);
			}
		}

		succs = cfg.succs(block).iterator();

		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final Iterator phis = varPhisAtBlock(succ).iterator();

			while (phis.hasNext()) {
				final PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

				final Expr operand = stmt.operandAt(block);

				if (operand instanceof StackExpr) {
					balance += operand.type().stackHeight();
				}

				if (stmt.target() instanceof StackExpr) {
					balance -= stmt.target().type().stackHeight();

					if (top != null) {
						top.setDownSafe(false);
						top = null;
					}
				}

				if ((operand != null) && (operand.def() == exprInfo.def())) {
					// Phi operands aren't allowed to define any of the
					// locals. This should never happen since none of the
					// locals should be dominated by the phi operand,
					// but we'll play it safe and set top to null.
					exprInfo.setDef((LocalExpr) operand, top);
					top = null;
				}

				if (stmt.target() == exprInfo.def()) {
					exprInfo.setDef((LocalExpr) stmt.target(), top);
					top = new RealDef((LocalExpr) stmt.target());
				}

				totalBalance += balance;

				if ((top != null) && (totalBalance < 0)) {
					top.setDownSafe(false);
				}
			}
		}

		final Iterator children = cfg.domChildren(block).iterator();

		while (children.hasNext()) {
			final Block child = (Block) children.next();
			search(child, exprInfo, top, totalBalance, seenDef);
		}
	}

	private boolean onBottom(final LocalExpr var, final boolean really) {
		// InitStmts and PhiStmts are always on the bottom.
		if ((var.stmt() instanceof InitStmt) || (var.stmt() instanceof PhiStmt)) {
			return true;
		}

		class Bool {
			boolean value = true;
		}
		;

		final Bool bottom = new Bool();

		var.stmt().visitChildren(new TreeVisitor() {
			boolean seen = false;

			public void visitExpr(final Expr expr) {
				if (StackPRE.DEBUG) {
					System.out.println("Checking " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}

				if (!seen) {
					expr.visitChildren(this);
				}

				if (!seen) {
					bottom.value = false;
					seen = true;
				}

				if (StackPRE.DEBUG) {
					System.out.println("Done with " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}
			}

			public void visitLocalExpr(final LocalExpr expr) {
				if (StackPRE.DEBUG) {
					System.out.println("Checking " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}

				if (!seen) {
					if (expr == var) {
						seen = true;
					} else if (expr.def() != var.def()) {
						bottom.value = false;
						seen = true;
					}
				}

				if (StackPRE.DEBUG) {
					System.out.println("Done with " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}
			}

			public void visitStackExpr(final StackExpr expr) {
				if (StackPRE.DEBUG) {
					System.out.println("Checking " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}

				if (really && !seen) {
					bottom.value = false;
					seen = true;
				}

				if (StackPRE.DEBUG) {
					System.out.println("Done with " + expr + " seen=" + seen
							+ " bottom=" + bottom.value);
				}
			}
		});

		return bottom.value;
	}

	/**
	 * Mark each def as not down safe if there is a control flow path from that
	 * Phi along which the expression is not evaluated before exit or being
	 * altered by refinition of one of the variables of the expression. This can
	 * happen if:
	 * 
	 * 1) There is a path to exit along which the Phi target is not used. 2)
	 * There is a path to exit along which the Phi target is used only as the
	 * operand of a non-down-safe Phi.
	 */
	private void downSafety(final ExprInfo exprInfo) {
		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi == null) {
				continue;
			}

			if (StackPRE.DEBUG) {
				System.out.println("    down safety for " + phi + " in "
						+ block);
			}

			if (phi.downSafe()) {
				if (StackPRE.DEBUG) {
					System.out.println("    already down safe");
				}

				continue;
			}

			// The phi is not down safe. Make all its operands not
			// down safe.

			final Iterator e = phi.operands().iterator();

			while (e.hasNext()) {
				final Def def = (Def) e.next();

				if (def != null) {
					resetDownSafe(def);
				}
			}
		}
	}

	private void resetDownSafe(final Def def) {
		if (StackPRE.DEBUG) {
			System.out.println("        reset down safe for " + def);
		}

		if (def instanceof Phi) {
			final Phi phi = (Phi) def;

			if (phi.downSafe()) {
				phi.setDownSafe(false);

				final Iterator e = phi.operands().iterator();

				while (e.hasNext()) {
					final Def operand = (Def) e.next();

					if (operand != null) {
						resetDownSafe(operand);
					}
				}
			}
		} else {
			def.setDownSafe(false);
		}
	}

	/**
	 * Predict whether the expression will be available at each Phi result
	 * following insertions for PRE.
	 */
	private void willBeAvail(final ExprInfo exprInfo) {
		computeCanBeAvail(exprInfo);
		computeLater(exprInfo);
	}

	private void computeCanBeAvail(final ExprInfo exprInfo) {
		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi == null) {
				continue;
			}

			if (!phi.downSafe() && phi.canBeAvail()) {
				resetCanBeAvail(exprInfo, phi);
			}
		}
	}

	private void resetCanBeAvail(final ExprInfo exprInfo, final Phi phi) {
		phi.setCanBeAvail(false);

		final Iterator blocks = cfg.nodes().iterator();

		// For each phi whose operand is at
		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi other = exprInfo.exprPhiAtBlock(block);

			if (other == null) {
				continue;
			}

			final Iterator e = cfg.preds(other.block()).iterator();

			while (e.hasNext()) {
				final Block pred = (Block) e.next();

				final Def def = other.operandAt(pred);

				if (def == phi) {
					other.setOperandAt(pred, null);

					if (!other.downSafe() && other.canBeAvail()) {
						resetCanBeAvail(exprInfo, other);
					}
				}
			}
		}
	}

	private void computeLater(final ExprInfo exprInfo) {
		Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi != null) {
				phi.setLater(phi.canBeAvail());
			}
		}

		blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if ((phi != null) && phi.later()) {
				final Iterator e = phi.operands().iterator();

				while (e.hasNext()) {
					final Def def = (Def) e.next();

					if (def instanceof RealDef) {
						resetLater(exprInfo, phi);
						break;
					}
				}
			}
		}
	}

	private void resetLater(final ExprInfo exprInfo, final Phi phi) {
		phi.setLater(false);

		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi other = exprInfo.exprPhiAtBlock(block);

			if (other == null) {
				continue;
			}

			final Iterator e = other.operands().iterator();

			while (e.hasNext()) {
				final Def def = (Def) e.next();

				if ((def == phi) && other.later()) {
					resetLater(exprInfo, other);
					break;
				}
			}
		}
	}

	private void finalize(final ExprInfo exprInfo) {
		final Iterator uses = exprInfo.def().uses().iterator();

		while (uses.hasNext()) {
			final LocalExpr use = (LocalExpr) uses.next();

			if (use.parent() instanceof PhiCatchStmt) {
				exprInfo.setSave(true);
				break;
			}
		}

		finalizeVisit(exprInfo, cfg.source());
	}

	private void finalizeVisit(final ExprInfo exprInfo, final Block block) {
		if (StackPRE.DEBUG) {
			System.out.println("    finalizing " + block);
		}

		// First finalize normal occurences of the local.
		final Iterator reals = exprInfo.varsAtBlock(block).iterator();

		while (reals.hasNext()) {
			final VarExpr node = (VarExpr) reals.next();

			if (node instanceof LocalExpr) {
				final LocalExpr real = (LocalExpr) node;

				if (StackPRE.DEBUG) {
					System.out.println("        -----------");
				}

				final Def def = exprInfo.def(real);

				if ((def != null) && def.downSafe()) {
					// We can reload from a stack variable, unless the we
					// can't safely push the phi operands.
					if (def instanceof Phi) {
						if (((Phi) def).willBeAvail()) {
							exprInfo.setPop(real, true);
						} else {
							exprInfo.setSave(true);
						}
					} else {
						exprInfo.setPush(((RealDef) def).var, true);
						exprInfo.setPop(real, true);
					}
				} else {
					// The real is not on the bottom. We must reload from a
					// local variable.
					if (real != exprInfo.def()) {
						exprInfo.setSave(true);
					}
				}
			}
		}

		// Next, handle code motion.
		Iterator succs = cfg.succs(block).iterator();

		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final Phi succPhi = exprInfo.exprPhiAtBlock(succ);

			if ((succPhi != null) && succPhi.willBeAvail()) {
				if (succPhi.insert(block)) {
					succPhi.setPushOperand(block, true);
				} else {
					final Def def = succPhi.operandAt(block);

					if (def instanceof RealDef) {
						Assert.isTrue(def.downSafe(), succPhi + " operand for "
								+ block + " is not DS: " + def);
						exprInfo.setPush(((RealDef) def).var, true);
					} else {
						Assert.isTrue(def instanceof Phi, succPhi
								+ " operand for " + block + " is not a phi: "
								+ def);
						Assert.isTrue(((Phi) def).willBeAvail(), succPhi
								+ " operand for " + block + " is not WBA: "
								+ def);
					}
				}
			}
		}

		// Lastly, finalize occurences in variable phis. We do this
		// after the StackPRE hoisting since the hoisted code will
		// occur before the phis.
		succs = cfg.succs(block).iterator();

		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final Iterator phis = varPhisAtBlock(succ).iterator();

			while (phis.hasNext()) {
				final PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

				final Expr operand = stmt.operandAt(block);

				if ((operand != null) && (operand.def() == exprInfo.def())) {
					final LocalExpr var = (LocalExpr) operand;
					final Def def = exprInfo.def(var);

					if ((def != null) && def.downSafe()) {
						// We can reload from a stack variable, unless the we
						// can't safely push the phi operands.
						if (def instanceof Phi) {
							if (((Phi) def).willBeAvail()) {
								exprInfo.setPop(var, true);
							} else {
								exprInfo.setSave(true);
							}
						} else {
							exprInfo.setPush(((RealDef) def).var, true);
							exprInfo.setPop(var, true);
						}
					}
				}
			}
		}

		final Iterator children = cfg.domChildren(block).iterator();

		while (children.hasNext()) {
			final Block child = (Block) children.next();
			finalizeVisit(exprInfo, child);
		}
	}

	private void codeMotion(final ExprInfo exprInfo, final StackExpr tmp,
			final SSAConstructionInfo consInfo) {
		// Be sure to visit pre-order so at least one predecessor is visited
		// before each block.
		final Iterator blocks = cfg.preOrder().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			if ((block == cfg.source()) || (block == cfg.sink())) {
				continue;
			}

			boolean added = false;

			final Iterator reals = exprInfo.varsAtBlock(block).iterator();

			while (reals.hasNext()) {
				final VarExpr node = (VarExpr) reals.next();

				if (node instanceof LocalExpr) {
					final LocalExpr var = (LocalExpr) node;

					// If marked push, save it to a stack variable.
					// If marked pop, reload from a stack variable.

					final boolean push = exprInfo.push(var);
					boolean pop = exprInfo.pop(var);

					if (var.isDef() && exprInfo.save()) {
						pop = false;
					}

					if (push && pop) {
						Assert.isTrue(var != exprInfo.def());

						final StackExpr t1 = (StackExpr) tmp.clone();
						final StackExpr t2 = (StackExpr) tmp.clone();

						final StoreExpr store = new StoreExpr(t1, t2, t2.type());
						var.replaceWith(store);

						consInfo.addReal(t2);
						consInfo.addReal(t1);
						added = true;
					} else if (push) {
						final StackExpr t1 = (StackExpr) tmp.clone();

						final LocalExpr t2 = (LocalExpr) var.clone();
						t2.setDef(exprInfo.def());

						final StoreExpr store = new StoreExpr(t1, t2, t2.type());

						if (var != exprInfo.def()) {
							var.replaceWith(store);
						} else {
							final Node parent = var.parent();

							if (parent instanceof Stmt) {
								// InitStmt or PhiStmt.
								final Stmt stmt = new ExprStmt(store);
								block.tree().addStmtAfter(stmt, (Stmt) parent);
							} else {
								// a := E -> a := (S := E)
								Assert.isTrue(parent instanceof StoreExpr);
								final Expr rhs = ((StoreExpr) parent).expr();
								parent.visit(new ReplaceVisitor(rhs, store));
								store.visit(new ReplaceVisitor(t2, rhs));
								t2.cleanup();
							}
						}

						consInfo.addReal(t1);
						added = true;
					} else if (pop) {
						final StackExpr t1 = (StackExpr) tmp.clone();
						var.replaceWith(t1);

						consInfo.addReal(t1);
						added = true;
					}
				}
			}

			final List s = stackvars[cfg.preOrderIndex(block)];

			if (added) {
				s.clear();

				block.tree().visitChildren(new TreeVisitor() {
					public void visitStackExpr(final StackExpr expr) {
						s.add(expr);
					}
				});
			}

			Iterator succs = cfg.succs(block).iterator();

			while (succs.hasNext()) {
				final Block succ = (Block) succs.next();

				final Phi succPhi = exprInfo.exprPhiAtBlock(succ);

				if ((succPhi != null) && succPhi.pushOperand(block)) {
					final StackExpr t1 = (StackExpr) tmp.clone();
					final LocalExpr t2 = (LocalExpr) exprInfo.def().clone();
					t2.setDef(exprInfo.def());

					final StoreExpr store = new StoreExpr(t1, t2, t1.type());

					block.tree().addStmtBeforeJump(new ExprStmt(store));

					s.add(t1);

					consInfo.addReal(t1);

					if (StackPRE.DEBUG) {
						System.out.println("insert at end of " + block + ": "
								+ store);
					}
				}
			}

			succs = cfg.succs(block).iterator();

			while (succs.hasNext()) {
				final Block succ = (Block) succs.next();

				final Iterator phis = varPhisAtBlock(succ).iterator();

				while (phis.hasNext()) {
					final PhiJoinStmt stmt = (PhiJoinStmt) phis.next();

					final Expr operand = stmt.operandAt(block);

					if ((operand != null) && (operand.def() == exprInfo.def())) {
						final LocalExpr var = (LocalExpr) operand;

						Assert.isFalse(exprInfo.push(var));

						if (exprInfo.pop(var)) {
							final StackExpr t1 = (StackExpr) tmp.clone();
							var.replaceWith(t1);
							consInfo.addReal(t1);
						}
					}
				}
			}
		}
	}

	abstract class Def {
		int version;

		boolean downSafe;

		public Def() {
			this.version = next++;
			this.downSafe = true;
		}

		public void setDownSafe(final boolean flag) {
			if (StackPRE.DEBUG) {
				System.out.println(this + " DS = " + flag);
			}

			downSafe = flag;
		}

		public boolean downSafe() {
			return downSafe;
		}
	}

	class RealDef extends Def {
		LocalExpr var;

		public RealDef(final LocalExpr var) {
			this.var = var;

			if (StackPRE.DEBUG) {
				System.out
						.println("new def for " + var + " in " + var.parent());
			}
		}

		public LocalExpr var() {
			return var;
		}

		public String toString() {
			return var.toString() + "{" + version + ","
					+ (downSafe() ? "" : "!") + "DS}";
		}
	}

	class Phi extends Def {
		Block block;

		HashMap operands;

		HashMap saveOperand;

		boolean live;

		boolean downSafe;

		boolean canBeAvail;

		boolean later;

		public Phi(final Block block) {
			this.block = block;

			operands = new HashMap(cfg.preds(block).size() * 2);
			saveOperand = new HashMap(cfg.preds(block).size() * 2);

			downSafe = true;
			canBeAvail = true;
			later = true;
		}

		public Block block() {
			return block;
		}

		public Collection operands() {
			return new AbstractCollection() {
				public int size() {
					return cfg.preds(block).size();
				}

				public boolean contains(final Object obj) {
					if (obj == null) {
						return operands.size() != cfg.preds(block).size();
					}

					return operands.containsValue(obj);
				}

				public Iterator iterator() {
					final Iterator iter = cfg.preds(block).iterator();

					return new Iterator() {
						public boolean hasNext() {
							return iter.hasNext();
						}

						public Object next() {
							final Block block = (Block) iter.next();
							return operandAt(block);
						}

						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		public Def operandAt(final Block block) {
			return (Def) operands.get(block);
		}

		public void setOperandAt(final Block block, final Def def) {
			if (def != null) {
				operands.put(block, def);
			} else {
				operands.remove(block);
			}
		}

		public void setPushOperand(final Block block, final boolean flag) {
			if (StackPRE.DEBUG) {
				System.out.println("    operand " + block + " save=" + flag);
			}

			saveOperand.put(block, new Boolean(flag));
		}

		public boolean pushOperand(final Block block) {
			final Boolean flag = (Boolean) saveOperand.get(block);
			return (flag != null) && flag.booleanValue();
		}

		public boolean insert(final Block block) {
			final Def def = operandAt(block);

			if (def == null) {
				return true;
			}

			if (!def.downSafe()) {
				return true;
			}

			if ((def instanceof Phi) && !((Phi) def).willBeAvail()) {
				return true;
			}

			return false;
		}

		public boolean willBeAvail() {
			return canBeAvail && !later;
		}

		public void setCanBeAvail(final boolean flag) {
			if (StackPRE.DEBUG) {
				System.out.println(this + " CBA = " + flag);
			}

			canBeAvail = flag;
		}

		public boolean canBeAvail() {
			return canBeAvail;
		}

		public void setLater(final boolean flag) {
			if (StackPRE.DEBUG) {
				System.out.println(this + " Later = " + flag);
			}

			later = flag;
		}

		public boolean later() {
			return later;
		}

		public String toString() {
			String s = "";

			final Iterator iter = cfg.preds(block).iterator();

			while (iter.hasNext()) {
				final Block pred = (Block) iter.next();
				final Def def = operandAt(pred);

				s += pred.label() + "=";

				if (def == null) {
					s += "null";
				} else {
					s += def.version;
				}

				if (iter.hasNext()) {
					s += ", ";
				}
			}

			return "phi" + "{" + version + "," + (downSafe() ? "" : "!")
					+ "DS," + (canBeAvail() ? "" : "!") + "CBA,"
					+ (later() ? "" : "!") + "Later}(" + s + ")";
		}
	}

	public List varPhisAtBlock(final Block block) {
		return varphis[cfg.preOrderIndex(block)];
	}

	/**
	 * Maintain the occurences so that they are visited in a preorder traversal
	 * of the dominator tree.
	 */
	private final class ExprInfo {
		ArrayList[] vars;

		Phi[] phis;

		boolean save;

		Map pushes;

		Map pops;

		Map defs;

		LocalExpr def;

		ArrayList cleanup;

		public ExprInfo(final LocalExpr def) {
			this.def = def;

			vars = new ArrayList[cfg.size()];

			for (int i = 0; i < vars.length; i++) {
				vars[i] = new ArrayList();
			}

			phis = new Phi[cfg.size()];

			save = false;

			pushes = new HashMap();
			pops = new HashMap();

			defs = new HashMap();

			cleanup = new ArrayList();
		}

		public void cleanup() {
			final Iterator iter = cleanup.iterator();

			while (iter.hasNext()) {
				final Node node = (Node) iter.next();
				node.cleanup();
			}

			vars = null;
			phis = null;
			pushes = null;
			pops = null;
			defs = null;
			def = null;
			cleanup = null;
		}

		public void registerForCleanup(final Node node) {
			cleanup.add(node);
		}

		public void setSave(final boolean flag) {
			save = flag;
		}

		public boolean save() {
			return save;
		}

		public void setPush(final LocalExpr expr, final boolean flag) {
			pushes.put(expr, new Boolean(flag));
		}

		public boolean push(final LocalExpr expr) {
			final Boolean b = (Boolean) pushes.get(expr);
			return (b != null) && b.booleanValue();
		}

		public void setPop(final LocalExpr expr, final boolean flag) {
			pops.put(expr, new Boolean(flag));
		}

		public boolean pop(final LocalExpr expr) {
			final Boolean b = (Boolean) pops.get(expr);
			return (b != null) && b.booleanValue();
		}

		public void setDef(final LocalExpr expr, final Def def) {
			if (StackPRE.DEBUG) {
				System.out.println("        setting def for " + expr + " to "
						+ def);
			}

			if (def != null) {
				defs.put(expr, def);
			} else {
				defs.remove(expr);
			}
		}

		public Def def(final LocalExpr expr) {
			final Def def = (Def) defs.get(expr);

			if (StackPRE.DEBUG) {
				System.out.println("        def for " + expr + " is " + def);
			}

			return def;
		}

		public LocalExpr def() {
			return def;
		}

		public void addPhi(final Block block) {
			Phi phi = phis[cfg.preOrderIndex(block)];

			if (phi == null) {
				if (StackPRE.DEBUG) {
					System.out.println("    add phi for " + def + " at "
							+ block);
				}

				phi = new Phi(block);
				phis[cfg.preOrderIndex(block)] = phi;
			}
		}

		public List varsAtBlock(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);

			final List list = new ArrayList(vars[blockIndex].size()
					+ stackvars[blockIndex].size());

			final Iterator viter = vars[blockIndex].iterator();
			final Iterator siter = stackvars[blockIndex].iterator();

			if (!viter.hasNext() && !siter.hasNext()) {
				return new ArrayList(0);
			}

			block.tree().visitChildren(new TreeVisitor() {
				VarExpr vnext = null;

				VarExpr snext = null;

				{
					if (viter.hasNext()) {
						vnext = (VarExpr) viter.next();
					}

					if (siter.hasNext()) {
						snext = (VarExpr) siter.next();
					}
				}

				public void visitStmt(final Stmt stmt) {
					if (((vnext != null) && (vnext.stmt() == stmt))
							|| ((snext != null) && (snext.stmt() == stmt))) {
						super.visitStmt(stmt);
					}
				}

				public void visitVarExpr(final VarExpr expr) {
					super.visitExpr(expr);

					if (expr == vnext) {
						if (viter.hasNext()) {
							vnext = (VarExpr) viter.next();
						} else {
							vnext = null;
						}

						if (expr == snext) {
							if (siter.hasNext()) {
								snext = (VarExpr) siter.next();
							} else {
								snext = null;
							}
						}

						list.add(expr);
					} else if (expr == snext) {
						if (siter.hasNext()) {
							snext = (VarExpr) siter.next();
						} else {
							snext = null;
						}

						list.add(expr);
					}
				}
			});

			return list;
		}

		public Phi exprPhiAtBlock(final Block block) {
			return phis[cfg.preOrderIndex(block)];
		}

		protected void print() {
			System.out.println("Print for " + def + "------------------");

			cfg.visit(new PrintVisitor() {
				Phi phi = null;

				public void visitBlock(final Block block) {
					phi = exprPhiAtBlock(block);
					super.visitBlock(block);
				}

				public void visitLabelStmt(final LabelStmt stmt) {
					super.visitLabelStmt(stmt);

					if (stmt.label().startsBlock()) {
						if (phi != null) {
							println(phi);
						}
					}
				}

				public void visitLocalExpr(final LocalExpr expr) {
					super.visitLocalExpr(expr);

					if (expr.def() == def) {
						super.print("{" + defs.get(expr) + "}");
					}
				}
			});

			System.out.println("End Print ----------------------------");
		}
	}

	class Worklist {
		Map exprInfos;

		LinkedList exprs;

		public Worklist() {
			exprInfos = new HashMap();
			exprs = new LinkedList();
		}

		public boolean isEmpty() {
			return exprs.isEmpty();
		}

		public ExprInfo removeFirst() {
			final ExprInfo exprInfo = (ExprInfo) exprs.removeFirst();
			exprInfos.remove(exprInfo.def());
			return exprInfo;
		}

		public void addLocalVar(final LocalExpr var) {
			final int blockIndex = cfg.preOrderIndex(var.block());

			if (StackPRE.DEBUG) {
				System.out.println("add var " + var);
			}

			ExprInfo exprInfo = (ExprInfo) exprInfos.get(var.def());

			if (exprInfo == null) {
				exprInfo = new ExprInfo((LocalExpr) var.def());
				exprs.add(exprInfo);
				exprInfos.put(var.def(), exprInfo);

				if (StackPRE.DEBUG) {
					System.out.println("    add info for " + var);
				}
			}

			exprInfo.vars[blockIndex].add(var);
		}

		public void addStackVar(final StackExpr var) {
			final int blockIndex = cfg.preOrderIndex(var.block());

			if (StackPRE.DEBUG) {
				System.out.println("add var " + var);
			}

			stackvars[blockIndex].add(var);
		}

		public void addVarPhi(final PhiJoinStmt stmt) {
			varphis[cfg.preOrderIndex(stmt.block())].add(stmt);
		}

		public void prependVarPhi(final PhiJoinStmt stmt) {
			final List v = varphis[cfg.preOrderIndex(stmt.block())];

			if (!v.contains(stmt)) {
				v.add(0, stmt);
			}
		}
	}
}
