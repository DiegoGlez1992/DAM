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
 * <tt>JumpStmt</tt> is the super class for several classes that represent
 * statements that chang the flow of control in a program.
 * 
 * @see GotoStmt
 * @see IfStmt
 * @see JsrStmt
 */
public abstract class JumpStmt extends Stmt {
	Set catchTargets;

	public JumpStmt() {
		catchTargets = new HashSet();
	}

	/**
	 * The <tt>Block</tt> containing this <tt>JumpStmt</tt> may lie within a
	 * try block (i.e. it is a <i>protected</i> block). If so, then
	 * <tt>catchTargets()</tt> returns a set of <tt>Block</tt>s that begin
	 * the exception handler for the exception that may be thrown in the
	 * protected block.
	 */
	public Collection catchTargets() {
		return catchTargets;
	}

	protected Node copyInto(final Node node) {
		((JumpStmt) node).catchTargets.addAll(catchTargets);
		return super.copyInto(node);
	}
}
