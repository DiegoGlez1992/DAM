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
package com.db4o.nativequery.optimization;

import com.db4o.foundation.*;
import com.db4o.instrumentation.api.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.query.*;

public class SODAQueryBuilder {		
	private static class SODAQueryVisitor implements ExpressionVisitor {
		private Object _predicate;
		private Query _query;
		private Constraint _constraint;
		private NativeClassFactory _classSource;
		private ReferenceResolver _referenceResolver;

		SODAQueryVisitor(Query query, Object predicate, NativeClassFactory classSource, ReferenceResolver referenceResolver) {
			_query=query;
			_predicate = predicate;
			_classSource = classSource;
			_referenceResolver = referenceResolver;
		}
		
		public void visit(AndExpression expression) {
			expression.left().accept(this);
			Constraint left=_constraint;
			expression.right().accept(this);
			left.and(_constraint);
			_constraint=left;
		}

		public void visit(BoolConstExpression expression) {
		}

		public void visit(OrExpression expression) {
			expression.left().accept(this);
			Constraint left=_constraint;
			expression.right().accept(this);
			left.or(_constraint);
			_constraint=left;
		}

		public void visit(ComparisonExpression expression) {
			Query subQuery = descend(expression.left());
			ComparisonQueryGeneratingVisitor visitor = new ComparisonQueryGeneratingVisitor(
					_predicate, _classSource, _referenceResolver);
			expression.right().accept(visitor);
			_constraint = subQuery.constrain(visitor.value());
			ComparisonOperator op = expression.op();
			if (op.equals(ComparisonOperator.VALUE_EQUALITY)) {
				return;
			}
			if (op.equals(ComparisonOperator.REFERENCE_EQUALITY)) {
				_constraint.identity();
				return;
			}
			if (op.equals(ComparisonOperator.GREATER)) {
				_constraint.greater();
				return;
			}
			if (op.equals(ComparisonOperator.SMALLER)) {
				_constraint.smaller();
				return;
			} 
			if (op.equals(ComparisonOperator.CONTAINS)) {
				_constraint.contains();
				return;
			}
			if (op.equals(ComparisonOperator.STARTS_WITH)) {
				_constraint.startsWith(true);
				return;
			}
			if (op.equals(ComparisonOperator.ENDS_WITH)) {
				_constraint.endsWith(true);
				return;
			}
			throw new RuntimeException("Can't handle constraint: "
					+ op);
		}

		private Query descend(final FieldValue left) {
			Query subQuery = _query;
			Iterator4 fieldNameIterator = fieldNames(left);
			while (fieldNameIterator.moveNext()) {
				subQuery = subQuery.descend((String) fieldNameIterator
						.current());
			}
			return subQuery;
		}

		public void visit(NotExpression expression) {
			expression.expr().accept(this);
			_constraint.not();
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
		
	public void optimizeQuery(Expression expr, Query query, Object predicate, NativeClassFactory classSource, ReferenceResolver referenceResolver) {
		expr.accept(new SODAQueryVisitor(query, predicate, classSource, referenceResolver));
	}	
}
