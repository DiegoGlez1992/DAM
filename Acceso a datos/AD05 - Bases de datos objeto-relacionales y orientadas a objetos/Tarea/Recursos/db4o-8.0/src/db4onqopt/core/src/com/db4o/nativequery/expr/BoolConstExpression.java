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


public class BoolConstExpression implements Expression {	
	public static final BoolConstExpression TRUE=new BoolConstExpression(true);
	public static final BoolConstExpression FALSE=new BoolConstExpression(false);

	private boolean _value;
	
	private BoolConstExpression(boolean value) {
		this._value=value;
	}
	
	public boolean value() {
		return _value;
	}
	
	public String toString() {
		return String.valueOf(_value);
	}
	
	public static BoolConstExpression expr(boolean value) {
		return (value ? TRUE : FALSE);
	}

	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	public Expression negate() {
		return (_value ? FALSE : TRUE);
	}
}
