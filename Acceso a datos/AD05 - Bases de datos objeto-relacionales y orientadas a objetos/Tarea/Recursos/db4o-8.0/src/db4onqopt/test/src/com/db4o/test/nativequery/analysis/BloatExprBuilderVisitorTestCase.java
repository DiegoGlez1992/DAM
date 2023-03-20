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
package com.db4o.test.nativequery.analysis;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.tree.*;

import com.db4o.activation.*;
import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.core.*;
import com.db4o.nativequery.*;
import com.db4o.nativequery.analysis.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.expr.cmp.*;
import com.db4o.nativequery.expr.cmp.operand.*;
import com.db4o.test.nativequery.mocks.*;

import db4ounit.*;


public class BloatExprBuilderVisitorTestCase implements TestCase,TestLifeCycle {	
	private static final String INT_WRAPPED_FIELDNAME = "idWrap";
	private static final String BOOLEAN_FIELDNAME = "bool";
	private static final String BOOLEAN_WRAPPED_FIELDNAME = "boolWrapper";
	private static final String DATE_FIELDNAME = "date";
	private static final String INT_FIELDNAME = "id";
	private static final String FLOAT_FIELDNAME = "value";
	private static final String OTHER_FLOAT_FIELDNAME = "otherValue";
	private static final String DATA_FIELDNAME="next";
	private static final String STRING_FIELDNAME = "name";
	private final static boolean BOOLEAN_CMPVAL=false;
	private final static int INT_CMPVAL=42;
	private final static float FLOAT_CMPVAL=12.3f;
	private final static String STRING_CMPVAL="Test";
	private final static Integer INT_WRAPPER_CMPVAL=new Integer(INT_CMPVAL);
	private final static Boolean BOOLEAN_WRAPPER_CMPVAL=Boolean.TRUE;
	private final Integer intWrapperCmpVal=new Integer(INT_CMPVAL);
	
	private boolean boolMember=false;
	private String stringMember="foo";
	private int intMember=43;
	private float floatMember=47.11f;
	private int[] intArrayMember={};
	private Data[] objArrayMember={};
	private Date dateMember;
	private Data predicateData;

	private ClassFileLoader loader;
	private BloatLoaderContext _context;
	
	private int intMemberPlusOne() {
		return intMember+1;
	}

	private int sum(int a,int b) {
		return a+b;
	}
	
	public void setUp() throws Exception {
		ClassSource classSource = new Db4oClassSource(new ClassLoaderNativeClassFactory(Data.class.getClassLoader()));
		loader=new ClassFileLoader(classSource);
		_context=new BloatLoaderContext(loader);
	}
	
	// unconditional

	boolean sampleTrue(Data data) {
		return true;
	}

	public void testTrue() throws Exception {
		Assert.areEqual(BoolConstExpression.TRUE,expression("sampleTrue"));
	}

	boolean sampleFalse(Data data) {
		return false;
	}

	public void testFalse() throws Exception {
		// COR-2292
		assertInvalid("sampleFalse");
		// Assert.areEqual(BoolConstExpression.FALSE,expression("sampleFalse"));
	}

	// primitive identity

	// boolean
	
	boolean sampleFieldBooleanComp(Data data) {
		return data.bool;
	}

	public void testFieldBooleanComp() throws Exception {
		assertComparison("sampleFieldBooleanComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldBooleanConstantEqualsComp(Data data) {
		return data.bool==true;
	}

	public void testFieldBooleanConstantEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanConstantEqualsComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleUnnecessarilyComplicatedFieldBooleanConstantEqualsComp(Data data) {
		if(data.bool) {
			return true;
		}
		return false;
	}

	public void testUnnecessarilyComplicatedFieldBooleanConstantEqualsComp() throws Exception {
		assertComparison("sampleUnnecessarilyComplicatedFieldBooleanConstantEqualsComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldBooleanNotComp(Data data) {
		return !data.bool;
	}

	public void testFieldBooleanNotComp() throws Exception {
		assertComparison("sampleFieldBooleanNotComp",BOOLEAN_FIELDNAME,Boolean.FALSE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldBooleanEqualsComp(Data data) {
		return data.bool==boolMember;
	}

	public void testFieldBooleanEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanEqualsComp",
				BOOLEAN_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE, "boolMember", Boolean.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleBooleanFieldEqualsComp(Data data) {
		return boolMember==data.bool;
	}

	public void testBooleanFieldEqualsComp() throws Exception {
		assertComparison("sampleBooleanFieldEqualsComp",
				BOOLEAN_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE, "boolMember", Boolean.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleFieldBooleanNotEqualsComp(Data data) {
		return data.bool!=boolMember;
	}

	public void testFieldBooleanNotEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanNotEqualsComp",
				BOOLEAN_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE, "boolMember", Boolean.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				true);
	}

	boolean sampleBooleanFieldNotEqualsComp(Data data) {
		return boolMember!=data.bool;
	}

	public void testBooleanFieldNotEqualsComp() throws Exception {
		assertComparison("sampleBooleanFieldNotEqualsComp",
				BOOLEAN_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE, "boolMember", Boolean.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				true);
	}

	// int
	
	boolean sampleFieldIntZeroEqualsComp(Data data) {
		return data.id==0;
	}

	public void testFieldIntZeroEqualsComp() throws Exception {
		assertComparison("sampleFieldIntZeroEqualsComp",
				INT_FIELDNAME,
				new Integer(0),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleFieldIntEqualsComp(Data data) {
		return data.id==INT_CMPVAL;
	}

	public void testFieldIntEqualsComp() throws Exception {
		assertComparison("sampleFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldIntNotEqualsComp(Data data) {
		return data.id!=INT_CMPVAL;
	}

	public void testFieldIntNotEqualsComp() throws Exception {
		assertComparison("sampleFieldIntNotEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleIntFieldEqualsComp(Data data) {
		return INT_CMPVAL==data.id;
	}

	public void testIntFieldEqualsComp() throws Exception {
		assertComparison("sampleIntFieldEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIntFieldNotEqualsComp(Data data) {
		return INT_CMPVAL!=data.id;
	}

	public void testIntFieldNotEqualsComp() throws Exception {
		assertComparison("sampleIntFieldNotEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,true);
	}

	// float
	
	boolean sampleFieldFloatZeroEqualsComp(Data data) {
		return data.value==0.0f;
	}

	public void testFieldFloatZeroEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatZeroEqualsComp",FLOAT_FIELDNAME,new Float(0.0f),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldFloatZeroIntEqualsComp(Data data) {
		return data.value==0;
	}

	public void testFieldFloatZeroIntEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatZeroIntEqualsComp",FLOAT_FIELDNAME,new Float(0.0f),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldFloatEqualsComp(Data data) {
		return data.value==FLOAT_CMPVAL;
	}

	public void testFieldFloatEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFieldFloatNotEqualsComp(Data data) {
		return data.value!=FLOAT_CMPVAL;
	}

	public void testFieldFloatNotEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatNotEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleFloatFieldEqualsComp(Data data) {
		return FLOAT_CMPVAL==data.value;
	}

	public void testFloatFieldEqualsComp() throws Exception {
		assertComparison("sampleFloatFieldEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleFloatFieldNotEqualsComp(Data data) {
		return FLOAT_CMPVAL!=data.value;
	}

	public void testFloatFieldNotEqualsComp() throws Exception {
		assertComparison("sampleFloatFieldNotEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,true);
	}

	// object identity

	boolean sampleCandidateNull(Data data) {
		return data==null;
	}

	public void testCandidateNull() throws Exception {
		assertInvalid("sampleCandidateNull");
	}

	boolean sampleCandidateIdentity(Data data) {
		return data==predicateData;
	}

	// COR-1745
	public void testCandidateIdentity() throws Exception {
		assertInvalid("sampleCandidateIdentity");
	}

	boolean sampleIdentityNullComp(Data data) {
		return data.next==null;
	}

	public void testIdentityNullComp() throws Exception {
		assertComparison("sampleIdentityNullComp",DATA_FIELDNAME,null,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleNotIdentityNullComp(Data data) {
		return data.next!=null;
	}

	public void testNotIdentityNullComp() throws Exception {
		assertComparison("sampleNotIdentityNullComp",DATA_FIELDNAME,null,ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleIdentityComp(Data data) {
		return data.next==predicateData;
	}

	public void testIdentityComp() throws Exception {
		assertComparison("sampleIdentityComp",DATA_FIELDNAME,new FieldValue(PredicateFieldRoot.INSTANCE, fieldRef("predicateData", Data.class)),ComparisonOperator.REFERENCE_EQUALITY,false);
	}

	// primitive unequal comparison
	
	// int
	
	boolean sampleFieldIntSmallerComp(Data data) {
		return data.id<INT_CMPVAL;
	}

	public void testFieldIntSmallerComp() throws Exception {
		assertComparison("sampleFieldIntSmallerComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleFieldIntGreaterComp(Data data) {
		return data.id>INT_CMPVAL;
	}

	public void testFieldIntGreaterComp() throws Exception {
		assertComparison("sampleFieldIntGreaterComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleFieldIntSmallerEqualsComp(Data data) {
		return data.id<=INT_CMPVAL;
	}

	public void testFieldIntSmallerEqualsComp() throws Exception {
		assertComparison("sampleFieldIntSmallerEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldIntGreaterEqualsComp(Data data) {
		return data.id>=INT_CMPVAL;
	}

	public void testFieldIntGreaterEqualsComp() throws Exception {
		assertComparison("sampleFieldIntGreaterEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldSmallerComp(Data data) {
		return INT_CMPVAL<data.id;
	}

	public void testIntFieldSmallerComp() throws Exception {
		assertComparison("sampleIntFieldSmallerComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntFieldGreaterComp(Data data) {
		return INT_CMPVAL>data.id;
	}

	public void testIntFieldGreaterComp() throws Exception {
		assertComparison("sampleIntFieldGreaterComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleIntFieldSmallerEqualsComp(Data data) {
		return INT_CMPVAL<=data.id;
	}

	public void testIntFieldSmallerEqualsComp() throws Exception {
		assertComparison("sampleIntFieldSmallerEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,true);
	}

	boolean sampleIntFieldGreaterEqualsComp(Data data) {
		return INT_CMPVAL>=data.id;
	}

	public void testIntFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntFieldGreaterEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleFieldFloatSmallerComp(Data data) {
		return data.value<FLOAT_CMPVAL;
	}

	public void testFieldFloatSmallerComp() throws Exception {
		assertComparison("sampleFieldFloatSmallerComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleFieldFloatGreaterComp(Data data) {
		return data.value>FLOAT_CMPVAL;
	}

	public void testFieldFloatGreaterComp() throws Exception {
		assertComparison("sampleFieldFloatGreaterComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleFieldFloatSmallerEqualsComp(Data data) {
		return data.value<=FLOAT_CMPVAL;
	}

	public void testFieldFloatSmallerEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatSmallerEqualsComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleMemberIntSmallerEqualsComp(Data data) {
		return intMember<=data.id;
	}

	public void testMemberIntSmallerEqualsComp() throws Exception {
		assertComparison("sampleMemberIntSmallerEqualsComp",
				INT_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"intMember", Integer.TYPE),
				ComparisonOperator.SMALLER,
				true);
	}
	
	boolean sampleMemberFloatSmallerEqualsComp(Data data) {
		return floatMember<=data.value;
	}

	public void testMemberFloatSmallerEqualsComp() throws Exception {
		assertComparison("sampleMemberFloatSmallerEqualsComp",
				FLOAT_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"floatMember", Float.TYPE),
				ComparisonOperator.SMALLER,
				true);
	}
	
	// string equality
	
	boolean sampleFieldStringEqualsComp(Data data) {
		return data.name.equals(STRING_CMPVAL);
	}

	public void testFieldStringEqualsComp() throws Exception {
		assertComparison("sampleFieldStringEqualsComp",
				STRING_FIELDNAME,
				STRING_CMPVAL,
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.name);
	}

	public void testStringFieldEqualsComp() throws Exception {
		assertComparison("sampleStringFieldEqualsComp",
				STRING_FIELDNAME,
				STRING_CMPVAL,
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}
	
	// string-specific comparisons

	@decaf.Ignore
	boolean sampleFieldStringContains(Data data) {
		return data.name.contains(STRING_CMPVAL);
	}

	@decaf.Ignore
	public void testFieldStringContains() throws Exception {
		assertComparison("sampleFieldStringContains",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.CONTAINS,false);
	}
	
	@decaf.Ignore
	boolean sampleFieldStringContainsWrongWay(Data data) {
		return STRING_CMPVAL.contains(data.name);
	}

	@decaf.Ignore
	public void testFieldStringContainsWrongWay() throws Exception {
		assertInvalid("sampleFieldStringContainsWrongWay");
	}

	boolean sampleFieldStringStartsWith(Data data) {
		return data.name.startsWith(STRING_CMPVAL);
	}

	public void testFieldStringStartsWith() throws Exception {
		assertComparison("sampleFieldStringStartsWith",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.STARTS_WITH,false);
	}

	boolean sampleFieldStringStartsWithWrongWay(Data data) {
		return STRING_CMPVAL.startsWith(data.name);
	}

	public void testFieldStringStartsWithWrongWay() throws Exception {
		assertInvalid("sampleFieldStringStartsWithWrongWay");
	}

	boolean sampleFieldStringEndsWith(Data data) {
		return data.name.endsWith(STRING_CMPVAL);
	}

	public void testFieldStringEndsWith() throws Exception {
		assertComparison("sampleFieldStringEndsWith",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.ENDS_WITH,false);
	}

	boolean sampleFieldStringEndsWithWrongWay(Data data) {
		return STRING_CMPVAL.endsWith(data.name);
	}

	public void testFieldStringEndsWithWrongWay() throws Exception {
		assertInvalid("sampleFieldStringEndsWithWrongWay");
	}

	boolean sampleFieldStringToLowerCaseStartsWith(Data data) throws Exception {
		return data.getName().toLowerCase().startsWith(STRING_CMPVAL);
	}

	public void testFieldStringToLowerCaseStartsWith() throws Exception {
		assertInvalid("sampleFieldStringToLowerCaseStartsWith");
	}

	boolean sampleFieldStringLength(Data data) throws Exception {
		return data.getName().length() > 6;
	}

	public void testFieldStringLength() throws Exception {
		assertInvalid("sampleFieldStringLength");
	}

	// primitive wrapper equality

	boolean sampleFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(intWrapperCmpVal);
	}

	public void testFieldIntWrapperEqualsComp() throws Exception {
		assertComparison("sampleFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal", java.lang.Integer.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleIntWrapperFieldEqualsComp(Data data) {
		return intWrapperCmpVal.equals(data.getIdWrapped());
	}

	public void testIntWrapperFieldEqualsComp() throws Exception {
		assertComparison("sampleIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"intWrapperCmpVal",Integer.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}	
	
	boolean sampleFieldBooleanWrapperEqualsComp(Data data) {
		return data.boolWrapper.booleanValue();
	}

	public void testFieldBooleanWrapperEqualsComp() throws Exception {
		assertComparison("sampleFieldBooleanWrapperEqualsComp",
				BOOLEAN_WRAPPED_FIELDNAME,
				Boolean.TRUE,
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}
	
	// date equality

	boolean sampleFieldDateEqualsComp(Data data) {
		return data.getDate().equals(dateMember);
	}

	public void testFieldDateEqualsComp() throws Exception {
		assertComparison("sampleFieldDateEqualsComp",DATE_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"dateMember",java.util.Date.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleFieldDateCompareToComp(Data data) {
		return data.getDate().compareTo(dateMember) >= 0;
	}

	public void testFieldDateCompareToComp() throws Exception {
		assertComparison("sampleFieldDateCompareToComp",DATE_FIELDNAME,
				fieldValue(PredicateFieldRoot.INSTANCE,"dateMember",java.util.Date.class),
				ComparisonOperator.SMALLER,
				true);
	}

	// descend into primitive wrapper

	boolean sampleWrapperFieldValueIntSameComp(Data data) {
		return data.getIdWrapped().intValue()==INT_CMPVAL;
	}

	public void testWrapperFieldValueIntSameComp() throws Exception {
		assertComparison("sampleWrapperFieldValueIntSameComp",INT_WRAPPED_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}	

	boolean sampleNotValueBoolWrapperFieldSameComp(Data data) {
		return data.boolWrapper.booleanValue();
	}

	public void testNotValueBoolWrapperFieldSameComp() throws Exception {
		assertComparison("sampleNotValueBoolWrapperFieldSameComp",
				BOOLEAN_WRAPPED_FIELDNAME,
				Boolean.TRUE,
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}	

	// primitive field against wrapper

	boolean sampleFieldWrapperIntSameComp(Data data) {
		return data.getId()==INT_WRAPPER_CMPVAL.intValue();
	}

	public void testFieldWrapperIntSameComp() throws Exception {
		assertComparison("sampleFieldWrapperIntSameComp",
			INT_FIELDNAME,
			methodCallValue(
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				CallingConvention.VIRTUAL, 
				Integer.class, 
				"intValue",
				new Class[0], 
				new ComparisonOperand[0]
			),
			ComparisonOperator.VALUE_EQUALITY,
			false
		);
	}

	private StaticFieldRoot staticFieldRoot(final Class declaringClass) {
		return new StaticFieldRoot(new MockTypeRef(declaringClass));
	}	

	boolean sampleBoolWrapperFieldSameComp(Data data) {
		return data.bool==BOOLEAN_WRAPPER_CMPVAL.booleanValue();
	}

	public void testBoolWrapperFieldSameComp() throws Exception {
		assertComparison("sampleBoolWrapperFieldSameComp",
			BOOLEAN_FIELDNAME,
			methodCallValue(
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"BOOLEAN_WRAPPER_CMPVAL",
					Boolean.class),
				CallingConvention.VIRTUAL, 
				Boolean.class, 
				"booleanValue",
				new Class[0], 
				new ComparisonOperand[0]
				),
			ComparisonOperator.VALUE_EQUALITY,
			false);
	}	

	// wrapper comparison

	boolean sampleFieldWrapperIntCompToEquals(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)==0;
	}

	public void testFieldWrapperIntCompToEquals() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToEquals",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				ComparisonOperator.VALUE_EQUALITY,false);
	}	

	boolean sampleFieldWrapperIntCompToNotEquals(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)!=0;
	}

	public void testFieldWrapperIntCompToNotEquals() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToNotEquals",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
					ComparisonOperator.VALUE_EQUALITY,
					true);
	}	

	boolean sampleFieldWrapperIntCompToGreater(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)>0;
	}

	public void testFieldWrapperIntCompToGreater() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToGreater",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				ComparisonOperator.GREATER,
				false);
	}	

	boolean sampleFieldWrapperIntCompToLE(Data data) {
		return data.getIdWrapped().compareTo(INT_WRAPPER_CMPVAL)<=0;
	}

	public void testFieldWrapperIntCompToLE() throws Exception {
		assertComparison("sampleFieldWrapperIntCompToLE",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(BloatExprBuilderVisitorTestCase.class),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				ComparisonOperator.GREATER,
				true);
	}	

	//static member comparison

	boolean sampleStaticFieldIntWrapperEqualsComp(Data data) {
		return data.getIdWrapped().equals(INT_WRAPPER_CMPVAL);
	}

	public void testStaticFieldIntWrapperEqualsComp() throws Exception {
		//assertInvalid("sampleStaticFieldIntWrapperEqualsComp");
		assertComparison("sampleStaticFieldIntWrapperEqualsComp",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(getClass()),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleStaticIntWrapperFieldEqualsComp(Data data) {
		return INT_WRAPPER_CMPVAL.equals(data.getIdWrapped());
	}

	public void testStaticIntWrapperFieldEqualsComp() throws Exception {
		//assertInvalid("sampleStaticIntWrapperFieldEqualsComp");
		assertComparison("sampleStaticIntWrapperFieldEqualsComp",INT_WRAPPED_FIELDNAME,
				fieldValue(
					staticFieldRoot(getClass()),
					"INT_WRAPPER_CMPVAL",
					Integer.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}	
	
	// getter

	boolean sampleGetterBoolComp(Data data) {
		return data.getBool();
	}

	public void testGetterBoolComp() throws Exception {
		assertComparison("sampleGetterBoolComp",BOOLEAN_FIELDNAME,Boolean.TRUE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleBoolGetterNotEqualsComp(Data data) {
		return BOOLEAN_CMPVAL!=data.getBool();
	}

	public void _testBoolGetterNotEqualsComp() throws Exception {
		assertComparison("sampleBoolGetterNotEqualsComp",BOOLEAN_FIELDNAME,Boolean.valueOf(!BOOLEAN_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterIntEqualsComp(Data data) {
		return data.getId()==INT_CMPVAL;
	}

	public void testGetterIntEqualsComp() throws Exception {
		assertComparison("sampleGetterIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterStringEqualsComp(Data data) {
		return data.getName().equals(STRING_CMPVAL);
	}

	public void testGetterStringEqualsComp() throws Exception {
		assertComparison("sampleGetterStringEqualsComp",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterFloatSmallerComp(Data data) {
		return data.getValue()<FLOAT_CMPVAL;
	}

	public void testGetterFloatSmallerComp() throws Exception {
		assertComparison("sampleGetterFloatSmallerComp",FLOAT_FIELDNAME,new Float(FLOAT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	// field cascade

	boolean sampleCascadeFieldStringEqualsComp(Data data) {
		return data.next.name.equals(STRING_CMPVAL);
	}

	public void testCascadeFieldStringEqualsComp() throws Exception {
		assertComparison("sampleCascadeFieldStringEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterCascadeIntFieldEqualsComp(Data data) {
		return INT_CMPVAL==data.getNext().getId();
	}

	public void testGetterCascadeIntFieldEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeIntFieldEqualsComp",new String[]{DATA_FIELDNAME,INT_FIELDNAME},new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleCascadeStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.next.name);
	}

	public void testCascadeStringFieldEqualsComp() throws Exception {
		assertComparison("sampleCascadeStringFieldEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterCascadeStringFieldEqualsComp(Data data) {
		return STRING_CMPVAL.equals(data.getNext().getName());
	}

	public void testGetterCascadeStringFieldEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeStringFieldEqualsComp",new String[]{DATA_FIELDNAME,STRING_FIELDNAME},STRING_CMPVAL,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleGetterCascadeFloatFieldGreaterEqualsComp(Data data) {
		return FLOAT_CMPVAL>=data.getNext().getValue();
	}

	public void testGetterCascadeFloatFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleGetterCascadeFloatFieldGreaterEqualsComp",new String[]{DATA_FIELDNAME,FLOAT_FIELDNAME},new Float(FLOAT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	// member field comparison

	boolean sampleFieldIntMemberEqualsComp(Data data) {
		return data.getId()==intMember;
	}

	public void testFieldIntMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldIntMemberEqualsComp",new String[]{INT_FIELDNAME},
				fieldValue(PredicateFieldRoot.INSTANCE,"intMember",Integer.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleIntMemberFieldGreaterEqualsComp(Data data) {
		return intMember>=data.getId();
	}

	public void testIntMemberFieldGreaterEqualsComp() throws Exception {
		assertComparison("sampleIntMemberFieldGreaterEqualsComp",new String[]{INT_FIELDNAME},
				fieldValue(PredicateFieldRoot.INSTANCE,"intMember",Integer.TYPE),
				ComparisonOperator.GREATER,
				true);
	}

	boolean sampleFieldStringMemberEqualsComp(Data data) {
		return data.getName().equals(stringMember);
	}

	public void testFieldStringMemberEqualsComp() throws Exception {
		assertComparison("sampleFieldStringMemberEqualsComp",new String[]{STRING_FIELDNAME},
				fieldValue(PredicateFieldRoot.INSTANCE,"stringMember",String.class),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleFieldFloatMemberNotEqualsComp(Data data) {
		return data.getValue()!=floatMember;
	}

	public void testFieldFloatMemberNotEqualsComp() throws Exception {
		assertComparison("sampleFieldFloatMemberNotEqualsComp",new String[]{FLOAT_FIELDNAME},
				fieldValue(PredicateFieldRoot.INSTANCE,"floatMember",Float.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				true);
	}

	boolean sampleFloatMemberFieldNotEqualsComp(Data data) {
		return floatMember!=data.getValue();
	}

	public void testFloatMemberFieldNotEqualsComp() throws Exception {
		assertComparison("sampleFloatMemberFieldNotEqualsComp",new String[]{FLOAT_FIELDNAME},
				fieldValue(PredicateFieldRoot.INSTANCE,"floatMember",Float.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				true);
	}

	// negations
	
	boolean sampleStringNot(Data data) {
		return !STRING_CMPVAL.equals(data.name);
	}
	
	public void testStringNot() throws Exception {
		assertComparison("sampleStringNot",STRING_FIELDNAME,STRING_CMPVAL,ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleIntEqualsNot(Data data) {
		return !(data.id==INT_CMPVAL);
	}
	
	public void testIntEqualsNot() throws Exception {
		assertComparison("sampleIntEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleIntNotEqualsNot(Data data) {
		return !(data.id!=INT_CMPVAL);
	}
	
	public void testIntNotEqualsNot() throws Exception {
		assertComparison("sampleIntNotEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIntGreaterNot(Data data) {
		return !(data.id>INT_CMPVAL);
	}
	
	public void testIntGreaterNot() throws Exception {
		assertComparison("sampleIntGreaterNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,true);
	}

	boolean sampleIntSmallerEqualsNot(Data data) {
		return !(data.id<=INT_CMPVAL);
	}
	
	public void testIntSmallerEqualsNot() throws Exception {
		assertComparison("sampleIntSmallerEqualsNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntNotNot(Data data) {
		return !(!(data.id<INT_CMPVAL));
	}
	
	public void testIntNotNot() throws Exception {
		assertComparison("sampleIntNotNot",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	// conjunctions

	boolean sampleBoolBoolAnd(Data data) {
		return !data.getBool()&&data.getBool();
	}
	
	public void testBoolBoolAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleBoolBoolAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{BOOLEAN_FIELDNAME},Boolean.FALSE,ComparisonOperator.VALUE_EQUALITY,false);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{BOOLEAN_FIELDNAME},Boolean.TRUE,ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIntIntAnd(Data data) {
		return (data.id>42)&&(data.id<100);
	}
	
	public void testIntIntAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleIntIntAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.GREATER,false);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{"id"},new Integer(100),ComparisonOperator.SMALLER,false);
	}

	boolean sampleStringIntOr(Data data) {
		return (data.name.equals("Foo"))||(data.id==42);
	}

	public void testStringIntOr() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleStringIntOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.VALUE_EQUALITY,false);
		ComparisonExpression right=(ComparisonExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(right,new String[]{"id"},new Integer(42),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIntStringNotOr(Data data) {
		return !((data.id==42)||(data.name.equals("Foo")));
	}

	public void testIntStringNotOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleIntStringNotOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.VALUE_EQUALITY,true);
		NQOptimizationAssertUtil.assertComparison(expr.right(),new String[]{"name"},"Foo",ComparisonOperator.VALUE_EQUALITY,true);
	}

	boolean sampleOuterOrInnerAnd(Data data) {
		return (data.id==42)&&(data.getName().equals("Bar"))||(data.name.equals("Foo"));
	}

	public void testOuterOrInnerAnd() throws Exception {
		OrExpression expr = (OrExpression)expression("sampleOuterOrInnerAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"name"},"Foo",ComparisonOperator.VALUE_EQUALITY,false);
		AndExpression andExpr=(AndExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(andExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.VALUE_EQUALITY,false);
		NQOptimizationAssertUtil.assertComparison(andExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleOuterAndInnerOr(Data data) {
		return ((data.id<42)||(data.getName().equals("Bar")))&&(data.getId()>10);
	}

	public void testOuterAndInnerOr() throws Exception {
		AndExpression expr = (AndExpression)expression("sampleOuterAndInnerOr");
		NQOptimizationAssertUtil.assertComparison(expr.left(),new String[]{"id"},new Integer(10),ComparisonOperator.GREATER,false);
		OrExpression orExpr=(OrExpression)expr.right();
		NQOptimizationAssertUtil.assertComparison(orExpr.left(),new String[]{"id"},new Integer(42),ComparisonOperator.SMALLER,false);
		NQOptimizationAssertUtil.assertComparison(orExpr.right(),new String[]{"name"},"Bar",ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleDateCompareToAnd(Data data) {
		return data.date.compareTo(dateMember) >= 0 && data.date.compareTo(dateMember) <= 0;
	}
	
	public void testDateCompareToAnd() throws Exception {
		AndExpression expr = (AndExpression) expression("sampleDateCompareToAnd");
		NQOptimizationAssertUtil.assertComparison(expr.left(), new String[]{DATE_FIELDNAME}, fieldValue(PredicateFieldRoot.INSTANCE,"dateMember",java.util.Date.class), ComparisonOperator.SMALLER, true);
		NQOptimizationAssertUtil.assertComparison(expr.right(), new String[]{DATE_FIELDNAME}, fieldValue(PredicateFieldRoot.INSTANCE,"dateMember",java.util.Date.class), ComparisonOperator.GREATER, true);
	}
	
	// arithmetic
	
	boolean sampleSanityIntAdd(Data data) {
		return data.id<INT_CMPVAL+INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntAdd() throws Exception {
		assertComparison("sampleSanityIntAdd",INT_FIELDNAME,new Integer(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleSanityIntMultiply(Data data) {
		return data.id<2*INT_CMPVAL; // compile time constant!
	}
	
	public void testSanityIntMultiply() throws Exception {
		assertComparison("sampleSanityIntMultiply",INT_FIELDNAME,new Integer(2*INT_CMPVAL),ComparisonOperator.SMALLER,false);
	}

	boolean sampleMemberIntMultiply(Data data) {
		return data.id<2*intMember;
	}
	
	public void testMemberIntMultiply() throws Exception {
		assertComparison("sampleMemberIntMultiply",INT_FIELDNAME,new ArithmeticExpression(new ConstValue(new Integer(2)),intMemberFieldValue(),ArithmeticOperator.MULTIPLY),ComparisonOperator.SMALLER,false);
	}

	boolean sampleMemberIntModulo(Data data) {
		return data.id < (2 % intMember);
	}
	
	public void testMemberIntModulo() throws Exception {
		assertComparison("sampleMemberIntModulo",INT_FIELDNAME,new ArithmeticExpression(new ConstValue(new Integer(2)),intMemberFieldValue(),ArithmeticOperator.MODULO),ComparisonOperator.SMALLER,false);
	}

	boolean sampleMemberStringUnknownOperatorInMethodCall(Data data) {
		return data.name.contains(String.valueOf(intMember & 42));
	}
	
	public void testMemberStringUnknownOperatorInMethodCall() throws Exception {
		assertInvalid("sampleMemberStringUnknownOperatorInMethodCall");
	}

	private FieldValue intMemberFieldValue() {
		return fieldValue(PredicateFieldRoot.INSTANCE,"intMember",Integer.TYPE);
	}

	boolean sampleIntMemberDivide(Data data) {
		return data.id>intMember/2;
	}
	
	public void testIntMemberDivide() throws Exception {
		assertComparison("sampleIntMemberDivide",INT_FIELDNAME,new ArithmeticExpression(intMemberFieldValue(),new ConstValue(new Integer(2)),ArithmeticOperator.DIVIDE),ComparisonOperator.GREATER,false);
	}

	boolean sampleIntMemberMemberAdd(Data data) {
		return data.id==intMember+intMember;
	}
	
	public void testIntMemberMemberAdd() throws Exception {
		assertComparison("sampleIntMemberMemberAdd",INT_FIELDNAME,new ArithmeticExpression(intMemberFieldValue(),intMemberFieldValue(),ArithmeticOperator.ADD),ComparisonOperator.VALUE_EQUALITY,false);
	}

	// array access
	
	boolean sampleIntArrayAccess(Data data) {
		return data.id==intArrayMember[0];
	}

	public void testIntArrayAccess() throws Exception {
		final PredicateFieldRoot instance = PredicateFieldRoot.INSTANCE;
		assertComparison("sampleIntArrayAccess",
				"id",
				new ArrayAccessValue(
					fieldValue(instance, "intArrayMember", int[].class),
					new ConstValue(new Integer(0))),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	boolean sampleObjectArrayAccess(Data data) {
		return data.next.id==objArrayMember[0].id;
	}

	public void testObjectArrayAccess() throws Exception {
		assertComparison("sampleObjectArrayAccess",
				new String[]{"next","id"},
				fieldValue(
					new ArrayAccessValue(
						fieldValue(PredicateFieldRoot.INSTANCE,"objArrayMember", Data[].class),
						new ConstValue(new Integer(0))),
					"id",
					Integer.TYPE),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	
	// non-candidate method calls

	boolean sampleIntAddInPredicateMethod(Data data) {
		return data.getId()==intMemberPlusOne();
	}

	public void testIntAddInPredicateMethod() throws Exception {
		assertComparison("sampleIntAddInPredicateMethod",
			INT_FIELDNAME,
			methodCallValue(
				PredicateFieldRoot.INSTANCE,
				CallingConvention.VIRTUAL, 
				BloatExprBuilderVisitorTestCase.class, 
				"intMemberPlusOne",
				new Class[0], 
				new ComparisonOperand[0]
			),
			ComparisonOperator.VALUE_EQUALITY,
			false);
	}

	boolean sampleStaticMethodCall(Data data) {
		return data.id==Integer.parseInt(stringMember);
	}

	public void testStaticMethodCall() throws Exception {
		assertComparison("sampleStaticMethodCall",
			INT_FIELDNAME,
			methodCallValue(
				staticFieldRoot(Integer.class),
				CallingConvention.STATIC, 
				Integer.class, 
				"parseInt",
				new Class[] { String.class }, 
				new ComparisonOperand[] { fieldValue(PredicateFieldRoot.INSTANCE, "stringMember", String.class) }
			),
			ComparisonOperator.VALUE_EQUALITY,
			false);
	}

	boolean sampleTwoParamMethodCall(Data data) {
		return data.id==sum(intMember,0);
	}

	public void testTwoParamMethodCall() throws Exception {
		assertComparison("sampleTwoParamMethodCall",
				INT_FIELDNAME,
				methodCallValue(
					PredicateFieldRoot.INSTANCE,
					CallingConvention.VIRTUAL, 
					BloatExprBuilderVisitorTestCase.class, 
					"sum",
					new Class[] { Integer.TYPE, Integer. TYPE }, 
					new ComparisonOperand[] { fieldValue(PredicateFieldRoot.INSTANCE, "intMember", Integer.TYPE), new ConstValue(new Integer(0)) }
				),
				ComparisonOperator.VALUE_EQUALITY,
				false);
	}

	// multiple methods with the same name
	
	boolean sampleTimesValueMethodEqualsComp(Data data) {
		return data.getValue(INT_CMPVAL)==floatMember;
	}

	public void testTimesValueMethodEqualsComp() throws Exception {
		assertComparison("sampleTimesValueMethodEqualsComp",new String[]{OTHER_FLOAT_FIELDNAME},floatMemberFieldValue(),ComparisonOperator.VALUE_EQUALITY,false);
	}

	private FieldValue floatMemberFieldValue() {
		return fieldValue(PredicateFieldRoot.INSTANCE,"floatMember",Float.TYPE);
	}
	
	// not applicable
	
	// TODO: definitely applicable - fix!
	boolean sampleInvalidOtherMemberEqualsComp(Data data) {
		return stringMember.equals(STRING_CMPVAL);
	}

	public void testInvalidOtherMemberEqualsComp() throws Exception {
		assertInvalid("sampleInvalidOtherMemberEqualsComp");
	}

	boolean sampleInvalidLocalVarComp(Data data) {
		Data next=data.next;
		return next.bool;
	}
	
	public void testInvalidLocalVarComp() throws Exception {
		assertInvalid("sampleInvalidLocalVarComp");
	}

	boolean sampleInvalidLocalVarCombinedComp(Data data) {
		Data next=data.next;
		return next.bool && data.bool;
	}
		
	public void testInvalidLocalVarCombinedComp() throws Exception {
		assertInvalid("sampleInvalidLocalVarCombinedComp");
	}
	
	boolean sampleInvalidNotOptimizableMethodCallCombined(Data data) {
		return data.getName().indexOf(STRING_CMPVAL)>=0&&data.bool;
	}
	
	public void testInvalidNotOptimizableMethodCallCombined() throws Exception {
		assertInvalid("sampleInvalidNotOptimizableMethodCallCombined");
	}

	boolean sampleInvalidOtherMemberSameComp(Data data) {
		return stringMember==STRING_CMPVAL;
	}

	public void testInvalidOtherMemberSameComp() throws Exception {
		assertInvalid("sampleInvalidOtherMemberSameComp");
	}

	boolean sampleInvalidCandidateMemberArithmetic(Data data) {
		return data.id-1==INT_CMPVAL;
	}

	public void testInvalidCandidateMemberArithmetic() throws Exception {
		assertInvalid("sampleInvalidCandidateMemberArithmetic");
	}

	boolean sampleInvalidTemporaryStorage(Data data) {
		int val=INT_CMPVAL-1;
		return data.id==val;
	}

	public void testInvalidTemporaryStorage() throws Exception {
		assertInvalid("sampleInvalidTemporaryStorage");
	}

	boolean sampleInvalidMethodCall(Data data) {
		data.someMethod();
		return true;
	}

	public void testInvalidMethodCall() throws Exception {
		assertInvalid("sampleInvalidMethodCall");
	}

	boolean sampleInvalidConstructorCall(Data data) {
		return data.next==new Data().getNext();
	}

	public void testInvalidConstructorCall() throws Exception {
		assertInvalid("sampleInvalidConstructorCall");
	}

	boolean sampleSimpleObjectComparison(Data data) {
		return data.equals(new Data());
	}

	public void testSimpleObjectComparison() throws Exception {
		assertInvalid("sampleSimpleObjectComparison");
	}

	boolean sampleSimpleFieldObjectComparison(Data data) {
		return data.next.equals(new Data());
	}

	public void testSimpleFieldObjectComparison() throws Exception {
		assertInvalid("sampleSimpleFieldObjectComparison");
	}

	boolean sampleSimpleFieldObjectIdentityComparison(Data data) {
		return data.next.equals(data.next);
	}

	public void testSimpleFieldObjectIdentityComparison() throws Exception {
		assertInvalid("sampleSimpleFieldObjectIdentityComparison");
	}

	boolean sampleCandEqualsNullComparison(Data data) {
		return data.equals(null);
	}

	public void testCandEqualsNullComparison() throws Exception {
		assertInvalid("sampleCandEqualsNullComparison");
	}

	boolean sampleCandIdentityObjectComparison(Data data) {
		return data.equals(data);
	}

	public void testCandIdentityObjectComparison() throws Exception {
		assertInvalid("sampleCandIdentityObjectComparison");
	}

	boolean sampleRecursiveCall(Data data) {
		return sampleRecursiveCall(data);
	}

	public void testRecursiveCall() throws Exception {
		assertInvalid("sampleRecursiveCall");
	}

	boolean sampleCandidateIntArrayAccess(Data data) {
		return data.intArray[0]==0;
	}

	public void testCandidateIntArrayAccess() throws Exception {
		assertInvalid("sampleCandidateIntArrayAccess");
	}

	boolean sampleCandidateObjectArrayAccess(Data data) {
		return data.objArray[0].id==0;
	}

	public void testCandidateObjectArrayAccess() throws Exception {
		assertInvalid("sampleCandidateObjectArrayAccess");
	}

	boolean sampleCandidateParamMethodCall(Data data) {
		return data.id==sum(data.id,0);
	}

	public void testCandidateParamMethodCall() throws Exception {
		assertInvalid("sampleCandidateParamMethodCall");
	}

	boolean sampleCandidateParamStaticMethodCall(Data data) {
		return data.id==Integer.parseInt(data.name);
	}

	public void testCandidateParamStaticMethodCall() throws Exception {
		assertInvalid("sampleCandidateParamStaticMethodCall");
	}

	boolean sampleSwitch(Data data) {
		switch(data.id) {
			case 0:
			case 1:
			case 2:
			case 4:
				return true;
			default:
				return false;
		}
	}

	public void testSwitch() throws Exception {
		assertInvalid("sampleSwitch");
	}

	boolean sampleStringAppend(Data data) {
		return data.name.equals(stringMember+"X");
	}

	public void testStringAppend() throws Exception {
		assertInvalid("sampleStringAppend");
	}

	boolean sampleExternalWrapperComp(Data data) {
		return INT_WRAPPER_CMPVAL.compareTo(INT_WRAPPER_CMPVAL)==0;
	}

	public void testExternalWrapperComp() throws Exception {
		assertInvalid("sampleExternalWrapperComp");
	}

	boolean sampleNotApplicableIfCondition(Data data) {
		if(stringMember.equals("XXX")) {
			return data.getName().equals(STRING_CMPVAL);
		}
		else {
			return false;
		}
	}

	public void testNotApplicableIfCondition() throws Exception {
		assertInvalid("sampleNotApplicableIfCondition");
	}

	boolean sampleNotApplicableIfStringAppendCondition(Data data) {
		if(stringMember.equals(stringMember+"X")) {
			return data.getName().equals(STRING_CMPVAL);
		}
		else {
			return false;
		}
	}

	public void testNotApplicableIfStringAppendCondition() throws Exception {
		assertInvalid("sampleNotApplicableIfStringAppendCondition");
	}

	boolean sampleNotApplicableSideEffectIfThenBranch(Data data) {
		if(data.getName().equals("foo")) {
			intMember+=data.getId();
			return true;
		}
		else {
			return false;
		}
	}

	public void testIOSideEffect() throws Exception {
		assertInvalid("sampleIOSideEffect");
	}

	boolean sampleIOSideEffect(Data data) {
		System.out.println("illegal i/o");
		return data.id == 2;
	}

	// activate calls
	
	boolean sampleActivateThenFieldIntEqualsComp(Base data) {
		data.activate(ActivationPurpose.READ);
		return data.id==INT_CMPVAL;
	}

	public void testActivateThenFieldIntEqualsComp() throws Exception {
		assertComparison("sampleActivateThenFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleOverriddenActivateThenFieldIntEqualsComp(Data data) {
		data.activate(ActivationPurpose.READ);
		return data.id==INT_CMPVAL;
	}

	public void testOverriddenActivateThenFieldIntEqualsComp() throws Exception {
		assertComparison("sampleOverriddenActivateThenFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIndirectOverriddenActivateThenFieldIntEqualsComp(Data data) {
		data.activate();
		return data.id==INT_CMPVAL;
	}

	public void testIndirectOverriddenActivateThenFieldIntEqualsComp() throws Exception {
		assertComparison("sampleIndirectOverriddenActivateThenFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleWrongActivateThenFieldIntEqualsComp(Data data) {
		data.activate("foo");
		return data.id==INT_CMPVAL;
	}

	public void testWrongActivateThenFieldIntEqualsComp() throws Exception {
		assertInvalid("sampleWrongActivateThenFieldIntEqualsComp");
	}

	boolean sampleStaticIndirectActivateThenFieldIntEqualsComp(Base data) {
		Data.activate(data);
		return data.id==INT_CMPVAL;
	}

	public void testStaticIndirectActivateThenFieldIntEqualsComp() throws Exception {
		assertComparison("sampleStaticIndirectActivateThenFieldIntEqualsComp",INT_FIELDNAME,new Integer(INT_CMPVAL),ComparisonOperator.VALUE_EQUALITY,false);
	}

	boolean sampleIllegalPrivateFieldAccess(Data data) {
		return predicateData.sameSecret(data);
	}

	public void testIllegalPrivateFieldAccess() throws Exception {
		assertInvalid("sampleIllegalPrivateFieldAccess");
	}

	// internal
	
	private void assertComparison(String methodName, String fieldName,Object value, ComparisonOperator op,boolean negated) {
		assertComparison(methodName,new String[]{fieldName},value,op,negated);
	}

	private void assertComparison(String methodName, String[] fieldNames,Object value, ComparisonOperator op,boolean negated) {
		try {
			Expression expr = expression(methodName);
			NQOptimizationAssertUtil.assertComparison(expr, fieldNames, value, op, negated);
		} catch (ClassNotFoundException e) {
			Assert.fail(e.getMessage());
		}
	}

	private void assertInvalid(String methodName) throws ClassNotFoundException {
		Expression expression = expression(methodName);
		if(expression!=null) {
			System.err.println(expression);
		}
		Assert.isNull(expression);
	}
	
	private Expression expression(String methodName) throws ClassNotFoundException {
		BloatExprBuilderVisitor visitor = new BloatExprBuilderVisitor(_context);	
		FlowGraph flowGraph=_context.flowGraph(getClass().getName(),methodName);
		if(NQDebug.LOG) {
			flowGraph.visit(new PrintVisitor());
//		flowGraph.visit(new TreeStructureVisitor());
		}
		flowGraph.visit(visitor);
		Expression expr = visitor.expression();
		if(NQDebug.LOG) {
			System.out.println(expr);
		}
		return expr;		
	}

	private FieldValue fieldValue(final ComparisonOperandAnchor instance,
			final String fieldName, final Class fieldType) {
		return new FieldValue(instance, fieldRef(fieldName, fieldType));
	}

	private FieldRef fieldRef(String fieldName, Class fieldType) {
		return new MockFieldRef(fieldName, new MockTypeRef(fieldType));
	}

	private MethodCallValue methodCallValue(final ComparisonOperandAnchor parent, CallingConvention convention, Class clazz,
			final String methodName, final Class[] argTypes, ComparisonOperand[] args) throws NoSuchMethodException {
		return new MethodCallValue(methodRef(clazz, methodName, argTypes), convention, parent, args);
	}

	private MethodRef methodRef(Class clazz, String name, Class[] argTypes) throws NoSuchMethodException {
		return new MockMethodRef(clazz.getDeclaredMethod(name, argTypes));
	}


	public void tearDown() throws Exception {
		_context = null;
	}
	
	public static void main(String[] args) throws Exception {
//		java.lang.reflect.Method method=BloatExprBuilderVisitorTestCase.class.getMethod("testFieldWrapperIntCompToEquals",new Class[]{});
//		Test[] tests={
//				new TestMethod(new BloatExprBuilderVisitorTestCase(),method)
//		};
//		TestSuite suite=new TestSuite(tests);
//		new TestRunner(suite).run();
		new ConsoleTestRunner(BloatExprBuilderVisitorTestCase.class).run();
	}
}
