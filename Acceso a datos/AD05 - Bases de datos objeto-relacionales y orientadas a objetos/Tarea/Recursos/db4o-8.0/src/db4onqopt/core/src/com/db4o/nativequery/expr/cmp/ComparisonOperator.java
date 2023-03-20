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

// TODO: switch to individual classes and visitor dispatch?
public final class ComparisonOperator {
	public final static int EQUALS_ID=0;
	public final static int SMALLER_ID=1;
	public final static int GREATER_ID=2;
	public final static int CONTAINS_ID=3;
	public final static int STARTSWITH_ID=4;
	public final static int ENDSWITH_ID=5;
	public final static int IDENTITY_ID=6;

	public final static ComparisonOperator VALUE_EQUALITY=new ComparisonOperator(EQUALS_ID,"==", true);
	public final static ComparisonOperator SMALLER=new ComparisonOperator(SMALLER_ID,"<", false);
	public final static ComparisonOperator GREATER=new ComparisonOperator(GREATER_ID,">", false);
	public final static ComparisonOperator CONTAINS=new ComparisonOperator(CONTAINS_ID,"<CONTAINS>", false);
	public final static ComparisonOperator STARTS_WITH=new ComparisonOperator(STARTSWITH_ID,"<STARTSWITH>", false);
	public final static ComparisonOperator ENDS_WITH=new ComparisonOperator(ENDSWITH_ID,"<ENDSWITH>", false);
	public final static ComparisonOperator REFERENCE_EQUALITY=new ComparisonOperator(IDENTITY_ID,"===", true);

	private int _id;
	private String _op;
	private boolean _symmetric;
	
	private ComparisonOperator(int id, String op, boolean symmetric) {
		_id=id;
		_op=op;
		_symmetric=symmetric;
	}
	
	public int id() {
		return _id;
	}
	
	public String toString() {
		return _op;
	}
	
	public boolean isSymmetric() {
		return _symmetric;
	}
}
