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

import java.util.*;

import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * VerifyCFG visits the nodes in a control flow graph and verifies that certain
 * properties of the graph are true. For instance, value numbers of expressions
 * are not equal to -1, node connections are consistent, exception handlers are
 * set up correctly, etc. Mostly used for debugging purposes.
 */
public class VerifyCFG extends TreeVisitor {
	Block block; // The Block containing the node being visited

	Node parent; // The (expected) parent of the node being visited

	FlowGraph cfg; // The CFG being visited

	Set uses; // Expressions in which a definition is used

	Set nodes; // (Visited) nodes in the CFG

	boolean checkValueNumbers; // Do we check the value numbers of expressions?

	/**
	 * Constructor. Don't check value numbers.
	 */
	public VerifyCFG() {
		this(false);
	}

	/**
	 * Constructor. Since value numbers are not strictly part of the control
	 * flow graph, they may or may not be checked. For instance, if a CFG is
	 * being verfied before value numbers are assigned, we would not want to
	 * check them.
	 * 
	 * @param checkValueNumbers
	 *            Are the value numbers of expressions checked?
	 */
	public VerifyCFG(final boolean checkValueNumbers) {
		this.checkValueNumbers = checkValueNumbers;
	}

	/**
	 * Visit the blocks and expression trees in a control flow graph. Examine
	 * the uses of a variable that is defined in the CFG. Make that all uses are
	 * reachable (i.e. are in the CFG).
	 */
	public void visitFlowGraph(final FlowGraph cfg) {
		if (FlowGraph.DEBUG) {
			System.out.println("Verifying CFG for " + cfg.method());
		}

		this.cfg = cfg;

		uses = new HashSet();
		nodes = new HashSet();

		cfg.visitChildren(this);

		final Iterator e = uses.iterator();

		while (e.hasNext()) {
			final Expr use = (Expr) e.next();
			Assert.isTrue(nodes.contains(use), "use = " + use + " ("
					+ System.identityHashCode(use) + ") is not in the CFG");
		}

		if (FlowGraph.DEBUG) {
			System.out.println("Verification successful");
		}

		this.cfg = null;
		uses = null;
		nodes = null;
		block = null;
		parent = null;
	}

	/**
	 * First make sure that the <tt>Block</tt> indeed is in the CFG. If the
	 * block begins an exception handler, then make sure that all edges from
	 * protected blocks lead to the handler block. Also make sure that all of
	 * the handler block's predacessor lead to protected blocks. Finally, make
	 * sure that the successor/predacessor relationship holds.
	 */
	public void visitBlock(final Block block) {
		Assert.isTrue(block.graph() == cfg, block + " is not in the CFG");

		Iterator e;

		final Handler handler = (Handler) cfg.handlersMap().get(block);

		// If this block is the first block in an exception handler, make
		// sure that the only predacessor edges the block has are
		// protected blocks that may throw the exception handled by the
		// block. Additionally, we check that there are edges from all
		// protected blocks to the handler block.
		//
		// The predacessor to the first block in an exception handler may
		// be the init block. However, it is not really a protected
		// block, so just overlook it.

		if (handler != null) {
			final HashSet handlerPreds = new HashSet();

			e = handler.protectedBlocks().iterator();

			while (e.hasNext()) {
				final Block prot = (Block) e.next();
				handlerPreds.add(prot);
				handlerPreds.addAll(cfg.preds(prot));
			}

			final HashSet extra = new HashSet(cfg.preds(block));
			extra.removeAll(handlerPreds);
			final HashSet missing = new HashSet(handlerPreds);
			missing.removeAll(cfg.preds(block));

			Assert.isTrue(((extra.size() == 0) && (missing.size() == 0))
					|| ((missing.size() == 1) && missing.contains(cfg.init())),
					"Handler prots = " + handlerPreds
							+ " != handler block preds = " + cfg.preds(block)
							+ " extra = " + extra + " missing = " + missing);
		}

		// Make sure that the predacessor has a successor and vice versa.
		e = cfg.preds(block).iterator();

		while (e.hasNext()) {
			final Block pred = (Block) e.next();
			Assert.isTrue(cfg.succs(pred).contains(block), pred
					+ " has no succ " + block);
			Assert.isTrue(cfg.preds(block).contains(pred), block
					+ " has no pred " + pred);
		}

		e = cfg.succs(block).iterator();

		while (e.hasNext()) {
			final Block succ = (Block) e.next();
			Assert.isTrue(cfg.succs(block).contains(succ), block
					+ " has no succ " + succ);
			Assert.isTrue(cfg.preds(succ).contains(block), succ
					+ " has no pred " + block);
		}

		this.block = block;

		parent = null;
		block.visitChildren(this);
	}

	/**
	 * Make sure that all of targets of the <tt>ret</tt> are valid. The
	 * targets are the blocks to which the subroutine can return.
	 */
	public void visitRetStmt(final RetStmt stmt) {
		final Set targets = new HashSet();

		final Iterator iter = stmt.sub().paths().iterator();

		while (iter.hasNext()) {
			final Block[] path = (Block[]) iter.next();
			targets.add(path[1]);
		}

		targets.addAll(stmt.catchTargets());

		verifyTargets(stmt.block(), targets);

		visitNode(stmt);
	}

	/**
	 * Make sure that all of the targets of the <tt>jsr</tt> are valid. The
	 * only target is the entry block of the subroutine.
	 */
	public void visitJsrStmt(final JsrStmt stmt) {
		final Set targets = new HashSet();
		targets.add(stmt.sub().entry());
		targets.addAll(stmt.catchTargets());
		verifyTargets(stmt.block(), targets);

		visitNode(stmt);
	}

	/**
	 * Make sure that that all of the targets of the switch are valid.
	 */
	public void visitSwitchStmt(final SwitchStmt stmt) {
		final Set targets = new HashSet();

		targets.add(stmt.defaultTarget());

		for (int i = 0; i < stmt.targets().length; i++) {
			targets.add(stmt.targets()[i]);
		}

		targets.addAll(stmt.catchTargets());

		verifyTargets(stmt.block(), targets);

		visitNode(stmt);
	}

	/**
	 * Make sure that the targets of the if statement are valid. Targets consist
	 * of the true target, the false target, and the first blocks of any
	 * exceptions that may be thrown by the if statement.
	 */
	public void visitIfStmt(final IfStmt stmt) {
		final Set targets = new HashSet();
		targets.add(stmt.trueTarget());
		targets.add(stmt.falseTarget());
		targets.addAll(stmt.catchTargets());
		verifyTargets(stmt.block(), targets);

		visitNode(stmt);
	}

	/**
	 * Make sure that the target of <tt>goto</tt> is valid.
	 */
	public void visitGotoStmt(final GotoStmt stmt) {
		final Set targets = new HashSet();
		targets.add(stmt.target());
		targets.addAll(stmt.catchTargets());
		verifyTargets(stmt.block(), targets);

		visitNode(stmt);
	}

	/**
	 * Verifies information about the targets of a jump in a given block. First,
	 * make sure that the number of targets is the same as the number of
	 * sucessor nodes to the block. Make sure that targets of all of the jumps
	 * are indeed in the CFG. Make sure that every target is a successor of the
	 * block.
	 */
	private void verifyTargets(final Block block, final Set targets) {
		Assert.isTrue(targets.size() == cfg.succs(block).size(), block
				+ " has succs " + cfg.succs(block) + " != " + targets + " in "
				+ block.tree().lastStmt());

		final Iterator iter = targets.iterator();

		while (iter.hasNext()) {
			final Block target = (Block) iter.next();
			Assert.isTrue(block.graph().hasNode(target), target
					+ " is not in the CFG");
			Assert.isTrue(cfg.succs(block).contains(target), target
					+ " is not a succ of " + block + " "
					+ block.tree().lastStmt());
		}
	}

	/**
	 * If desired, makes sure that the store expression's value number is not
	 * -1. Makes sure that the store expression's block and parent <tt>Node</tt>
	 * are what we expect them to be. If the type of the <tt>StoreExpr</tt> is
	 * void, then make sure that its parent is an <tt>ExprStmt</tt> (i.e. make
	 * sure it is not nested within another expression).
	 */
	public void visitStoreExpr(final StoreExpr node) {
		nodes.add(node);

		if (checkValueNumbers) {
			Assert.isTrue(node.valueNumber() != -1, node
					+ ".valueNumber() = -1");
		}

		Assert.isTrue(node.block() == block, node + ".block() = "
				+ node.block() + " != block = " + block);
		Assert.isTrue(node.parent() == parent, node + ".parent() = "
				+ node.parent() + " != parent = " + parent);

		// Visit the MemExpr into which node stores.
		parent = node;
		node.target().visit(this);

		// Visit the expression whose value is being stored by node
		parent = node;
		node.expr().visit(this);

		parent = node.parent();

		if (node.type().isVoid()) {
			Assert.isTrue(parent instanceof ExprStmt, "parent of " + node
					+ " = " + parent + " is not an ExprStmt");
		}
	}

	/**
	 * Make sure that the <tt>Node</tt> resides in the block that we expect it
	 * to and that it has the expected parent expression tree <tt>Node</tt>.
	 * Make sure that the children of this <tt>Node</tt> are also correct.
	 */
	public void visitNode(final Node node) {
		nodes.add(node);

		Assert.isTrue(node.block() == block, node + ".block() = "
				+ node.block() + " != block = " + block);
		Assert.isTrue(node.parent() == parent, node + ".parent() = "
				+ node.parent() + " != parent = " + parent);

		final ArrayList children = new ArrayList();

		node.visitChildren(new TreeVisitor() {
			public void visitNode(final Node n) {
				children.add(n);
			}
		});

		final Iterator e = children.iterator();

		while (e.hasNext()) {
			final Node child = (Node) e.next();
			parent = node;
			child.visit(this);
		}

		parent = node.parent();
	}

	/**
	 * If desired, make sure that the value number of the <tt>Expr</tt> is not
	 * -1.
	 */
	public void visitExpr(final Expr expr) {
		if (checkValueNumbers) {
			Assert.isTrue(expr.valueNumber() != -1, expr
					+ ".valueNumber() = -1");
		}

		visitNode(expr);
	}

	/**
	 * Keep track of all the uses of the expression defined by the
	 * <tt>DefExpr</tt>. This information is used when verifying the
	 * <tt>FlowGraph</tt>.
	 */
	public void visitDefExpr(final DefExpr expr) {
		uses.addAll(expr.uses());
		visitExpr(expr);
	}

	/**
	 * Make sure that the <tt>VarExpr</tt> either defines a local variable, is
	 * defined by another expression, or is the child of a <tt>PhiStmt</tt>
	 * (therefore making the <tt>VarExpr</tt> a phi-variable).
	 */
	public void visitVarExpr(final VarExpr expr) {
		Assert.isTrue(expr.isDef() || (expr.def() != null)
				|| (expr.parent() instanceof PhiStmt), "Null def for variable "
				+ expr);

		visitDefExpr(expr);
	}
}
