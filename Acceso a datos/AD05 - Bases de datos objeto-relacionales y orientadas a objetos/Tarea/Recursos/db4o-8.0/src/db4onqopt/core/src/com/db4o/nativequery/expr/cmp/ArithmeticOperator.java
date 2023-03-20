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
package com.db4o.nativequery.expr.cmp;

public final class ArithmeticOperator {
	public final static int ADD_ID=0;
	public final static int SUBTRACT_ID=1;
	public final static int MULTIPLY_ID=2;
	public final static int DIVIDE_ID=3;
	public final static int MODULO_ID=4;
	
	public final static ArithmeticOperator ADD=new ArithmeticOperator(ADD_ID,"+");
	public final static ArithmeticOperator SUBTRACT=new ArithmeticOperator(SUBTRACT_ID,"-");
	public final static ArithmeticOperator MULTIPLY=new ArithmeticOperator(MULTIPLY_ID,"*");
	public final static ArithmeticOperator DIVIDE=new ArithmeticOperator(DIVIDE_ID,"/");
	public final static ArithmeticOperator MODULO=new ArithmeticOperator(MODULO_ID,"%");
	
	private String _op;
	private int _id;
	
	private ArithmeticOperator(int id,String op) {
		_id=id;
		_op=op;
	}
	
	public int id() {
		return _id;
	}
	
	public String toString() {
		return _op;
	}
}
