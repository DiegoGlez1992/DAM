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

/**
 * VarExpr represents an expression that accesses a local variable or a variable
 * on the stack.
 * 
 * @see StackExpr
 * @see LocalExpr
 * 
 * @see DefExpr
 */
public abstract class VarExpr extends MemExpr {
	int index;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            Index giving location of expression. For instance, the number
	 *            local variable represented or the position of the stack
	 *            variable represented.
	 * @param type
	 *            Type (descriptor) of this expression.
	 */
	public VarExpr(final int index, final Type type) {
		super(type);
		this.index = index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	/**
	 * Returns the expression that defines this expression.
	 */
	public DefExpr def() {
		if (isDef()) {
			return this;
		}

		return super.def();
	}
}
