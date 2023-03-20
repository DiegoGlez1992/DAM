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
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;

class ComparisonBytecodeGeneratingVisitor implements ComparisonOperandVisitor {
	private MethodBuilder _methodBuilder;
	private TypeRef _predicateClass;
	private boolean _inArithmetic=false;
	private TypeRef _opClass=null;
	private TypeRef _staticRoot=null;

	public ComparisonBytecodeGeneratingVisitor(MethodBuilder methodBuilder, TypeRef predicateClass) {
		this._methodBuilder = methodBuilder;
		this._predicateClass = predicateClass;
	}

	public void visit(ConstValue operand) {
		Object value = operand.value();
		if(value!=null) {
			_opClass=typeRef(value.getClass());
		}
		_methodBuilder.ldc(value);
		if(value!=null) {
			box(_opClass,!_inArithmetic);
		}
	}	

	private TypeRef typeRef(Class type) {
		return _methodBuilder.references().forType(type);
	}

	public void visit(FieldValue fieldValue) {
		TypeRef lastFieldClass = fieldValue.field().type();
		boolean needConversion=lastFieldClass.isPrimitive();
			
		fieldValue.parent().accept(this);
		if(_staticRoot!=null) {
			_methodBuilder.loadStaticField(fieldValue.field());
			_staticRoot=null;
			return;
		}
		_methodBuilder.loadField(fieldValue.field());
		
		box(lastFieldClass,!_inArithmetic&&needConversion);
	}

	public void visit(CandidateFieldRoot root) {
		_methodBuilder.loadArgument(1);
	}

	public void visit(PredicateFieldRoot root) {
		_methodBuilder.loadArgument(0);
	}

	public void visit(StaticFieldRoot root) {
		_staticRoot=root.type();
	}

	public void visit(ArrayAccessValue operand) {
		TypeRef cmpType=deduceFieldClass(operand.parent()).elementType();
		operand.parent().accept(this);
		boolean outerInArithmetic=_inArithmetic;
		_inArithmetic=true;
		operand.index().accept(this);
		_inArithmetic=outerInArithmetic;
		_methodBuilder.loadArrayElement(cmpType);
		box(cmpType, !_inArithmetic);
	}

	public void visit(MethodCallValue operand) {
		MethodRef method=operand.method();
		TypeRef retType=method.returnType();
		// FIXME: this should be handled within conversions
		boolean needConversion=retType.isPrimitive();
		operand.parent().accept(this);
		boolean oldInArithmetic=_inArithmetic;
		for (int paramIdx = 0; paramIdx < operand.args().length; paramIdx++) {
			_inArithmetic=operand.method().paramTypes()[paramIdx].isPrimitive();
			operand.args()[paramIdx].accept(this);
		}
		_inArithmetic=oldInArithmetic;
		_methodBuilder.invoke(method, operand.callingConvention());
		box(retType, !_inArithmetic&&needConversion);
	}

	public void visit(ArithmeticExpression operand) {
		boolean oldInArithmetic=_inArithmetic;
		_inArithmetic=true;
		operand.left().accept(this);
		operand.right().accept(this);
		TypeRef operandType=arithmeticType(operand);
		switch(operand.op().id()) {
			case ArithmeticOperator.ADD_ID:
				_methodBuilder.add(operandType);
				break;
			case ArithmeticOperator.SUBTRACT_ID:
				_methodBuilder.subtract(operandType);
				break;
			case ArithmeticOperator.MULTIPLY_ID:
				_methodBuilder.multiply(operandType);
				break;
			case ArithmeticOperator.DIVIDE_ID:
				_methodBuilder.divide(operandType);
				break;
			case ArithmeticOperator.MODULO_ID:
				_methodBuilder.modulo(operandType);
				break;
			default:
				throw new RuntimeException("Unknown operand: "+operand.op());
		}
		box(_opClass,!oldInArithmetic);
		_inArithmetic=oldInArithmetic;
		// FIXME: need to map dX,fX,...
	}

	private void box(TypeRef boxedType, boolean canApply) {
		if (!canApply) {
			return;
		}
		_methodBuilder.box(boxedType);
	}

	private TypeRef deduceFieldClass(ComparisonOperand fieldValue) {
		TypeDeducingVisitor visitor=new TypeDeducingVisitor(_methodBuilder.references(), _predicateClass);
		fieldValue.accept(visitor);
		return visitor.operandClass();
	}

	private TypeRef arithmeticType(ComparisonOperand operand) {
		if (operand instanceof ConstValue) {
			return primitiveType(((ConstValue) operand).value().getClass());
		}
		if (operand instanceof FieldValue) {
			return ((FieldValue)operand).field().type();
		}
		if (operand instanceof ArithmeticExpression) {
			ArithmeticExpression expr=(ArithmeticExpression)operand;
			TypeRef left=arithmeticType(expr.left());
			TypeRef right=arithmeticType(expr.right());
			if(left==doubleType()||right==doubleType()) {
				return doubleType();
			}
			if(left==floatType()||right==floatType()) {
				return floatType();
			}
			if(left==longType()||right==longType()) {
				return longType();
			}
			return intType();
		}
		return null;
	}

	private TypeRef primitiveType(Class klass) {
		if (klass == Integer.class
			|| klass == Short.class
			|| klass == Boolean.class
			|| klass == Byte.class) {
			return intType();
		}
		if (klass == Double.class) {
			return doubleType();
		}
		if (klass == Float.class) {
			return floatType();
		}
		if (klass == Long.class) {
			return longType();
		}
		return typeRef(klass);
	}

	private TypeRef intType() {
		return typeRef(Integer.TYPE);
	}	

	private TypeRef longType() {
		return typeRef(Long.TYPE);
	}

	private TypeRef floatType() {
		return typeRef(Float.TYPE);
	}

	private TypeRef doubleType() {
		return typeRef(Double.TYPE);
	}
}
