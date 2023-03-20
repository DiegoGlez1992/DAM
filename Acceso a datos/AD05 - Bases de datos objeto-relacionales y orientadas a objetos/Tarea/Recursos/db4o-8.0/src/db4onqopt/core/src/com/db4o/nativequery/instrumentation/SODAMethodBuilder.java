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

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;
import com.db4o.internal.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

public class SODAMethodBuilder {	
	private final static boolean LOG_BYTECODE=false;

	private MethodRef descendRef;
	private MethodRef constrainRef;
	private MethodRef greaterRef;
	private MethodRef smallerRef;
	private MethodRef containsRef;
	private MethodRef startsWithRef;
	private MethodRef endsWithRef;
	private MethodRef notRef;
	private MethodRef andRef;
	private MethodRef orRef;
	private MethodRef identityRef;

	private final TypeEditor _editor;

	private MethodBuilder _builder;

	public static final String OPTIMIZE_QUERY_METHOD_NAME = "optimizeQuery";

	private class SODAExpressionBuilder implements ExpressionVisitor {

		private TypeRef predicateClass;
		
		public SODAExpressionBuilder(TypeRef predicateClass) {
			this.predicateClass=predicateClass;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			invoke(andRef);
		}

		public void visit(BoolConstExpression expression) {
			loadQuery();
			//throw new RuntimeException("No boolean constants expected in parsed expression tree");
		}

		private void loadQuery() {
			loadArgument(1);
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			expression.right().accept(this);
			invoke(orRef);
		}

		public void visit(final ComparisonExpression expression) {
			loadQuery();
			
			descend(fieldNames(expression.left()));
			
			expression.right().accept(comparisonEmitter());
			
			constrain(expression.op());
		}

		private void descend(Iterator4 fieldNames) {
			while (fieldNames.moveNext()) {
				descend(fieldNames.current());
			}
		}

		private ComparisonBytecodeGeneratingVisitor comparisonEmitter() {
			return new ComparisonBytecodeGeneratingVisitor(_builder, predicateClass);
		}

		private void constrain(ComparisonOperator op) {
			invoke(constrainRef);
			
			if (op.equals(ComparisonOperator.VALUE_EQUALITY)) {
				return;
			}
			if (op.equals(ComparisonOperator.REFERENCE_EQUALITY)) {
				invoke(identityRef);
				return;
			}
			if (op.equals(ComparisonOperator.GREATER)) {
				invoke(greaterRef);
				return;
			}
			if (op.equals(ComparisonOperator.SMALLER)) {
				invoke(smallerRef);
				return;
			}
			if (op.equals(ComparisonOperator.CONTAINS)) {
				invoke(containsRef);
				return;
			}
			if (op.equals(ComparisonOperator.STARTS_WITH)) {
				ldc(new Integer(1));
				invoke(startsWithRef);
				return;
			}
			if (op.equals(ComparisonOperator.ENDS_WITH)) {
				ldc(new Integer(1));
				invoke(endsWithRef);
				return;
			}
			throw new RuntimeException("Cannot interpret constraint: " + op);
		}

		private void descend(final Object fieldName) {
			ldc(fieldName);
			invoke(descendRef);
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			invoke(notRef);
		}
		
		private Iterator4 fieldNames(FieldValue fieldValue) {
			Collection4 coll=new Collection4();
			ComparisonOperand curOp=fieldValue;
			while(curOp instanceof FieldValue) {
				FieldValue curField=(FieldValue)curOp;
				coll.prepend(curField.fieldName());
				curOp=curField.parent();
			}
			return coll.iterator();
		}
	}
	
	public SODAMethodBuilder(TypeEditor editor) {
		_editor = editor;
		buildMethodReferences();
	}
	
	public void injectOptimization(Expression expr) {
		_editor.addInterface(typeRef(Db4oEnhancedFilter.class));
		_builder = _editor.newPublicMethod(platformName(OPTIMIZE_QUERY_METHOD_NAME), typeRef(Void.TYPE), new TypeRef[] { typeRef(Query.class) });
		
		TypeRef predicateClass = _editor.type();
		expr.accept(new SODAExpressionBuilder(predicateClass));
		_builder.pop();
		if (LOG_BYTECODE) {
			System.out.println("Expression: " + expr);
			_builder.print(System.out);
		}
		_builder.endMethod();
	}

	private TypeRef typeRef(Class type) {
		return _editor.references().forType(type);
	}

	private String platformName(final String name) {
		return NativeQueriesPlatform.toPlatformName(name);
	}
	
	private void loadArgument(int index) {
		_builder.loadArgument(index);
	}
	
	private void invoke(MethodRef method) {
		_builder.invoke(method, CallingConvention.INTERFACE);
	}
	
	private void ldc(Object value) {
		_builder.ldc(value);
	}
	
	private void buildMethodReferences() {
		descendRef=methodRef(Query.class,"descend",new Class[]{String.class});
		constrainRef=methodRef(Query.class,"constrain",new Class[]{Object.class});
		greaterRef=methodRef(Constraint.class,"greater",new Class[]{});
		smallerRef=methodRef(Constraint.class,"smaller",new Class[]{});
		containsRef=methodRef(Constraint.class,"contains",new Class[]{});
		startsWithRef=methodRef(Constraint.class,"startsWith",new Class[]{Boolean.TYPE});
		endsWithRef=methodRef(Constraint.class,"endsWith",new Class[]{Boolean.TYPE});
		notRef=methodRef(Constraint.class,"not",new Class[]{});
		andRef=methodRef(Constraint.class,"and",new Class[]{Constraint.class});
		orRef=methodRef(Constraint.class,"or",new Class[]{Constraint.class});
		identityRef=methodRef(Constraint.class,"identity",new Class[]{});
	}
	
	private MethodRef methodRef(Class parent,String name,Class[] args) {
		try {
			return _editor.references().forMethod(parent.getMethod(platformName(name), args));
		} catch (Exception e) {
			throw new InstrumentationException(e);
		}
	}
}
