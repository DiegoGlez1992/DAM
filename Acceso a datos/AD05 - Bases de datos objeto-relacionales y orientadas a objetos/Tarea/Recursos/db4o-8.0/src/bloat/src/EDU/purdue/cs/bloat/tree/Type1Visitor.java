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

/**
 * Type1Visitor...
 */
public class Type1Visitor extends AscendVisitor {

	Node turningPoint; /*
						 * where we were when we started descending the tree
						 * instead of ascending
						 */

	boolean found; /* have we found an earlier occurence */

	public Type1Visitor(final Hashtable defInfoMap, final Hashtable useInfoMap) {
		super(defInfoMap, useInfoMap);
	}

	public void search(final LocalExpr start) {

		this.start = start;
		previous = this.start;
		found = false;
		start.parent().visit(this);
		if (!found) {

			if (turningPoint != null) {
				(new Type1UpVisitor(defInfoMap, useInfoMap)).search(
						turningPoint, start);
			} else { // search failed (one place I saw this was in
				// a ZeroCheckExpression)

				((DefInformation) defInfoMap.get(start.def())).type1s += 3;
			}

		}

	}

	public void check(final Node node) {

		if ((node instanceof Expr) && ((Expr) node).type().isWide()) {
			turningPoint = null;
			return; // give up. We cannot swap around a wide value.
		}

		turningPoint = node;

		if (node instanceof StoreExpr) {
			check(((StoreExpr) node).expr()); // to search down the expression
			// being stored
		} else if (!(node instanceof LocalExpr) && (node instanceof Expr)) {
			found = (new Type1DownVisitor(useInfoMap, defInfoMap)).search(node,
					start);
		}

	}
}

class Type1UpVisitor extends AscendVisitor {

	Node turningPoint;

	boolean found;

	Type1UpVisitor(final Hashtable defInfoMap, final Hashtable useInfoMap) {
		super(defInfoMap, useInfoMap);
	}

	public void search(final Node turningPoint, final LocalExpr start) {

		found = false;
		this.start = start;
		previous = turningPoint;
		this.turningPoint = turningPoint;
		if ((turningPoint.parent() != null)
				&& !(turningPoint.parent() instanceof Tree)) {
			// go more than one statement earlier
			// because we don't know if the intermediate
			// statment leaves anything on the stack.
			turningPoint.parent().visit(this);
		}

		if (!found) {
			// if we've found nothing by now, we won't find anything.
			// setting the type1s of the definition to something 3 or
			// greater insures the variable will be stored
			((DefInformation) defInfoMap.get(start.def())).type1s += 3;
		}

	}

	public void check(final Node node) {

		if (node instanceof ExprStmt) {
			check(((ExprStmt) node).expr()); // might be something we want
		} else if (node instanceof StoreExpr) {
			check(((StoreExpr) node).target()); // to see if the target matches
		} else if ((node instanceof LocalExpr)
				&& ((((LocalExpr) node).index() == start.index() // compare
																	// index))
				&& (((LocalExpr) node).def() == start.def())))) { // and def
			// we've found a match
			// update information
			((UseInformation) useInfoMap.get(start)).type = 1;
			((UseInformation) useInfoMap.get(node)).type1s++;
			((DefInformation) defInfoMap.get(start.def())).type1s++;
			found = true;
			return;
		}

	}

}

class Type1DownVisitor extends DescendVisitor {

	public Type1DownVisitor(final Hashtable useInfoMap,
			final Hashtable defInfoMap) {
		super(useInfoMap, defInfoMap);
	}

	public void visitLocalExpr(final LocalExpr expr) {

		if ((expr.index() == start.index()) && (expr.def() == start.def())) {
			// we've found a match
			// update information
			((UseInformation) useInfoMap.get(start)).type = 1;
			final UseInformation ui = (UseInformation) useInfoMap.get(expr);
			ui.type1s++;
			if (exchangeFactor == 1) {
				ui.type1_x1s++;
			}
			if (exchangeFactor == 2) {
				ui.type1_x2s++;
			}
			// System.err.println(expr.toString());
			((DefInformation) defInfoMap.get(expr.def())).type1s++;
			found = true;
		}
	}

}
