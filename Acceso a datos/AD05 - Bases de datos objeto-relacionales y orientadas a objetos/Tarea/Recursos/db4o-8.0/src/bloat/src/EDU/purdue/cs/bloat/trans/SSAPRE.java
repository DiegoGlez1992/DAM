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
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Perform partial redundancy elimination of a CFG in SSA form using the
 * SSA-based algorithm described in:
 * 
 * <pre>
 *     Fred Chow, Sun Chan, Robert Kennedy, Shin-Ming Liu, Raymond Lo,
 *     and Peng Tu, &quot;A New Algorithm for Partial Redundancy Elimination
 *     based on SSA Form&quot;, Proc. PLDI '97: 273-286, 1997.
 * </pre>
 * 
 * NOTE: The type for all occurrences of an inserted variable is the same as the
 * type of the first occurrence of the expression the variable replaces. This
 * type is guaranteed since we only group expression with equal types.
 */

/*
 * Okay, you asked for it: SSAPRE. SSAPRE stands for Partial Redundency
 * Elimination in Static Single-Assignment form. Partial Redundency Elimination
 * invovles finding multiple occurreces of the same expressions (i.e.
 * expressions that are lexically equivalent) and moving those occurrences up
 * higher in the CFG so that the expression is evaluated fewer times.
 * Occurrences of the expression that has been moved are replaced with a
 * temporary variable that is assigned the value of the moved expression. PRE is
 * a superset of Common Subexpression Elimination.
 * 
 * The Golden Rule of SSAPRE: Move expressions to eliminate redundent
 * evaluations, but do not make more work along a given path in the CFG by
 * introducing an expression along a path where it originally was not.
 * 
 * PRE techniques have been around for quite sometime, but most algorithms used
 * a bit vector notation to represent occurrences of an expression. In order to
 * apply those PRE techniques, the CFG in SSA form would have to be transformed
 * into bit vector notation before PRE could be performed. The modified bit
 * vectors would then have to be transformed back into a CFG in SSA form before
 * any other optimizations (or code generation) could take place.
 * 
 * That was the way life was until [Chow, et. al.] came along. At the 1997 PLDI
 * conference, they presented a paper that showed how PRE could be performed
 * directly on a CFG in SSA form. The paper itself is a rather difficult read.
 * So don't feel bad if you don't understand it on the first (or seventeenth)
 * read.
 * 
 * BLOAT performs SSAPRE on the CFG for optimizable methods. Due to some of
 * Java's quirks (e.g. exceptions), this implementation does not follow Chow's
 * exactly. However, it is very close and understanding this implementation will
 * aid the understanding of Chow and vice versa.
 * 
 * The first step in SSAPRE is to create a worklist of all expressions that are
 * candidates for SSAPRE. Only first-order expressions are entered into the
 * worklist. First-order expressions consist of a single operators and two
 * operands that are either a variable or have a constant value. For instance,
 * a+b is a first-order expression, while a+b-c is not. The first-order
 * expressions that are inserted into the worklist are "real occurrences" of the
 * expression (as opposed to PHI-statements or PHI-operands, as we shall shortly
 * see). In addition to real occurrences of the expression, the worklist also
 * contains kill statements. Certain constructs in Java, such as exceptions,
 * method calls, and synchronized blocks, limit the assumptions one can make
 * about the behavior of the code. Thus, at places in the code where such
 * constructs occur, kills are inserted to say "We cannot move code across this
 * point." The above is performed in collectOccurrences().
 * 
 * SSAPRE is then performed on each expression in the worklist. The first step
 * in SSAPRE is to place PHI-functions. PHI-functions are similar to the
 * phi-functions that occur in converting a CFG to SSA form. Essentially, a
 * PHI-function is placed at a block (node in the CFG) at which a join occurs
 * (i.a. there are two paths that can enter the block), at which the expression
 * is available on at least one of the incoming paths, and at which the
 * expression will be used again (e.g. we don't bother putting a PHI-function at
 * the "exit" node). This leads us to place PHI-functions in two situations.
 * First, for each block that contains a real occurrence of the expression, a
 * PHI-function is placed at every block in its iterated dominance frontier
 * (DF+). Second, a PHI-function is placed in every block that contains a
 * phi-function for either of the operands to the expression. At this point, the
 * operands to the PHI-functions are unknown. They are of the form:
 * 
 * h = PHI(h, h, ..., h)
 * 
 * and are referred to as PHI-statements (also called "expression-PHIs" in
 * Chow's paper). The method placePhis() inserts these hypothetical
 * PHI-functions into the CFG.
 * 
 * Once the expression-PHIs have been placed, version numbers are assigned to
 * each hypothetical h. Both real occurrences of the expression and
 * PHI-statements for the expression have a numbered h value associated with
 * them. Occurrences that have the same version number have identical values and
 * any control flow path that includes two difference h-versions must cross an
 * assignment to an operand of the expression or an PHI-statement for h. The
 * version numbers are assigned in two passes over the CFG. The first pass
 * compiles of a worklist containing all of the real occurrences that are
 * defined by an PHI-statement. It also maintains a stack of real occurrences of
 * the expression. The first pass optimistically assumes that versions of the
 * operands to a PHI-function are the same as the version of the expression that
 * is on top of the expression stack. The second pass corrects any false
 * assumptions that were made during the first pass. Since the first pass saw
 * all of the occurrences, the versions of the h-variables can be determined
 * from the existence of a phi-function (note lowercase phi) at that block. In
 * some cases, the occurrence may be placed back into the worklist for further
 * processing. The rename() method handles the assignment of version numbers
 * using this two-pass method.
 * 
 * Now that the PHI-functions have been placed and version numbers have been
 * assigned, other information about the hypotheticals is extracted and will be
 * used to move redundent occurrences of the expression. The first piece of
 * information that is calculated is whether or not an PHI-statement is
 * "down-safe" (also referred to as being "anticipated"). When version numbers
 * were assigned to hypotheticals, a use-def relationship was created:
 * PHI-statements use operands that are hypotheticals defined by other
 * occurrences. One can conceptualize a directed graph consisting of
 * PHI-statements and the directed edges going from a use of a hypothetical to
 * its definition. This graph is referred to by Chow as the "SSA Graph". (This
 * is not to be confused with the class SSAGraph which models the SSA Graph for
 * variables, not expressions. This implementation does not directly model the
 * SSA Graph.) Using the SSA Graph the down-safety of an PHI-statement is
 * computed by backwards propagation along the use-def chains. An PHI-statement
 * is not down-safe if there is a control flow path from that PHI-statement
 * along which the expression is not evaluated (i.e. there is no real
 * occurrence) before the exit block is encountered or one of the expression's
 * variables is redefined. Why is down-safety important? If an PHI-statement is
 * not down-safe, it is not worthwhile to hoist it any higher in the code. If it
 * were to be hoisted, it might add a unnecessary evaluation of the expression
 * along some path. This would break the golden rule of SSAPRE. There are two
 * situations in which an PHI-statement is not down-safe. The first occurs when
 * there is no path to exit along which the result (left hand side) of the
 * PHI-statement is not used. The second occurs when there is a path to exit
 * along which the only use of the PHI-statement result is as an operand to an
 * PHI-statement which itself is not down-safe. The PHI-statement that fit the
 * first criterion can be marked as not down-safe during the renaming step. In
 * addition, each operand to an PHI-statement (called an PHI-operand) is marked
 * as "has real use" when the path to the PHI-operand crosses a real occurrence
 * of the same version of the expression. Simply put, an PHI-operand has a real
 * use, if it is associated with a real expression instead of an PHI-statement.
 * The down-safety of the PHI-statements in the CFG is computed using of the
 * downSafety() and resetDownSafe() methods.
 * 
 * The above description was written by Dave and Steve Lennon in early September
 * of 1998. I'm surprised at how much we knew then. Consult the BLOAT Book for
 * the conclusion to our exciting SSAPRE saga.
 */

public class SSAPRE {
	public static boolean DEBUG = false;

	public static boolean NO_THREAD = false; // Do we ignore threads?

	public static boolean NO_PRECISE = false; // Are exceptions not precise?

	public static boolean NO_ACCESS_PATHS = false;

	protected FlowGraph cfg; // CFG on which to perform SSAPRE

	protected int nextValueNumber; // Next value number to assign

	protected EditorContext context;

	protected ResizeableArrayList[] kills;

	protected boolean[] killsSorted;

	protected SideEffectChecker sideEffects;

	protected ExprWorklist worklist; // Worklist containing expr to analyze

	// Maps phi statements together as to allow for access path reduction?
	protected HashMap phiRelated;

	/**
	 * Constructor.
	 * 
	 * @param cfg
	 *            Control flow graph on which to perform SSA-based PRE.
	 * @param context The EditorContext containing all the classes that BLOAT
	 *        knows about.
	 */
	public SSAPRE(final FlowGraph cfg, final EditorContext context) {
		this.cfg = cfg;
		this.context = context;
	}

	/**
	 * Performs SSA-based partial redundency elimination (PRE) on a control flow
	 * graph.
	 */
	public void transform() {
		sideEffects = new SideEffectChecker(context);

		kills = new ResizeableArrayList[cfg.size()];
		killsSorted = new boolean[cfg.size()];

		for (int i = 0; i < kills.length; i++) {
			kills[i] = new ResizeableArrayList();
			killsSorted[i] = false;
		}

		// In a single pass over the CFG:
		//
		// Number the expressions in each block in ascending order.
		// Insert all first-order expressions into the worklist.
		// Insert access path kills into the worklist.
		// Insert exception throw kills into the worklist.
		// Locate phi-related expressions.
		// Find the next value number.

		worklist = new ExprWorklist();
		phiRelated = new HashMap();

		// Compile the worklist of expressions on which to perform SSAPRE.
		collectOccurrences();

		// Do the transformation for each expression.
		while (!worklist.isEmpty()) {
			final ExprInfo exprInfo = worklist.removeFirst();
			transform(exprInfo);
		}

		// null these guys so that they'll be garbage collected sooner
		sideEffects = null;
		kills = null;
		worklist = null;
	}

	/**
	 * Performs partial redundency elimination on a given expression. This
	 * method is called on every lexically-distinct expression in a method.
	 * 
	 * @see #collectOccurrences
	 * 
	 * @see #placePhis
	 * @see #rename
	 * @see #downSafety
	 * @see #willBeAvail
	 * @see #finalize
	 */
	private void transform(final ExprInfo exprInfo) {
		if (SSAPRE.DEBUG) {
			System.out.println("PRE for " + exprInfo.prototype()
					+ " -------------------------");
		}

		if (exprInfo.numUses() == 0) {
			if (SSAPRE.DEBUG) {
				System.out.println("Skipping...all occurrences are "
						+ "as targets. -------------------------");
			}

			exprInfo.cleanup();
			return;
		}

		if (SSAPRE.DEBUG) {
			System.out.println("Placing Phis for " + exprInfo.prototype()
					+ " -------------------------");
		}

		// Place the PHI nodes for the expression. Note that these PHI nodes are
		// for expressions, not variables. However, the same Phi classes are
		// used.
		placePhis(exprInfo);

		if (SSAPRE.DEBUG) {
			exprInfo.print();
			System.out.println("Renaming for " + exprInfo.prototype()
					+ " -------------------------");
		}

		// Calculate version numbers for each occurrence of the expression
		// in exprInfo. Rename occurrences that have the same version number.
		rename(exprInfo);

		if (SSAPRE.DEBUG) {
			exprInfo.print();
			System.out.println("Down safety for " + exprInfo.prototype()
					+ " -------------------------");
		}

		// Determine which PHI-nodes are "down safe". "Down safe" nodes are used
		// at least once on all paths from the PHI-node to the exit node.
		downSafety(exprInfo);

		if (SSAPRE.DEBUG) {
			System.out.println("Will be available for " + exprInfo.prototype()
					+ " -------------------------");
		}

		// Determine at which PHI-nodes the expression in exprInfo will be
		// available after code insertions are performed. Code can only be
		// inserted at the end of the predacessor blocks of these nodes.
		willBeAvail(exprInfo);

		if (SSAPRE.DEBUG) {
			System.out.println("Finalize for " + exprInfo.prototype()
					+ " -------------------------");
		}

		finalize(exprInfo);

		if (SSAPRE.DEBUG) {
			System.out.println("Code motion for " + exprInfo.prototype()
					+ " -------------------------");
		}

		final Type type = exprInfo.prototype().type();
		final LocalVariable v = cfg.method().newLocal(type);
		final VarExpr tmp = new LocalExpr(v.index(), type);

		final SSAConstructionInfo consInfo = new SSAConstructionInfo(cfg, tmp);
		codeMotion(exprInfo, tmp, consInfo);

		if (SSAPRE.DEBUG) {
			System.out.println("Performing incremental SSA for "
					+ exprInfo.prototype() + " -------------------------");
		}

		// OK, this shouldn't be necessary. We should construct the SSA
		// form for t as we do code motion using the expr-phis. But that was
		// quite buggy in the early implementations (and probably still is),
		// so I just build SSA form in another pass. If you change it to
		// build SSA form for t during code motion, you must also remove
		// the phis not in the IDF of the defs of t and fix up the FUD chains
		// afterward.

		SSA.transform(cfg, consInfo);

		// Set the value numbers for all the new exprs.
		// This uses the occurrences of the var and the var-phi information
		// added to consInfo by SSA construction.

		setValueNumbers(consInfo);

		// Add parents of the real occurrences to the var.

		enqueueParents(consInfo);

		if (SSAPRE.DEBUG) {
			exprInfo.print();
			System.out.println("Done with PRE for " + exprInfo.prototype()
					+ " -------------------------");
		}

		// Null out all the pointers in the exprInfo in case the exprInfo
		// is still reachable.
		exprInfo.cleanup();
	}

	/**
	 * Visits the CFG and for each lexically-distinct first-order expression
	 * whose subexpressions no have subexpressions nor side effects (that is,
	 * expressions containing one operatand and comprised of only local
	 * variables and/or constants), places all occurrences of that expression,
	 * sorted by their pre-order positions in the CFG, into a worklist.
	 * 
	 * Note that only real occurrences of expression are inserted into the
	 * worklist. PHI and PHI-operand occurrences have not been placed yet.
	 * 
	 * Additionally, Kill expressions are placed in the worklist to indicate
	 * boundaries across which code cannot be hoisted.
	 */
	private void collectOccurrences() {
		// count represents the preorder number for each expression. It is
		// assigned to each expression's key
		final Int count = new Int();

		// maxValue is the maximum value number encountered on a traversal
		// of the expression tree. It is used to determine this.nextValueNumber
		final Int maxValue = new Int();

		// A Set of Blocks that begin a protected region
		final Set beginTry = beginTry();

		// Visit each node in the CFG. At each Expr node make note of the
		// node's preorder number. Keep track of the largest value number
		// encountered. Add Kills to the worklist when necessary. Add
		// first-order real occurrences of an expression to the worklist at
		// MemRefExpr (access paths) and Expr (expression) nodes.
		cfg.visit(new TreeVisitor() {
			public void visitBlock(final Block block) {
				if (beginTry.contains(block)) {
					// If the block begins a protected region, then we must
					// insert
					// a Kill to prevent hoisting out of the region.
					worklist.addKill(block, new ExceptionKill(count.value++));
				}

				block.visitChildren(this);
			}

			public void visitPhiStmt(final PhiStmt stmt) {
				if (maxValue.value < stmt.valueNumber()) {
					maxValue.value = stmt.valueNumber();
				}

				stmt.visitChildren(this);

				final Iterator iter = stmt.operands().iterator();

				// Iterate over all of the operands to the phi node.
				// Make special note of local (or stack) variables.
				while (iter.hasNext()) {
					final Expr operand = (Expr) iter.next();

					if (operand instanceof VarExpr) {
						if (operand.def() != null) {
							phiRelatedUnion(operand.def(), stmt.target());
						}
					}
				}
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				expr.setKey(count.value++);
			}

			public void visitVarExpr(final VarExpr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				expr.setKey(count.value++);
			}

			public void visitCatchExpr(final CatchExpr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				expr.visitChildren(this);
				expr.setKey(count.value++);
				worklist.addKill(expr.block(), new ExceptionKill(expr.key()));
			}

			public void visitMonitorStmt(final MonitorStmt stmt) {
				if (maxValue.value < stmt.valueNumber()) {
					maxValue.value = stmt.valueNumber();
				}

				if (!SSAPRE.NO_THREAD) {
					stmt.visitChildren(this);
					stmt.setKey(count.value++);
					worklist.addKill(stmt.block(), new MemRefKill(stmt.key()));
				}
			}

			public void visitCallExpr(final CallExpr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				expr.visitChildren(this);
				expr.setKey(count.value++);
				worklist.addKill(expr.block(), new MemRefKill(expr.key()));
			}

			public void visitMemRefExpr(final MemRefExpr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				boolean firstOrder = isFirstOrder(expr);

				if (!firstOrder) {
					expr.visitChildren(this);
				}

				expr.setKey(count.value++);

				if (expr.isDef()) {
					worklist.addKill(expr.block(), new MemRefKill(expr, expr
							.key()));
				}

				if (firstOrder) {
					worklist.addReal(expr);
				}
			}

			public void visitStmt(final Stmt stmt) {
				if (maxValue.value < stmt.valueNumber()) {
					maxValue.value = stmt.valueNumber();
				}

				stmt.visitChildren(this);
			}

			public void visitExpr(final Expr expr) {
				if (maxValue.value < expr.valueNumber()) {
					maxValue.value = expr.valueNumber();
				}

				if (isFirstOrder(expr)) {
					worklist.addReal(expr);
				} else {
					expr.visitChildren(this);
				}

				expr.setKey(count.value++);
			}
		});

		nextValueNumber = maxValue.value + 1;
	}

	/**
	 * Returns a Set of Blocks that begin the protected regions in the CFG.
	 */
	private Set beginTry() {
		final Set beginTry = new HashSet();

		final Iterator blocks = cfg.catchBlocks().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Handler handler = (Handler) cfg.handlersMap().get(block);

			if (handler != null) {
				final HashSet p = new HashSet();

				final Iterator prots = handler.protectedBlocks().iterator();

				while (prots.hasNext()) {
					final Block prot = (Block) prots.next();
					p.addAll(cfg.preds(prot));
				}

				p.removeAll(handler.protectedBlocks());

				// Add the protected region blocks which have preds outside the
				// region to the beginTry set.
				final Iterator preds = p.iterator();

				while (preds.hasNext()) {
					final Block pred = (Block) preds.next();
					beginTry.addAll(cfg.succs(pred));
				}
			}
		}

		return beginTry;
	}

	private void enqueueParents(final SSAConstructionInfo consInfo) {
		final Set seen = new HashSet();

		final Iterator iter = cfg.nodes().iterator();

		while (iter.hasNext()) {
			final Block block = (Block) iter.next();

			final Iterator e = consInfo.realsAtBlock(block).iterator();

			while (e.hasNext()) {
				final VarExpr real = (VarExpr) e.next();

				Node p = real.parent();

				if ((p instanceof StoreExpr) && real.isDef()) {
					p = p.parent();
				}

				if ((p instanceof Expr) && !seen.contains(p)) {
					final Expr expr = (Expr) p;

					seen.add(p);

					if (isFirstOrder(expr)) {
						worklist.addReal(expr);
					}
				}
			}
		}
	}

	private void setValueNumbers(final SSAConstructionInfo consInfo) {
		// Compute value numbers using the RPO algorithm \cite{Simpson96}.
		// For such a small set of numbers this should be faster than
		// recomputing the strongly connected components of the entire SSA
		// graph and using the SCC-based algorithm.

		boolean changed = true;

		while (changed) {
			changed = false;

			final List postOrder = cfg.postOrder();
			final ListIterator iter = postOrder.listIterator(postOrder.size());

			while (iter.hasPrevious()) {
				final Block block = (Block) iter.previous();

				final PhiStmt phi = consInfo.phiAtBlock(block);

				if (phi != null) {
					if (phi.target().valueNumber() == -1) {
						phi.target().setValueNumber(nextValueNumber++);
						changed = true;
					}

					final Iterator operands = phi.operands().iterator();

					while (operands.hasNext()) {
						final VarExpr operand = (VarExpr) operands.next();

						if (operand == null) {
							continue;
						}

						final VarExpr def = (VarExpr) operand.def();

						if (def == null) {
							if (operand.valueNumber() == -1) {
								operand.setValueNumber(nextValueNumber++);
								changed = true;
							}
							continue;
						}

						if (def.valueNumber() == -1) {
							def.setValueNumber(nextValueNumber++);
							changed = true;
						}

						if (def.valueNumber() != operand.valueNumber()) {
							operand.setValueNumber(def.valueNumber());
							changed = true;
						}
					}
				}

				final Iterator e = consInfo.realsAtBlock(block).iterator();

				while (e.hasNext()) {
					final VarExpr real = (VarExpr) e.next();

					if (real.isDef()) {
						Assert.isTrue(real.parent() instanceof StoreExpr);

						final StoreExpr store = (StoreExpr) real.parent();
						final Expr rhs = store.expr();

						if (rhs.valueNumber() == -1) {
							// This should only happen with hoisted stores.
							rhs.setValueNumber(nextValueNumber++);
							changed = true;
						}

						if (store.valueNumber() != rhs.valueNumber()) {
							// This should only happen with hoisted stores.
							store.setValueNumber(rhs.valueNumber());
							changed = true;
						}

						if (real.valueNumber() != rhs.valueNumber()) {
							real.setValueNumber(rhs.valueNumber());
							changed = true;
						}
					} else {
						final VarExpr def = (VarExpr) real.def();

						if (def == null) {
							if (real.valueNumber() == -1) {
								real.setValueNumber(nextValueNumber++);
								changed = true;
							}
							continue;
						}

						if (def.valueNumber() == -1) {
							// This shouldn't happen.
							def.setValueNumber(nextValueNumber++);
							changed = true;
						}

						if (def.valueNumber() != real.valueNumber()) {
							real.setValueNumber(def.valueNumber());
							changed = true;
						}
					}
				}
			}
		}

		final Iterator iter = cfg.nodes().iterator();

		while (iter.hasNext()) {
			final Block block = (Block) iter.next();

			final PhiStmt phi = consInfo.phiAtBlock(block);

			if (phi != null) {

				final Iterator operands = phi.operands().iterator();

				while (operands.hasNext()) {
					final Expr operand = (Expr) operands.next();

					if (operand instanceof VarExpr) {
						if (operand.def() != null) {
							phiRelatedUnion(operand.def(), phi.target());
						}
					}
				}
			}
		}
	}

	/**
	 * A PHI-function (different from a phi-function) is needed whenever
	 * different values of the same expression reach a common point in the
	 * program. A PHI is inserted in a block in two different situations:
	 * 
	 * 1) Place PHI at the expression's iterated dominance frontier (DF+) 2)
	 * When there is a phi for a variable contained in the expression (this
	 * indicates an alteration in the expression)
	 * 
	 * It is only necessary to insert a PHI at a merge point when the expression
	 * will occur again after that block.
	 */
	private void placePhis(final ExprInfo exprInfo) {
		// Place Phis for each expression at the iterated dominance
		// frontier of the blocks containing the expression.

		// w contains all of the blocks in which the expression occurs
		final ArrayList w = new ArrayList(cfg.size());

		Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			if (exprInfo.occurrencesAtBlock(block).size() > 0) {
				w.add(block);
			}
		}

		// The iterated dominance frontier for all of the blocks containing
		// the expression. Will ultimately contain all of the places at which
		// PHI-function need to be inserted.
		final Set df = new HashSet(cfg.iteratedDomFrontier(w));

		// Set of phi functions for the variables in the expression
		final ArrayList worklist = new ArrayList();

		// Set of phi functions that have ever been added to the worklist.
		// When blocks in the worklist are processed, they are removed.
		// inWorklist ensures that a block is not processed more than once.
		final Set inWorklist = new HashSet();

		// For each variable occurrence in exprInfo, place a Phi where
		// there is a phi for the variable.

		blocks = cfg.nodes().iterator();

		// Iterate over every block in the method and make a worklist of all
		// phi functions that define one of the variables in this expression.
		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Iterator e = exprInfo.realsAtBlock(block).iterator();

			while (e.hasNext()) {
				final Expr real = (Expr) e.next();

				real.visit(new TreeVisitor() {
					public void visitVarExpr(final VarExpr var) {
						final Expr def = var.def();

						if (def == null) {
							return;
						}

						final Node p = def.parent();

						if ((p instanceof PhiStmt) && !inWorklist.contains(p)) {
							worklist.add(p);
							inWorklist.add(p);
						}
					}
				});
			}
		}

		// Go through the worklist and add the blocks containing the
		// phi-functions to list of blocks to which to add PHI-functions.
		// Also, examine each operand to the phi-function and add it to
		// the worklist if it itself is defined by a phi-function.
		while (!worklist.isEmpty()) {
			final PhiStmt phi = (PhiStmt) worklist.remove(worklist.size() - 1);
			df.add(phi.block());

			final Iterator iter = phi.operands().iterator();

			while (iter.hasNext()) {
				final Expr expr = (Expr) iter.next();

				if (expr instanceof VarExpr) {
					final VarExpr var = (VarExpr) expr;

					final Expr def = var.def();

					if (def == null) {
						continue;
					}

					final Node p = def.parent();

					if ((p instanceof PhiStmt) && !inWorklist.contains(p)) {
						worklist.add(p);
						inWorklist.add(p);
					}
				}
			}
		}

		// df contains all of the blocks in which an PHI-statement for the
		// expression should be added. Add them to the exprInfo.
		final Iterator iter = df.iterator();

		while (iter.hasNext()) {
			final Block block = (Block) iter.next();
			exprInfo.addPhi(block);
		}
	}

	/**
	 * Rename all occurrences of the expression. placePhis went through the CFG
	 * and placed PHI-functions at merge blocks in the code. Now, we have to go
	 * through and assign version numbers to all of the "h" variables generated
	 * by the PHI-functions.
	 * <p>
	 * There are two methods outlined in [Chow 1997]. The first is more
	 * straightforward (ya right, it took us two days to figure it out) while
	 * the second is more space efficient. The second method delays the renaming
	 * of the "h" variables. It makes two passes over the CFG (actually, they
	 * are preorder traversals of the dominator tree).
	 * <p>
	 * The first pass builds a worklist containing all of the real occurrences
	 * that are defined by a PHI for a given expression. We optimisitically
	 * assume that versions of PHI operands are the same as the version on top
	 * of the expression stack. These assumptions are checked for correctness
	 * during the second pass.
	 * <p>
	 * The second pass performs the correct renaming. It relies on seeing a
	 * later occurrence of the expression. That is, it implies that at the
	 * earlier PHI, the expression is partially anticipated. The second pass
	 * operates on all of the real occurrences in the worklist built in the
	 * first pass. From the versions of the variables at the merge block of a
	 * PHI, the versions of the variables at each predacessor block are
	 * determined based on the presence or absence of a phi-function for the at
	 * that merge block. If the versions are different from the assumed versions
	 * from the first pass, the operand is rest to null (bottom). Otherwise, the
	 * operand is correct. If the PHI operand is also defined by a PHI, it is
	 * added to the worklost and is handled later.
	 */
	// Rename all occurrences of the expression. This is done in two passes.
	//
	// The first pass assigns version numbers (FUD chain pointers really) in a
	// pre-order traversal of the dominator tree and builds the worklist for
	// pass 2.
	//
	// We optimistically assume that a Phi can be used as a definition for a
	// real and clean up in pass 2 by adjusting all the FUD chains if the
	// assumption proves false.
	// 
	// NOTE: Renaming is where almost all previous PRE bugs have come from, so
	// when looking for a bug, it might be good to start looking here first.
	private void rename(final ExprInfo exprInfo) {
		// Renaming pass 1. This assigns version numbers (FUD chain
		// pointers really) in a pre-order traversal of the dominator tree
		// and builds the worklist for pass 2.

		final ArrayList renameWorklist = new ArrayList();

		search(cfg.source(), exprInfo, null, null, renameWorklist);

		// Pass 2.

		// First, build another worklist which uses the leaves of the reals
		// on the old worklist. We extend this worklist later with the leaves
		// factored through var-phis.

		final HashSet seen = new HashSet();

		final LinkedList leavesWorklist = new LinkedList();

		final Iterator iter = renameWorklist.iterator();

		while (iter.hasNext()) {
			// Examine each real occurrence that may need more work.
			// Construct a list of the operands of the real occurrence. We
			// should have already determined that the occurrence is first
			// order. So, if we hit anything other than a constant, local
			// variable, or stack expression, we have a problem.

			final Expr real = (Expr) iter.next();
			final Phi phi = (Phi) exprInfo.def(real);

			// Keep track of operands of the real occurrence.
			final ArrayList leaves = new ArrayList();

			real.visitChildren(new TreeVisitor() {
				public void visitStoreExpr(final StoreExpr expr) {
					// This should have been checked before adding
					// the real to the worklist.
					throw new RuntimeException();
				}

				public void visitConstantExpr(final ConstantExpr expr) {
					leaves.add(expr);
				}

				public void visitVarExpr(final VarExpr expr) {
					leaves.add(expr.def());
				}

				public void visitExpr(final Expr expr) {
					throw new RuntimeException();
				}
			});

			// Save the leaves for later use when building phi operands.
			phi.setLeaves(leaves);

			leavesWorklist.add(phi);
		}

		// Now we actually go about assigning version numbers to the
		// operands of the PHI-statement. If the operand is defined by a
		// real occurrence (RealDef), examine the children of the real
		// occurrence.

		while (!leavesWorklist.isEmpty()) {
			final Phi phi = (Phi) leavesWorklist.removeFirst();
			phi.setLive(true);

			final List leaves = phi.leaves();

			// Compare the leaves against what we expect for the Phi operands.
			final Iterator preds = cfg.preds(phi.block()).iterator();

			PREDS: while (preds.hasNext()) {
				final Block pred = (Block) preds.next();
				final Def operand = phi.operandAt(pred);

				if (operand instanceof RealDef) {
					final Expr expr = ((RealDef) operand).expr;

					final Bool match = new Bool();
					match.value = true;

					final Iterator leafIter = leaves.iterator();

					expr.visitChildren(new TreeVisitor() {
						public void visitExpr(final Expr expr) {
							throw new RuntimeException();
						}

						public void visitStoreExpr(final StoreExpr expr) {
							expr.target().visit(this);
						}

						public void visitConstantExpr(final ConstantExpr expr) {
							visitLeaf(expr);
						}

						public void visitVarExpr(final VarExpr expr) {
							visitLeaf(expr);
						}

						public void visitLeaf(final Expr expr) {
							if (!leafIter.hasNext()) {
								// We've already examined all of the leaves,
								// they
								// don't match
								match.value = false;
								return;
							}

							Expr leaf = (Expr) leafIter.next();

							// Factor the leaf through any var-phis there. That
							// is,
							// If the leaf is defined by a phi-statement, use
							// the
							// corresponding phi-operand as the leaf. If the
							// leaves
							// don't match (i.e. are not constants, variables,
							// nor
							// have the same value number), say so.
							if (leaf instanceof VarExpr) {
								Assert.isTrue(((VarExpr) leaf).isDef());

								if (leaf.parent() instanceof PhiJoinStmt) {
									final PhiJoinStmt leafPhi = (PhiJoinStmt) leaf
											.parent();

									if (leafPhi.block() == phi.block()) {
										leaf = leafPhi.operandAt(pred);
									}
								}
							}

							if (!(leaf instanceof ConstantExpr)
									&& !(leaf instanceof VarExpr)) {

								match.value = false;
								return;
							}

							if (expr.valueNumber() != leaf.valueNumber()) {
								match.value = false;
								return;
							}
						}
					});

					if (!match.value || leafIter.hasNext()) {
						// If the leaves do not match (or if we didn't get to
						// all
						// of the leaves), then we have a null PHI-operand
						// (bottom) and the operand does not have a real use.
						phi.setOperandAt(pred, null);
						phi.setHasRealUse(pred, false);
					}

				} else if (operand instanceof Phi) {
					final ArrayList newLeaves = new ArrayList(leaves.size());
					final Phi opPhi = (Phi) operand;

					final Iterator leafIter = leaves.iterator();

					// If the operand is defined by a PHI-statement,

					LEAVES: while (leafIter.hasNext()) {
						Expr leaf = (Expr) leafIter.next();

						// Factor the leaf through a phi.
						if (leaf instanceof VarExpr) {
							Assert.isTrue(((VarExpr) leaf).isDef());

							if (leaf.parent() instanceof PhiJoinStmt) {
								final PhiJoinStmt leafPhi = (PhiJoinStmt) leaf
										.parent();

								if (leafPhi.block() == phi.block()) {
									leaf = leafPhi.operandAt(pred);
								}
							}
						}

						if (leaf instanceof VarExpr) {
							leaf = leaf.def();

							if (leaf.block() == opPhi.block()) {
								if (leaf.parent() instanceof PhiJoinStmt) {
									newLeaves.add(leaf);
									continue LEAVES;
								}
							} else if (leaf.block().dominates(opPhi.block())) {
								newLeaves.add(leaf);
								continue LEAVES;
							}
						}

						// The leaf is defined after the operand.
						phi.setOperandAt(pred, null);
						phi.setHasRealUse(pred, false);
						continue PREDS;
					}

					Assert.isTrue(leaves.size() == newLeaves.size());

					// If we got here, the real only uses leaves defined above
					// the operand. Add the operand to the worklist.
					final Pair pair = new Pair(phi, opPhi);

					if (!seen.contains(pair)) {
						seen.add(pair);
						opPhi.setLeaves(newLeaves);
						leavesWorklist.add(opPhi);
					}
				}
			}
		}

		// Remove the dead phis.
		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if ((phi != null) && !phi.live()) {
				if (SSAPRE.DEBUG) {
					System.out.println("    dead Phi at " + block);
				}

				exprInfo.removePhi(block);
			}
		}
	}

	class Pair {
		Object a;

		Object b;

		Pair(final Object a, final Object b) {
			this.a = a;
			this.b = b;
		}

		public boolean equals(final Object o) {
			return (o instanceof Pair) && ((Pair) o).a.equals(a)
					&& ((Pair) o).b.equals(b);
		}

		public int hashCode() {
			return a.hashCode() ^ b.hashCode();
		}
	}

	/**
	 * This method performs the first pass of the delayed renaming algorithm.
	 * Recall that the original renaming algorithm kept a stack for the
	 * "current" version number of each variable used in the expression being
	 * renamed. Well, when a real occurrence of the expression is encountered,
	 * we don't need the stacks because the real occurrence contains the current
	 * version numbers of the variables. So, we only need the version numbers
	 * when renaming the "h" variables for PHI-operands.
	 * 
	 * When this first pass encounters a PHI-operand, it optimistically assumes
	 * that the version on top of the stack is the correct version. The "h"
	 * values for real occurrences will be handled correctly.
	 * 
	 * This implementation represents an occurrences "h" value by its Def (the
	 * "setDef" method of ExprInfo). This method performs a pre-order traversal
	 * of the CFG's dominator tree and assigns "names" (actually references to
	 * Defs) to occurrences of an expression.
	 * 
	 * The end result of this traversal is a worklist of real occurrences that
	 * require further renaming. Along the way, we compute the down safety (or
	 * lack there of) of some PHI-statements.
	 * 
	 * @param block
	 *            The block in the CFG being traversed
	 * @param exprInfo
	 *            The expression on which we are performing PRE.
	 * @param top
	 *            The most recently encountered real occurrence of the
	 *            expression. It can be thought of as the "top" of a stack of
	 *            expressions.
	 * @param topdef
	 *            top's Def. That is, its "h" value.
	 */
	// This pass is pretty much as described in Chow97 in the Delayed
	// Renaming section, except we have to handle kills for access paths
	// and for exceptions.
	//
	// Instead of using an explicit stack of occurrences, top points to
	// the occurrence at the top of the stack and topdef points to top's
	// def. top is null if topdef is a Phi and a real occurrence hasn't
	// followed it. Thus a Phi is not down safe if it is killed and top
	// is null.
	private void search(final Block block, final ExprInfo exprInfo, Expr top,
			Def topdef, final List renameWorklist) {
		if (SSAPRE.DEBUG) {
			System.out.println("    renaming in " + block);
		}

		final Phi phi = exprInfo.exprPhiAtBlock(block);

		// If there's a PHI in the block, make this PHI the new topdef.
		// 
		if (phi != null) {

			top = null;
			topdef = phi;

			// If the expression has a stack variable, don't allow any
			// hoisting.
			//
			// To prevent hoisting, it is sufficient to make the phi not
			// down safe. If any operand of the phi is null and the phi is
			// not down safe, no hoisting will be attempted (see
			// canBeAvail). If an operand is non-null, then the expression
			// is already evaluated on that path and no hoisting should be
			// attempted.

			if (exprInfo.hasStackVariable()) {
				phi.setDownSafe(false);
			}

			// If the expression can throw an exception, don't allow any
			// hoisting. This is stricter than it should be.
			//
			// We can fix this for fields, divisions, and remainders with
			// a trick like: NullCheck(p).f or x / ZeroCheck(y).
			//
			// Array refs are more complicated since you need both the
			// array and the index checked.
			//
			// Don't bother if exceptions are not precise.

			if (!SSAPRE.NO_PRECISE && exprInfo.hasSideEffects()) {
				phi.setDownSafe(false);
			}
		}

		// If we hit the sink node, a phi at the top of the stack is not
		// down safe.
		if (block == cfg.sink()) {
			if ((topdef instanceof Phi) && (top == null)) {
				((Phi) topdef).setDownSafe(false);
			}

			// The sink node has no real occurrences and no children in
			// the dominator tree. So, go home.
			return;
		}

		// Kill (nullify) topdef in catch blocks. This prevents hoisting into
		// protected regions.
		if (cfg.catchBlocks().contains(block)) {
			if ((topdef instanceof Phi) && (top == null)) {
				((Phi) topdef).setDownSafe(false);
			}

			if (SSAPRE.DEBUG) {
				System.out.println("Top killed at catch " + block);
			}

			top = null;
			topdef = null;
		}

		// Go through all of the real occurrences (and any kills) in the
		// block in the order that they appear.
		final Iterator e = exprInfo.occurrencesAtBlock(block).iterator();

		while (e.hasNext()) {
			final Object obj = e.next();

			if (obj instanceof Kill) {
				if (topdef != null) {
					final Kill kill = (Kill) obj;

					if (SSAPRE.DEBUG) {
						System.out.println("Kill " + kill.expr);
					}

					boolean die = false;

					// If we have a memory reference (access path), we need to
					// check if the Kill could be an alias def for this
					// expression.
					if (exprInfo.prototype() instanceof MemRefExpr) {
						if (kill instanceof MemRefKill) {
							final MemRefExpr k = (MemRefExpr) kill.expr;
							final MemRefExpr p = (MemRefExpr) exprInfo
									.prototype();

							if (kill.expr == null) {
								// If kill.expr is null, kill everything.
								die = true;

							} else if (TBAA.canAlias(context, k, p)) {
								die = true;
							}
						}
					}

					// If we haven't been killed yet, see if the kill is there
					// to prevent us from hoisting out of protected regions.
					//
					// This is possibly not necessary since if the exception
					// can be thrown outside the protected region, we won't get
					// here in the first place. Removing this code could give
					// us better results.
					if (!die && exprInfo.hasSideEffects()) {
						if (kill instanceof ExceptionKill) {
							// Just a kill to keep us from hoisting out of
							// a protected region or out of a handler.
							die = true;
						}
					}

					if (die) {
						if (SSAPRE.DEBUG) {
							System.out.println("Killed");
						}

						if ((topdef instanceof Phi) && (top == null)) {
							((Phi) topdef).setDownSafe(false); // Can't use it
						}

						top = null;
						topdef = null;
					}
				}

				continue;
			}

			// If we get here, we are dealing with a real occurrence of the
			// expression. Now we need to determine whether or not the real
			// occurrence matches the "h" value (definition) on top of the
			// "stack" (topdef).
			final Expr real = (Expr) obj;

			// If the real has a store in it, we can't reuse the def at
			// the top of stack, even if it is a Phi. Because something
			// got redefined inside the expression.
			final Bool hasStore = new Bool();

			if (real.isDef()) {
				hasStore.value = true;

			} else {
				real.visit(new TreeVisitor() {
					public void visitStoreExpr(final StoreExpr expr) {
						hasStore.value = true;
					}

					public void visitExpr(final Expr expr) {
						if (!hasStore.value) {
							expr.visitChildren(this);
						}
					}
				});
			}

			boolean matches = true;

			if (hasStore.value) {
				matches = false;

				if (SSAPRE.DEBUG) {
					System.out.println("real has store");
				}
			}

			if (matches && (topdef == null)) {
				matches = false;

				if (SSAPRE.DEBUG) {
					System.out.println("null topdef");
				}
			}

			if (matches && (topdef instanceof Phi)) {
				if (!matchesPhi(real, (Phi) topdef)) {
					// Some variable used in the real got redefined after the
					// PHI. So they'll have different values.
					matches = false;

					if (SSAPRE.DEBUG) {
						System.out.println("uses var defined after topdef");
					}
				}
			}

			if (matches && (top != null)) {
				if (!matches(top, real)) {
					matches = false;

					if (SSAPRE.DEBUG) {
						System.out.println("mismatch " + top + " != " + real);
					}
				}
			}

			// If topdef does not match the real occurrence, then make the real
			// occurrence the new topdef.
			if (!matches) {
				if ((top == null) && (topdef instanceof Phi)) {
					// No real occurrence after the Phi, so the Phi is not down
					// safe.
					((Phi) topdef).setDownSafe(false);
				}

				// We know that the real occurrence defines the expression
				final RealDef def = new RealDef(real);
				exprInfo.setDef(real, def);
				topdef = def;

			} else {
				// The operands of the real occurrence and the PHI-statement
				// match. So, the definition on top of the "stack" defines
				// the expression.

				Assert.isTrue(topdef != null);

				if (SSAPRE.DEBUG) {
					System.out.println("copying top def");
				}

				if (topdef instanceof Phi) {
					// If the definition on top of the renaming stack is a
					// PHI-statement, the real occurrence may need more work.
					// Add it to the renameWorklist.
					renameWorklist.add(real);
				}

				// Copy the definition from the top of the stack.
				exprInfo.setDef(real, topdef);
			}

			top = real;
		}

		final Iterator succs = cfg.succs(block).iterator();

		// Examine each successor block of the block being traversed. If
		// the block contains a PHI-statement, set the PHI-statement's
		// operand corresponding to the block being traversed to the
		// definition on top of the renaming stack.
		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			// If we hit the sink node, a Phi at the top of the stack is not
			// down safe.
			if (succ == cfg.sink()) {
				if ((top == null) && (topdef instanceof Phi)) {
					((Phi) topdef).setDownSafe(false);
				}
			}

			final Phi succPhi = exprInfo.exprPhiAtBlock(succ);

			if (succPhi != null) {
				succPhi.setOperandAt(block, topdef);

				if (top != null) {
					succPhi.setHasRealUse(block, true);
				}
			}
		}

		final Iterator children = cfg.domChildren(block).iterator();

		// Visit each child in the dominator tree.
		while (children.hasNext()) {
			final Block child = (Block) children.next();
			search(child, exprInfo, top, topdef, renameWorklist);
		}
	}

	/**
	 * This method determines whether or not a given (real occurrence of an)
	 * expression has the same operands as the target of a PHI-statement. That
	 * is, has one of the operands of the real occurrence changed since the the
	 * PHI-statement?
	 */
	private boolean matchesPhi(final Expr real, final Phi phi) {
		final Bool match = new Bool();
		match.value = true;

		real.visitChildren(new TreeVisitor() {
			public void visitExpr(final Expr expr) {
				if (match.value) {
					expr.visitChildren(this);
				}
			}

			public void visitStoreExpr(final StoreExpr expr) {
				// A store means a new SSA number, so they won't match
				match.value = false;
			}

			public void visitVarExpr(final VarExpr expr) {
				if (!match.value) {
					return;
				}

				// We're dealing with one of the operands of the real
				// occurrence. If the operand is defined by a phi-statement
				// that occurrs in the same block as the PHI-statement, then
				// the variable in the real occurrence is the same as that in
				// the PHI-statement. Similarly, is the block in which the
				// real occurrence's definition occurs dominate the block in
				// which the PHI-statement occurs, the two versions of the
				// variable are the same. Otherwise the variable has been
				// modified between the PHI-statement and the real occurrence.

				final VarExpr def = (VarExpr) expr.def();

				if (def == null) {
					match.value = false;
					return;
				}

				final Block block = phi.block();

				final Node p = def.parent();

				if (block == p.block()) {
					// Anything other than a var-phi means the real occurrence
					// uses a variable defined after the Phi.
					if (p instanceof PhiJoinStmt) {
						return;
					}

				} else if (p.block().dominates(block)) {
					// The real uses a var defined above the phi.
					// This, too, is okay.
					return;
				}

				// The real uses a variable defined after the Phi.
				match.value = false;
			}
		});

		return match.value;
	}

	/**
	 * Compares two expressions and determines whether or not they match.
	 */
	private boolean matches(final Expr expr1, final Expr expr2) {
		final LinkedList leaves = new LinkedList();

		expr1.visit(new TreeVisitor() {
			public void visitStoreExpr(final StoreExpr expr) {
				expr.target().visit(this);
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				leaves.add(expr);
			}

			public void visitVarExpr(final VarExpr expr) {
				leaves.add(expr);
			}
		});

		final Bool match = new Bool();
		match.value = true;

		expr2.visit(new TreeVisitor() {
			public void visitExpr(final Expr expr) {
				if (match.value) {
					expr.visitChildren(this);
				}
			}

			public void visitStoreExpr(final StoreExpr expr) {
				if (match.value) {
					expr.target().visit(this);
				}
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				visitLeaf(expr);
			}

			public void visitVarExpr(final VarExpr expr) {
				visitLeaf(expr);
			}

			public void visitLeaf(final Expr expr) {
				if (leaves.isEmpty()) {
					match.value = false;
					return;
				}

				final Expr leaf = (Expr) leaves.removeFirst();

				if ((leaf == null)
						|| (expr.valueNumber() != leaf.valueNumber())) {
					match.value = false;
				}
			}
		});

		return match.value;
	}

	private Expr buildPhiOperand(final ExprInfo exprInfo, final Phi phi,
			final Block pred) {
		final Iterator leaves = phi.leaves().iterator();

		final Expr expr = (Expr) exprInfo.prototype().clone();

		expr.visit(new TreeVisitor() {
			public void visitExpr(final StoreExpr expr) {
				throw new RuntimeException();
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				visitLeaf(expr);
			}

			public void visitVarExpr(final VarExpr expr) {
				visitLeaf(expr);
			}

			public void visitLeaf(final Expr expr) {
				if (leaves.hasNext()) {
					Expr leaf = (Expr) leaves.next();

					if (leaf instanceof VarExpr) {
						Assert.isTrue(((VarExpr) leaf).isDef());

						if (leaf.parent() instanceof PhiJoinStmt) {
							final PhiJoinStmt leafPhi = (PhiJoinStmt) leaf
									.parent();

							if (leafPhi.block() == phi.block()) {
								leaf = leafPhi.operandAt(pred);

								if (leaf instanceof VarExpr) {
									leaf = leaf.def();
								}
							}
						}
					}

					Assert.isTrue(leaf != null);

					final Expr copy = (Expr) leaf.clone();

					if (leaf.isDef()) {
						copy.setDef((VarExpr) leaf);
					}

					expr.replaceWith(copy);
				} else {
					throw new RuntimeException();
				}
			}
		});

		expr.setValueNumber(nextValueNumber++);

		return expr;
	}

	/**
	 * A Phi is not down safe if there is a control flow path from that Phi
	 * along which the expression is not evaluated before exit or being altered
	 * by redefinition of one of the variables of the expression. This can
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

			if ((phi == null) || phi.downSafe()) {
				continue;
			}

			final Iterator e = cfg.preds(block).iterator();

			while (e.hasNext()) {
				final Block pred = (Block) e.next();
				resetDownSafe(phi, pred);
			}
		}
	}

	private void resetDownSafe(final Phi phi, final Block block) {
		if (phi.hasRealUse(block)) {
			return;
		}

		final Def def = phi.operandAt(block);

		if (def instanceof Phi) {
			final Phi phidef = (Phi) def;

			if (phidef.downSafe()) {
				phidef.setDownSafe(false);

				if (SSAPRE.DEBUG) {
					System.out.println("            def = Phi in "
							+ phidef.block());
					System.out.println("            def made not down safe");
				}

				final Iterator e = cfg.preds(block).iterator();

				while (e.hasNext()) {
					final Block pred = (Block) e.next();
					resetDownSafe(phidef, pred);
				}
			}
		}
	}

	/**
	 * Determines whether or not a PHI expression is "will be available". Will
	 * be available determines where we end up placing an evaluation of the
	 * expression. Will be available = Can be available AND (not Later)
	 */
	private void willBeAvail(final ExprInfo exprInfo) {
		computeCanBeAvail(exprInfo);
		computeLater(exprInfo);
	}

	/**
	 * Can be available (cba) means "at this point, we can insert an evaluation
	 * of the expression". If cba = 0, then the PHI-statement is "useless" and
	 * uses of it are changed to tack (bottom). Can be available depends on the
	 * down safety of the PHI-statement.
	 */
	private void computeCanBeAvail(final ExprInfo exprInfo) {
		final Iterator blocks = cfg.nodes().iterator();

		// Go through every PHI-statement of the exprInfo.
		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi == null) {
				continue;
			}

			if (!phi.canBeAvail()) {
				continue;
			}

			if (phi.downSafe()) {
				continue;
			}

			final Iterator e = cfg.preds(block).iterator();

			// We determined above that:
			// 1. This PHI-statement is not down safe
			// 2. It is currently can be available
			// Now, if one of the PHI-statement's operands is tack (null),
			// reset "can be avail" for this PHI-statement.

			while (e.hasNext()) {
				final Block pred = (Block) e.next();

				final Def operand = phi.operandAt(pred);

				if (operand == null) {
					resetCanBeAvail(exprInfo, phi);
					break;
				}
			}
		}
	}

	/**
	 * Resets the cba flag for a given PHI-expression and then iterates over the
	 * PHI-statement's operands and resets them under certain conditions.
	 */
	private void resetCanBeAvail(final ExprInfo exprInfo, final Phi phi) {
		phi.setCanBeAvail(false);

		final Iterator blocks = cfg.nodes().iterator();

		// Iterate over every PHI-statement, other, that uses the
		// the "h" defined by phi as an operand...

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi other = exprInfo.exprPhiAtBlock(block);

			if (other == null) {
				continue;
			}

			final Iterator e = cfg.preds(block).iterator();

			while (e.hasNext()) {
				final Block pred = (Block) e.next();

				final Def operand = other.operandAt(pred);

				// For each use of the "h" defined by exprInfo...
				if (operand == phi) {
					if (other.hasRealUse(pred)) {
						continue;
					}

					// If the use does not have a real use, set the use to tack
					// (bottom).
					other.setOperandAt(pred, null);

					// Since we changed other (by setting one of its operands to
					// tack), if other is not down safe, propagate this
					// information
					// back up the CFG by resetting its cba.
					if (!other.downSafe() && other.canBeAvail()) {
						resetCanBeAvail(exprInfo, other);
					}
				}
			}
		}
	}

	/**
	 * Later basically says, "We cannot place an evaluation of the expression
	 * any later that this point without adding additional evaluation(s) along
	 * some path". An expression is "interesting" when later is false.
	 */
	private void computeLater(final ExprInfo exprInfo) {
		Iterator blocks = cfg.nodes().iterator();

		// Initialize later to can be available...
		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi == null) {
				continue;
			}

			phi.setLater(phi.canBeAvail());
		}

		blocks = cfg.nodes().iterator();

		// Iterate over each PHI-statement, phi...

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi == null) {
				continue;
			}

			if (!phi.later()) {
				continue;
			}

			final Iterator e = cfg.preds(block).iterator();

			// If later == true and there is an operand of phi that:
			// 1. is not tack
			// 2. has a real use
			// set later to false and propagate this information back up the
			// CFG.
			// Basically, what we're saying is that if an operand of the
			// PHI-statement has a real use, we want to evaluate the expression
			// now.

			while (e.hasNext()) {
				final Block pred = (Block) e.next();
				final Def operand = phi.operandAt(pred);

				if ((operand != null) && phi.hasRealUse(pred)) {
					resetLater(exprInfo, phi);
					break;
				}
			}
		}
	}

	/**
	 * Resets later and propagates this information back up the CFG.
	 */
	private void resetLater(final ExprInfo exprInfo, final Phi phi) {
		phi.setLater(false);

		final Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi other = exprInfo.exprPhiAtBlock(block);

			if (other == null) {
				continue;
			}

			final Iterator e = cfg.preds(block).iterator();

			// For PHI-statement that has the "h" defined by phi as an
			// operand...

			while (e.hasNext()) {
				final Block pred = (Block) e.next();
				final Def operand = other.operandAt(pred);

				if (operand == phi) {
					if (!other.later()) {
						continue;
					}

					// Propagate later = false up the CFG.
					resetLater(exprInfo, other);
					break;
				}
			}
		}
	}

	/**
	 * Finalize is the final step in preparing for the placement of temporaries
	 * and evaluations of the expression. It decides whether the results of real
	 * occurrences should be computed on the spot (and saved to a temporary) or
	 * reloaded from a temporary. Some PHI-statements are removed and some are
	 * replaced by PHI-statements operating on the temporaries. Additional
	 * evaluations of the expression may be added where the expression is not
	 * available.
	 */
	private void finalize(final ExprInfo exprInfo) {
		// We assume that all availDef for exprInfo are tack.
		// Perform a perorder traversal of the dominance tree. Remember that
		// the root of the dominance tree is also the root of the CFG.
		finalizeVisit(exprInfo, cfg.source(), null);
	}

	/**
	 * 
	 * 
	 * @param exprInfo
	 *            The expression on which we're performing SSAPRE.
	 * @param block
	 *            The block to search for occurrences of exprInfo.
	 * @param top
	 *            Top is used to determine when an element of Avail_def
	 *            dominates a given occurrence.
	 */
	private void finalizeVisit(final ExprInfo exprInfo, final Block block,
			Def top) {
		if (SSAPRE.DEBUG) {
			System.out.println("    finalizing " + block);
		}

		// Get the (only) PHI-statement at the current block. If wba = 1 for
		// the PHI-statement,
		final Phi phi = exprInfo.exprPhiAtBlock(block);

		if (phi != null) {
			if (phi.willBeAvail()) {
				exprInfo.setAvailDef(phi, phi);
				top = phi;
			} else {
				top = null;
			}
		}

		final Iterator reals = exprInfo.realsAtBlock(block).iterator();

		// Iterate over all of the real occurrences in the block.

		while (reals.hasNext()) {
			final Expr real = (Expr) reals.next();

			if (SSAPRE.DEBUG) {
				System.out.println("        -----------");
			}

			// Get defining "h" occurrence for the expression
			final Def def = exprInfo.def(real);
			Assert.isTrue(def != null, real + " is undefined");

			// Get Avail_def[i][x]
			final Def availDef = exprInfo.availDef(def);

			// If Avail_def[i][x] == bottom (tack)
			// or Avail_def[i][x] does not dominate this occurrence of E[i]
			// Avail_def[i][x] = this occurrence of E[i]
			//
			// The statement (availDef != top) is equivalent to saying "availDef
			// does not dominate real". Why is this so? Top essentially keeps
			// track of the last PHI-statement we've seen. Thus, top will only
			// be changed when we encounter a PHI-statement. We only encounter
			// PHI-statements at join blocks, which are obviously not dominated
			// by a block (containing availDef) along one of its paths.
			if ((availDef == null) || (availDef != top)) {
				top = new RealDef(real);
				exprInfo.setAvailDef(def, top);
			}
			// If the available definition is a real occurrence, set its
			// save and reload flags
			else if (availDef instanceof RealDef) {
				exprInfo.setReload(real, true);
				exprInfo.setSave(((RealDef) availDef).expr, true);
			} else {
				Assert.isTrue(availDef instanceof Phi);
				exprInfo.setReload(real, true);
			}
		}

		final Iterator succs = cfg.succs(block).iterator();

		// Iterate over each successor block in the CFG...
		while (succs.hasNext()) {
			final Block succ = (Block) succs.next();

			final Phi succPhi = exprInfo.exprPhiAtBlock(succ);

			// If the PHI-statement is will be available,
			if (succPhi != null) {
				if (succPhi.willBeAvail()) {
					if (succPhi.canInsert(block)) {
						succPhi.setSaveOperand(block, true);
					} else {
						final Def operand = succPhi.operandAt(block);

						Assert.isTrue(operand != null);

						final Def availDef = exprInfo.availDef(operand);

						if (availDef instanceof RealDef) {
							exprInfo.setSave(((RealDef) availDef).expr, true);
						}
					}
				}
			}
		}

		final Iterator children = cfg.domChildren(block).iterator();

		while (children.hasNext()) {
			final Block child = (Block) children.next();
			finalizeVisit(exprInfo, child, top);
		}
	}

	private void codeMotion(final ExprInfo exprInfo, final VarExpr tmp,
			final SSAConstructionInfo consInfo) {
		final List[] targets = new List[cfg.size()];

		Iterator blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Phi phi = exprInfo.exprPhiAtBlock(block);

			if (phi != null) {
				final Iterator preds = cfg.preds(block).iterator();

				while (preds.hasNext()) {
					final Block pred = (Block) preds.next();

					if (!phi.saveOperand(pred)) {
						continue;
					}

					final Expr operand = buildPhiOperand(exprInfo, phi, pred);
					Assert.isTrue(operand != null);

					final VarExpr t = (VarExpr) tmp.clone();
					t.setValueNumber(operand.valueNumber());

					final StoreExpr store = new StoreExpr(t, operand, t.type());
					store.setValueNumber(operand.valueNumber());
					pred.tree().addStmtBeforeJump(new ExprStmt(store));

					if (SSAPRE.DEBUG) {
						System.out.println("Created new store: " + store);
					}

					// Save the target for later since we need to add
					// it to consInfo last.
					final int predIndex = cfg.preOrderIndex(pred);

					if (targets[predIndex] == null) {
						targets[predIndex] = new ArrayList();
					}

					targets[predIndex].add(t);

					if (SSAPRE.DEBUG) {
						System.out.println("insert at end of " + pred + ": "
								+ store);
					}
				}
			}

			final Iterator e = exprInfo.realsAtBlock(block).iterator();

			while (e.hasNext()) {
				final Expr real = (Expr) e.next();

				if (exprInfo.save(real)) {
					if (!real.isDef()) {
						save(exprInfo, tmp, real, consInfo);
					} else {
						saveTarget(exprInfo, tmp, real, consInfo);
					}

				} else if (exprInfo.reload(real)) {
					Assert.isFalse(real.isDef(), "Can't reload a def: " + real
							+ " in " + real.parent());
					reload(exprInfo, tmp, real, consInfo);
				}
			}
		}

		blocks = cfg.nodes().iterator();

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();
			final int blockIndex = cfg.preOrderIndex(block);

			if (targets[blockIndex] != null) {
				final Iterator iter = targets[blockIndex].iterator();

				while (iter.hasNext()) {
					final VarExpr t = (VarExpr) iter.next();
					consInfo.addReal(t);
				}
			}
		}
	}

	private void save(final ExprInfo exprInfo, final VarExpr tmp,
			final Expr real, final SSAConstructionInfo consInfo) {
		if (SSAPRE.DEBUG) {
			System.out.println("SAVE: " + real + " to " + tmp
					+ "--------------------------------");
		}

		if ((real instanceof CheckExpr) && exprInfo.hasStackVariable()) {
			// Check(x) leaves x on the stack. Do nothing.
			return;
		}

		// Replace expression
		// use x + e
		// with
		// use x + (t := e)
		// We must evaluate x before e.

		final Node parent = real.parent();
		final VarExpr t = (VarExpr) tmp.clone();
		t.setValueNumber(real.valueNumber());

		final StoreExpr store = new StoreExpr(t, real, real.type());
		store.setValueNumber(real.valueNumber());
		parent.visit(new ReplaceVisitor(real, store));

		consInfo.addReal(t);

		if (SSAPRE.DEBUG) {
			System.out.println("END SAVE--------------------------------");
		}
	}

	private void reload(final ExprInfo exprInfo, final VarExpr tmp,
			final Expr real, final SSAConstructionInfo consInfo) {
		if (SSAPRE.DEBUG) {
			System.out.println("RELOAD: " + real + " to " + tmp
					+ "--------------------------------");
		}

		Expr t;

		if ((real instanceof CheckExpr) && exprInfo.hasStackVariable()) {
			// Check(x) leaves x on the stack. Replace with just x.

			t = ((CheckExpr) real).expr();
			real.parent().visit(new ReplaceVisitor(real, t));
			real.cleanupOnly();

		} else {
			// Replace
			// use e
			// with
			// use t

			t = (VarExpr) tmp.clone();
			t.setValueNumber(real.valueNumber());
			real.replaceWith(t);

			consInfo.addReal((VarExpr) t);
		}

		if (SSAPRE.DEBUG) {
			System.out.println("reload t " + t + " in " + t.parent());
		}

		if (SSAPRE.DEBUG) {
			System.out.println("END RELOAD--------------------------------");
		}
	}

	private void saveTarget(final ExprInfo exprInfo, final VarExpr tmp,
			final Expr real, final SSAConstructionInfo consInfo) {
		if (SSAPRE.DEBUG) {
			System.out.println("SAVE TARGET: " + real + " to " + tmp
					+ "--------------------------------");
		}

		Assert.isTrue(real instanceof MemRefExpr);

		// Replace
		// a.b := c
		// with:
		// a.b := (t := c);

		final VarExpr t = (VarExpr) tmp.clone();
		t.setValueNumber(real.valueNumber());

		final StoreExpr store = (StoreExpr) real.parent();
		final Expr rhs = store.expr();

		final StoreExpr rhsStore = new StoreExpr(t, rhs, rhs.type());
		rhsStore.setValueNumber(real.valueNumber());
		store.visit(new ReplaceVisitor(rhs, rhsStore));

		consInfo.addReal(t);

		if (SSAPRE.DEBUG) {
			System.out.println("save target " + store);
		}

		if (SSAPRE.DEBUG) {
			System.out.println("END SAVE TARGET------------------------------");
		}
	}

	/**
	 * Returns whether or not an expression is first-order. A first-order
	 * expression has only one operator. For example, a+b is first-order.
	 */
	boolean isFirstOrder(final Expr expr) {
		final FirstOrderChecker f = new FirstOrderChecker();
		expr.visit(f);
		return f.firstOrder;
	}

	/**
	 * FirstOrderChecker is a TreeVistor that traverses an expression tree and
	 * determines whether or not it is first order. A first order expression
	 * Override all visitXXXExpr methods so that they do not visit children. We
	 * only want to check the expr we first visit.
	 */
	private final class FirstOrderChecker extends TreeVisitor {
		boolean firstOrder = false;

		public void visitExpr(final Expr expr) {
		}

		/**
		 * A leaf in the expression tree consists of an expression that only
		 * references local variables, or an expression that is a constant, or
		 * an expressions that stores into a local variable.
		 */
		private boolean isLeaf(final Expr expr) {
			if (expr instanceof StoreExpr) {
				return ((StoreExpr) expr).target() instanceof LocalExpr;
			}

			return (expr instanceof LocalExpr)
					|| (expr instanceof ConstantExpr);
		}

		public void visitCheckExpr(final CheckExpr expr) {
			// UGLY: We special case RC and UC to allow stack variables since
			// they do not change the operand stack at all. However, since
			// they do contain stack variables, we cannot hoist these
			// expressions, but we can one eliminate them so long as they
			// are replaced with stack variables rather than locals.

			if (isLeaf(expr.expr()) || (expr.expr() instanceof StackExpr)) {
				firstOrder = true;
			}
		}

		/**
		 * An arithmetic expression is first-order if both its left and right
		 * operands are leaves.
		 */
		public void visitArithExpr(final ArithExpr expr) {
			if (isLeaf(expr.left()) && isLeaf(expr.right())) {
				firstOrder = true;
			}
		}

		/**
		 * An ArrayLengthExpr is first-order when the array whose length is
		 * being taken is expressed as a leaf.
		 */
		public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
			if (isLeaf(expr.array())) {
				firstOrder = true;
			}
		}

		/**
		 * An ArrayRefExpr is first order when both the array it references and
		 * the index used to reference it are expressed as leaves.
		 */
		public void visitArrayRefExpr(final ArrayRefExpr expr) {
			if (SSAPRE.NO_ACCESS_PATHS) {
				return;
			}

			if (isLeaf(expr.array()) && isLeaf(expr.index())) {
				firstOrder = true;
			}
		}

		public void visitCastExpr(final CastExpr expr) {
			if (isLeaf(expr.expr())) {
				firstOrder = true;
			}
		}

		/**
		 * If a field is volatile (meaning that a field may be changed by other
		 * threads), a reference to it is not first order. I'm not too sure why
		 * this makes any difference.
		 */
		public void visitFieldExpr(final FieldExpr expr) {
			if (SSAPRE.NO_ACCESS_PATHS) {
				return;
			}

			if (isLeaf(expr.object())) {
				try {
					final FieldEditor e = context.editField(expr.field());

					if (!e.isVolatile()) {
						firstOrder = true;
					}

					context.release(e.fieldInfo());

				} catch (final NoSuchFieldException e) {
					// A field wasn't found. Silently assume it's volatile.
					firstOrder = false;
				}
			}
		}

		public void visitInstanceOfExpr(final InstanceOfExpr expr) {
			if (isLeaf(expr.expr())) {
				firstOrder = true;
			}
		}

		public void visitNegExpr(final NegExpr expr) {
			if (isLeaf(expr.expr())) {
				firstOrder = true;
			}
		}

		public void visitShiftExpr(final ShiftExpr expr) {
			if (isLeaf(expr.expr()) && isLeaf(expr.bits())) {
				firstOrder = true;
			}
		}

		/**
		 * Once again, an expression that references a volatile static field is
		 * not first-order.
		 * 
		 * @see #visitFieldExpr
		 */
		public void visitStaticFieldExpr(final StaticFieldExpr expr) {
			if (SSAPRE.NO_ACCESS_PATHS) {
				return;
			}

			try {
				final FieldEditor e = context.editField(expr.field());

				if (!e.isVolatile()) {
					firstOrder = true;
				}

				context.release(e.fieldInfo());

			} catch (final NoSuchFieldException e) {
				// A field wasn't found. Silently assume it's volatile.
				firstOrder = false;
			}
		}
	}

	/**
	 * Wrapper classes that are used to simulate pass-by-reference. That is,
	 * their values are changed inside methods. When used as parameters they
	 * must be declared as being final.
	 */
	class Int {
		int value = 0;
	}

	class Bool {
		boolean value = false;
	}

	int next = 0;

	/**
	 * Def represents a point at which a variable is defined. Each definition
	 * has a version number associated with it.
	 */
	abstract class Def {
		int version = next++;
	}

	/**
	 * RealDef represents a real occurrence of an expression.
	 */
	class RealDef extends Def {
		Expr expr;

		public RealDef(final Expr expr) {
			this.expr = expr;
		}

		public String toString() {
			return "[" + expr + "]_" + version;
		}
	}

	/**
	 * Phi represents a PHI-statement (PHI-function) for merging an expression
	 * along two paths.
	 * <p>
	 * Information about the operands to PHI-statements is maintained in the PHI
	 * class.
	 * <p>
	 * A PHI-statement has the form: h = PHI(h, h)
	 * 
	 * @see #operands
	 */
	class Phi extends Def {
		Block block; // Block in which the PHI-statement occurs

		// Note that arrays are indexed by a block's preorder number.

		Def[] operands; // Operand to the PHI-statement

		boolean[] hasRealUse; // Is the ith operand a real use?

		boolean[] saveOperand;

		boolean downSafe; // downsafe flag (ds)

		boolean canBeAvail; // can_be_available (cba)

		boolean later; // later flag (later)

		boolean live;

		List leaves;

		/**
		 * Constructor.
		 * 
		 * @param exprInfo
		 *            The expression that this PHI-statement is associated with.
		 * @param block
		 *            The block in which this PHI-statement occurs. Note that an
		 *            PHI-statement can only occur in one block.
		 */
		public Phi(final ExprInfo exprInfo, final Block block) {
			this.block = block;

			final int size = cfg.size();

			operands = new Def[size];
			hasRealUse = new boolean[size];
			saveOperand = new boolean[size];

			leaves = null;

			downSafe = true; // Initially, ds = 1
			canBeAvail = true; // Initially, cba = 1
			later = true; // Initially, later = cba
			live = false; // Initially, live = 0
		}

		/**
		 * Returns the block in which this PHI-statement is occurs.
		 */
		public Block block() {
			return block;
		}

		/**
		 * Sets the operands to a real occurrence of the expression. Leaves only
		 * apply to PHI-statements that are associated with a real occurrence of
		 * the expression.
		 */
		public void setLeaves(final List leaves) {
			if (SSAPRE.DEBUG) {
				System.out.println("setting leaves of " + this + " to "
						+ leaves);
			}

			this.leaves = new ArrayList(leaves);
		}

		/**
		 * Returns the operands to the real occurrence represented by this
		 * PHI-statement. It is assumed that this PHI-statement represents a
		 * real occurrence.
		 */
		public List leaves() {
			Assert.isTrue(leaves != null);

			final Iterator iter = leaves.iterator();

			while (iter.hasNext()) {
				final Expr e = (Expr) iter.next();
				Assert.isTrue((e instanceof VarExpr)
						|| (e instanceof ConstantExpr), "not a leaf: " + e);
			}

			return leaves;
		}

		/**
		 * Returns the operands of the PHI-statement. This is just a list of all
		 * of the block's predacessors.
		 */
		public Collection operands() {
			final LinkedList v = new LinkedList();

			final Iterator preds = cfg.preds(block).iterator();

			while (preds.hasNext()) {
				final Block pred = (Block) preds.next();
				v.addFirst(operandAt(pred));
			}

			return v;
		}

		/**
		 * Sets an operand of this PHI-statement. Recall that PHI-operands are
		 * associated with a predacessor block of the block in which the
		 * PHI-statement resides.
		 * 
		 * @param block
		 *            The block associated with the operand.
		 * @param def
		 *            The PHI-definition that is the operand.
		 */
		public void setOperandAt(final Block block, final Def def) {
			final int blockIndex = cfg.preOrderIndex(block);
			operands[blockIndex] = def;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		/**
		 * Returns the PHI-operand of this PHI-statement associated with a given
		 * block. Recall that PHI-operands are associated with a predacessor
		 * block of the block in which the PHI-statement resides.
		 */
		public Def operandAt(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);
			return operands[blockIndex];
		}

		/**
		 * Sets the "has real use" flag.
		 */
		public void setHasRealUse(final Block block, final boolean flag) {
			final int blockIndex = cfg.preOrderIndex(block);

			hasRealUse[blockIndex] = flag;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		public boolean hasRealUse(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);
			return hasRealUse[blockIndex];
		}

		/**
		 * 
		 */
		public void setSaveOperand(final Block block, final boolean flag) {
			final int blockIndex = cfg.preOrderIndex(block);
			saveOperand[blockIndex] = flag;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		public boolean saveOperand(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);
			return saveOperand[blockIndex];
		}

		/**
		 * Determines whether or not a PHI-operand satisfies "insert". For
		 * insert to hold, the following conditions must be met: 1. The
		 * PHI-statement is "will be available" 2. The PHI-operand is tack
		 * (null), or "has real use" is false and the operand is defined by an
		 * PHI-statement that does not satisfy "will be available".
		 * 
		 * @param block
		 *            The block with which a desired operand is associated with.
		 *            Recall that PHI-operands are associated with the block
		 *            that is a predacessor of the block in which they are
		 *            contained.
		 */
		public boolean canInsert(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);

			final Def def = operands[blockIndex];

			if (def == null) {
				return true;
			}

			if (!hasRealUse[blockIndex]) {
				if (def instanceof Phi) {
					final Phi phi = (Phi) def;

					if (!phi.willBeAvail()) {
						return true;
					}
				}
			}

			return false;
		}

		/**
		 * Returns whether or not an PHI-statement satisfies "will be
		 * available". "Will be available" is used to determine the locations in
		 * which to insert evaluations of the expression in the finalize() pass.
		 * <p>
		 * willBeAvail = canBeAvail && !later
		 * 
		 * @see #finalize()
		 */
		public boolean willBeAvail() {
			// WBA => CBA => DS
			return canBeAvail && !later;
		}

		/**
		 * Sets the "can be available" flag.
		 */
		public void setCanBeAvail(final boolean flag) {
			canBeAvail = flag;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		/**
		 * Returns the "can be available" flag.
		 */
		public boolean canBeAvail() {
			return canBeAvail;
		}

		/**
		 * Sets the "later" flag. If the later flag is false, it means that an
		 * evaluation of the expression may not be placed at any point below
		 * this PHI-statement without introducing a useless computation along
		 * some path.
		 */
		public void setLater(final boolean flag) {
			later = flag;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		/**
		 * Returns the "later" flag.
		 * 
		 * @see #setLater
		 */
		public boolean later() {
			return later;
		}

		public void setLive(final boolean flag) {
			live = flag;
		}

		public boolean live() {
			return live;
		}

		/**
		 * Sets the "down-safe" flag. An PHI-statement is "down-safe" if there
		 * is no path from the PHI-statement to the exit block that does not
		 * recalculate the expression. If an PHI-statement is "down-safe" it is
		 * worthwhile to attempt to hoist it up higher in the program.
		 * <p>
		 * An PHI-statement is not "down-safe" when a) There is a path to exit
		 * along which the PHI-statement result is never used. b) There is a
		 * path to exit along which the only used of the result of the
		 * PHI-statement is an operand of an PHI-statement which itself is not
		 * "down-safe".
		 */
		public void setDownSafe(final boolean flag) {
			downSafe = flag;

			if (SSAPRE.DEBUG) {
				System.out.println(this);
			}
		}

		/**
		 * Returns whether or not this PHI-statement is "down-safe".
		 * 
		 * @see #setDownSafe
		 */
		public boolean downSafe() {
			return downSafe;
		}

		/**
		 * Returns a textual representation of this PHI-statement.
		 */
		public String toString() {
			String s = "Phi_" + version + "[";

			if (!downSafe) {
				s += "!";
			}

			s += "DS,";

			if (!canBeAvail) {
				s += "!";
			}

			s += "CBA,";

			if (!later) {
				s += "!";
			}

			s += "later](";

			if (operands != null) {
				final Iterator e = cfg.preds(block).iterator();

				while (e.hasNext()) {
					final Block pred = (Block) e.next();
					final int predIndex = cfg.preOrderIndex(pred);

					s += pred.label() + "=";

					final Def operand = operands[predIndex];

					if (operand == null) {
						s += "undef[";
					} else {
						s += operand.version + "[";
					}

					if (!hasRealUse[predIndex]) {
						s += "!";
					}

					s += "HRU,";

					if (!saveOperand[predIndex]) {
						s += "!";
					}

					s += "save,";

					if (!canInsert(pred)) {
						s += "!";
					}

					s += "insert]";

					if (e.hasNext()) {
						s += ", ";
					}
				}
			}

			s += ")";

			return s;
		}
	}

	/**
	 * ExprInfo represents an expression that we are performing SSA-based PRE
	 * on. An occurrence of an expression can take one of three forms: 1) A real
	 * occurrence of an expression (h = a+b) 2) A (target of a) PHI function (h =
	 * PHI(...)) 3) An operand to a PHI function (PHI(h, ...))
	 * 
	 * The occurrences of an expression are ordered according to a preorder
	 * traversal of the CFG.
	 * 
	 */
	private final class ExprInfo {
		ExprKey key; // A unique key for an Expr instance

		private int numUses; // Number of uses (not defs) of this expr

		private List[] reals; // The real occurrences of this expression

		private boolean[] realsSorted; // Are the reals at a given block
										// sorted?

		private Phi[] phis; // PHI expressions for this occurrences

		Map defs; // Maps an Expr to its defining occurrence

		// "h" in the CFG.
		Map availDefs; // 

		Map saves;

		Map reloads;

		private Expr prototype; // The actual expression being represented

		private boolean isFinal; // Does the expression access a final field?

		private boolean hasSideEffects;

		private boolean hasStackVariable;

		/**
		 * Constructor.
		 * 
		 * @param expr
		 *            The expression (real occurrence) represented by this
		 *            ExprInfo.
		 * @param key
		 *            A unique key by which this expression can be identified.
		 */
		public ExprInfo(final Expr expr, final ExprKey key) {
			this.key = key;

			prototype = (Expr) expr.clone();

			// Clean up the expression's children (remember that expr's children
			// are also cloned, so we aren't changing the tree).
			prototype.visitChildren(new TreeVisitor() {
				public void visitStoreExpr(final StoreExpr expr) {
					expr.target().setDef(null);
					expr.target().setParent(null);
					expr.replaceWith(expr.target(), false);
					expr.cleanupOnly();
					expr.expr().cleanup();
				}

				public void visitVarExpr(final VarExpr expr) {
					expr.setDef(null);
				}

				public void visitConstantExpr(final ConstantExpr expr) {
				}

				// The prototype expression should only
				// contain StoreExpr, VarExpr, or
				// ConstantExpr...
				public void visitExpr(final Expr expr) {
					throw new RuntimeException();
				}
			});

			numUses = 0;

			reals = new ArrayList[cfg.size()];
			realsSorted = new boolean[cfg.size()];

			for (int i = 0; i < reals.length; i++) {
				reals[i] = new ArrayList();
				realsSorted[i] = false;
			}

			phis = new Phi[cfg.size()];

			defs = new HashMap();
			availDefs = new HashMap();
			saves = new HashMap();
			reloads = new HashMap();

			if (prototype instanceof MemRefExpr) {
				// Traverse the tree and determine whether expr accesses a final
				// field.
				final FinalChecker fch = new FinalChecker();
				prototype.visit(fch);
				isFinal = fch.isFinal;

			} else {
				isFinal = true;
			}

			// For PRE, RCs, UCs, stores, and possible reassignment
			// through aliases are not considered side effects.
			sideEffects.reset();
			prototype.visit(sideEffects);

			int flag = sideEffects.sideEffects();
			flag &= ~SideEffectChecker.STORE;
			flag &= ~SideEffectChecker.ALIAS;
			flag &= ~SideEffectChecker.RC;
			flag &= ~SideEffectChecker.UC;
			hasSideEffects = flag != 0;

			// Special case: allow RC(S) and UC(S).
			if ((flag & SideEffectChecker.STACK) != 0) {
				Assert.isTrue(prototype instanceof CheckExpr);
				hasStackVariable = true;
			}
		}

		public boolean hasStackVariable() {
			return hasStackVariable;
		}

		public boolean hasSideEffects() {
			return hasSideEffects;
		}

		public int numUses() {
			return numUses;
		}

		public void cleanup() {
			reals = null;
			phis = null;
			saves = null;
			reloads = null;
			defs = null;
			availDefs = null;
			prototype = null;
		}

		// Reload is used in finalize
		public void setReload(final Expr expr, final boolean flag) {
			if (SSAPRE.DEBUG) {
				System.out.println("        setting reload for " + expr
						+ " to " + flag);
			}

			reloads.put(expr, new Boolean(flag));
		}

		public boolean reload(final Expr expr) {
			final Boolean flag = (Boolean) reloads.get(expr);
			return (flag != null) && flag.booleanValue();
		}

		// Save is used in finalize
		public void setSave(final Expr expr, final boolean flag) {
			if (SSAPRE.DEBUG) {
				System.out.println("        setting save for " + expr + " to "
						+ flag);
			}

			saves.put(expr, new Boolean(flag));
		}

		public boolean save(final Expr expr) {
			final Boolean flag = (Boolean) saves.get(expr);
			return (flag != null) && flag.booleanValue();
		}

		// AvailDef is used in finalize
		public void setAvailDef(final Def def, final Def availDef) {
			if (SSAPRE.DEBUG) {
				System.out.println("        setting avail def for " + def
						+ " to " + availDef);
			}

			availDefs.put(def, availDef);
		}

		public Def availDef(final Def def) {
			final Def availDef = (Def) availDefs.get(def);

			if (SSAPRE.DEBUG) {
				System.out.println("        avail def for " + def + " is "
						+ availDef);
			}

			return availDef;
		}

		/**
		 * Sets the defining occurrence (the "h") of a given real occurrence.
		 */
		public void setDef(final Expr expr, final Def def) {
			if (SSAPRE.DEBUG) {
				System.out.println("        setting def for " + expr + " to "
						+ def);
			}

			if (def != null) {
				defs.put(expr, def);
			} else {
				defs.remove(expr);
			}
		}

		/**
		 * Returns the Def (either a ReafDef or Phi) defining a given occurrence
		 * of the expression modeled by this ExprInfo.
		 */
		public Def def(final Expr expr) {
			final Def def = (Def) defs.get(expr);

			if (SSAPRE.DEBUG) {
				System.out.println("        def for " + expr + " is " + def);
			}

			return def;
		}

		public Expr prototype() {
			return prototype;
		}

		/**
		 * Notifies this ExprInfo of the existence of another real occurrence of
		 * the expression.
		 */
		public void addReal(final Expr real) {
			if (!real.isDef()) {
				numUses++;
			}

			final int blockIndex = cfg.preOrderIndex(real.block());
			reals[blockIndex].add(real);
			realsSorted[blockIndex] = false;
		}

		/**
		 * Notifies this ExprInfo of the existence of an PHI-statement for this
		 * expression. If the PHI is not already present, a new Phi instance is
		 * created for it and placed in the phis array.
		 * 
		 * @param block
		 *            The block at which the PHI occurs.
		 */
		public void addPhi(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);

			if (phis[blockIndex] == null) {
				if (SSAPRE.DEBUG) {
					System.out.println("    add phi for " + prototype + " at "
							+ block);
				}

				phis[blockIndex] = new Phi(this, block);
			}
		}

		/**
		 * Removes a PHI occurrence from the phis array.
		 */
		public void removePhi(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);
			phis[blockIndex] = null;
		}

		/**
		 * Returns the PHI occurrence for this expression at a given Block in
		 * the code.
		 */
		public Phi exprPhiAtBlock(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);
			return phis[blockIndex];
		}

		/**
		 * Returns the real occurrences of this expression at a given Block in
		 * the code.
		 */
		public List realsAtBlock(final Block block) {
			final int blockIndex = cfg.preOrderIndex(block);

			final List r = reals[blockIndex];

			if (!realsSorted[blockIndex]) {
				sortExprs(r);
				realsSorted[blockIndex] = true;
			}

			return r;
		}

		/**
		 * Returns a List of the real occurrences of the expression and any Kill
		 * expressions contained in a given Block in the code.
		 */
		public List occurrencesAtBlock(final Block block) {
			if (isFinal && !hasSideEffects) {
				return realsAtBlock(block);
			}

			final int blockIndex = cfg.preOrderIndex(block);

			final List a = kills[blockIndex];
			final List r = reals[blockIndex];

			if (!killsSorted[blockIndex]) {
				sortKills(a);
				killsSorted[blockIndex] = true;
			}

			if (!realsSorted[blockIndex]) {
				sortExprs(r);
				realsSorted[blockIndex] = true;
			}

			// return a list that is essentially a combination of the
			// real occurrences and the kill expressions
			return new AbstractList() {
				public int size() {
					return r.size() + a.size();
				}

				public boolean contains(final Object obj) {
					if (obj instanceof Kill) {
						return a.contains(obj);
					} else if (obj instanceof Expr) {
						return r.contains(obj);
					}

					return false;
				}

				public Object get(final int index) {
					throw new UnsupportedOperationException();
				}

				public Iterator iterator() {
					return new Iterator() {
						Iterator aiter;

						Iterator riter;

						Kill anext;

						Expr rnext;

						{
							aiter = a.iterator();
							riter = r.iterator();

							if (aiter.hasNext()) {
								anext = (Kill) aiter.next();
							} else {
								anext = null;
							}

							if (riter.hasNext()) {
								rnext = (Expr) riter.next();
							} else {
								rnext = null;
							}
						}

						public boolean hasNext() {
							return (anext != null) || (rnext != null);
						}

						public Object next() {
							boolean real = false;

							if (anext == null) {
								if (rnext == null) {
									throw new NoSuchElementException();
								}

								real = true;

							} else if (rnext == null) {
								real = false;

							} else {
								// Kills go first if keys are equal.
								if (anext.key() <= rnext.key()) {
									real = false;
								} else {
									real = true;
								}
							}

							if (real) {
								final Object t = rnext;

								if (riter.hasNext()) {
									rnext = (Expr) riter.next();
								} else {
									rnext = null;
								}

								return t;

							} else {
								final Object t = anext;

								if (aiter.hasNext()) {
									anext = (Kill) aiter.next();
								} else {
									anext = null;
								}

								return t;
							}
						}

						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}

				public ListIterator listIterator() {
					throw new UnsupportedOperationException();
				}
			};
		} // end occurrencesAtBlock()

		/**
		 * Sort a list of expressions into preorder.
		 * 
		 * Recall that the key of each occurrence node was set to its preorder
		 * number in collectOccurrences.
		 */
		private void sortExprs(final List list) {
			Collections.sort(list, new Comparator() {
				public int compare(final Object a, final Object b) {
					if (a == b) {
						return 0;
					}

					final int ka = ((Expr) a).key();
					final int kb = ((Expr) b).key();

					return ka - kb;
				}
			});
		}

		/**
		 * Sorts a lists of Kills into preorder. That is, the Kills in a given
		 * block are sorted by the pre-order number.
		 */
		private void sortKills(final List list) {
			Collections.sort(list, new Comparator() {
				public int compare(final Object a, final Object b) {
					if (a == b) {
						return 0;
					}

					final int ka = ((Kill) a).key();
					final int kb = ((Kill) b).key();

					return ka - kb;
				}
			});
		}

		/**
		 * Print a textual description of this ExprInfo.
		 */
		protected void print() {
			System.out.println("Print for " + prototype + "------------------");

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
							phi = null;
						}
					}
				}
			});

			System.out.println("End Print ----------------------------");
		}
	} // end class ExprInfo

	/**
	 * Traverses a tree and determines if a final (class or instance) field is
	 * accessed.
	 */
	class FinalChecker extends TreeVisitor {
		public boolean isFinal = true;

		public void visitExpr(final Expr expr) {
			if (isFinal) {
				expr.visitChildren(this);
			}
		}

		public void visitArrayRefExpr(final ArrayRefExpr expr) {
			isFinal = false;
		}

		public void visitFieldExpr(final FieldExpr expr) {
			final MemberRef field = expr.field();

			try {
				final FieldEditor e = context.editField(field);
				if (!e.isFinal()) {
					isFinal = false;
				}
				context.release(e.fieldInfo());

			} catch (final NoSuchFieldException e) {
				// A field wasn't found. Silently assume it's not final.
				isFinal = false;
			}

			if (isFinal) {
				expr.visitChildren(this);
			}
		}

		public void visitStaticFieldExpr(final StaticFieldExpr expr) {
			final MemberRef field = expr.field();

			try {
				final FieldEditor e = context.editField(field);
				if (!e.isFinal()) {
					isFinal = false;
				}
				context.release(e.fieldInfo());

			} catch (final NoSuchFieldException e) {
				// A field wasn't found. Silently assume it's not final.
				isFinal = false;
			}

			if (isFinal) {
				expr.visitChildren(this);
			}
		}
	}

	/**
	 * ExprWorklist is a worklist of expressions (represented by ExprInfo)
	 * containing all of the first-order expressions in the CFG (method). The
	 * worklist is assembled in collectOccurrences() and its expressions are
	 * used throughout SSAPRE.
	 */
	class ExprWorklist {
		Map exprInfos; // A mapping between ExprKey and ExprInfo

		LinkedList exprs; // All the ExprInfos we know about

		public ExprWorklist() {
			exprInfos = new HashMap();
			exprs = new LinkedList();
		}

		public boolean isEmpty() {
			return exprs.isEmpty();
		}

		public ExprInfo removeFirst() {
			final ExprInfo exprInfo = (ExprInfo) exprs.removeFirst();
			exprInfos.remove(exprInfo.key);
			return exprInfo;
		}

		/**
		 * Add a real occurrence of an expression to the worklist. If necessary,
		 * an ExprInfo is created to represent the expression.
		 */
		public void addReal(final Expr real) {
			if (SSAPRE.DEBUG) {
				System.out.println("    add to worklist=" + real);
			}

			final ExprKey key = new ExprKey(real);

			ExprInfo exprInfo = (ExprInfo) exprInfos.get(key);

			if (exprInfo == null) {
				exprInfo = new ExprInfo(real, key);
				exprs.add(exprInfo);
				exprInfos.put(key, exprInfo);

				if (SSAPRE.DEBUG) {
					System.out.println("    add info");
				}
			}

			exprInfo.addReal(real);
		}

		/**
		 * Adds a Kill expression to the worklist at a given block.
		 */
		public void addKill(final Block block, final Kill kill) {
			if (SSAPRE.DEBUG) {
				System.out.println("    add alias to worklist=" + kill.expr
						+ " " + kill);
			}

			final int blockIndex = cfg.preOrderIndex(block);
			kills[blockIndex].add(kill);
			killsSorted[blockIndex] = false;
		}
	}

	/**
	 * Kill represents a point at which code cannot be hoisted across.
	 */
	abstract class Kill {
		int key;

		Expr expr;

		/**
		 * Constructor.
		 * 
		 * @param expr
		 *            The expression that causes this kill.
		 */
		public Kill(final Expr expr, final int key) {
			this.expr = expr;
			this.key = key;
		}

		public Kill(final int key) {
			this(null, key);
		}

		public int key() {
			return key;
		}
	}

	/**
	 * ExceptionKill is a Kill that occurrs because an exception may be
	 * encountered. An ExceptionKill is used when a Block that begins a
	 * protected region or an expression that catches an exception is
	 * encountered.
	 */
	class ExceptionKill extends Kill {
		public ExceptionKill(final Expr expr, final int key) {
			super(expr, key);
		}

		public ExceptionKill(final int key) {
			super(key);
		}
	}

	/**
	 * MemRefKill is a Kill that occurrs because a reference to a memory
	 * location may be made. A MemRefKill is used when a synchronized
	 * (monitorenter and monitorexit) block of code, or an expression that
	 * accesses a memory location (MemRefExpr) and defines a variable, or an
	 * expression that invokes a method is encountered.
	 */
	class MemRefKill extends Kill {
		public MemRefKill(final Expr expr, final int key) {
			super(expr, key);
		}

		public MemRefKill(final int key) {
			super(key);
		}
	}

	/**
	 * Represents an expression and a hash code for that expression.
	 */
	class ExprKey {
		Expr expr;

		int hash;

		public ExprKey(final Expr expr) {
			this.expr = expr;
			this.hash = NodeComparator.hashCode(expr) + expr.type().hashCode();
		}

		public int hashCode() {
			return hash;
		}

		private List listChildren(Expr expr) {
			final List children = new ArrayList();

			if (expr instanceof StoreExpr) {
				expr = ((StoreExpr) expr).target();
			}

			expr.visitChildren(new TreeVisitor() {
				public void visitStoreExpr(final StoreExpr expr) {
					// Ignore the RHS.
					children.add(expr.target());
				}

				public void visitExpr(final Expr expr) {
					children.add(expr);
				}
			});

			return children;
		}

		public boolean equals(final Object obj) {
			if (obj instanceof ExprKey) {
				final ExprKey other = (ExprKey) obj;

				if (!expr.type().equals(other.expr.type())) {
					return false;
				}

				if (!NodeComparator.equals(expr, other.expr)) {
					return false;
				}

				final List children = listChildren(expr);
				final List otherChildren = listChildren(other.expr);

				if (children.size() != otherChildren.size()) {
					return false;
				}

				final Iterator iter1 = children.iterator();
				final Iterator iter2 = otherChildren.iterator();

				while (iter1.hasNext() && iter2.hasNext()) {
					final Expr child1 = (Expr) iter1.next();
					final Expr child2 = (Expr) iter2.next();

					if ((child1 instanceof StackExpr) != (child2 instanceof StackExpr)) {
						return false;
					}

					if ((child1 instanceof VarExpr)
							&& (child2 instanceof VarExpr)) {

						if (phiRelatedFind(child1.def()) != phiRelatedFind(child2
								.def())) {

							return false;
						}

					} else {
						Assert.isTrue((child1 instanceof ConstantExpr)
								|| (child2 instanceof ConstantExpr), "neither "
								+ child1 + " nor " + child2 + " are constants");

						// If one is a constant the other must have the same
						// value as the constant.
						if (child1.valueNumber() != child2.valueNumber()) {
							return false;
						}
					}
				}

				if (iter1.hasNext() || iter2.hasNext()) {
					// Size mismatch.
					return false;
				}

				return true;
			}

			return false;
		}
	} // end class ExprKey

	/**
	 * 
	 */
	Expr phiRelatedFind(Expr a) {
		final ArrayList stack = new ArrayList();

		while (a != null) {
			Object p = phiRelated.get(a);

			if ((p == a) || (p == null)) {
				// Path compression.
				final Iterator iter = stack.iterator();

				while (iter.hasNext()) {
					p = iter.next();

					if (p != a) {
						phiRelated.put(p, a);
					}
				}

				return a;
			}

			stack.add(a);
			a = (Expr) p;
		}

		return null;
	}

	/**
	 * phiRelatedUnion associates a variable (local or stack)
	 */
	void phiRelatedUnion(final Expr a, final Expr b) {
		final Expr p = phiRelatedFind(a);
		final Expr q = phiRelatedFind(b);
		if (p != q) {
			phiRelated.put(p, q);
		}
	}
}
