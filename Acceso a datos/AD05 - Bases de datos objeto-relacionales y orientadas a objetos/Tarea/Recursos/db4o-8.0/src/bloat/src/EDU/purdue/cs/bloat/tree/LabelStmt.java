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
 * LabelStmt is a placeholder in a Tree for a Label (the target of a jump).
 * 
 * @see Label
 * @see Tree#addLabel
 */
public class LabelStmt extends Stmt {
	Label label;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label that comprises this statement.
	 */
	public LabelStmt(final Label label) {
		this.label = label;
	}

	public Label label() {
		return label;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitLabelStmt(this);
	}

	public Object clone() {
		return copyInto(new LabelStmt(label));
	}
}
