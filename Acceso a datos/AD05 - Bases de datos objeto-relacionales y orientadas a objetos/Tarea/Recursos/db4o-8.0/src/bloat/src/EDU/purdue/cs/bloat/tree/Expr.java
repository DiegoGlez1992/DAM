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

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Expr is the superclass for a number of other classes representing expressions
 * in byte code. Expressions are typed and may be nested.
 * 
 * @see DefExpr
 */
public abstract class Expr extends Node implements Cloneable {
	protected Type type; // The type (descriptor) of this expression

	private DefExpr def; // The expression in which this expression

	// is defined (if applicable)
	private Object comparator;

	/**
	 * Constructor. Initializes an expression with a given type.
	 * 
	 * @param type
	 *            The initial Type (descriptor) of this expression.
	 */
	public Expr(final Type type) {
		this.def = null;
		this.comparator = new ExprComparator();
		this.type = type;
	}

	/**
	 * Sets the type of this expression. Returns whether or not the type changed
	 * as a result of calling this method.
	 */
	public boolean setType(final Type type) {

		if (!this.type.equals(type)) {
			// if (Tree.DEBUG) {
			// System.out.println(" setting typeof(" + this + ") = " + type);
			// }
			this.type = type;

			return true;
		}

		return false;
	}

	/**
	 * Returns whether or not this expression is a defining occurrence. By
	 * default, false is returned.
	 */
	public boolean isDef() {
		return false;
	}

	/**
	 * Returns the statement to which this expression belongs. It essentially
	 * searches up the expression tree for this expression's first ancestor
	 * which is a Stmt.
	 */
	public Stmt stmt() {
		Node p = parent;

		while (!(p instanceof Stmt)) {
			Assert.isTrue(!(p instanceof Tree), "Invalid ancestor of " + this);
			Assert.isTrue(p != null, "Null ancestor of " + this);
			p = p.parent;
		}

		return (Stmt) p;
	}

	/**
	 * Returns the Type of this expression.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Cleans up this expression only, not its children.
	 */
	public void cleanupOnly() {
		setDef(null);
	}

	/**
	 * Sets the expression that defines this expression.
	 * 
	 * @param def
	 *            Defining expression.
	 */
	public void setDef(final DefExpr def) {
		// if (Tree.DEBUG) {
		// System.out.println(" setting def of " + this +
		// " (" + System.identityHashCode(this) + ") to " + def);
		// }

		if (this.def == def) {
			return;
		}

		// If this Expr already had a defining statement, remove this from the
		// DefExpr use list.
		if (this.def != null) {
			this.def.removeUse(this);
		}

		if (this.isDef()) {
			Assert.isTrue((def == this) || (def == null));
			this.def = null;
			return;
		}

		this.def = def;

		if (this.def != null) {
			this.def.addUse(this); // This Expr is a use of def
		}
	}

	/**
	 * Returns the expression in which this Expr is defined.
	 */
	public DefExpr def() {
		return def;
	}

	/**
	 * Returns the hash code for this expresion.
	 */
	public abstract int exprHashCode();

	/**
	 * Compares this expression to another.
	 * 
	 * @param other
	 *            Expr to which to compare this.
	 */
	public abstract boolean equalsExpr(Expr other);

	public abstract Object clone();

	/**
	 * Copies the contents of another expression in this one.
	 * 
	 * @param expr
	 *            The expression from which to copy.
	 */
	protected Expr copyInto(Expr expr) {
		expr = (Expr) super.copyInto(expr);

		final DefExpr def = def();

		if (isDef()) {
			expr.setDef(null);
		} else {
			expr.setDef(def);
		}

		return expr;
	}

	/**
	 * Returns an Object that can be used to compare other Expr to this.
	 */
	public Object comparator() {
		return comparator;
	}

	/**
	 * ExprComparator is used to provide a different notion of equality among
	 * expressions than the default ==. In most cases, we want ==, but
	 * occasionally we want the equalsExpr() functionality when inserting in
	 * Hashtables, etc.
	 */
	private class ExprComparator {
		Expr expr = Expr.this;

		public boolean equals(final Object obj) {
			if (obj instanceof ExprComparator) {
				final Expr other = ((ExprComparator) obj).expr;
				return expr.equalsExpr(other)
						&& expr.type.simple().equals(other.type.simple());
			}

			return false;
		}

		public int hashCode() {
			return Expr.this.exprHashCode();
		}
	}
}
