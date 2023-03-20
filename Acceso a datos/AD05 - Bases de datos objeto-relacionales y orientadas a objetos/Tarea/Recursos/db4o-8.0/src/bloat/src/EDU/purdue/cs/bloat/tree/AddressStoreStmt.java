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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * Associated with an AddressStoreStmt is a Subroutine whose address (offset in
 * the instruction sequence) is to be stored. Addresses may be loaded (using
 * <i>astore</i>), but cannot be reloaded. Therefore, AddressStoreStmt is
 * needed to differentiate between a regular (object reference) <i>astore</i>
 * which is modeled by a LocalExpr.
 * 
 * @see Tree#visit_astore
 * @see Subroutine
 * @see LocalExpr
 */
public class AddressStoreStmt extends Stmt {
	Subroutine sub;

	/**
	 * Constructor.
	 * 
	 * @param sub
	 * 
	 */
	public AddressStoreStmt(final Subroutine sub) {
		this.sub = sub;
	}

	public Subroutine sub() {
		return sub;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitAddressStoreStmt(this);
	}

	public Object clone() {
		return copyInto(new AddressStoreStmt(sub));
	}
}
