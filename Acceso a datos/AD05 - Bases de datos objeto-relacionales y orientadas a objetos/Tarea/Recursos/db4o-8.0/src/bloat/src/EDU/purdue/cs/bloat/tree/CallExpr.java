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
 * <tt>CallExpr</tt> is a superclass of expressions that represent the
 * invocation of a method. It consists of an array of <tt>Expr</tt> that
 * represent the arguments to a method and a <tt>MemberRef</tt> that
 * represents the method itself.
 * 
 * @see CallMethodExpr
 * @see CallStaticExpr
 */
public abstract class CallExpr extends Expr {
	Expr[] params; // The parameters to the method

	MemberRef method; // The method to be invoked

	public int voltaPos; // used for placing swaps and stuff

	/**
	 * Constructor.
	 * 
	 * @param params
	 *            Parameters to the method. Note that these parameters do not
	 *            contain parameter 0, the "this" pointer.
	 * @param method
	 *            The method that is to be invoked.
	 * @param type
	 *            The type of this expression (i.e. the return type of the
	 *            method being called).
	 */
	public CallExpr(final Expr[] params, final MemberRef method, final Type type) {
		super(type);
		this.params = params;
		this.method = method;

		for (int i = 0; i < params.length; i++) {
			params[i].setParent(this);
		}
	}

	public MemberRef method() {
		return method;
	}

	public Expr[] params() {
		return params;
	}
}
