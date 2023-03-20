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
package EDU.purdue.cs.bloat.cfg;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * FlowGraph constructs and represents a Control Flow Graph (CFG) used for
 * analyzing a method. It consists of the basic blocks of a method.
 * <p>
 * 
 * 
 * @see MethodEditor
 * @see Block
 */
public class FlowGraph extends Graph {
	public static final int PEEL_NO_LOOPS = 0;

	public static final int PEEL_ALL_LOOPS = -1;

	public static int PEEL_LOOPS_LEVEL = 1;

	public static boolean DEBUG = false;

	public static boolean DB_GRAPHS = false;

	public static boolean PRINT_GRAPH = false;

	MethodEditor method; // The method that we create a CFG for.

	Map subroutines; // Mapping between a Subroutine and its

	// entry Block
	List catchBlocks; // The Blocks that begin exception handlers

	Map handlers; // Maps first block of exception handler to

	// its Handler object.

	Block srcBlock; // The first (source) basic Block in this method

	Block snkBlock; // The "last" Block (where throw and return go)

	Block iniBlock; // Block that handles initialization of parameters

	// A trace is a series of basic blocks that have the following properties:
	// 1) Blocks that end with a conditional jump are followed by the block
	// that is executed when the conditon is false.
	// 2) Where possible, blocks ending with a unconditional jump are
	// followed by the block that is the target of that unconditional jump.
	// Property 1) allows conditionals that resolve to false to "fall through"
	// and property 2) allows for the removal of labels. Typically,
	// bytecode will already be in trace form.

	List trace; // All of the basic Blocks except source and sink

	Graph loopTree; // A graph representing the loop nesting in

	// the method.

	// Modification counts for the dominator tree and the loop tree.
	// Recall that superclass Graph maintains the modifications counts
	// on nodes and edges.
	int domEdgeModCount;

	int loopEdgeModCount;

	// The maximum (greatest) loop depth/level
	int maxLoopDepth = 0;

	private void db(final String s) {
		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param method
	 *            The method to create the CFG for.
	 */
	public FlowGraph(final MethodEditor method) {
		this.method = method;

		subroutines = new HashMap();
		catchBlocks = new ArrayList(method.tryCatches().size());
		handlers = new HashMap(method.tryCatches().size() * 2 + 1);
		trace = new LinkedList();

		srcBlock = newBlock();
		iniBlock = newBlock();
		snkBlock = newBlock();

		trace.add(iniBlock);

		// If this method is empty(!) just make some default cfg edges
		if (method.codeLength() == 0) {
			addEdge(srcBlock, iniBlock);
			addEdge(iniBlock, snkBlock);
			addEdge(srcBlock, snkBlock);

			buildSpecialTrees(null, null);

			return;
		}

		final Map labelPos = new HashMap();
		buildBlocks(labelPos);
		removeUnreachable();

		// Make sure any labels in the removed blocks are saved.
		saveLabels();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			System.out.println("---------- After building tree:");
			print(System.out);
			System.out.println("---------- end print after building tree");
		}

	}

	/**
	 * Returns the maximum loop depth (also the maximum loop height) in the
	 * control flow graph.
	 */
	public int maxLoopDepth() {
		return (maxLoopDepth);
	}

	/**
	 * Sets up the control flow graph. Computes the dominators and the dominance
	 * frontier, cleans up the tree, works with the loops, inserts stores to aid
	 * copy and constant propagation as well as code generation.
	 */
	public void initialize() {
		if (method.codeLength() == 0) {
			computeDominators();
			buildLoopTree();
			return;
		}

		// Determine which vertices dominate which vertices, update the blocks
		// in the cfg appropriately
		computeDominators();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After computing dominators (Begin)");
			this.print(System.out);
			db("------ After computing dominators (End)");
		}

		// Make sure that no block is both an entry block and a return target.
		splitPhiBlocks();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After splitting phi blocks (Begin)");
			this.print(System.out);
			db("------ After splitting phi blocks (End)");
		}

		removeUnreachable();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After removing unreachable 1 (Begin)");
			this.print(System.out);
			db("------ After removing unreachable 1 (End)");
		}

		splitIrreducibleLoops();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After splitting irreduciable loops (Begin)");
			this.print(System.out);
			db("------ After splitting irreducible loops (End)");
		}

		removeUnreachable();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After removing unreachable 2 (Begin)");
			this.print(System.out);
			db("------ After removing unreachable 2 (End)");
		}

		splitReducibleLoops();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After splitting reducible loops (Begin)");
			this.print(System.out);
			db("------ After splitting reducible loops (End)");
		}

		removeUnreachable();

		if (FlowGraph.DEBUG || FlowGraph.DB_GRAPHS) {
			db("------ After removing unreachable 3 (Begin)");
			this.print(System.out);
			db("------ After removing unreachable 3 (End)");
		}

		buildLoopTree();

		peelLoops(FlowGraph.PEEL_LOOPS_LEVEL);

		removeCriticalEdges();
		removeUnreachable();

		// Insert stores after conditional branches to aid copy and constant
		// propagation.
		insertConditionalStores();

		// Insert stores at the beginnings of protected regions to aid
		// code generation for PhiCatchStmts.
		insertProtectedRegionStores();

		if (FlowGraph.DEBUG) {
			System.out.println("---------- After splitting loops:");
			print(System.out);
			System.out.println("---------- end print after splitting loops");
		}
	}

	/**
	 * Returns the loop tree for the method modeled by this flow graph. The loop
	 * tree represents the nesting of the loops in a method. The procedure is at
	 * the root of the loop tree. Nested loops are represented by a parent and
	 * child relationship.
	 */
	public Graph loopTree() {
		if (loopEdgeModCount != edgeModCount) {
			buildLoopTree();
		}

		return loopTree;
	}

	/**
	 * Builds the loop tree.
	 * 
	 * @see #loopTree
	 * @see LoopNode
	 */
	private void buildLoopTree() {
		db("  Building loop tree");

		loopEdgeModCount = edgeModCount;

		removeUnreachable();

		setBlockTypes();

		final LoopNode root = new LoopNode(srcBlock);

		// loopTree has one root, the node containing the srcBlock.
		loopTree = new Graph() {
			public Collection roots() {
				final ArrayList r = new ArrayList(1);
				r.add(root);
				return r;
			}
		};

		loopTree.addNode(srcBlock, root);

		Iterator iter = nodes().iterator();

		// Iterate over the blocks in the control flow graph. Add blocks
		// to the loop tree node corresponding to their loop header. If
		// the block itself is a header block, make a new loop tree node
		// for it. An edge in the loop tree is added from the outer loop
		// tree node to the inner loop tree node.
		while (iter.hasNext()) {
			final Block block = (Block) iter.next();
			final Block header = block.header();

			if (header != null) {
				LoopNode headerLoop = (LoopNode) loopTree.getNode(header);

				if (headerLoop == null) {
					headerLoop = new LoopNode(header);
					loopTree.addNode(header, headerLoop);
				}

				headerLoop.elements.add(block);

				if (block.blockType() != Block.NON_HEADER) {
					LoopNode loop = (LoopNode) loopTree.getNode(block);

					if (loop == null) {
						loop = new LoopNode(block);
						loopTree.addNode(block, loop);
					}

					// Edges go from outer loops in.
					loopTree.addEdge(headerLoop, loop);
				}
			}
		}

		// Iterate over the loop tree from the bottom up and determine
		// each node's level. Level 0 occurs at the leaf nodes.
		iter = loopTree.postOrder().iterator();

		while (iter.hasNext()) {
			final LoopNode loop = (LoopNode) iter.next();

			// The level of the node is max(level(succs)) + 1.
			int level = 0;

			final Iterator succs = loopTree.succs(loop).iterator();

			while (succs.hasNext()) {
				final LoopNode inner = (LoopNode) succs.next();

				if (level < inner.level) {
					level = inner.level;
				}
			}

			loop.level = level + 1;
		}

		// Iterate over the loop tree from the top down and determine each
		// node's depth. Depth 0 occurs at the root node.
		iter = loopTree.preOrder().iterator();

		while (iter.hasNext()) {
			final LoopNode loop = (LoopNode) iter.next();

			// The depth of the node is depth(pred) + 1.
			final Iterator preds = loopTree.preds(loop).iterator();

			if (preds.hasNext()) {
				final LoopNode outer = (LoopNode) preds.next();
				loop.depth = outer.depth + 1;

			} else {
				loop.depth = 0;
			}
		}
	}

	/**
	 * Creates the basic blocks for the method that is the cfg.
	 * 
	 * @param labelPos
	 *            A mapping between the Labels in the code that start a basic
	 *            block and their offset in the code (an Integer).
	 */
	private void buildBlocks(final Map labelPos) {
		db("  Building blocks");

		// Get the Labels and Instructions of this method
		ListIterator iter = method.code().listIterator();

		// Go through the code, find each Label that starts a block, create
		// a Block for that Label, and add it to the trace.
		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof Label) {
				final Label label = (Label) obj;

				if (label.startsBlock()) {
					trace.add(newBlock(label));
				}
			}
		}

		Instruction lastInst = null;

		Block currBlock = iniBlock;
		Block firstBlock = null;

		int i = 0;
		iter = method.code().listIterator();

		while (iter.hasNext()) {
			final Object curr = iter.next();

			if (curr instanceof Label) {
				final Label label = (Label) curr;

				if (label.startsBlock()) {
					labelPos.put(label, new Integer(i));

					final Block nextBlock = (Block) getNode(label);

					// If the last instruction we saw was a jsr, establish a
					// path
					// between the current block and the block that contains the
					// subroutine (operand to the jsr).
					if ((lastInst != null) && lastInst.isJsr()) {
						final Block target = (Block) getNode(lastInst.operand());
						final Subroutine sub = (Subroutine) subroutines
								.get(target);
						sub.addPath(currBlock, nextBlock);
					}

					currBlock = nextBlock; // Go on to next block

					if (firstBlock == null) {
						firstBlock = currBlock;
					}
				}

			} else if (curr instanceof Instruction) {
				final Instruction currInst = (Instruction) curr;

				lastInst = currInst;

				// Call setsubEntry to maintain a mapping between the entry
				// block of a Subroutine and the Subroutine itself
				if (currInst.isJsr()) {
					final Label label = (Label) currInst.operand();
					final Block target = (Block) getNode(label);

					if (!subroutines.containsKey(target)) {
						final Subroutine sub = new Subroutine(this);
						setSubEntry(sub, target);
					}
				}
			} else {
				throw new IllegalArgumentException();
			}

			i++; // Go to next instruction
		}

		// Start the tedious process of building the expression trees for
		// the basic blocks
		buildTrees(firstBlock, labelPos);
	}

	/**
	 * Build the trees for the blocks, construct subroutines and add edges in
	 * the flow graph.
	 * 
	 * There is a edge from the source block to the init block, to the entry of
	 * each subroutine and to the catch block of each exception handler.
	 * 
	 * After building trees for all nodes reachable from the source, blocks with
	 * null trees are removed since they are unreachable.
	 * 
	 * Edges are added to the sink block from each node ending in a return, a
	 * throw, or a ret.
	 * 
	 * In addition, an edge is added to the sink block from each node ending in
	 * unconditional branch to an ancestor. These edges are used to allow the
	 * post dominator tree to be contructed in the presence of loops.
	 * 
	 * @param firstBlock
	 *            The first block of code in this method.
	 * @param labelPos
	 *            A mapping between Labels and their instruction number (offset
	 *            into the code).
	 */
	private void buildTrees(final Block firstBlock, final Map labelPos) {
		db("  Building trees for " + firstBlock);

		// Maps a "catch block" (beginning of exception handler that
		// stores the exception) to a "catch body" (the code immediately
		// follows the "catch block" -- the rest of the handler).
		final HashMap catchBodies = new HashMap(
				method.tryCatches().size() * 2 + 1);

		final Iterator tryCatches = method.tryCatches().iterator();

		while (tryCatches.hasNext()) {
			final TryCatch tc = (TryCatch) tryCatches.next();

			// We create two blocks for each handler.
			// catchBlock is the handler target. It contains the code
			// which saves the exception on the operand stack.
			// catchBody is the block following the handler target.
			// It contains the code for the exception handler body.
			// We need to split these two blocks so that the handler target
			// cannot possibly be a loop header.

			// This block will be the target of the exception handler.
			final Block catchBlock = newBlock();

			// This block will hold the instructions in the catch body.
			final Block catchBody = (Block) getNode(tc.handler());

			catchBodies.put(catchBlock, catchBody);

			// Make sure we include the new block in any protected area
			// containing the catch body.
			Integer pos = (Integer) labelPos.get(tc.handler());
			labelPos.put(catchBlock.label(), pos);

			addEdge(catchBlock, catchBody);
			trace.add(trace.indexOf(catchBody), catchBlock);

			Type type = tc.type();

			if (type == null) {
				type = Type.NULL;
			}

			catchBlocks.add(catchBlock);

			// Save the exception to the stack.
			final StackExpr lhs = new StackExpr(0, Type.THROWABLE);
			final CatchExpr rhs = new CatchExpr(type, Type.THROWABLE);
			final StoreExpr store = new StoreExpr(lhs, rhs, Type.THROWABLE);

			// Build the tree for the exception handler target block.
			final Tree tree = new Tree(catchBlock, new OperandStack());
			catchBlock.setTree(tree);
			tree.addStmt(new ExprStmt(store));
			tree.addStmt(new GotoStmt(catchBody));

			// Create the Handler.
			final Integer start = (Integer) labelPos.get(tc.start());
			final Integer end = (Integer) labelPos.get(tc.end());

			final Handler handler = new Handler(catchBlock, type);
			handlers.put(catchBlock, handler);

			final Iterator blocks = nodes().iterator();

			// Examine all of the basic blocks in this CFG. If the block's
			// offset into the code is between the start and end points of
			// the TryCatch, then it is a protected block. So, the block
			// should be added to the Handler's list of protected blocks.
			while (blocks.hasNext()) {
				final Block block = (Block) blocks.next();

				pos = (Integer) labelPos.get(block.label());

				if (pos == null) {
					// This is one of the special blocks such as the source,
					// sink, and init block.
					continue;
				}

				if (start.intValue() <= pos.intValue()) {
					if ((end == null) || (pos.intValue() < end.intValue())) {
						handler.protectedBlocks().add(block);
					}
				}
			}
		}

		addEdge(srcBlock, iniBlock);
		addEdge(srcBlock, snkBlock);
		addEdge(iniBlock, firstBlock);

		buildSpecialTrees(catchBodies, labelPos);

		// Build the trees for the blocks reachable from the firstBlock.
		buildTreeForBlock(firstBlock, iniBlock.tree().stack(), null, labelPos,
				catchBodies);
	}

	/**
	 * Insert stores after conditional branches to aid copy and constant
	 * propagation.
	 * 
	 * If a+b and c+d are non-leaf expressions, we convert:
	 * 
	 * if (a+b == c+d) X else Y
	 * 
	 * to:
	 * 
	 * if ((e = a+b) == (f = c+d)) e = f X else Y
	 * 
	 * We can't do this for reference types since we may loose type information.
	 * Consider:
	 * 
	 * class A {} class B extends A { void foo(); }
	 * 
	 * L := someA(); M := someB();
	 * 
	 * if (L == M) { M.foo(); }
	 * 
	 * -->
	 * 
	 * if (L == M) { M = L; // M now has type A, not B M.foo(); }
	 * 
	 */
	private void insertConditionalStores() {
		db("  Inserting conditional stores");

		final Iterator blocks = new ImmutableIterator(nodes());

		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Stmt last = block.tree().lastStmt();

			if (last instanceof IfCmpStmt) {
				final IfCmpStmt stmt = (IfCmpStmt) last;

				// Where do we insert the conditional? (The target of the true
				// or false branch.)
				Block target = null;

				// Exclude targets which are mentioned more than once.
				// This prevents:
				//
				// if (i == j) goto L
				// else goto L
				// L:
				// i = j;
				// 
				// Note: this shouldn't happen with IfStmts after critical.
				// edges are removed.
				//
				if (stmt.trueTarget() == stmt.falseTarget()) {
					continue;
				}

				// Ignore all comparison operations except EQ and NE
				if (stmt.comparison() == IfStmt.EQ) {
					target = stmt.trueTarget();

				} else if (stmt.comparison() == IfStmt.NE) {
					target = stmt.falseTarget();
				}

				if (target != null) {
					Expr left = stmt.left();
					Expr right = stmt.right();

					// If either the left expression or the right expresion is a
					// reference, then we can't do anything. See above.
					if (!left.type().isReference()
							&& !right.type().isReference()) {

						// If either of the expression is a leaf expression
						// (meaning that it has no children expressions), make a
						// new local variable to store the result of the
						// expression. Replace the expression with a StoreExpr
						// that stores the result of the expression into the
						// local
						// variable.

						if (!(left instanceof LeafExpr)) {
							final LocalVariable v = method
									.newLocal(left.type());
							final LocalExpr tmp = new LocalExpr(v.index(), left
									.type());
							final Expr copy = (Expr) left.clone();
							copy.setDef(null);
							left.replaceWith(new StoreExpr(tmp, copy, left
									.type()));
							left = tmp;
						}

						if (!(right instanceof LeafExpr)) {
							final LocalVariable v = method.newLocal(right
									.type());
							final LocalExpr tmp = new LocalExpr(v.index(),
									right.type());
							final Expr copy = (Expr) right.clone();
							copy.setDef(null);
							right.replaceWith(new StoreExpr(tmp, copy, right
									.type()));
							right = tmp;
						}

						// If either the left expression or the right expression
						// is a LocalExpr (meaning that it used to be a non-leaf
						// expression and was replaced with a LocalExpr above),
						// then prepend an assignment to the LocalExpr to the
						// target block.

						if (left instanceof LocalExpr) {
							final LocalExpr tmp = (LocalExpr) left.clone();
							tmp.setDef(null);
							final Expr copy = (Expr) right.clone();
							copy.setDef(null);
							final Stmt insert = new ExprStmt(new StoreExpr(tmp,
									copy, left.type()));

							target.tree().prependStmt(insert);

						} else if (right instanceof LocalExpr) {
							final LocalExpr tmp = (LocalExpr) right.clone();
							tmp.setDef(null);
							final Expr copy = (Expr) left.clone();
							copy.setDef(null);
							final Stmt insert = new ExprStmt(new StoreExpr(tmp,
									copy, right.type()));

							target.tree().prependStmt(insert);

						} else {
							Assert.isTrue((left instanceof ConstantExpr)
									&& (right instanceof ConstantExpr));
						}
					}
				}

			} else if (last instanceof IfZeroStmt) {
				final IfZeroStmt stmt = (IfZeroStmt) last;
				Block target = null;

				// Exclude targets which are mentioned more than once.
				// This prevents:
				//
				// if (i == j) goto L
				// else goto L
				// L:
				// i = j;
				// 
				// Note: this shouldn't happen with IfStmts after critical.
				// edges are removed.
				//
				if (stmt.trueTarget() == stmt.falseTarget()) {
					continue;
				}

				// Ignore all comparisons except for EQ and NE
				if (stmt.comparison() == IfStmt.EQ) {
					target = stmt.trueTarget();

				} else if (stmt.comparison() == IfStmt.NE) {
					target = stmt.falseTarget();
				}

				if (target != null) {
					Expr left = stmt.expr();

					if (!left.type().isReference()) {
						// If left is not a leaf expression, make a new local
						// variable and replace left with an assignment from
						// left
						// to the local variable.

						if (!(left instanceof LeafExpr)) {
							final LocalVariable v = method
									.newLocal(left.type());
							final LocalExpr tmp = new LocalExpr(v.index(), left
									.type());
							final Expr copy = (Expr) left.clone();
							copy.setDef(null);
							left.replaceWith(new StoreExpr(tmp, copy, left
									.type()));
							left = tmp;
						}

						// Value of the right hand side. 0 if left is an
						// integer,
						// null otherwise (left is a reference type).
						Object value = null;

						final Type type = left.type();

						if (left.type().isIntegral()) {
							value = new Integer(0);

						} else {
							Assert.isTrue(left.type().isReference());
						}

						if (left instanceof LocalExpr) {
							// Prepend the target block with an assignment from
							// the
							// value of the right hand side to the left
							// expression.

							final LocalExpr copy = (LocalExpr) left.clone();
							copy.setDef(null);
							final Stmt insert = new ExprStmt(new StoreExpr(
									copy, new ConstantExpr(value, type), left
											.type()));

							target.tree().prependStmt(insert);

						} else {
							Assert.isTrue(left instanceof ConstantExpr);
						}
					}
				}

			} else if (last instanceof SwitchStmt) {
				final SwitchStmt stmt = (SwitchStmt) last;

				Expr index = stmt.index();

				if (!(index instanceof LeafExpr)) {
					// Replace index with a store into a new local variable

					final LocalVariable v = method.newLocal(index.type());
					final LocalExpr tmp = new LocalExpr(v.index(), index.type());
					final Expr copy = (Expr) index.clone();
					copy.setDef(null);
					index.replaceWith(new StoreExpr(tmp, copy, index.type()));
					index = tmp;
				}

				if (index instanceof LocalExpr) {
					final Block[] targets = stmt.targets();
					final int[] values = stmt.values();

					// Exclude targets which are mentioned more than once.
					// This prevents:
					//
					// switch (i) {
					// case 0:
					// case 1:
					// case 2:
					// i = 0;
					// i = 1;
					// i = 2;
					// use(i);
					// break;
					// case 3:
					// break;
					// }
					//
					final HashSet seen = new HashSet();

					// Targets that are branched to more than once
					final HashSet duplicate = new HashSet();

					for (int i = 0; i < targets.length; i++) {
						if (seen.contains(targets[i])) {
							duplicate.add(targets[i]);
						} else {
							seen.add(targets[i]);
						}
					}

					for (int i = 0; i < targets.length; i++) {
						final Block target = targets[i];

						// Skip targets that can be branched to in multiple
						// places
						if (duplicate.contains(target)) {
							continue;
						}

						// Why do we split the edge?
						splitEdge(block, targets[i]);

						// Make sure the edge was split.
						Assert.isTrue(targets[i] != target);

						// Insert a store to the index on the new empty block.
						final LocalExpr copy = (LocalExpr) index.clone();
						copy.setDef(null);
						final Stmt insert = new ExprStmt(new StoreExpr(copy,
								new ConstantExpr(new Integer(values[i]), index
										.type()), index.type()));

						targets[i].tree().prependStmt(insert);
					}
				}
			}
		}
	}

	/**
	 * Insert stores at the beginnings of protected regions to aid code
	 * generation for PhiCatchStmts.
	 */
	private void insertProtectedRegionStores() {
		db("  Inserting protected region stores");

		final HashSet tryPreds = new HashSet();

		final Iterator blocks = catchBlocks.iterator();

		// Iterate over the blocks in this control flow graph. Build a
		// set of predacessors of all protected blocks that themselves are
		// not in the protected block. These blocks end with a jump into
		// a protected region.
		while (blocks.hasNext()) {
			final Block block = (Block) blocks.next();

			final Handler handler = (Handler) handlers.get(block);

			if (handler != null) {
				final HashSet p = new HashSet();

				final Iterator prots = handler.protectedBlocks().iterator();

				while (prots.hasNext()) {
					final Block prot = (Block) prots.next();
					p.addAll(preds(prot));
				}

				p.removeAll(handler.protectedBlocks());

				tryPreds.addAll(p);
			}
		}

		// Starting with the source block,
		insertProtStores(srcBlock, tryPreds, new ResizeableArrayList());
	}

	/**
	 * Insters copy statements into blocks whose successors lie in a protected
	 * region.
	 * 
	 * @param block
	 *            A block we are considering.
	 * @param tryPreds
	 *            All blocks whose successor blocks lie in protected regions.
	 * @param defs
	 *            Stores the expressions (LocalExprs) that define a given local
	 *            variable (index into array) in block. Basically, it contains
	 *            the LocalExpr that defines each local variable at a given
	 *            block.
	 */
	private void insertProtStores(final Block block, final HashSet tryPreds,
			final ResizeableArrayList defs) {
		final Tree tree = block.tree();

		// Visit all LocalExprs in block. Recall that LocalExpr
		// represents a reference to a local variable. If the LocalExpr
		// defines the variable, then added it to the defs array. defs is
		// indexed by local variable.
		tree.visitChildren(new TreeVisitor() {
			public void visitLocalExpr(final LocalExpr expr) {
				if (expr.isDef()) {
					final int index = expr.index();

					if (expr.type().isWide()) {
						defs.ensureSize(index + 2);
						defs.set(index, expr);
						defs.set(index + 1, null);

					} else {
						defs.ensureSize(index + 1);
						defs.set(index, expr);
					}
				}
			}
		});

		// If block ends in a jump to a block in a protected region, add
		// statements that make a copy of each local variable. This is
		// done to avoid redefining local variables used by the jump
		// statement. I'm not too sure about all of this.

		if (tryPreds.contains(block)) {
			// Examine all of the definitions of all the local variables
			for (int i = 0; i < defs.size(); i++) {
				final LocalExpr expr = (LocalExpr) defs.get(i);

				if (expr != null) {
					// Insert stores before the last stmt to ensure we don't
					// redefine locals used by(?) the branch stmt.
					final Stmt last = tree.lastStmt();

					// Visit the Exprs in the last statement block. Remember
					// that this statement ends in a jump to a protected block.
					// Insert a store of the a copy of all Expr into a stack
					// variable right before the jump. I think this saves all
					// of the expressions in the jump statement to the stack.
					// Why? I don't know.

					last.visitChildren(new TreeVisitor() {
						public void visitExpr(final Expr expr) {
							StackExpr var = tree.newStack(expr.type());
							var.setValueNumber(expr.valueNumber());

							final Node p = expr.parent();
							expr.setParent(null);
							p.visit(new ReplaceVisitor(expr, var));

							var = (StackExpr) var.clone();
							var.setDef(null);
							final StoreExpr store = new StoreExpr(var, expr,
									expr.type());
							store.setValueNumber(expr.valueNumber());

							final Stmt storeStmt = new ExprStmt(store);
							storeStmt.setValueNumber(expr.valueNumber());

							tree.addStmtBeforeJump(storeStmt);
						}

						public void visitStackExpr(final StackExpr expr) {
						}
					});

					// Add assignment statements (StoreExpr) that store a copy
					// of expr (a defining instance of LocalExpr) into itself.

					final LocalExpr copy1 = (LocalExpr) expr.clone();
					final LocalExpr copy2 = (LocalExpr) expr.clone();
					copy1.setDef(null);
					copy2.setDef(null);

					final StoreExpr store = new StoreExpr(copy1, copy2, expr
							.type());

					tree.addStmtBeforeJump(new ExprStmt(store));
				}
			}
		}

		final Iterator children = domChildren(block).iterator();

		// Examine all of the blocks that block dominates. Note that
		// local variables will have the same definitions as in block
		// unless they are overriden in the child.
		while (children.hasNext()) {
			final Block child = (Block) children.next();
			insertProtStores(child, tryPreds, new ResizeableArrayList(defs));
		}
	}

	/**
	 * Removing unreachable Blocks means that there are Labels that are no
	 * longer label valid blocks (e.g. start basic blocks) in the CFG. However,
	 * we still want the Labels to point to something meaningful. So, we hoist
	 * them out of CFG and place them into the init block as LabelStmts.
	 */
	private void saveLabels() {
		// Make sure any labels in the removed blocks are saved.
		boolean save = false;

		final Iterator iter = method.code().listIterator();

		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof Label) {
				final Label label = (Label) obj;

				if (label.startsBlock()) {
					if (getNode(label) == null) {
						save = true;
					} else {
						save = false;
					}
				}

				if (save) {
					label.setStartsBlock(false);
					iniBlock.tree().addStmt(new LabelStmt(label));
				}
			}
		}
	}

	/**
	 * Removes a subroutine from this method.
	 * 
	 * @param sub
	 *            The subroutine to remove.
	 */
	public void removeSub(final Subroutine sub) {
		subroutines.remove(sub.entry());
	}

	/**
	 * Adds an edge between two nodes in this graph.
	 * 
	 * @param src
	 *            Node at which the edge originates.
	 * @param dst
	 *            Node at which the edge terminates.
	 */
	public void addEdge(final GraphNode src, final GraphNode dst) {
		if (FlowGraph.DEBUG) {
			System.out.println("    ADDING EDGE " + src + " -> " + dst);
		}

		super.addEdge(src, dst);
	}

	/**
	 * Removes an edge from the graph and performs the necessary cleanup.
	 * 
	 * @param v
	 *            Node at which edge to be removed originates.
	 * @param w
	 *            Node at which edge to be removed terminates.
	 */
	public void removeEdge(final GraphNode v, final GraphNode w) {
		final Block src = (Block) v;
		final Block dst = (Block) w;

		if (FlowGraph.DEBUG) {
			System.out.println("    REMOVING EDGE " + src + " -> " + dst);
		}

		super.removeEdge(src, dst);

		cleanupEdge(src, dst);
	}

	/**
	 * Visit the tree starting at the destination node.
	 */
	private void cleanupEdge(final Block src, final Block dst) {
		dst.visit(new TreeVisitor() {
			public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
				final Expr operand = stmt.operandAt(src);

				if (operand != null) {
					operand.cleanup();

					// Remove the operand associated with src
					// from a PhiJoinStmt
					stmt.setOperandAt(src, null);
				}
			}

			public void visitStmt(final Stmt stmt) {
			}
		});
	}

	/**
	 * Returns a new <tt>Block</tt> with the next available <tt>Label</tt>.
	 */
	public Block newBlock() {
		return newBlock(method.newLabel());
	}

	/**
	 * Creates a new Block starting with the specified Label. The Block is added
	 * to this FlowGraph using its label as its key.
	 * 
	 * @param label
	 *            The new Block's Label
	 */
	Block newBlock(final Label label) {
		final Block block = new Block(label, this);
		addNode(label, block);

		if (FlowGraph.DEBUG) {
			System.out.println("    new block " + block);
		}

		return block;
	}

	/**
	 * Uses classes DominatorTree and DominaceFrontier to calculate which blocks
	 * dominate which blocks.
	 * 
	 * @see DominatorTree
	 * @see DominanceFrontier
	 */
	private void computeDominators() {
		db("  Computing Dominators");

		domEdgeModCount = edgeModCount;

		removeUnreachable();

		// Forward
		DominatorTree.buildTree(this, false);
		DominanceFrontier.buildFrontier(this, false);

		// Reverse
		DominatorTree.buildTree(this, true);
		DominanceFrontier.buildFrontier(this, true);
	}

	/**
	 * Locate the reducible loops. We get better results if we call
	 * splitIrreducibleLoops first to split reducible loop headers from
	 * irreducible loops. This method is based on the analyze_loops algorithm
	 * in:
	 * 
	 * Paul Havlak, "Nesting of Reducible and Irreducible Loops", TOPLAS, 19(4):
	 * 557-567, July 1997.
	 */
	private void setBlockTypes() {
		db("  Setting block types");

		final List blocks = preOrder();

		// A block's predacessors that do not occur along back edges
		final Set[] nonBackPreds = new Set[blocks.size()];

		// A block's predacessors that DO occur along back edges
		final Set[] backPreds = new Set[blocks.size()];

		ListIterator iter = blocks.listIterator();

		while (iter.hasNext()) {
			final Block w = (Block) iter.next();
			final int wn = preOrderIndex(w);

			final Set nonBack = new HashSet();
			nonBackPreds[wn] = nonBack;

			final Set back = new HashSet();
			backPreds[wn] = back;

			w.setHeader(srcBlock);
			w.setBlockType(Block.NON_HEADER);

			final Iterator preds = preds(w).iterator();

			while (preds.hasNext()) {
				final Block v = (Block) preds.next();

				// If w is an ancestor of v, (v,w) is a back edge.
				if (isAncestorToDescendent(w, v)) {
					back.add(v);
				} else {
					nonBack.add(v);
				}
			}
		}

		srcBlock.setHeader(null);

		final UnionFind uf = new UnionFind(blocks.size());

		iter = blocks.listIterator(blocks.size());

		while (iter.hasPrevious()) {
			final Block w = (Block) iter.previous();
			final int wn = preOrderIndex(w);

			final Set nonBack = nonBackPreds[wn];
			final Set back = backPreds[wn];

			final Set body = new HashSet(); // The body of a loop

			final Iterator preds = back.iterator();

			// For each loop header, follow the back edges to construct the
			// body of the loop
			while (preds.hasNext()) {
				final Block v = (Block) preds.next();

				if (v != w) {
					final int vn = preOrderIndex(v);
					final Block f = (Block) blocks.get(uf.find(vn));
					body.add(f);

				} else {
					// Self loop
					w.setBlockType(Block.REDUCIBLE);
				}
			}

			if (body.size() == 0) {
				continue;
			}

			// Initially assume the block is reducible
			w.setBlockType(Block.REDUCIBLE);

			final LinkedList worklist = new LinkedList(body);

			while (!worklist.isEmpty()) {
				final Block x = (Block) worklist.removeFirst();
				final int xn = preOrderIndex(x);

				final Iterator e = nonBackPreds[xn].iterator();

				while (e.hasNext()) {
					final Block y = (Block) e.next(); // a block in the loop
					final int yn = preOrderIndex(y);
					final Block z = (Block) blocks.get(uf.find(yn)); // loop
																		// header
																		// of y

					if (!isAncestorToDescendent(w, z)) {
						// If a block in the loop is not a descendent of the
						// loop
						// header, then there must be another entry path into
						// the
						// loop. Thus, the loop (and its header) are
						// IRREDUCIBLE.
						w.setBlockType(Block.IRREDUCIBLE);
						nonBack.add(z);

					} else {
						if (!body.contains(z) && (z != w)) {
							// If we haven't seen z yet, add it to the worklist
							body.add(z);
							worklist.add(z);
						}
					}
				}
			}

			final Iterator e = body.iterator();

			// Merge all the blocks in the loop into the UnionFind set
			while (e.hasNext()) {
				final Block x = (Block) e.next();
				final int xn = preOrderIndex(x);
				x.setHeader(w);
				uf.union(xn, wn);
			}
		}

		// Say all loops containing jsrs or catch blocks are irreducible.
		// This prevents some problems with peeling.
		Iterator e = subroutines.values().iterator();

		while (e.hasNext()) {
			final Subroutine sub = (Subroutine) e.next();

			final Iterator paths = sub.paths().iterator();

			while (paths.hasNext()) {
				final Block[] path = (Block[]) paths.next();

				if (path[0].blockType() != Block.NON_HEADER) {
					path[0].setBlockType(Block.IRREDUCIBLE);
				}

				if (path[1].blockType() != Block.NON_HEADER) {
					path[1].setBlockType(Block.IRREDUCIBLE);
				}

				Block h;

				h = path[0].header();

				if (h != null) {
					h.setBlockType(Block.IRREDUCIBLE);
				}

				h = path[1].header();

				if (h != null) {
					h.setBlockType(Block.IRREDUCIBLE);
				}
			}
		}

		e = catchBlocks.iterator();

		while (e.hasNext()) {
			final Block catchBlock = (Block) e.next();

			if (catchBlock.blockType() != Block.NON_HEADER) {
				catchBlock.setBlockType(Block.IRREDUCIBLE);
			}

			final Block h = catchBlock.header();

			if (h != null) {
				h.setBlockType(Block.IRREDUCIBLE);
			}
		}
	}

	/**
	 * Ensure that no reducible back edge shares a destination with a
	 * irreducible back edge by splitting reducible loop headers from
	 * irredicible loops. This is based on the fix_loops algorithm in:
	 * 
	 * Paul Havlak, "Nesting of Reducible and Irreducible Loops", TOPLAS, 19(4):
	 * 557-567, July 1997.
	 */
	private void splitIrreducibleLoops() {
		db("  Splitting irreducible loops");

		final List removeEdges = new LinkedList();

		Iterator iter = nodes().iterator();

		// Iterate over all the blocks in this cfg. If a block could be
		// the header of a reducible loop (i.e. it is the target of a
		// "reducible backedge", a backedge for which its destination
		// dominates its source), the block is to be split. All
		// "irreducible backedges" are placed in a list and will be used
		// to insert an empty block so that the number of reducible loop
		// headers is maximize.
		while (iter.hasNext()) {
			final Block w = (Block) iter.next();

			boolean hasReducibleBackIn = false;
			final Set otherIn = new HashSet();

			final Iterator preds = preds(w).iterator();

			while (preds.hasNext()) {
				final Block v = (Block) preds.next();

				if (w.dominates(v)) {
					// (v,w) is a reducible back edge.
					hasReducibleBackIn = true;

				} else {
					otherIn.add(v);
				}
			}

			if (hasReducibleBackIn && (otherIn.size() > 1)) {
				final Iterator e = otherIn.iterator();

				while (e.hasNext()) {
					final Block v = (Block) e.next();
					removeEdges.add(new Block[] { v, w });
				}
			}
		}

		// Split the irreducible back edges.
		iter = removeEdges.iterator();

		while (iter.hasNext()) {
			final Block[] edge = (Block[]) iter.next();
			splitEdge(edge[0], edge[1]);
		}
	}

	/**
	 * Ensure that no reducible back edge shares a destination with another
	 * reducible back edge by splitting reducible loop headers. This makes loop
	 * nesting easier to detect since each loop has a unique header.
	 */
	private void splitReducibleLoops() {
		db("  Splitting reducible loops");

		final Map reducibleBackIn = new HashMap();

		final Stack stack = new Stack();

		final Iterator iter = nodes().iterator();

		while (iter.hasNext()) {
			final Block w = (Block) iter.next();

			final Set edges = new HashSet(); // reducible back edges

			final Iterator preds = preds(w).iterator();

			while (preds.hasNext()) {
				final Block v = (Block) preds.next();

				if (w.dominates(v)) {
					// (v,w) is a reducible back edge.
					edges.add(v);
				}
			}

			// There are strange cases in which a handler block may be the
			// target of a reducible backedge. Ignore it.
			if ((edges.size() > 1) && !handlers.containsKey(w)) {
				stack.push(w);
				reducibleBackIn.put(w, edges);
			}
		}

		while (!stack.isEmpty()) {
			final Block w = (Block) stack.pop();
			final Set edges = (Set) reducibleBackIn.get(w);

			// Find the back predecessor with the lowest pre-order index.
			Block min = null;

			Iterator preds = edges.iterator();

			while (preds.hasNext()) {
				final Block v = (Block) preds.next();
				final int vn = preOrderIndex(v);

				if ((min == null) || (vn < preOrderIndex(min))) {
					min = v;
				}
			}

			Assert.isTrue(min != null);

			Assert.isFalse(handlers.containsKey(w));
			Assert.isFalse(subroutines.containsKey(w));

			// Split the edge (min, w) from w.

			// Create a new block immediately before the header.
			final Block newBlock = newBlock();

			trace.add(trace.indexOf(w), newBlock);

			final Tree tree = new Tree(newBlock, min.tree().stack());
			newBlock.setTree(tree);

			tree.addInstruction(new Instruction(Opcode.opcx_goto, w.label()));

			// If the header is a protected block, the new block must be
			// also since code can be moved from the header up.
			final JumpStmt newJump = (JumpStmt) tree.lastStmt();

			final Iterator e = handlers.values().iterator();

			while (e.hasNext()) {
				final Handler handler = (Handler) e.next();

				if (handler.protectedBlocks().contains(w)) {
					Assert.isTrue(succs(w).contains(handler.catchBlock()));
					handler.protectedBlocks().add(newBlock);
					addEdge(newBlock, handler.catchBlock());
					newJump.catchTargets().add(handler.catchBlock());
				}
			}

			// Change all preds of the header, except min, to have an edge
			// to the new block instead.
			preds = new ImmutableIterator(preds(w));

			while (preds.hasNext()) {
				final Block v = (Block) preds.next();

				if (v != min) {
					addEdge(v, newBlock);
					removeEdge(v, w);
					v.visit(new ReplaceTarget(w, newBlock));
				}
			}

			// Add an edge from the new block to the header.
			addEdge(newBlock, w);

			// If the new block has more than one back edge, push it on the
			// stack to handle it next.
			edges.remove(min);

			if (edges.size() > 1) {
				stack.push(newBlock);
				reducibleBackIn.put(newBlock, edges);
			}
		}
	}

	/**
	 * Loop peeling is a process by which the first iteration of a loop is
	 * duplicated in the control flow graph. An code that has side effects (such
	 * as throwing an exception) will be tested in the first iteration. This
	 * allows us to make assumptions about the code in the second iteration.
	 * 
	 * To detect loop nesting more easily we require that each loop header have
	 * at most one incoming back edge.
	 * 
	 * For each loop, peel up to the last exit if: 1. There is more than one
	 * exit, or, 2. The last exit has a successor in the loop body (not the
	 * header).
	 */
	private void peelLoops(final int level) {
		if (FlowGraph.DEBUG) {
			System.out.println("Peeling loops");
			System.out.println("  loop tree = " + loopTree);
		}

		// Find the blocks that have expressions that can throw exceptions
		// and on which we can perform PRE.
		final Set hoistable = new HashSet();

		visit(new TreeVisitor() {
			public void visitNode(final Node node) {
				if (!hoistable.contains(node.block())) {
					node.visitChildren(this);
				}
			}

			public void visitCastExpr(final CastExpr expr) {
				if (expr.castType().isReference()) {
					if (expr.expr() instanceof LeafExpr) {
						hoistable.add(expr.block());
					}
				}

				visitNode(expr);
			}

			public void visitArithExpr(final ArithExpr expr) {
				if ((expr.operation() == ArithExpr.DIV)
						|| (expr.operation() == ArithExpr.REM)) {
					if (expr.type().isIntegral()
							&& (expr.left() instanceof LeafExpr)
							&& (expr.right() instanceof LeafExpr)) {
						hoistable.add(expr.block());
					}
				}

				visitNode(expr);
			}

			public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
				if (expr.array() instanceof LeafExpr) {
					hoistable.add(expr.block());
				}

				visitNode(expr);
			}

			public void visitArrayRefExpr(final ArrayRefExpr expr) {
				if ((expr.array() instanceof LeafExpr)
						&& (expr.index() instanceof LeafExpr)) {
					hoistable.add(expr.block());
				}

				visitNode(expr);
			}

			public void visitFieldExpr(final FieldExpr expr) {
				if (expr.object() instanceof LeafExpr) {
					hoistable.add(expr.block());
				}

				visitNode(expr);
			}
		});

		// For each loop, from the innermost loop out, unroll the loop body
		// up to the last block which exits the loop.

		// The (pre-order indices of the headers) loops that should be
		// peeled
		final List peel = new ArrayList(loopTree.size());

		// The header blocks of loops to be peeled
		final List headers = new ArrayList(loopTree.size());

		// The outer loop of the loops to be peeled (i.e. parent in the
		// loop tree)
		final List outer = new ArrayList(loopTree.size());

		// All the loops in a tree in post-order
		final List loops = new ArrayList(loopTree.postOrder());

		for (int i = 0; i < loops.size(); i++) {
			final LoopNode loop = (LoopNode) loops.get(i);

			// Don't peel irreducible loops or the outermost loop.
			if ((loopTree.preds(loop).size() > 0)
					&& (loop.header.blockType() != Block.IRREDUCIBLE)) {

				headers.add(loop.header);
				peel.add(new Integer(i));

				// Find the next outer loop.
				LoopNode outerLoop = null;

				final Iterator e = loopTree.preds(loop).iterator();
				Assert.isTrue(e.hasNext());

				outerLoop = (LoopNode) e.next();
				Assert.isTrue(!e.hasNext());

				final int outerIndex = loops.indexOf(outerLoop);
				Assert.isTrue(outerIndex != -1);

				outer.add(new Integer(outerIndex));
			}
		}

		// The level of each loop to be peeled
		final int[] levels = new int[loops.size()];

		// Replace the integer indicies in loops with the blocks in the
		// loop to be peeled and note the level of each loop
		for (int i = 0; i < loops.size(); i++) {
			final LoopNode loop = (LoopNode) loops.get(i);
			loops.set(i, new ArrayList(loop.elements));
			levels[i] = loop.level;
			maxLoopDepth = (loop.level > maxLoopDepth ? loop.level
					: maxLoopDepth);
		}

		LOOPS:
		// Examine each loop that is a candidate for peeling. Peel it if
		// we can. If we can't peel it, we might be able to invert it.
		for (int i = 0; i < peel.size(); i++) {
			// Index of loop header
			final Integer loopIndex = (Integer) peel.get(i);
			final Integer outerLoopIndex = (Integer) outer.get(i);
			final Block header = (Block) headers.get(i);

			final Collection loop = (Collection) loops
					.get(loopIndex.intValue());
			final Collection outerLoop = (Collection) loops.get(outerLoopIndex
					.intValue());

			// Remove any blocks from the loop that are not in this control
			// flow graph.
			loop.retainAll(nodes());

			if (FlowGraph.DEBUG) {
				System.out.println("  loop = " + loop);
				System.out.println("  outer = " + outerLoop);
			}

			boolean canPeel = false;
			boolean canInvert = false;

			// If we haven't exceeded the peeling level and the loop
			// contains a block containing an expression that can be
			// hoisted, then we should peel it.
			if (level != FlowGraph.PEEL_NO_LOOPS) {
				if ((level == FlowGraph.PEEL_ALL_LOOPS)
						|| (level >= levels[loopIndex.intValue()])) {
					final Iterator e = loop.iterator();

					while (e.hasNext()) {
						final Block block = (Block) e.next();

						if (hoistable.contains(block)) {
							canPeel = true;
							break;
						}
					}
				}
			}

			// If we can't peel it, we may still be able to invert it...
			if (!canPeel) {
				boolean hasExitSucc = false;
				boolean hasLoopSucc = false;

				final Iterator succs = succs(header).iterator();

				while (succs.hasNext()) {
					final Block succ = (Block) succs.next();

					if (!loop.contains(succ)) {
						hasExitSucc = true;
					} else if (succ != header) {
						hasLoopSucc = true;
					}
				}

				// If the loop header has an edge to a block that is not in
				// the loop, then it can be inverted.
				canInvert = hasExitSucc && hasLoopSucc;
			}

			// The blocks in the loop that are to be copied
			final Set copySet = new HashSet();

			if (canPeel) {
				// Find the blocks which have exits outside the loop.
				final Set exits = new HashSet();

				// All blocks in the loop that may throw exceptions have an
				// edge to outside the loop.
				exits.addAll(hoistable);
				exits.retainAll(loop);

				Iterator e = loop.iterator();

				BLOCKS: while (e.hasNext()) {
					final Block block = (Block) e.next();

					final Iterator succs = succs(block).iterator();

					while (succs.hasNext()) {
						final Block succ = (Block) succs.next();

						if (!loop.contains(succ)) {
							// If the successor of one of the blocks in the loop
							// is
							// itself not in the loop, then it is an exit block.
							exits.add(block);
							continue BLOCKS;
						}
					}
				}

				final ArrayList stack = new ArrayList(exits);

				e = exits.iterator();

				// Add all "exit" blocks to the copy of the loop
				while (e.hasNext()) {
					final Block block = (Block) e.next();
					copySet.add(block);
					stack.add(block);
				}

				// Copy all reachable blocks into the copy of the loop. Start
				// with the exit blocks and work upwards.
				while (!stack.isEmpty()) {
					final Block block = (Block) stack.remove(stack.size() - 1);

					final Iterator preds = preds(block).iterator();

					while (preds.hasNext()) {
						final Block pred = (Block) preds.next();

						if (!copySet.contains(pred)) {
							copySet.add(pred);
							stack.add(pred);
						}
					}
				}

			} else if (canInvert) {
				// If all we're doing is inverting, just copy the loop header.
				copySet.add(header);

			} else {
				// If we can't invert or peel the loop, copy all the blocks
				// to the outer loop and go to the next loop.
				if (outerLoop != null) {
					outerLoop.addAll(loop);
				}

				// Consider the next loop to be peeled
				continue LOOPS;
			}

			// Maintain a mapping between a block in the loop and its copy
			final Map copies = new HashMap();

			Iterator e = copySet.iterator();

			// Go throught the blocks in the copy set and create a copy of
			// each of them using copyBlock(). Make sure there are no
			// duplicates.
			while (e.hasNext()) {
				final Block block = (Block) e.next();

				// Jeez, are we dealing with a finally block?
				if (FlowGraph.DEBUG) {
					final Stmt jump = block.tree().lastStmt();

					if (jump instanceof JsrStmt) {
						final JsrStmt jsr = (JsrStmt) jump;
						Assert.isTrue(copySet.contains(jsr.follow()));
						Assert.isTrue(copySet.contains(jsr.sub().entry()));
					}
				}

				if (loop.contains(block)) {
					Block copy = (Block) copies.get(block);

					if (copy == null) {
						copy = copyBlock(block);
						copies.put(block, copy);
					}

					// Add the copy to the list of hositable blocks
					if (hoistable.contains(block)) {
						hoistable.add(copy);
					}
				}
			}

			if (FlowGraph.DEBUG) {
				System.out.println("  copy = " + copies);
			}

			int copyIndex = -1;

			e = preds(header).iterator();

			// Determine the index into the trace to add the copy of the
			// loop. Place the loop after the header's "latest" predacessor
			// in the trace.
			while (e.hasNext()) {
				final Block pred = (Block) e.next();

				if (!header.dominates(pred)) {
					final int index = trace.indexOf(pred);

					if (copyIndex <= index) {
						copyIndex = index + 1;
					}
				}
			}

			if (copyIndex < 0) {
				copyIndex = trace.indexOf(header);
			}

			// Insert the copies into the trace just above the loop.
			final List copyTrace = new ResizeableArrayList(copies.size());

			e = trace.iterator();

			while (e.hasNext()) {
				final Block block = (Block) e.next();
				final Block copy = (Block) copies.get(block);

				if (copy != null) {
					copyTrace.add(copy);
				}
			}

			// Add copy of loop to trace
			trace.addAll(copyIndex, copyTrace);

			// Edges to add to the control flow graph
			final List addEdges = new LinkedList();

			// Edges to remove from the control flow graph
			final List removeEdges = new LinkedList();

			// Fix up the edges for the block copies.

			// Add the edges within the peeled body and from the peeled body
			// to the original body.
			e = copies.entrySet().iterator();

			while (e.hasNext()) {
				final Map.Entry pair = (Map.Entry) e.next();

				final Block block = (Block) pair.getKey();
				final Block copy = (Block) pair.getValue();

				final Iterator h = handlers.values().iterator();

				// The copy of the a protected block is also protected
				while (h.hasNext()) {
					final Handler handler = (Handler) h.next();

					if (handler.protectedBlocks().contains(block)) {
						handler.protectedBlocks().add(copy);
					}
				}

				final Iterator succs = succs(block).iterator();

				// Make a list of edges to add to the control flow graph.
				// Create edges within the copied loop so that it looks like
				// the original loop.
				while (succs.hasNext()) {
					final Block succ = (Block) succs.next();
					final Block succCopy = (Block) copies.get(succ);

					if ((succ != header) && (succCopy != null)) {
						addEdges.add(new Block[] { copy, succCopy });
						copy.visit(new ReplaceTarget(succ, succCopy));
					} else {
						addEdges.add(new Block[] { copy, succ });
					}
				}
			}

			// Add the edges from outside the loop to the peeled body.
			// Remove the edges from outside the loop to the original body.
			e = copies.entrySet().iterator();

			while (e.hasNext()) {
				final Map.Entry pair = (Map.Entry) e.next();

				final Block block = (Block) pair.getKey();
				final Block copy = (Block) pair.getValue();

				final Iterator preds = preds(block).iterator();

				while (preds.hasNext()) {
					final Block pred = (Block) preds.next();

					if (!loop.contains(pred)) {
						addEdges.add(new Block[] { pred, copy });
						removeEdges.add(new Block[] { pred, block });
						pred.visit(new ReplaceTarget(block, copy));
					}
				}
			}

			e = addEdges.iterator();

			// Add edges to the control flow graph
			while (e.hasNext()) {
				final Block[] edge = (Block[]) e.next();
				addEdge(edge[0], edge[1]);
			}

			e = removeEdges.iterator();

			// Remove edges into the original (non-copied) loop
			while (e.hasNext()) {
				final Block[] edge = (Block[]) e.next();
				final Block v = edge[0];
				final Block w = edge[1];

				if (hasNode(v) && hasNode(w) && hasEdge(v, w)) {
					removeEdge(v, w);
				}
			}

			// Copy all the blocks to the outer loop.
			if (outerLoop != null) {
				outerLoop.addAll(copies.values());
				outerLoop.addAll(loop);
			}
		}

		if (FlowGraph.DEBUG) {
			System.out.println("Begin after peeling:");
			System.out.println(this);
			System.out.println("End after peeling");
		}
	}

	/**
	 * Creates a copy of a block including its expression tree.
	 */
	private Block copyBlock(final Block block) {
		final Block copy = newBlock();

		// Copy the stack from the end of the old block.
		// But don't change it when instructions are added.

		final Tree tree = new Tree(copy, block.tree().stack());
		copy.setTree(tree);

		// Fill the tree.
		final Iterator stmts = block.tree().stmts().iterator();

		while (stmts.hasNext()) {
			final Stmt stmt = (Stmt) stmts.next();

			if (stmt instanceof LabelStmt) {
				continue;
			}

			tree.addStmt((Stmt) stmt.clone());
		}

		return copy;
	}

	/**
	 * Returns the <tt>Subroutine</tt> whose entry block is labeled by a given
	 * <tt>Label</tt>.
	 */
	public Subroutine labelSub(final Label label) {
		return (Subroutine) subroutines.get(getNode(label));
	}

	/**
	 * Set the entry in the mapping between subroutine entry <tt>Block</tt>s
	 * and the <tt>Subroutine</tt>s that they begin. It also sets the
	 * <tt>Subroutine</tt>'s entry block.
	 * 
	 * @param sub
	 *            The subroutine whose entry block is being set.
	 * @param entry
	 *            The subroutine's entry Block.
	 * 
	 * @see Subroutine#setEntry
	 */
	void setSubEntry(final Subroutine sub, final Block entry) {
		if (sub.entry() != null) {
			subroutines.remove(sub.entry());
		}

		sub.setEntry(entry);
		subroutines.put(entry, sub);
	}

	/**
	 * Returns all of the <tt>Subroutine</tt>s in the method modeled by this
	 * <tt>FlowGraph</tt>.
	 */
	public Collection subroutines() {
		return subroutines.values();
	}

	int file = 0;

	public void print(final PrintStream out) {
		print(new PrintWriter(out, true));
	}

	/**
	 * Prints the graph.
	 * 
	 * @param out
	 *            The writer to which to print.
	 */
	public void print(final PrintWriter out) {
		final String dateString = java.text.DateFormat.getDateInstance()
				.format(new Date());
		out.println("Print " + ++file + " at " + dateString + " "
				+ method.type() + " " + method.name() + ":");

		visit(new PrintVisitor(out));

		if (FlowGraph.PRINT_GRAPH) {
			printGraph();
		}
	}

	int next = 1;

	public void printGraph() {
		try {
			final PrintStream out = new PrintStream(new FileOutputStream(method
					.name()
					+ "." + next++ + ".dot"));
			printGraph(out);

		} catch (final IOException e) {
		}

	}

	public void print() {
		try {
			final PrintStream out = new PrintStream(new FileOutputStream(method
					.name()
					+ "." + next++ + ".cfg"));
			print(out);

		} catch (final IOException e) {
		}

	}

	/**
	 * Creates a graphical description of the CFG in the dot language. The name
	 * of the generated file is the name of the method modeled by this CFG
	 * followed by a number and the ".dot" postfix. For more information about
	 * dot and tools that use it see:
	 * <p align=center>
	 * http://www.research.att.com/sw/tools/graphviz/
	 */
	public void printGraph(final PrintStream out) {
		printGraph(new PrintWriter(out, true));
	}

	public void printGraph(final PrintWriter out) {
		printGraph(out, "cfg");
	}

	public void printGraph(final PrintWriter out, final String name) {
		out.println("digraph " + name + " {");
		out.println("    fontsize=8;");
		out.println("    ordering=out;");
		out.println("    center=1;");

		visit(new PrintVisitor(out) {
			public void println() {
				super.print("\\n");
			}

			public void println(final Object obj) {
				super.print(obj);
				super.print("\\n");
			}

			public void visitBlock(final Block block) {
				super
						.print("    "
								+ block.label()
								+ " [shape=box,fontname=\"Courier\",fontsize=6,label=\"");
				block.visitChildren(this);
				super.print("\"];\n");

				final Iterator succs = succs(block).iterator();

				while (succs.hasNext()) {
					final Block succ = (Block) succs.next();

					super.print("    " + block.label() + " -> " + succ.label());

					if (handlers.containsKey(succ)) {
						super.print(" [style=dotted];\n");

					} else {
						super.print(" [style=solid];\n");
					}
				}
			}
		});

		out.println("    page=\"8.5,11\";");
		out.println("}");
		out.close();
	}

	/**
	 * Visit each node (block) in this CFG in pre-order.
	 */
	public void visitChildren(final TreeVisitor visitor) {
		final List list = preOrder();

		if (!visitor.reverse()) {
			final ListIterator iter = list.listIterator();

			while (iter.hasNext()) {
				final Block block = (Block) iter.next();
				block.visit(visitor);
			}

		} else {
			final ListIterator iter = list.listIterator(list.size());

			while (iter.hasPrevious()) {
				final Block block = (Block) iter.previous();
				block.visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitFlowGraph(this);
	}

	/**
	 * Returns the method editor for the method modeled by this graph.
	 */
	public MethodEditor method() {
		return method;
	}

	/**
	 * Removes the critical edges (edges from a block with more than one
	 * successor to a block with more than one predecessor) from the graph.
	 * Critical edges can screw up code motion.
	 * <p>
	 * For code generation, the block must be inserted after the predecessor if
	 * the successor is the default target. Throw successors and predecessors
	 * are copied from the successor block.
	 */
	private void removeCriticalEdges() {
		// The critical edges
		final List edges = new LinkedList();

		final Iterator blocks = nodes().iterator();

		// Examine each block in this CFG. Blocks in subroutines,
		// exception handlers, blocks with one or fewer predacessors, and
		// the sink block are ignored. For all other blocks, dst, their
		// predacessors are examined. If the predacessor, src, has more
		// than one sucessor, then the edge from src to dst is a critical
		// edge. A List of critical egdes is maintained.
		while (blocks.hasNext()) {
			final Block dst = (Block) blocks.next();

			// Skip edges to subroutine entries.
			if (subroutines.containsKey(dst)) {
				continue;
			}

			// Skip edges from protected blocks to handlers.
			if (handlers.containsKey(dst)) {
				continue;
			}

			if (preds(dst).size() <= 1) {
				continue;
			}

			if (dst == snkBlock) {
				continue;
			}

			final Iterator preds = preds(dst).iterator();

			while (preds.hasNext()) {
				final Block src = (Block) preds.next();

				if (succs(src).size() <= 1) {
					continue;
				}

				// The edge src->dst is a critical edge. Plop a new
				// block on the edge.

				edges.add(new Block[] { src, dst });
			}
		}

		final Iterator e = edges.iterator();

		// Remove the critical edges from this CFG. Call splitEdge to add
		// a block between the source and destination blocks of the
		// critical edges so that it is no longer critical.
		while (e.hasNext()) {
			final Block[] edge = (Block[]) e.next();
			final Block v = edge[0];
			final Block w = edge[1];

			if (hasEdge(v, w)) {
				if (FlowGraph.DEBUG) {
					System.out.println("removing critical edge from " + v
							+ " to " + w);
				}

				splitEdge(v, w);

				Assert.isFalse(hasEdge(v, w));
			}
		}
	}

	/**
	 * Splits an edge by inserting an new block between a source and a
	 * destination block. The new block consists of a goto to the destination
	 * block. However, later optimizations may move code from the destination
	 * block into the new block. Thus, if the destination block is a proteced
	 * block, the new block must also be a protected block.
	 */
	private void splitEdge(final Block src, final Block dst) {
		// This shouldn't happen since
		// (1) edges from the source are either source->init, or
		// source->catch. Edges with catch blocks are already split.
		// (2) edges to the sink are always unconditional jumps.
		//
		Assert.isFalse((src == srcBlock) || (dst == snkBlock),
				"Can't split an edge from the source or to the sink");

		// Don't split exception edges
		if (handlers.containsKey(dst)) {
			if (FlowGraph.DEBUG) {
				System.out.println("not removing exception edge " + src
						+ " -> " + dst);
			}

			return;
		}

		final Block newBlock = newBlock();

		// Insert in the trace before the dst.
		trace.add(trace.indexOf(dst), newBlock);

		final Tree tree = new Tree(newBlock, src.tree().stack());
		newBlock.setTree(tree);

		tree.addInstruction(new Instruction(Opcode.opcx_goto, dst.label()));

		if (FlowGraph.DEBUG) {
			System.out.println("add edge " + src + " -> " + newBlock);
			System.out.println("add edge " + newBlock + " -> " + dst);
			System.out.println("remove edge " + src + " -> " + dst);
		}

		src.visit(new ReplaceTarget(dst, newBlock));

		addEdge(src, newBlock);
		addEdge(newBlock, dst);
		removeEdge(src, dst);

		Assert.isTrue(hasEdge(src, newBlock));
		Assert.isTrue(hasEdge(newBlock, dst));
		Assert.isFalse(hasEdge(src, dst));

		// If the dst is a protected block, the new block must be
		// also since code can be moved from the dst up.
		final JumpStmt newJump = (JumpStmt) newBlock.tree().lastStmt();

		final Iterator e = handlers.values().iterator();

		while (e.hasNext()) {
			final Handler handler = (Handler) e.next();

			if (handler.protectedBlocks().contains(dst)) {
				Assert.isTrue(succs(dst).contains(handler.catchBlock()));
				handler.protectedBlocks().add(newBlock);
				addEdge(newBlock, handler.catchBlock());
				newJump.catchTargets().add(handler.catchBlock());
			}
		}
	}

	/**
	 * Finds any blocks in the CFG that are both the entry block of a subroutine
	 * and the return target of (another) subroutine.
	 * <p>
	 * The Subroutine's in the cfg are examined. If a block is encountered that
	 * is both a subroutine entry block and the target of a subroutine return,
	 * then we have to make two new blocks: a new target block and a new entry
	 * block. The edges have to be adjusted accordingly.
	 */
	private void splitPhiBlocks() {
		// Make sure a block is not more than one of: a catch block,
		// a sub entry block, a sub return target.
		// Otherwise, more than one SSA phi could be placed at the block.
		//
		// Since catch blocks and return targets are mutually exclusive
		// and since catch blocks and sub entries are mutually exclusive,
		// we need only check if the block is both an entry block and a
		// return target. Actually, I don't think this can possibly
		// happen, but do it just to be sure.
		//
		// Note that a phi can also be placed at the block if it has
		// more than one predecessor, but this condition and the others
		// are mutually exclusive since catch blocks and sub entries have
		// only the single source predecessor and return targets have
		// only the caller block as its predecessor.
		//
		final Iterator entries = subroutines.values().iterator();

		ENTRIES: while (entries.hasNext()) {
			final Subroutine entrySub = (Subroutine) entries.next();

			// An entry block of a subroutine
			final Block block = entrySub.entry();

			Subroutine returnSub = null;

			// A block that calls a subroutine that is followed by a block
			// (the return target of the subroutine) that also starts a
			// subroutine.
			Block returnSubCaller = null;

			final Iterator returns = subroutines.values().iterator();

			RETURNS: while (returns.hasNext()) {
				returnSub = (Subroutine) returns.next();

				if (returnSub == entrySub) {
					continue;
				}

				final Iterator paths = returnSub.paths().iterator();

				while (paths.hasNext()) {
					final Block[] path = (Block[]) paths.next();

					// If the block to which returnSub returns is also the entry
					// block of entrySub, then note the caller of returnSub as
					// the
					// returnSubCaller.
					if (block == path[1]) {
						returnSubCaller = path[0];
						break RETURNS;
					}
				}
			}

			if (returnSubCaller == null) {
				continue ENTRIES;
			}

			if (FlowGraph.DEBUG) {
				System.out.println("" + block
						+ " is both an entry and a return target");
			}

			// Create new blocks to be the new sub entry block and the new
			// return target.
			//
			// Use the returning subroutine's exit block to get the state
			// of the operand stack.
			//
			final int traceIndex = trace.indexOf(block);

			Tree tree;

			final Block newEntry = newBlock();

			// Insert in the trace before the block.
			trace.add(traceIndex, newEntry);

			tree = new Tree(newEntry, returnSub.exit().tree().stack());
			newEntry.setTree(tree);

			tree
					.addInstruction(new Instruction(Opcode.opcx_goto, block
							.label()));

			addEdge(newEntry, block);

			final Iterator paths = entrySub.paths().iterator();

			while (paths.hasNext()) {
				final Block[] path = (Block[]) paths.next();
				removeEdge(path[0], block);
				addEdge(path[0], newEntry);
				path[0].visit(new ReplaceTarget(block, newEntry));
			}

			setSubEntry(entrySub, newEntry);

			final Block newTarget = newBlock();

			// Insert in the trace immediately after the jsr block.
			trace.add(traceIndex, newTarget);

			tree = new Tree(newTarget, returnSub.exit().tree().stack());
			newTarget.setTree(tree);

			tree
					.addInstruction(new Instruction(Opcode.opcx_goto, block
							.label()));

			returnSub.exit().visit(new ReplaceTarget(block, newTarget));
			((JsrStmt) returnSubCaller.tree().lastStmt()).setFollow(newTarget);

			addEdge(newTarget, block);
			addEdge(returnSub.exit(), newTarget);
			removeEdge(returnSub.exit(), block);

			final JumpStmt entryJump = (JumpStmt) newEntry.tree().lastStmt();
			final JumpStmt targetJump = (JumpStmt) newTarget.tree().lastStmt();

			final Iterator e = handlers.values().iterator();

			// If block itself is a protected block (man, this block has
			// problems), add egdes from the newEntry and newTarget blocks
			// to the handlers for block.
			while (e.hasNext()) {
				final Handler handler = (Handler) e.next();

				if (handler.protectedBlocks().contains(block)) {
					Assert.isTrue(succs(block).contains(handler.catchBlock()));

					handler.protectedBlocks().add(newEntry);
					addEdge(newEntry, handler.catchBlock());
					entryJump.catchTargets().add(handler.catchBlock());

					handler.protectedBlocks().add(newTarget);
					addEdge(newTarget, handler.catchBlock());
					targetJump.catchTargets().add(handler.catchBlock());
				}
			}
		}
	}

	/**
	 * Builds the expressions trees for the "special" blocks (source, sink, and
	 * init blocks). Empty expressions trees are built for the source and sink
	 * blocks. The init block's expression tree contains code that initializes
	 * the method's parameters (represented as local variables).
	 * <p>
	 */
	private void buildSpecialTrees(final Map catchBodies, final Map labelPos) {
		Tree tree;

		tree = new Tree(srcBlock, new OperandStack());
		srcBlock.setTree(tree);

		tree = new Tree(snkBlock, new OperandStack());
		snkBlock.setTree(tree);

		tree = new Tree(iniBlock, new OperandStack());
		iniBlock.setTree(tree);

		if (method.codeLength() > 0) {
			tree.initLocals(methodParams(method));
			tree.addInstruction(new Instruction(Opcode.opcx_goto, method
					.firstBlock()));

			// (pr)
			if (catchBodies != null) {
				addHandlerEdges(iniBlock, catchBodies, labelPos, null,
						new HashSet());
			}
		}
	}

	/**
	 * If a block may throws an exception (i.e. it is in a protected region),
	 * there must be an edge in the control flow graph from that block to the
	 * block that begins the exception handler. This method adds that edge.
	 * <p>
	 * We iterate over all of the Handler objects created for this FlowGraph. If
	 * the block of interest lies in the protected region of the Handler, make
	 * note of this fact and add an edge between the block and the first block
	 * of the exception handler. Generate the expression tree for the exception
	 * handler, if necessary.
	 * <p>
	 * Recursively call addHandlerEdges for the exception handler to accomodate
	 * exception handlers within exception handlers.
	 * 
	 * @param block
	 *            The "block of interest" (i.e. may throw an exception)
	 * @param catchBodies
	 *            Maps "catch blocks" (first block of exception handler) to
	 *            "catch bodies" (block that begins the actual work of the
	 *            exception handler).
	 * @param labelPos
	 *            Maps Labels to their offset in the code (needed for
	 *            buildTreeForBlock)
	 * @param sub
	 *            The current Subroutine we're in (needed for
	 *            buildTreeForBlock).
	 */
	private void addHandlerEdges(final Block block, final Map catchBodies,
			final Map labelPos, final Subroutine sub, final Set visited) {
		// (pr)
		if (visited.contains(block)) {
			return;
		}
		visited.add(block);

		final Tree tree = block.tree();

		Assert.isTrue(tree != null);

		final Iterator hiter = handlers.values().iterator();

		// Iterate over every Handler object created for this FlowGraph
		while (hiter.hasNext()) {
			final Handler handler = (Handler) hiter.next();

			boolean prot = false;

			// Determine whether or not the block of interest lies within
			// the Handler's protected region
			if (handler.protectedBlocks().contains(block)) {
				prot = true;

			} else {
				final Iterator succs = succs(block).iterator();

				while (succs.hasNext()) {
					final Block succ = (Block) succs.next();

					if (handler.protectedBlocks().contains(succ)) {
						prot = true;
						break;
					}
				}
			}

			// If the block of interest lies in a protected region, add an
			// edge in this CFG from the block to the Handler's "catch block"
			// (i.e. first block in Handler). Also examine the JumpStmt that
			// ends the block of interest and add the catch block to its list
			// of catch targets.
			//
			// Note that we do not want the init block to be protected.
			// This may happen if the first block in the CFG is protected.
			//
			// Next, obtain the "catch body" block (contains the real code)
			// of the method. If no expression tree has been constructed for
			// it, create a new OperandStack containing only the exception
			// object and build a new tree for it.
			//
			// Finally, recursively add the handler edges for the first block
			// of the exception handler.
			if (prot) { // && block != iniBlock) {
				final Block catchBlock = handler.catchBlock();

				final JumpStmt jump = (JumpStmt) tree.lastStmt();

				jump.catchTargets().add(catchBlock);
				addEdge(block, catchBlock);

				// Build the tree for the exception handler body.
				// We must have already added the edge from the catch block
				// to the catch body.

				final Block catchBody = (Block) catchBodies.get(catchBlock);
				Assert.isTrue(catchBody != null);

				if (catchBody.tree() == null) {
					final OperandStack s = new OperandStack();
					s.push(new StackExpr(0, Type.THROWABLE));

					buildTreeForBlock(catchBody, s, sub, labelPos, catchBodies);
				}
				// (pr)
				// if(!handler.catchBlock.equals(block)) {
				addHandlerEdges(catchBlock, catchBodies, labelPos, sub, visited);
				// }
			}
		}
	}

	/**
	 * Dave sez: Builds the expression tree for a basic block and all blocks
	 * reachable from that block. Basically, the block's code (Instructions and
	 * Labels) are iterated over. The instructions are added to the tree with
	 * calls to Tree#addInstruction. If an instruction invovles a change of
	 * control flow (e.g. jsr, jump, switch), add an edge in the control flow
	 * graph between the appropriate blocks. After all that is done, call
	 * addHandlerEdges to add edges between blocks that may throw exceptions and
	 * the blocks that handle those exceptions
	 * 
	 * Nate sez: Visit a block other than source or catch. Since blocks are
	 * visited depth-first, one predecessor was already visited, get the operand
	 * stack state at the end of the predecessor block and use it as the initial
	 * operand stack state for this block. We assume the class file passed
	 * verification so which predecessor used shouldn't matter.
	 * 
	 * @param block
	 *            The block for which to generate an expression tree.
	 * @param stack
	 *            The operand stack before the block is executed.
	 * @param sub
	 *            The current Subroutine.
	 * @param labelPos
	 *            A mapping between Labels and their offset into the code
	 * @param catchBodies
	 *            A mapping between "catch blocks" and "catch bodies"
	 */
	private void buildTreeForBlock(final Block block, final OperandStack stack,
			final Subroutine sub, final Map labelPos, final Map catchBodies) {
		if (block.tree() != null) {
			return;
		}

		final Tree tree = new Tree(block, stack);
		block.setTree(tree);

		final Integer start = (Integer) labelPos.get(block.label());
		Integer targetStart;

		final ListIterator iter = method.code().listIterator(
				start.intValue() + 1);

		CODE:
		// Iterate over the code in the method...
		while (iter.hasNext()) {
			final Object ce = iter.next();

			if (ce instanceof Instruction) {
				final Instruction inst = (Instruction) ce;

				Block target; // The target of a jump
				Block next = null; // The Block following a jump

				// For jump instructions, look for the next Block
				if (inst.isJsr() || inst.isConditionalJump()) {
					int save = 0;

					while (iter.hasNext()) {
						final Object obj = iter.next();
						save++;

						if (obj instanceof Label) {
							if (((Label) obj).startsBlock()) {
								next = (Block) getNode(obj);

								while (save-- > 0) {
									iter.previous();
								}

								break;
							}

						} else {
							throw new RuntimeException(inst
									+ " not followed by a label: " + obj + " ("
									+ obj.getClass() + ")");
						}
					}
				}

				if (inst.opcodeClass() == Opcode.opcx_astore) {
					// We need the current subroutine in case this is a
					// returnAdress store.
					tree.addInstruction(inst, sub);

				} else if (inst.isRet()) {
					sub.setExit(block);
					tree.addInstruction(inst, sub);

					final Iterator paths = sub.paths().iterator();

					// Add edges from the exit Block of the Subroutine to the
					// Block that begins with the Subroutine's return address
					while (paths.hasNext()) {
						final Block[] path = (Block[]) paths.next();
						addEdge(block, path[1]);
					}

					break CODE;

				} else if (inst.isThrow() || inst.isReturn()) {
					tree.addInstruction(inst);
					addEdge(block, snkBlock);
					break CODE;

				} else if (inst.isJsr()) {
					Assert.isTrue(next != null, inst
							+ " not followed by a block");

					tree.addInstruction(inst, next);

					final Label label = (Label) inst.operand();

					target = (Block) getNode(label);
					Assert.isTrue(target != null, inst + " target not found");

					final Subroutine nextSub = labelSub(label);
					setSubEntry(nextSub, target);

					buildTreeForBlock(target, tree.stack(), nextSub, labelPos,
							catchBodies);
					addEdge(block, target);

					if (nextSub.exit() != null) {
						buildTreeForBlock(next, nextSub.exit().tree().stack(),
								sub, labelPos, catchBodies);
						addEdge(nextSub.exit(), next);
					}

					break CODE;

				} else if (inst.isGoto()) {
					tree.addInstruction(inst);

					final Label label = (Label) inst.operand();

					target = (Block) getNode(label);
					Assert.isTrue(target != null, inst + " target not found");

					addEdge(block, target);

					buildTreeForBlock(target, tree.stack(), sub, labelPos,
							catchBodies);

					break CODE;

				} else if (inst.isConditionalJump()) {
					Assert.isTrue(next != null, inst
							+ " not followed by a block");

					tree.addInstruction(inst, next);

					final Label label = (Label) inst.operand();

					target = (Block) getNode(label);
					Assert.isTrue(target != null, inst + " target not found");

					addEdge(block, target);
					buildTreeForBlock(target, tree.stack(), sub, labelPos,
							catchBodies);

					addEdge(block, next);
					buildTreeForBlock(next, tree.stack(), sub, labelPos,
							catchBodies);

					break CODE;

				} else if (inst.isSwitch()) {
					tree.addInstruction(inst);

					final Switch sw = (Switch) inst.operand();

					target = (Block) getNode(sw.defaultTarget());

					addEdge(block, target);

					buildTreeForBlock(target, tree.stack(), sub, labelPos,
							catchBodies);

					for (int j = 0; j < sw.targets().length; j++) {
						target = (Block) getNode(sw.targets()[j]);

						addEdge(block, target);

						targetStart = (Integer) labelPos.get(target.label());

						buildTreeForBlock(target, tree.stack(), sub, labelPos,
								catchBodies);
					}

					break CODE;

				} else {
					tree.addInstruction(inst);
				}

			} else if (ce instanceof Label) {
				final Label label = (Label) ce;

				if (label.startsBlock()) {
					tree
							.addInstruction(new Instruction(Opcode.opcx_goto,
									label));

					final Block next = (Block) getNode(label);

					Assert.isTrue(next != null, "Block for " + label
							+ " not found");

					addEdge(block, next);
					buildTreeForBlock(next, tree.stack(), sub, labelPos,
							catchBodies);
					break CODE;
				}

				tree.addLabel(label);
			}
		}

		addHandlerEdges(block, catchBodies, labelPos, sub, new HashSet());
	}

	/**
	 * Returns an ArrayList of the parameters of a method, including the
	 * receiver (non-static methods only).
	 * 
	 * @param method
	 *            The method.
	 */
	private ArrayList methodParams(final MethodEditor method) {
		final ArrayList locals = new ArrayList();

		int index = 0;

		if (!method.isStatic()) {
			// Add the this pointer to the locals.
			final Type type = method.declaringClass().type();
			final LocalVariable var = method.paramAt(index++);
			locals.add(new LocalExpr(var.index(), type));
		}

		final Type[] paramTypes = method.type().indexedParamTypes();

		for (int i = 0; i < paramTypes.length; i++) {
			if (paramTypes[i] != null) {
				final LocalVariable var = method.paramAt(index);
				locals.add(new LocalExpr(var.index(), paramTypes[i]));
			}

			index++;
		}

		return locals;
	}

	/**
	 * Returns the basic blocks contained in this CFG in trace order. Trace
	 * order implies that basic blocks that end with a conditional jump are
	 * followed by their false branch and, where possible, that blocks that end
	 * in an unconditional jump are followed by the block that is the target of
	 * the unconditional branch.
	 * <p>
	 * The trace does not contain the source and the sink blocks.
	 * 
	 * @return The basic Blocks in this CFG.
	 */
	public List trace() {
		// The trace must include everything but the source and sink.
		Assert.isTrue(trace.size() == size() - 2, "trace contains "
				+ trace.size() + " " + trace + " blocks, not " + (size() - 2)
				+ " " + nodes());
		return trace;
	}

	/**
	 * Commit changes back to the method editor.
	 */
	public void commit() {
		method.clearCode();

		final CodeGenerator codegen = new CodeGenerator(method);
		visit(codegen);

		final Label endLabel = method.newLabel();
		method.addLabel(endLabel);

		// Add all the handlers back in the same order we got them.
		// This ensures that the correct catch clause will be called
		// when an exception is thrown.
		final Iterator iter = catchBlocks.iterator();

		while (iter.hasNext()) {
			final Block catchBlock = (Block) iter.next();

			final Handler handler = (Handler) handlers.get(catchBlock);
			Assert.isTrue(handler != null);

			Type type = handler.catchType();

			if (type.isNull()) {
				type = null;
			}

			Block begin = null;

			final Iterator blocks = trace().iterator();

			while (blocks.hasNext()) {
				final Block block = (Block) blocks.next();

				if (handler.protectedBlocks().contains(block)) {
					if (begin == null) {
						begin = block;
					}
				} else if (begin != null) {
					final TryCatch tc = new TryCatch(begin.label(), block
							.label(), catchBlock.label(), type);

					method.addTryCatch(tc);

					begin = null;
				}
			}
		}
	}

	/**
	 * Returns the "Enter" block of this CFG. That is, the block through which
	 * all paths enter.
	 */
	public Block source() {
		return srcBlock;
	}

	/**
	 * Returns the initialization block.
	 * 
	 */
	public Block init() {
		return iniBlock;
	}

	/**
	 * Returns the sink block. That is, the block through which all paths exit.
	 */
	public Block sink() {
		return snkBlock;
	}

	/**
	 * Returns the iterated dominance frontiers for several basic blocks.
	 * 
	 * @see Block#domFrontier
	 */
	public Collection iteratedDomFrontier(final Collection blocks) {
		return idf(blocks, false);
	}

	/**
	 * Returns the iterated postdominance frontier for several basic blocks.
	 * 
	 * @see Block#pdomFrontier
	 */
	public Collection iteratedPdomFrontier(final Collection blocks) {
		return idf(blocks, true);
	}

	/**
	 * Returns the iterated dominance frontier (DF+) for a given set of blocks.
	 * <p>
	 * The iterated dominance frontier for a set of nodes is defined to be the
	 * union of the dominance frontiers of all the nodes in the set.
	 * <p>
	 * The iterated dominance frontier is particularly useful because the DF+ of
	 * an assignment node for a variable (expression) specifies the nodes at
	 * which phi-functions (PHI-functions) need to be inserted.
	 * 
	 * @param blocks
	 *            The
	 * @param reverse
	 *            Do we find the reverse (i.e. postdominance) dominance
	 *            frontier.
	 * 
	 * @see SSAPRE#placePhis
	 */
	private Collection idf(final Collection blocks, boolean reverse) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		final HashSet idf = new HashSet();

		final HashSet inWorklist = new HashSet(blocks);
		final LinkedList worklist = new LinkedList(inWorklist);

		while (!worklist.isEmpty()) {
			final Block block = (Block) worklist.removeFirst();

			Collection df;

			if (!reverse) {
				df = block.domFrontier();
			} else {
				df = block.pdomFrontier();
			}

			final Iterator iter = df.iterator();

			while (iter.hasNext()) {
				final Block dfBlock = (Block) iter.next();
				idf.add(dfBlock);

				if (inWorklist.add(dfBlock)) {
					worklist.add(dfBlock);
				}
			}
		}

		return idf;
	}

	/**
	 * @return A Collection containing the root(s) of this FlowGraph. In this
	 *         case there is only one root, so the Collection only contains the
	 *         source block.
	 */
	public Collection roots() {
		return new AbstractCollection() {
			public int size() {
				return 1;
			}

			public boolean contains(final Object obj) {
				return obj == srcBlock;
			}

			public Iterator iterator() {
				return new Iterator() {
					Object next = srcBlock;

					public boolean hasNext() {
						return next != null;
					}

					public Object next() {
						final Object n = next;
						next = null;
						return n;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * @return A Collection containing only the sink block.
	 * 
	 * @see #roots
	 */
	public Collection reverseRoots() {
		return new AbstractCollection() {
			public int size() {
				return 1;
			}

			public boolean contains(final Object obj) {
				return obj == snkBlock;
			}

			public Iterator iterator() {
				return new Iterator() {
					Object next = snkBlock;

					public boolean hasNext() {
						return next != null;
					}

					public Object next() {
						final Object n = next;
						next = null;
						return n;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Removes a node (a Block) from the graph.
	 * 
	 * @param key
	 *            Block to remove
	 */
	public void removeNode(final Object key) {
		final Block block = (Block) getNode(key);
		removeBlock(block);
	}

	/**
	 * Returns A Map mapping the first block in an exception handler to its
	 * <tt>Handler</tt> object.
	 * 
	 * @see Handler
	 */
	public Map handlersMap() {
		return handlers;
	}

	/**
	 * Returns all of the <tt>Handler</tt> objects in this CFG.
	 */
	public Collection handlers() {
		return handlers.values();
	}

	/**
	 * Returns the<tt>Block</tt>s in this CFG that begin exception handlers.
	 */
	public List catchBlocks() {
		return catchBlocks;
	}

	private void removeBlock(final Block block) {
		trace.remove(block);
		subroutines.remove(block);
		catchBlocks.remove(block);
		handlers.remove(block);

		// edgeModCount is incremented by super.removeNode().
		// Dominators will be recomputed automatically if needed, so just
		// clear the pointers to let the GC work.

		block.setDomParent(null);
		block.setPdomParent(null);
		block.domChildren().clear();
		block.pdomChildren().clear();
		block.domFrontier().clear();
		block.pdomFrontier().clear();

		Iterator iter = handlers.values().iterator();

		while (iter.hasNext()) {
			final Handler handler = (Handler) iter.next();
			handler.protectedBlocks().remove(block);
		}

		iter = subroutines.values().iterator();

		while (iter.hasNext()) {
			final Subroutine sub = (Subroutine) iter.next();
			sub.removePathsContaining(block);

			if (sub.exit() == block) {
				sub.setExit(null);
			}
		}

		if (block.tree() != null) {
			iter = block.tree().stmts().iterator();

			while (iter.hasNext()) {
				final Stmt s = (Stmt) iter.next();

				if (s instanceof LabelStmt) {
					final Label label = ((LabelStmt) s).label();
					label.setStartsBlock(false);
					iniBlock.tree().addStmt(new LabelStmt(label));
				}

				s.cleanup();
			}
		}

		super.removeNode(block.label());
	}

	/**
	 * Returns the blocks that a given block dominates.
	 */
	public Collection domChildren(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.domChildren();
	}

	/**
	 * Returns the <tt>Block</tt> that dominates a given block.
	 */
	public Block domParent(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.domParent();
	}

	/**
	 * Returns the type of a given block. A block's type is one of
	 * <tt>Block.NON_HEADER</tt>, <tt>Block.IRREDUCIBLE</tt>, or
	 * <tt>Block.REDUCIBLE</tt>.
	 */
	public int blockType(final Block block) {
		if (loopEdgeModCount != edgeModCount) {
			buildLoopTree();
		}

		return block.blockType();
	}

	/**
	 * Returns the depth of the loop in which a block is contained. The block
	 * must be contained in a loop. The procedure has depth 0. A loop (while,
	 * for, etc.) at the procedure level has depth 1. Depth increases as loops
	 * are nested.
	 * 
	 * @param block
	 *            A block whose depth we are interested in.
	 * 
	 * @see #loopLevel
	 */
	public int loopDepth(final Block block) {
		if (loopEdgeModCount != edgeModCount) {
			buildLoopTree();
		}

		if ((block == srcBlock) || (block.blockType() != Block.NON_HEADER)) {
			final LoopNode loop = (LoopNode) loopTree.getNode(block);
			Assert.isTrue(loop != null, "no loop for " + block);
			return loop.depth;
		}

		if (block.header() != null) {
			final LoopNode loop = (LoopNode) loopTree.getNode(block.header());
			Assert.isTrue(loop != null, "no loop for " + block.header());
			return loop.depth;
		}

		throw new RuntimeException();
	}

	/**
	 * Returns the level of the loop containing a given block. The innermost
	 * loops have level 0. The level increases as you go outward to higher loop
	 * nestings. For any given loop, the level is the maximum possible.
	 * <p>
	 * 
	 * <pre>
	 *  procedure()
	 *  {
	 *    // Depth 0, Level 2 (max possible)
	 *    while()
	 *    {
	 *      // Depth 1, Level 1
	 *      while()
	 *      {
	 *        // Depth 2, Level 0
	 *      }
	 *    }
	 *    while()
	 *    {
	 *      // Depth 1, Level 0
	 *    }
	 *  }
	 * </pre>
	 * 
	 * @param block
	 *            A block whose loop level we want to know. This block must be
	 *            contained in a loop.
	 */
	public int loopLevel(final Block block) {
		if (loopEdgeModCount != edgeModCount) {
			buildLoopTree();
		}

		if ((block == srcBlock) || (block.blockType() != Block.NON_HEADER)) {
			final LoopNode loop = (LoopNode) loopTree.getNode(block);
			Assert.isTrue(loop != null, "no loop for " + block);
			return loop.level;
		}

		if (block.header() != null) {
			final LoopNode loop = (LoopNode) loopTree.getNode(block.header());
			Assert.isTrue(loop != null, "no loop for " + block.header());
			return loop.level;
		}

		throw new RuntimeException();
	}

	/**
	 * Returns the loop header of the loop containing a given block. The loop
	 * header is the block that dominates all of the blocks in the loop.
	 */
	public Block loopHeader(final Block block) {
		if (loopEdgeModCount != edgeModCount) {
			buildLoopTree();
		}

		return block.header();
	}

	/**
	 * Returns the blocks in the flow graph sorted in pre-order.
	 */
	public List preOrder() {
		return super.preOrder();
	}

	/**
	 * Returns the blocks in the flow graph sorted in post-order.
	 */
	public List postOrder() {
		return super.postOrder();
	}

	/**
	 * Returns the postdominator children of a given block.
	 * 
	 * @see Block#pdomChildren
	 */
	public Collection pdomChildren(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.pdomChildren();
	}

	/**
	 * Returns the postdominator parent of a given block.
	 * 
	 * @see Block#pdomParent
	 */
	public Block pdomParent(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.pdomParent();
	}

	/**
	 * Returns the dominance frontier of a given block.
	 * 
	 * @see Block#domFrontier
	 */
	public Collection domFrontier(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.domFrontier();
	}

	/**
	 * Returns the postdominance frontier of a given block.
	 * 
	 * @see Block#pdomFrontier
	 */
	public Collection pdomFrontier(final Block block) {
		if (domEdgeModCount != edgeModCount) {
			computeDominators();
		}

		return block.pdomFrontier();
	}

	/**
	 * A LoopNode is a node in the loop tree. The loop tree is represents the
	 * nesting of loops in the method being modeled in this CFG.
	 */
	class LoopNode extends GraphNode {
		Block header;

		int depth;

		int level;

		Set elements;

		public LoopNode(final Block header) {
			this.header = header;
			this.depth = 1;
			this.level = 1;
			this.elements = new HashSet();
			this.elements.add(header);
		}

		public String toString() {
			return "level=" + level + " depth=" + depth + " header=" + header
					+ " " + elements;
		}
	}

	/**
	 * Returns a brief textual description of this <tt>FlowGraph</tt>, namely
	 * the name of the method it represents.
	 */
	public String toString() {
		return ("CFG for " + method);
	}
}
