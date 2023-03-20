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
package com.db4o.test.nativequery.expr.build;

import com.db4o.nativequery.expr.AndExpression;
import com.db4o.nativequery.expr.BoolConstExpression;
import com.db4o.nativequery.expr.ComparisonExpression;
import com.db4o.nativequery.expr.Expression;
import com.db4o.nativequery.expr.NotExpression;
import com.db4o.nativequery.expr.OrExpression;
import com.db4o.nativequery.expr.build.ExpressionBuilder;
import com.db4o.nativequery.expr.cmp.ComparisonOperator;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.test.nativequery.mocks.*;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public class ExpressionBuilderTestCase implements TestCase, TestLifeCycle {
	private MockComparisonExpressionBuilder mockBuilder;
	private ExpressionBuilder builder;
	private Expression expr;
	private Expression other;
	
	public void setUp() throws Exception {
		mockBuilder=new MockComparisonExpressionBuilder();
		builder=new ExpressionBuilder();
		expr=mockBuilder.build();
		other=mockBuilder.build();
	}
	
	public void testConstant() {
		Assert.areSame(BoolConstExpression.TRUE,builder.constant(Boolean.TRUE));
		Assert.areSame(BoolConstExpression.FALSE,builder.constant(Boolean.FALSE));
		// TODO: Move to const expr (or expr) test
		Assert.areEqual(BoolConstExpression.FALSE,BoolConstExpression.expr(false));
		Assert.areEqual(BoolConstExpression.TRUE,BoolConstExpression.expr(true));
	}

	public void testNot() {
		Assert.areSame(BoolConstExpression.FALSE,builder.not(BoolConstExpression.TRUE));
		Assert.areSame(BoolConstExpression.TRUE,builder.not(BoolConstExpression.FALSE));
		Assert.areSame(BoolConstExpression.TRUE,builder.not(builder.not(BoolConstExpression.TRUE)));
		Assert.areSame(BoolConstExpression.FALSE,builder.not(builder.not(BoolConstExpression.FALSE)));
		Assert.areEqual(new NotExpression(expr),builder.not(expr));
		Assert.areEqual(new ComparisonExpression(fieldValue(CandidateFieldRoot.INSTANCE,"foo"),new ConstValue(Boolean.TRUE),ComparisonOperator.VALUE_EQUALITY),
					builder.not(new ComparisonExpression(fieldValue(CandidateFieldRoot.INSTANCE,"foo"),new ConstValue(Boolean.FALSE),ComparisonOperator.VALUE_EQUALITY)));
	}
	
	private FieldValue fieldValue(ComparisonOperandAnchor instance, String name) {
		return new FieldValue(instance, new MockFieldRef(name));
	}

	public void testAnd() {
		Assert.areSame(BoolConstExpression.FALSE,builder.and(BoolConstExpression.FALSE,expr));
		Assert.areSame(BoolConstExpression.FALSE,builder.and(expr,BoolConstExpression.FALSE));
		Assert.areSame(expr,builder.and(BoolConstExpression.TRUE,expr));
		Assert.areSame(expr,builder.and(expr,BoolConstExpression.TRUE));
		Assert.areEqual(expr,builder.and(expr,expr));
		Assert.areEqual(BoolConstExpression.FALSE,builder.and(expr,builder.not(expr)));
		Assert.areEqual(new AndExpression(expr,other),builder.and(expr,other));
	}

	public void testOr() {
		Assert.areSame(BoolConstExpression.TRUE,builder.or(BoolConstExpression.TRUE,expr));
		Assert.areSame(BoolConstExpression.TRUE,builder.or(expr,BoolConstExpression.TRUE));
		Assert.areSame(expr,builder.or(BoolConstExpression.FALSE,expr));
		Assert.areSame(expr,builder.or(expr,BoolConstExpression.FALSE));
		Assert.areSame(expr,builder.or(expr,expr));
		Assert.areEqual(BoolConstExpression.TRUE,builder.or(expr,builder.not(expr)));
		Assert.areEqual(new OrExpression(expr,other),builder.or(expr,other));
	}
	
	public void testIfThenElse() {
		Assert.areSame(expr,builder.ifThenElse(BoolConstExpression.TRUE,expr,other));
		Assert.areSame(other,builder.ifThenElse(BoolConstExpression.FALSE,expr,other));
		Assert.areSame(BoolConstExpression.TRUE,builder.ifThenElse(expr,BoolConstExpression.TRUE,BoolConstExpression.TRUE));
		Assert.areSame(BoolConstExpression.FALSE,builder.ifThenElse(expr,BoolConstExpression.FALSE,BoolConstExpression.FALSE));
		Assert.areSame(expr,builder.ifThenElse(expr,BoolConstExpression.TRUE,BoolConstExpression.FALSE));
		Assert.areEqual(new NotExpression(expr),builder.ifThenElse(expr,BoolConstExpression.FALSE,BoolConstExpression.TRUE));
		Assert.areEqual(builder.or(expr,other),builder.ifThenElse(expr,BoolConstExpression.TRUE,other));
		// FIXME more compund boolean constraint tests
		//Assert.areEqual(builder.or(expr,builder.and(builder.not(expr),other)),builder.ifThenElse(expr,BoolConstExpression.TRUE,other));
	}
	
	public void testCombined() {
		Expression a=mockBuilder.build();
		Expression b=mockBuilder.build();
		Expression exp1=builder.and(a,builder.constant(Boolean.TRUE));
		Expression exp2=builder.and(BoolConstExpression.FALSE,builder.not(b));
		Expression exp=builder.or(exp1,exp2);
		Assert.areEqual(a,exp);
	}

	public void tearDown() throws Exception {
	}
}
