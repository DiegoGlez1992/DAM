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
package EDU.purdue.cs.bloat.tree;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;

/**
 * StackOptimizer analyzes the relative distances of various uses of the same
 * definition of a local variable to add dups and swaps to the bytecode and
 * eliminate loads and stores.
 * 
 * @author Thomas VanDrunen
 */

public class StackOptimizer {

	static boolean DEBUG = false;

	Hashtable defInfoMap; /*
							 * maps LocalExprs (which are definitions) to
							 * DefInformations
							 */

	Hashtable useInfoMap; /* maps LocalExprs to UseInformations */

	Block owningBlock;

	public StackOptimizer(final Block owningBlock) {
		this.owningBlock = owningBlock;
		defInfoMap = new Hashtable();
		useInfoMap = new Hashtable();
	}

	public static void optimizeCFG(final FlowGraph cfg) {

		final List blocks = cfg.preOrder();
		for (final Iterator it = blocks.iterator(); it.hasNext();) {
			((Block) it.next()).stackOptimizer().optimize();
		}

	}

	/**
	 * Optimize runs the algorithm for analyzing the tree, looking for
	 * opportunities to replaces stores and loads with dups and swaps. It
	 * initiates several visitors, and information is sotred in defInfoMap and
	 * useInfoMap
	 */

	public void optimize() {

		final Vector LEs = (new LEGatherer()).getLEs(owningBlock); // get all
																	// the
		LocalExpr current; // LocalExprs in the block

		for (int i = 0; i < LEs.size(); i++) {

			current = (LocalExpr) LEs.elementAt(i);

			useInfoMap.put(current, new UseInformation());

			if (current.isDef()) {
				final DefInformation DI = new DefInformation(current.uses
						.size());
				defInfoMap.put(current, DI);
				// send the DefInformation the number of uses of the var
			} else if (current.def() != null) {

				final DefInformation DI = (DefInformation) defInfoMap
						.get(current.def());

				if (DI == null) {
					continue; // if it has no def information,
					// it's probably a parameter, and we need to store it
					// anyway.
				}

				DI.usesFound++;

				// handle a special case
				// if we have something like L := L + k, we can do
				// "iinc L, k", which would be better (wouldn't it?) than
				// propogating L on the stack, loading a constant, adding,
				// and saving. In that case, also, we'll have to load.
				// (see codegen/CodeGenerator.java, circa line 1334)

				if ((current.parent() instanceof ArithExpr)
						&& (current.parent().parent() instanceof StoreExpr)
						&& (((((ArithExpr) current.parent()).left() instanceof ConstantExpr) && ((ArithExpr) current
								.parent()).left().type().isIntegral()) || ((((ArithExpr) current
								.parent()).right() instanceof ConstantExpr) && ((ArithExpr) current
								.parent()).left().type().isIntegral()))
						&& (((StoreExpr) current.parent().parent()).target() instanceof LocalExpr)
						&& (((LocalExpr) ((StoreExpr) current.parent().parent())
								.target()).index() == current.index())) {
					DI.type1s += 3;
				} else if ((current.parent() instanceof StoreExpr)
						&& (current.parent().parent() instanceof ExprStmt)
						&& (((StoreExpr) current.parent()).target() instanceof LocalExpr)
						&& (((LocalExpr) ((StoreExpr) current.parent())
								.target()).index() == current.index())) {
					DI.type1s += 3; // the new "definition" no doubt
					// has uses, so we need the original stored
					continue;
				}

				else {

					// first search using a Type0Visitor. If that search
					// fails, use a Type1Visitor. (The second condition checks
					// whether we have too many type 1s already, and will
					// have to store it.... there's no point in looking for
					// anymore
					if (!((new Type0Visitor(defInfoMap, useInfoMap))
							.search(current))
							&& (DI.type1s < 3)) {

						// Java, I hate you as much as I love you.
						// I blink my eyes and more complications spring up.
						// So far I have been happily ignoring the problem
						// of wide expressions-- there's no way we can
						// do type 1 optimizations on wide values because
						// we can't do a swap on values that take up two
						// stack positions...

						if (current.type().isWide()) {
							DI.type1s += 3; // give up
						} else {
							(new Type1Visitor(defInfoMap, useInfoMap))
									.search(current);
						}
					}
				}
			}
		}
	}

	/**
	 * Various methods used by CodeGenerator, used as an interface into the
	 * information in defInfoMap and useInfoMap
	 */

	public boolean shouldStore(final LocalExpr expr) {

		// We should store if there are more than 2 type 1 uses or
		// any uses of type greater than one-- which will be indicated
		// by type1s being greater than 2

		// the parameter expr might be null, e.g., if this method is
		// called from "dups" in "!shouldStore((LocalExpr) expr.def())",
		// because if the expression is a use of a parameter in a method,
		// its definition is null. Return true in that case because it
		// will be saved to a local anyway
		if (expr == null) {
			return true;
		}

		final DefInformation DI = (DefInformation) defInfoMap.get(expr);
		if (DI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.shouldStore: parameter not found in defInfoMap:");
				System.err.println(expr.toString());
			}

			return true;
		}

		if ((DI.type1s > 2) || (DI.usesFound < DI.uses)) {
			return true;
		} else {
			return false;
		}
	}

	public int dups(final LocalExpr expr) {

		int toReturn = 0;
		final UseInformation UI = (UseInformation) useInfoMap.get(expr);

		if (UI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.dups: parameter not found in useInfoMap");
			}
			return toReturn;
		}

		toReturn += (UI.type0s - UI.type0_x1s - UI.type0_x2s);
		if ((expr.isDef() && !shouldStore(expr))
				|| (!(expr.isDef()) && !shouldStore((LocalExpr) expr.def()))) {
			toReturn += (UI.type1s - UI.type1_x1s - UI.type1_x2s);
		}

		return toReturn;
	}

	public int dup_x1s(final LocalExpr expr) {

		int toReturn = 0;
		final UseInformation UI = (UseInformation) useInfoMap.get(expr);

		if (UI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.dup_x1s: parameter not found in useInfoMap");
			}
			return toReturn;
		}

		toReturn += UI.type0_x1s;
		if ((expr.isDef() && !shouldStore(expr))
				|| (!(expr.isDef()) && !shouldStore((LocalExpr) expr.def()))) {
			toReturn += UI.type1_x1s;
		}

		return toReturn;
	}

	public int dup_x2s(final LocalExpr expr) {

		int toReturn = 0;
		final UseInformation UI = (UseInformation) useInfoMap.get(expr);

		if (UI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.dup_x2s: parameter not found in useInfoMap");
			}
			return toReturn;
		}

		toReturn += UI.type0_x2s;
		if ((expr.isDef() && !shouldStore(expr))
				|| (!(expr.isDef()) && !shouldStore((LocalExpr) expr.def()))) {
			toReturn += UI.type1_x2s;
		}

		return toReturn;
	}

	public boolean onStack(final LocalExpr expr) {

		if (expr.isDef()) {
			return false;
		}

		final UseInformation UI = (UseInformation) useInfoMap.get(expr);

		if (UI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.onStack: parameter not found in useInfoMap");
			}
			return false;
		}

		if (UI.type == 0) {
			return true;
		}

		if ((UI.type == 1) && !shouldStore((LocalExpr) expr.def())) {
			return true;
		}

		return false;
	}

	public boolean shouldSwap(final LocalExpr expr) {

		final UseInformation UI = (UseInformation) useInfoMap.get(expr);

		if (UI == null) {
			if (StackOptimizer.DEBUG) {
				System.err
						.println("Error in StackOptimizer.onStack: parameter not found in useInfoMap");
			}
			return false;
		}

		return (onStack(expr) && (UI.type == 1));
	}

	public void infoDisplay(final LocalExpr expr) {

		final UseInformation UI = (UseInformation) useInfoMap.get(expr);
		final DefInformation DI = (DefInformation) defInfoMap.get(expr);

		System.err.println(expr.toString());
		System.err.println(expr.parent().toString() + "-"
				+ expr.parent().parent().toString());
		if ((expr.parent().parent().parent() != null)
				&& (expr.parent().parent().parent().parent() != null)) {
			System.err.println(expr.parent().parent().parent().toString() + "-"
					+ expr.parent().parent().parent().parent().toString());
		}

		if (DI == null) {
			System.err.println("not a definition");
			if (expr.def() == null) {
				System.err.println("has no definition (is parameter?)");
			} else {
				System.err.println("has definition " + expr.def());
			}
		} else {
			System.err.println("a definition with " + DI.type1s
					+ " type1s total");
			System.err.println("uses: " + DI.uses);
			System.err.println("uses found: " + DI.usesFound);
			if (shouldStore(expr)) {
				System.err.println("should store");
			}
		}

		if (UI == null) {
			System.err.println("No use information entry. trouble");
		} else {
			if (DI == null) {
				System.err.println("type on stack: " + UI.type);
			}
			System.err.println("type0s for this instance: " + UI.type0s);
			System.err.println("of above, number of x1s: " + UI.type0_x1s);
			System.err.println("of above, number of x2s: " + UI.type0_x2s);
			System.err.println("type1s for this instance: " + UI.type1s);
			System.err.println("of above, number of x1s: " + UI.type1_x1s);
			System.err.println("of above, number of x2s: " + UI.type1_x2s);
		}

	}
}
