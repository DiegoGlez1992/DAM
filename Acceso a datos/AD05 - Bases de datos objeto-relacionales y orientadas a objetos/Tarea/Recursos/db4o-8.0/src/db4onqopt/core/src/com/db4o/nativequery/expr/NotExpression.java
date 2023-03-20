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


public class NotExpression implements Expression {
	private Expression _expr;

	public NotExpression(Expression expr) {
		this._expr = expr;
	}
	
	public String toString() {
		return "!("+_expr+")";
	}

	public Expression expr() {
		return _expr;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		NotExpression casted = (NotExpression) other;
		return _expr.equals(casted._expr);
	}
	
	public int hashCode() {
		return -_expr.hashCode();
	}

	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
