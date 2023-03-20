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
package com.db4o.nativequery.expr.cmp.operand;


public interface ComparisonOperandVisitor {
	void visit(ArithmeticExpression operand);
	void visit(ConstValue operand);
	void visit(FieldValue operand);
	void visit(CandidateFieldRoot root);
	void visit(PredicateFieldRoot root);
	void visit(StaticFieldRoot root);
	void visit(ArrayAccessValue operand);
	void visit(MethodCallValue value);
}