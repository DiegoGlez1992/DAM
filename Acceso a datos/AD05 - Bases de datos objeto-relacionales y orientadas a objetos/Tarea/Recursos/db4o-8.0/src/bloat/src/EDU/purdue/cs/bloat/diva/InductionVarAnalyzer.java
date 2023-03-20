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
package EDU.purdue.cs.bloat.diva;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * InductionVarAnalyzer traverses a control flow graph and looks for array
 * element swizzle operations inside loops. If possible, these swizzle
 * operations are hoisted out of the loop and are replaced with range swizzle
 * operations. The technique used is Demand-driven Induction Variable Analysis
 * (DIVA).
 * <p>
 * To accomplish its tasks, InductionVarAnalyzer keeps track of a number of
 * induction variables (represented by Swizzler objects) and local variables.
 * 
 * @see Swizzler
 * @see LocalExpr
 */
public class InductionVarAnalyzer {
	public static boolean DEBUG = false;

	SSAGraph ssaGraph;

	FlowGraph CFG; // Control flow graph being operated on

	HashMap IndStore; // Stores induction variables and

	// associated swizzlers
	HashMap LocalStore; // Stores local variables

	Expr ind_var = null; // An induction variable

	Expr ind_init = null; // Initial value of an induction variable

	Expr ind_term = null; // Not used???

	Expr ind_inc = null; // Expression used to increment induction

	// variable (all uses commented out)
	Expr tgt = null; // Target of the phi statement that defines

	// an induction var

	boolean changed = false; // Was the cfg changed?

	/**
	 * Searches the list of induction variables for an induction variable with a
	 * given value number.
	 * 
	 * @param vn
	 *            Value number to search for.
	 * 
	 * @return Swizzler object whose target has the desired value number or
	 *         null, if value number is not found.
	 */
	public Object get_swizzler(final int vn) {
		final Iterator iter = IndStore.values().iterator();

		while (iter.hasNext()) {
			final Swizzler swz = (Swizzler) iter.next();
			if ((swz.target().valueNumber() == vn)
					|| (swz.ind_var().valueNumber() == vn)) {
				return swz;
			}
		}
		return null;
	}

	/**
	 * Searchs the stored list of local variables for a local variable with a
	 * given value number.
	 * 
	 * @param vn
	 *            The value number to search for.
	 * 
	 * @return The local variable with the given value number, or null, if not
	 *         found.
	 */
	public MemExpr get_local(final int vn) {
		final Iterator iter = LocalStore.keySet().iterator();

		while (iter.hasNext()) {
			final MemExpr le = (MemExpr) iter.next();
			if (le.valueNumber() == vn) {
				return le;
			}
		}
		return null;
	}

	/**
	 * Displays (to System.out) all of the Swizzlers stored in the induction
	 * variable store.
	 * 
	 * @see Swizzler
	 */
	public void Display_store() {
		final Iterator iter = IndStore.values().iterator();

		while (iter.hasNext()) {
			final Swizzler indswz = (Swizzler) iter.next();

			System.out.println("\nIV: " + indswz.ind_var() + " tgt: "
					+ indswz.target() + "\narray: " + indswz.array()
					+ " init: " + indswz.init_val() + " end: "
					+ indswz.end_val());
		}
	}

	/**
	 * Displays the contents of a Swizzler object to System.out.
	 * 
	 * @param indswz
	 *            The Swizzler to display.
	 */
	public void displaySwizzler(final Swizzler indswz) {
		System.out.println("\nIV: " + indswz.ind_var() + "vn:"
				+ indswz.ind_var().valueNumber() + " tgt: " + indswz.target()
				+ "vn:" + indswz.target().valueNumber() + "\narray: "
				+ indswz.array() + " init: " + indswz.init_val() + " end: "
				+ indswz.end_val());
	}

	/**
	 * Adds a swizzle range statement (SRStmt) to the end of each predacessor
	 * block of the block containing the phi statement that defines an induction
	 * variable provided that the phi block does not dominate its predacessor.
	 * Phew.
	 * 
	 * @param indswz
	 *            Swizzler representing the induction variable on which the
	 *            range swizzle statement is added.
	 */
	public void insert_aswrange(final Swizzler indswz) {
		final Iterator iter = CFG.preds(indswz.phi_block()).iterator();
		while (iter.hasNext()) {
			final Block blk = (Block) iter.next();
			if (!indswz.phi_block().dominates(blk)) {
				final SRStmt aswrange = new SRStmt((Expr) indswz.array()
						.clone(), (Expr) indswz.init_val().clone(),
						(Expr) indswz.end_val().clone());
				blk.tree().addStmtBeforeJump(aswrange);
				changed = true;
				if (InductionVarAnalyzer.DEBUG) {
					System.out.println("Inserted ASWR: " + aswrange
							+ "\nin block: " + blk);

					System.out.println("$$$ can insert aswrange now\n"
							+ "array: " + indswz.array() + "\nIV: "
							+ indswz.ind_var() + "\ninit: " + indswz.init_val()
							+ "\nend: " + indswz.end_val());
				}
			}
		}
	}

	/* To determine if a phi statement is a mu */
	/* Returns null if not a MU and sets ind_var & ind_init */
	/* to refer to the IV & its initial value otherwise */

	/**
	 * Determines whether or not a phi statement is a mu function. A mu function
	 * is a phi function that merges values at loop headers, as opposed to those
	 * that occur as a result of forward branching. Mu functions always have two
	 * arguments.
	 * 
	 * @param phi
	 *            phi statement that may be a mu function
	 * @param cfg
	 *            CFG to look through <font color="ff0000">Get rid of this
	 *            parameter</font>
	 * 
	 * @return The block containing the mu functions external (that is, outside
	 *         the loop) argument, also known as the external ssalink. If the
	 *         phi statement is not a mu function, null is returned.
	 */
	public Block isMu(final PhiJoinStmt phi, final FlowGraph cfg) {
		// Does it contain two operands?
		if (phi.numOperands() != 2) {
			return null;
		}

		// Is it REDUCIBLE?
		if ((cfg.blockType(phi.block()) == Block.IRREDUCIBLE)
				|| (cfg.blockType(phi.block()) == Block.NON_HEADER)) {
			return null;
		}

		/*
		 * Does one of them dominate the phi and the other dominated by the phi?
		 */

		final Iterator iter = cfg.preds(phi.block()).iterator();
		final Block pred1 = (Block) iter.next();
		final Block pred2 = (Block) iter.next();

		if (pred1.dominates(phi.block()) && phi.block().dominates(pred2)
				&& (pred1 != phi.block())) {
			if (InductionVarAnalyzer.DEBUG) {
				System.out.println("Extlink = 1 pred1:" + pred1 + " pred2:"
						+ pred2);
			}
			ind_var = phi.operandAt(pred2);
			ind_init = phi.operandAt(pred1);
			return pred1;
		}
		if (pred2.dominates(phi.block()) && phi.block().dominates(pred1)
				&& (pred2 != phi.block())) {
			if (InductionVarAnalyzer.DEBUG) {
				System.out.println("Extlink = 2 pred1:" + pred1 + " pred2:"
						+ pred2);
			}
			ind_var = phi.operandAt(pred1);
			ind_init = phi.operandAt(pred2);
			return pred2;
		}

		return null;

	}

	/**
	 * Performs DIVA on a CFG public static void transform(FlowGraph cfg) { //
	 * Create a new instance to allow multiple threads. InductionVarAnalyzer me =
	 * new InductionVarAnalyzer(); me.transform(cfg); }
	 */

	/**
	 * Performs DIVA on a CFG.
	 */
	public void transform(final FlowGraph cfg) {
		ssaGraph = new SSAGraph(cfg);
		CFG = cfg;
		IndStore = new HashMap();
		LocalStore = new HashMap();
		changed = false;

		if (InductionVarAnalyzer.DEBUG) {
			System.out
					.println("----------Before visitComponents--------------");
			cfg.print(System.out);
		}

		// Visit each strongly connected component (SCC) in the CFG. SCCs
		// correspond to sequences in the program. Visit each node in the
		// SCC and build the local variable store. Create Swizzlers at
		// PhiStmts, if approproate, and store them in the induction
		// variable store. If it can be determined that an array element
		// swizzle can be hoisted out of a loop, it is hoisted.
		ssaGraph.visitComponents(new ComponentVisitor() {
			public void visitComponent(final List scc) {
				if (InductionVarAnalyzer.DEBUG) {
					System.out.println("SCC =");
				}

				final Iterator e = scc.iterator();

				while (e.hasNext()) {
					final Node v = (Node) e.next();
					if (InductionVarAnalyzer.DEBUG) {
						System.out.println(" " + v + "{" + v.key() + "} "
								+ v.getClass());
					}

					v.visit(new TreeVisitor() {
						public void visitPhiJoinStmt(final PhiJoinStmt phi) {
							if (isMu(phi, CFG) != null) {
								tgt = phi.target();
								if (InductionVarAnalyzer.DEBUG) {
									System.out.println("IV:" + ind_var + " VN:"
											+ ind_var.valueNumber() + "\ninit:"
											+ ind_init + " target: " + tgt
											+ " VN: " + tgt.valueNumber());
								}
								final Swizzler swz = new Swizzler(ind_var, tgt,
										ind_init, phi.block());
								if (InductionVarAnalyzer.DEBUG) {
									System.out
											.println("store swizzler for "
													+ ind_var.def() + " & "
													+ tgt.def());
									displaySwizzler(swz);
								}

								if (ind_var.def() != null) {
									IndStore.put(ind_var.def(), swz);
								}

								if (tgt.def() != null) {
									IndStore.put(tgt.def(), swz);
								}

								if (InductionVarAnalyzer.DEBUG) {
									System.out.println(" Mu: " + phi + "{"
											+ phi.key() + "}");
								}
							} else {
								if (InductionVarAnalyzer.DEBUG) {
									System.out.println("Phi: " + phi + "{"
											+ phi.key() + "}");
								}
							}
						}

						public void visitLocalExpr(final LocalExpr me) {
							if (me.def() != null) {
								if (LocalStore.get(me.def()) == null) {
									LocalStore.put(me.def(), me);
								}
							}
							if (LocalStore.get(me) == null) {
								LocalStore.put(me, me);
							}
							if (InductionVarAnalyzer.DEBUG) {
								System.out.println("stored ME: " + me
										+ " vn:  " + me.valueNumber());
							}
						}

						public void visitStaticFieldExpr(
								final StaticFieldExpr me) {
							if (me.def() != null) {
								if (LocalStore.get(me.def()) == null) {
									LocalStore.put(me.def(), me);
								}
							}
							if (LocalStore.get(me) == null) {
								LocalStore.put(me, me);
							}
							if (InductionVarAnalyzer.DEBUG) {
								System.out.println("stored ME: " + me
										+ " vn:  " + me.valueNumber());
							}
						}

						public void visitIfCmpStmt(final IfCmpStmt cmp) {
							Swizzler indswz = null;
							boolean set_term = false;

							if (cmp.left().def() != null) {
								indswz = (Swizzler) IndStore.get(cmp.left()
										.def());
							}
							if (indswz != null) {
								if (InductionVarAnalyzer.DEBUG) {
									displaySwizzler(indswz);
								}
								if (indswz.end_val() == null) {
									indswz.set_end_val(cmp.right());
									set_term = true;
									if (InductionVarAnalyzer.DEBUG) {
										System.out.println("Set end_val of "
												+ indswz.ind_var() + " to "
												+ cmp.right());
									}
								}
							} else {
								if (cmp.right().def() != null) {
									indswz = (Swizzler) IndStore.get(cmp
											.right().def());
								}
								if (indswz != null) {
									if (InductionVarAnalyzer.DEBUG) {
										displaySwizzler(indswz);
									}
									if (indswz.end_val() == null) {
										indswz.set_end_val(cmp.left());
										set_term = true;
										if (InductionVarAnalyzer.DEBUG) {
											System.out
													.println("Set end_val of "
															+ indswz.ind_var()
															+ " to "
															+ cmp.left());
										}
									}
								}
							}

							if (set_term && (indswz != null)
									&& (indswz.array() != null)) {
								indswz.aswizzle().set_redundant(true);
								insert_aswrange(indswz);
							}
						}

						public void visitSCStmt(final SCStmt sc) {
							Swizzler indswz;
							MemExpr le = null;

							if (InductionVarAnalyzer.DEBUG) {
								System.out.println("SC: array= " + sc.array()
										+ " VN:" + sc.array().valueNumber()
										+ "\nindex=" + sc.index() + " VN:"
										+ sc.index().valueNumber());
							}

							indswz = (Swizzler) get_swizzler(sc.index()
									.valueNumber());
							if (indswz != null) {
								if (InductionVarAnalyzer.DEBUG) {
									displaySwizzler(indswz);
								}
								if (indswz.array() == null) {
									le = get_local(sc.array().valueNumber());
									if ((le == null)
											&& (sc.array().def() != null)) {
										le = get_local(sc.array().def()
												.valueNumber());
									}
									if (le != null) {
										if (InductionVarAnalyzer.DEBUG) {
											System.out.println("Le: " + le);
										}
										indswz.set_array(le);
										indswz.set_aswizzle(sc);
									} else {
										return;
									}
								}
								if (indswz.end_val() != null) {
									sc.set_redundant(true);
									insert_aswrange(indswz);
								}
							}
						}

						/*
						 * public void visitStoreExpr(StoreExpr ind_store) {
						 * if(ind_var != null) {
						 * if(ind_var.equalsExpr(ind_store.target())) { if (tgt !=
						 * null && ind_store.expr() instanceof ArithExpr){
						 * ArithExpr ind_exp = (ArithExpr)ind_store.expr();
						 * if(tgt.equalsExpr(ind_exp.left())) ind_inc =
						 * ind_exp.right(); else if(tgt.equals(ind_exp.right()))
						 * ind_inc = ind_exp.left(); else { ind_inc = null;
						 * return; } System.out.println("Ind_inc: "+ind_inc); } } } }
						 */

					});

				}

			}

		});

		if (InductionVarAnalyzer.DEBUG) {
			System.out.println("------------After visitComponents---------");
			cfg.print(System.out);
		}

		// If the CFG changed (i.e. if an array range swizzle was added),
		// traverse
		// the graph and remove redundent swizzle statements.
		if (changed) {
			cfg.visit(new TreeVisitor() {
				ListIterator iter;

				public void visitTree(final Tree tree) {
					iter = tree.stmts().listIterator();

					while (iter.hasNext()) {
						final Stmt stmt = (Stmt) iter.next();
						stmt.visit(this);
					}
				}

				public void visitSCStmt(final SCStmt sc) {
					Object dup2stmt;
					if (sc.redundant()) {

						iter.remove();
						dup2stmt = iter.previous();
						iter.remove();
						if (InductionVarAnalyzer.DEBUG) {
							System.out.println("Removed Redundant ASW: " + sc
									+ "\nand " + dup2stmt);
						}
					}
				}
			});
		}

		if (InductionVarAnalyzer.DEBUG) {
			System.out.println("----------------After cfg.visit--------------");
			cfg.print(System.out);
		}
	}
}
