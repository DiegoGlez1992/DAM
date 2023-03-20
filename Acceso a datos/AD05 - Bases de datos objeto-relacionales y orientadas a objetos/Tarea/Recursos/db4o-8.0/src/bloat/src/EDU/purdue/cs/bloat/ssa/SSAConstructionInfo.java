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
 * <tt>SSAConstructionInfo</tt> contains information needed to convert a CFG
 * into SSA form. Each variable (VarExpr) has an SSAConstructionInfo associated
 * with it. Each <tt>SSAConstructionInfo</tt> keeps track of information such
 * as the <tt>PhiStmt</tt>s that define copies of the variable, the
 * <tt>Block</tt>s in which the variable is defined, and the occurrences
 * (uses) of the variable in both phi and non-phi statements. Note that no
 * <tt>PhiStmt</tt> is really inserted into a basic block. We just keep track
 * of the mapping. It should also be noted that once a phi statement for a given
 * variable is "inserted" into a block, no other phi statement for that variable
 * is inserted. Thus, the order of insertion determines the precedence of the
 * phi statements: <tt>PhiReturnStmt</tt> &gt; <tt>PhiCatchStmt</tt> &gt;
 * <tt>PhiJoinStmt</tt>.
 * 
 * <p>
 * 
 * Additionally, <tt>SSAConstruction</tt> has methods to insert various
 * flavors of <tt>PhiStmt</tt>s whose targets are the variable associated
 * with the <tt>SSAConstruction</tt> into <tt>Block</tt>s.
 * 
 * @see SSA
 * @see PhiStmt
 * @see PhiCatchStmt
 * @see PhiJoinStmt
 * @see PhiReturnStmt
 */
public class SSAConstructionInfo {
	FlowGraph cfg; // The cfg we're converting into SSA form

	VarExpr prototype; // The variable we're converting into SSA form

	LinkedList[] reals; // The real (non-phi) occurrences associated

	// with a given node (block)
	LinkedList allReals; // All the real occurrences of the variable

	PhiStmt[] phis; // Phi statement associated with a given block

	Set defBlocks; // Blocks in which variable is defined

	/**
	 * Constructor.
	 * 
	 * @param cfg
	 *            The control flow graph that is being converted to SSA form.
	 * @param expr
	 *            A variable in the CFG on which SSA analysis is being done.
	 */
	public SSAConstructionInfo(final FlowGraph cfg, final VarExpr expr) {
		this.cfg = cfg;

		prototype = (VarExpr) expr.clone();
		prototype.setDef(null);

		reals = new LinkedList[cfg.size()];
		allReals = new LinkedList();

		defBlocks = new HashSet();

		phis = new PhiStmt[cfg.size()];
	}

	/**
	 * Returns the program variable associated with this
	 * <tt>SSAConstructionInfo</tt>.
	 */
	public VarExpr prototype() {
		return prototype;
	}

	/**
	 * Makes note of a <tt>Block</tt> in which the variable is defined by a
	 * <tt>PhiStmt</tt>.
	 */
	public void addDefBlock(final Block block) {
		defBlocks.add(block);
	}

	/**
	 * Returns the phi statement for the variable represented by this
	 * SSAConstructionInfo at a given block in the CFG.
	 */
	public PhiStmt phiAtBlock(final Block block) {
		return phis[cfg.preOrderIndex(block)];
	}

	/**
	 * Removes the phi statement for this variable at a given block.
	 */
	public void removePhiAtBlock(final Block block) {
		final PhiStmt phi = phis[cfg.preOrderIndex(block)];

		if (phi != null) {
			if (SSA.DEBUG) {
				System.out.println("  removing " + phi + " at " + block);
			}

			phi.cleanup();
			phis[cfg.preOrderIndex(block)] = null;
		}
	}

	/**
	 * Adds a <tt>PhiJoinStmt</tt> for the variable represented by this
	 * <tt>SSAConstructionInfo</tt> to a given <tt>Block</tt>.
	 */
	public void addPhi(final Block block) {
		if (phis[cfg.preOrderIndex(block)] != null) {
			return;
		}

		final VarExpr target = (VarExpr) prototype.clone();

		final PhiJoinStmt phi = new PhiJoinStmt(target, block);
		phis[cfg.preOrderIndex(block)] = phi;

		if (SSA.DEBUG) {
			System.out.println("  place " + phi + " in " + block);
		}
	}

	/**
	 * Adds a <tt>PhiReturnStmt</tt> to all of the <tt>Block</tt>s that are
	 * executed upon returning from a given <tt>Subroutine</tt>.
	 * 
	 * @see PhiReturnStmt
	 * @see Subroutine#paths
	 */
	public void addRetPhis(final Subroutine sub) {
		final Iterator paths = sub.paths().iterator();

		while (paths.hasNext()) {
			final Block[] path = (Block[]) paths.next();
			addRetPhi(sub, path[1]);
		}
	}

	/**
	 * Inserts a <tt>PhiCatchStmt</tt> (whose target is the variable
	 * represented by this <tt>SSAConstructionInfo</tt>) into a given
	 * <tt>Block</tt>.
	 * 
	 * @see PhiCatchStmt
	 */
	public void addCatchPhi(final Block block) {
		if (phis[cfg.preOrderIndex(block)] != null) {
			return;
		}

		if (prototype instanceof LocalExpr) {
			final LocalExpr target = (LocalExpr) prototype.clone();

			final PhiCatchStmt phi = new PhiCatchStmt(target);
			phis[cfg.preOrderIndex(block)] = phi;

			if (SSA.DEBUG) {
				System.out.println("  place " + phi + " in " + block);
			}
		}
	}

	/**
	 * Adds a <tt>PhiReturnStmt</tt> associated with a given
	 * <tt>Subroutine</tt>. The <tt>PhiReturnStmt</tt> is placed in a given
	 * block.
	 * 
	 * @see PhiReturnStmt
	 */
	private void addRetPhi(final Subroutine sub, final Block block) {
		if (phis[cfg.preOrderIndex(block)] != null) {
			return;
		}

		final VarExpr target = (VarExpr) prototype.clone();

		final PhiReturnStmt phi = new PhiReturnStmt(target, sub);
		phis[cfg.preOrderIndex(block)] = phi;

		if (SSA.DEBUG) {
			System.out.println("  place " + phi + " in " + block);
		}
	}

	/**
	 * Notes a real occurrence (that is, a use that is not an operand to a phi
	 * statement) of the variable represented by this
	 * <tt>SSAConstructionInfo</tt>.
	 * 
	 * @see PhiStmt
	 */
	public void addReal(final VarExpr real) {
		if (real.stmt() instanceof PhiStmt) {
			return;
		}

		final Block block = real.block();

		if (real.isDef()) {
			defBlocks.add(block);
		}

		Assert.isTrue(block != null, real + " not in a " + block);

		LinkedList l = reals[cfg.preOrderIndex(block)];

		if (l == null) {
			l = new LinkedList();
			reals[cfg.preOrderIndex(block)] = l;
		}

		l.add(real);
		allReals.add(real);
	}

	/**
	 * Returns all of the real occurrences of this variable.
	 */
	public Collection reals() {
		return allReals;
	}

	/**
	 * Returns all of the real occurrences of this variable in a given block.
	 */
	public Collection realsAtBlock(final Block block) {
		LinkedList l = reals[cfg.preOrderIndex(block)];

		if (l == null) {
			l = new LinkedList();
			reals[cfg.preOrderIndex(block)] = l;
		}

		return l;
	}

	/**
	 * Returns the Blocks containing a definition of the variable represented by
	 * this SSAConstruction info.
	 */
	public Collection defBlocks() {
		return defBlocks;
	}
}
