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
package com.db4o.nativequery.expr;

import com.db4o.nativequery.expr.cmp.ComparisonOperator;
import com.db4o.nativequery.expr.cmp.operand.*;

public class ComparisonExpression implements Expression {
	private FieldValue _left;
	private ComparisonOperand _right;
	private ComparisonOperator _op;

	public ComparisonExpression(FieldValue left, ComparisonOperand right,ComparisonOperator op) {
		if(left==null||right==null||op==null) {
			throw new NullPointerException();
		}
		this._left = left;
		this._right = right;
		this._op = op;
	}

	public FieldValue left() {
		return _left;
	}
	
	public ComparisonOperand right() {
		return _right;
	}

	public ComparisonOperator op() {
		return _op;
	}

	public String toString() {
		return _left+" "+_op+" "+_right;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		ComparisonExpression casted = (ComparisonExpression) other;
		return _left.equals(casted._left)&&_right.equals(casted._right)&&_op.equals(casted._op);
	}

	public int hashCode() {
		return (_left.hashCode()*29+_right.hashCode())*29+_op.hashCode();
	}
	
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
