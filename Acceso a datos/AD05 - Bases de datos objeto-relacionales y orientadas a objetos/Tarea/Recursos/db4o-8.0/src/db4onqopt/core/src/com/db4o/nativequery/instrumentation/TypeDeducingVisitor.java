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
package com.db4o.nativequery.instrumentation;

import com.db4o.instrumentation.api.*;
import com.db4o.nativequery.expr.cmp.operand.*;

class TypeDeducingVisitor implements ComparisonOperandVisitor {
	private TypeRef _predicateClass;
	private TypeRef _clazz;
	private ReferenceProvider _referenceProvider;
	
	public TypeDeducingVisitor(ReferenceProvider provider, TypeRef predicateClass) {
		this._predicateClass = predicateClass;
		this._referenceProvider = provider;
		_clazz=null;
	}

	public void visit(PredicateFieldRoot root) {
		_clazz=_predicateClass;
	}

	public void visit(CandidateFieldRoot root) {
//		_clazz=_candidateClass;
	}

	public void visit(StaticFieldRoot root) {
		_clazz=root.type();
	}
	
	public TypeRef operandClass() {
		return _clazz;
	}

	public void visit(ArithmeticExpression operand) {
	}

	public void visit(ConstValue operand) {
		_clazz=_referenceProvider.forType(operand.value().getClass());
	}

	public void visit(FieldValue operand) {
		_clazz=operand.field().type();
	}

	public void visit(ArrayAccessValue operand) {
		operand.parent().accept(this);
		_clazz=_clazz.elementType();
	}

	public void visit(MethodCallValue operand) {
		_clazz=operand.method().returnType();
	}
}