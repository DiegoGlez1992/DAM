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
package com.db4o.test.nativequery;

import java.lang.reflect.*;
import java.util.*;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.*;
import com.db4o.nativequery.expr.*;
import com.db4o.nativequery.main.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class NQRegressionTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	private final static boolean RUN_LOADTIME = true;
	
	private static final String CSTR = "Cc";
	private static final String BSTR = "Ba";
	private static final String ASTR = "Aa";
	public final static Integer INTWRAPPER=new Integer(1);
	public final static Date DATE=new Date(0);
	private final static Integer PRIVATE_INTWRAPPER=new Integer(1);
	
	
	public static class ConstantHolder {
		public static Data _prevData = null;
		
		public static Data prevData() {
			return _prevData;
		}
	}

	public static abstract class Base {
		int id;
		Integer idWrap;
		
		public Base(int id) {
			this.id=id;
			idWrap=new Integer(id);
		}

		public int getId() {
			return id;
		}
	}
	
	private static class Other extends Base {
		public Other() {
			super(1);
		}
	}
	
	public static class Data extends Base implements Activatable {
		boolean bool;
		float value;
		String name;
		Data prev;
		int id2;
		Boolean boolWrap;
		java.util.Date curDate;
		
		public Data(int id, boolean bool,float value, String name,Data prev, int id2, java.util.Date curDate) {
			super(id);
			this.bool=bool;
			this.boolWrap=new Boolean(bool);
			this.value=value;
			this.name = name;
			this.prev=prev;
			this.id2=id2;
			this.curDate = curDate;
		}

		public float getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
		
		public boolean getBool() {
			return bool;
		}

		public Data getPrev() {
			return prev;
		}

		public void activate(ActivationPurpose purpose) {
		}

		public void bind(Activator activator) {
		}	
	}

	public static void main(String[] args) {
		Iterator4 suite=new Db4oTestSuiteBuilder(new Db4oSolo(),NQRegressionTestCase.class).iterator();
		new ConsoleTestRunner(suite).run();
	}

	public void store() {
		java.util.Date date1 = new java.util.Date(0);
		java.util.Date date2 = new java.util.Date();
		
		Data a=new Data(1,false,1.1f,ASTR,null, 0, date1);
		Data b=new Data(2,false,1.1f,BSTR,a, Integer.MIN_VALUE, date1);
		Data c=new Data(3,true,2.2f,CSTR,b, Integer.MIN_VALUE, date1);
		Data cc=new Data(3,false,3.3f,CSTR,null, Integer.MIN_VALUE, date2);
		ObjectContainer db=db();
		db.store(a);
		db.store(b);
		db.store(c);
		db.store(cc);
		db.store(new Other());
		ConstantHolder._prevData = a;
	}
	
	private abstract static class ExpectingPredicate<E> extends Predicate<E> {

		public ExpectingPredicate() {
		}
		
		public ExpectingPredicate(Class<? extends E> extentType) {
			super(extentType);
		}

		public abstract int expected();
	}	

	private static final class UnconditionalUntypedPredicate extends ExpectingPredicate<Object> {
		public int expected() { return 5;}

		public boolean match(Object candidate) {
			return true;
		}
	}

	public void testUnconditionalUntyped() throws Exception {
		assertNQResult(new UnconditionalUntypedPredicate());
	}

	private static class UnconditionalBaseTypedPredicate extends ExpectingPredicate<Base> {
		public int expected() { return 5;}

		public boolean match(Base candidate) {
			return true;
		}
	}

	public void testUnconditionalBaseTyped() throws Exception {
		assertNQResult(new UnconditionalBaseTypedPredicate());
	}

	private static final class UnconditionalDataTypedPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 4;}

		public boolean match(Data candidate) {
			return true;
		}
	}

	public void testUnconditionalDataTyped() throws Exception {
		assertNQResult(new UnconditionalDataTypedPredicate());
	}
	
	private static final class UnconditionalUntypedFalsePredicate extends ExpectingPredicate<Object> {
		public int expected() { return 0; }

		public boolean match(Object candidate) {
			return false;
		}
	}

	// COR-2292
	public void _testUnconditionalUntypedFalse() throws Exception {
		assertNQResult(new UnconditionalUntypedFalsePredicate());
	}

	private static final class BoolFieldPredicate extends ExpectingPredicate<Data> {
		public int expected() {
			return 1;
		}

		public boolean match(Data candidate) {
			return candidate.bool;
		}
	}

	public void testBoolField() throws Exception {
		assertNQResult(new BoolFieldPredicate());
	}

	private static final class NegatedBoolFieldPredicate extends ExpectingPredicate<Data> {
		public int expected() {
			return 3;
		}

		public boolean match(Data candidate) {
			return !candidate.bool;
		}
	}

	public void testNegatedBoolField() throws Exception {
		assertNQResult(new NegatedBoolFieldPredicate());
	}

	private static final class DataIntFieldConstantComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id2==0;
		}
	}

	public void testDataIntFieldConstantComparison() throws Exception {
		assertNQResult(new DataIntFieldConstantComparisonPredicate());
	}

	private static final class BaseIntFieldConstantOneComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id==1;
		}
	}

	public void testBaseIntFieldConstantOneComparison() throws Exception {
		assertNQResult(new BaseIntFieldConstantOneComparisonPredicate());
	}

	private static final class BaseIntFieldConstantThreeComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id==3;
		}
	}

	public void testBaseIntFieldConstantThreeComparison() throws Exception {
		assertNQResult(new BaseIntFieldConstantThreeComparisonPredicate());
	}

	private static final class FloatFieldConstantOneComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.value==1.1f;
		}
	}

	public void testFloatFieldConstantOneComparison() throws Exception {
		assertNQResult(new FloatFieldConstantOneComparisonPredicate());
	}

	private static final class FloatFieldConstantThreeComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.value==3.3f;
		}
	}

	public void testFloatFieldConstantThreeComparison() throws Exception {
		assertNQResult(new FloatFieldConstantThreeComparisonPredicate());
	}

	private static final class StringFieldEqualsAComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.name.equals(ASTR);
		}
	}

	public void testStringFieldEqualsAComparison() throws Exception {
		assertNQResult(new StringFieldEqualsAComparisonPredicate());
	}

	private static final class StringFieldEqualsCComparisonPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.name.equals(CSTR);
		}
	}

	public void testStringFieldEqualsCComparison() throws Exception {
		assertNQResult(new StringFieldEqualsCComparisonPredicate());
	}

	private static final class StringFieldStartsWithCPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.name.startsWith("C");
		}
	}

	public void testStringFieldStartsWithC() throws Exception {
		assertNQResult(new StringFieldStartsWithCPredicate());
	}

	private static final class StringFieldStartsWithAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 0;}

		public boolean match(Data candidate) {
			return candidate.name.startsWith("a");
		}
	}

	public void testStringFieldStartsWithA() throws Exception {
		assertNQResult(new StringFieldStartsWithAPredicate());
	}

	private static final class StringFieldEndsWithLowerCaseAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.name.endsWith("a");
		}
	}

	public void testStringFieldEndsWithLowerCaseA() throws Exception {
		assertNQResult(new StringFieldEndsWithLowerCaseAPredicate());
	}

	private static final class StringFieldEndsWithUpperCaseAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 0;}

		public boolean match(Data candidate) {
			return candidate.name.endsWith("A");
		}
	}

	public void testStringFieldEndsWithUpperCaseA() throws Exception {
		assertNQResult(new StringFieldEndsWithUpperCaseAPredicate());
	}

	private static final class StringFieldStartsWithCNegatedPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return !candidate.name.startsWith("C");
		}
	}

	public void testStringFieldStartsWithCNegated() throws Exception {
		assertNQResult(new StringFieldStartsWithCNegatedPredicate());
	}

	private static final class IntFieldSmallerTwoPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id<2;
		}
	}

	public void testIntFieldSmallerTwo() throws Exception {
		assertNQResult(new IntFieldSmallerTwoPredicate());
	}

	private static final class IntFieldGreaterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id>2;
		}
	}

	public void testIntFieldGreater() throws Exception {
		assertNQResult(new IntFieldGreaterPredicate());
	}

	private static final class IntFieldSmallerEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id<=2;
		}
	}

	public void testIntFieldSmallerEquals() throws Exception {
		assertNQResult(new IntFieldSmallerEqualsPredicate());
	}

	private static final class IntFieldGreaterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.id>=2;
		}
	}

	public void testIntFieldGreaterEquals() throws Exception {
		assertNQResult(new IntFieldGreaterEqualsPredicate());
	}

	private static final class FloatFieldGreaterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.value>2.9f;
		}
	}

	public void testFloatFieldGreater() throws Exception {
		assertNQResult(new FloatFieldGreaterPredicate());
	}

	private static final class FloatFieldSmallerEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return 1.5f >= candidate.value;
		}
	}

	public void testFloatFieldSmallerEquals() throws Exception {
		assertNQResult(new FloatFieldSmallerEqualsPredicate());
	}

	private static final class IntFieldEqualsFloatPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id==1.0f;
		}
	}

	public void testIntFieldEqualsFloat() throws Exception {
		assertNQResult(new IntFieldEqualsFloatPredicate());
	}

	private static final class IntFieldNotEqualsFloatPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.id!=1.0f;
		}
	}

	public void testIntFieldNotEqualsFloat() throws Exception {
		assertNQResult(new IntFieldNotEqualsFloatPredicate());
	}

	private static final class FloatFieldNotEqualsIntPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 4;}

		public boolean match(Data candidate) {
			return candidate.value!=1;
		}
	}

	public void testFloatFieldNotEqualsInt() throws Exception {
		assertNQResult(new FloatFieldNotEqualsIntPredicate());
	}

	private static final class DescendPrevIdGreaterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.getPrev()!=null&&candidate.getPrev().getId()>=1;
		}
	}

	public void testDescendPrevIdGreaterEquals() throws Exception {
		assertNQResult(new DescendPrevIdGreaterEqualsPredicate());
	}

	private static final class DescendPrevNameEqualsConstantPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return (candidate.getPrev()!=null)&&(BSTR.equals(candidate.getPrev().getName()));
		}
	}

	public void testDescendPrevNameEqualsConstant() throws Exception {
		assertNQResult(new DescendPrevNameEqualsConstantPredicate());
	}

	private static final class DescendPrevNameEqualsLiteralPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 0;}

		public boolean match(Data candidate) {
			return candidate.getPrev()!=null&&candidate.getPrev().getName().equals("");
		}
	}

	public void testDescendPrevNameEqualsLiteral() throws Exception {
		assertNQResult(new DescendPrevNameEqualsLiteralPredicate());
	}

	private static final class IntGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getId()==2;
		}
	}

	public void testIntGetterEquals() throws Exception {
		assertNQResult(new IntGetterEqualsPredicate());
	}

	private static final class IntGetterSmallerPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getId()<2;
		}
	}

	public void testIntGetterSmaller() throws Exception {
		assertNQResult(new IntGetterSmallerPredicate());
	}

	private static final class IntGetterGreaterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.getId()>2;
		}
	}

	public void testIntGetterGreater() throws Exception {
		assertNQResult(new IntGetterGreaterPredicate());
	}

	private static final class IntGetterSmallerEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.getId()<=2;
		}
	}

	public void testIntGetterSmallerEquals() throws Exception {
		assertNQResult(new IntGetterSmallerEqualsPredicate());
	}

	private static final class IntGetterGreaterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.getId()>=2;
		}
	}

	public void testIntGetterGreaterEquals() throws Exception {
		assertNQResult(new IntGetterGreaterEqualsPredicate());
	}

	private static final class StringGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.getName().equals(CSTR);
		}
	}

	public void testStringGetterEquals() throws Exception {
		assertNQResult(new StringGetterEqualsPredicate());
	}

	private static final class NotIntFieldEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return !(candidate.id==1);
		}
	}

	public void testNotIntFieldEquals() throws Exception {
		assertNQResult(new NotIntFieldEqualsPredicate());
	}

	private static final class NotIntGetterGreaterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return !(candidate.getId()>2);
		}
	}

	public void testNotIntGetterGreater() throws Exception {
		assertNQResult(new NotIntGetterGreaterPredicate());
	}

	private static final class NotStringGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return !(candidate.getName().equals(CSTR));
		}
	}

	public void testNotStringGetterEquals() throws Exception {
		assertNQResult(new NotStringGetterEqualsPredicate());
	}

	private static final class BoolFieldAndNotBoolGetterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 0;}

		public boolean match(Data candidate) {
			return candidate.bool&&!candidate.getBool();
		}
	}

	public void testBoolFieldAndNotBoolGetter() throws Exception {
		assertNQResult(new BoolFieldAndNotBoolGetterPredicate());
	}

	private static final class IdFieldGreaterAndNameGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return (candidate.id>1)&&candidate.getName().equals(CSTR);
		}
	}

	public void testIdFieldGreaterAndNameGetterEquals() throws Exception {
		assertNQResult(new IdFieldGreaterAndNameGetterEqualsPredicate());
	}

	private static final class IdFieldGreaterAndIdGetterSmallerEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return (candidate.id>1)&&(candidate.getId()<=2);
		}
	}

	public void testIdFieldGreaterAndIdGetterSmallerEquals() throws Exception {
		assertNQResult(new IdFieldGreaterAndIdGetterSmallerEqualsPredicate());
	}

	private static final class IdFieldGreaterAndIdGetterSmallerPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 0;}

		public boolean match(Data candidate) {
			return (candidate.id>1)&&(candidate.getId()<1);
		}
	}

	public void testIdFieldGreaterAndIdGetterSmaller() throws Exception {
		assertNQResult(new IdFieldGreaterAndIdGetterSmallerPredicate());
	}

	private static final class BoolFieldOrIdGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.bool||candidate.getId()==1;
		}
	}

	public void testBoolFieldOrIdGetterEquals() throws Exception {
		assertNQResult(new BoolFieldOrIdGetterEqualsPredicate());
	}

	private static final class IdFieldEqualsOrNameGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return (candidate.id==1)||candidate.getName().equals(CSTR);
		}
	}

	public void testIdFieldEqualsOrNameGetterEquals() throws Exception {
		assertNQResult(new IdFieldEqualsOrNameGetterEqualsPredicate());
	}

	private static final class IdFieldGreaterOrIdGetterSmallerEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 4;}

		public boolean match(Data candidate) {
			return (candidate.id>1)||(candidate.getId()<=2);
		}
	}

	public void testIdFieldGreaterOrIdGetterSmallerEquals() throws Exception {
		assertNQResult(new IdFieldGreaterOrIdGetterSmallerEqualsPredicate());
	}

	private static final class IdFieldSmallerEqualsOrIdGetterGreaterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return (candidate.id<=1)||(candidate.getId()>=3);
		}
	}

	public void testIdFieldSmallerEqualsOrIdGetterGreaterEquals() throws Exception {
		assertNQResult(new IdFieldSmallerEqualsOrIdGetterGreaterEqualsPredicate());
	}

	private static final class IdFieldGreaterEqualsOrNameGetterEqualsAndIdGetterSmallerPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return ((candidate.id>=1)||candidate.getName().equals(CSTR))&&candidate.getId()<3;
		}
	}

	public void testIdFieldGreaterEqualsOrNameGetterEqualsAndIdGetterSmaller() throws Exception {
		assertNQResult(new IdFieldGreaterEqualsOrNameGetterEqualsAndIdGetterSmallerPredicate());
	}

	private static final class IdFieldEqualsOrIdGetterSmallerEqualsAndNotNameGetterEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return ((candidate.id==2)||candidate.getId()<=1)&&!candidate.getName().equals(BSTR);
		}
	}

	public void testIdFieldEqualsOrIdGetterSmallerEqualsAndNotNameGetterEquals() throws Exception {
		assertNQResult(new IdFieldEqualsOrIdGetterSmallerEqualsAndNotNameGetterEqualsPredicate());
	}

	private static final class IdFieldGreaterEqualsPredicateFieldPredicate extends ExpectingPredicate<Data> {
		private int id=2;

		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.id>=id;
		}
	}

	public void testIdFieldGreaterEqualsPredicateField() throws Exception {
		assertNQResult(new IdFieldGreaterEqualsPredicateFieldPredicate());
	}

	private static final class NameGetterEqualsPredicateFieldPredicate extends ExpectingPredicate<Data> {
		private String name=BSTR;

		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getName().equals(name);
		}
	}

	public void testNameGetterEqualsPredicateField() throws Exception {
		assertNQResult(new NameGetterEqualsPredicateFieldPredicate());
	}

	private static final class IdFieldGreaterEqualsPredicateFieldPlusOnePredicate extends ExpectingPredicate<Data> {
		private int id=2;

		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id>=id+1;
		}
	}

	public void testIdFieldGreaterEqualsPredicateFieldPlusOne() throws Exception {
		assertNQResult(new IdFieldGreaterEqualsPredicateFieldPlusOnePredicate());
	}

	private static final class IdFieldEqualsIdFieldModuloThreePredicate extends ExpectingPredicate<Data> {
		private int val = 3;
		
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id == 7 % val;
		}
	}

	public void testIdFieldEqualsIdFieldModuloThreePredicate() throws Exception {
		assertNQResult(new IdFieldEqualsIdFieldModuloThreePredicate());
	}

	private static final class IdFieldGreaterEqualsPredicateArithmeticMethodPredicate extends ExpectingPredicate<Data> {
		private int factor=2;

		private int calc() {
			return factor+1;
		}

		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id>=calc();
		}
	}

	public void testIdFieldGreaterEqualsPredicateArithmeticMethod() throws Exception {
		assertNQResult(new IdFieldGreaterEqualsPredicateArithmeticMethodPredicate());
	}

	private static final class FloatFieldEqualsPredicateArithmeticMethodPredicate extends ExpectingPredicate<Data> {
		private float predFactor=2.0f;

		private float calc() {
			return predFactor*1.1f;
		}

		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getValue()==calc();
		}
	}

	public void testFloatFieldEqualsPredicateArithmeticMethod() throws Exception {
		assertNQResult(new FloatFieldEqualsPredicateArithmeticMethodPredicate());
	}

	private static final class ForceExtentPredicate extends ExpectingPredicate<Object> {
		private ForceExtentPredicate() {
			super(Data.class);
		}

		public int expected() { return 1;}

		public boolean match(Object candidate) {
			return ((Data)candidate).getId()==1;
		}
	}

	public void testForceExtent() throws Exception {
		assertNQResult(new ForceExtentPredicate());
	}

	private static final class IdFieldEqualsArrayElementPredicate extends ExpectingPredicate<Data> {
		private int[] data={0,1,2,3,4};

		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.id==data[3];
		}
	}

	public void testIdFieldEqualsArrayElement() throws Exception {
		assertNQResult(new IdFieldEqualsArrayElementPredicate());
	}

	private static final class DataFieldEqIdentityArrayElementPredicate extends ExpectingPredicate<Data> {
		private Data[] data={null,null,null,null};

		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.prev==data[3];
		}
	}

	public void testDataFieldEqIdentityArrayElement() throws Exception {
		assertNQResult(new DataFieldEqIdentityArrayElementPredicate());
	}

	private static final class IdFieldEqualsPredicateTwoArgsArithmeticMethodPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		private int sum(int a,int b) {
			return a+b;
		}

		public boolean match(Data candidate) {
			return candidate.id==sum(3,0);
		}
	}

	public void testIdFieldEqualsPredicateTwoArgsArithmeticMethod() throws Exception {
		assertNQResult(new IdFieldEqualsPredicateTwoArgsArithmeticMethodPredicate());
	}

	private static final class BoolWrapperFieldPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.boolWrap.booleanValue();
		}
	}

	public void testBoolWrapperField() throws Exception {
		assertNQResult(new BoolWrapperFieldPredicate());
	}

	private static final class IntWrapperFieldEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return NQRegressionTestCase.INTWRAPPER.equals(candidate.idWrap);
		}
	}

	public void testIntWrapperFieldEquals() throws Exception {
		assertNQResult(new IntWrapperFieldEqualsPredicate());
	}

	private static final class IntWrapperFieldValueEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.idWrap.intValue()==1;
		}
	}

	public void testIntWrapperFieldValueEquals() throws Exception {
		assertNQResult(new IntWrapperFieldValueEqualsPredicate());
	}

	private static final class IntWrapperFieldEqualsValuePredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.id==INTWRAPPER.intValue();
		}
	}

	public void testIntWrapperFieldEqualsValue() throws Exception {
		assertNQResult(new IntWrapperFieldEqualsValuePredicate());
	}

	private static final class IntWrapperFieldCompareToPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.idWrap.compareTo(INTWRAPPER)>0;
		}
	}

	public void testIntWrapperFieldCompareTo() throws Exception {
		assertNQResult(new IntWrapperFieldCompareToPredicate());
	}

	private static final class DateFieldEqualsPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return candidate.curDate.equals(DATE);
		}
	}

	public void testDateFieldEquals() throws Exception {
		assertNQResult(new DateFieldEqualsPredicate());
	}

	private static final class DateFieldCompareToAndPredicate extends ExpectingPredicate<Data> {
		private static Date MAX_DATE = new Date(Long.MAX_VALUE);
		
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.curDate.compareTo(DATE) > 0 && candidate.curDate.compareTo(MAX_DATE) < 0;
		}
	}

	public void testDateFieldCompareToAnd() throws Exception {
		assertNQResult(new DateFieldCompareToAndPredicate());
	}

	private static final class IntWrapperEqualsStaticAccessorPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return NQRegressionTestCase.PRIVATE_INTWRAPPER.equals(candidate.idWrap);
		}
	}

	public void testIntWrapperEqualsStaticAccessor() throws Exception {
		assertNQResult(new IntWrapperEqualsStaticAccessorPredicate());
	}

	private static final class IntWrapperEqualsStaticPredicateFieldPredicate extends ExpectingPredicate<Data> {
		private final static Integer PREDICATE_INTWRAPPER=new Integer(1);

		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return PREDICATE_INTWRAPPER.equals(candidate.idWrap);
		}
	}

	public void testIntWrapperEqualsStaticPredicateField() throws Exception {
		assertNQResult(new IntWrapperEqualsStaticPredicateFieldPredicate());
	}

	private static final class ActivationCallPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			candidate.activate(ActivationPurpose.READ);
			return candidate.id2==0;
		}
	}

	public void testActivationCall() throws Exception {
		assertNQResult(new ActivationCallPredicate());
	}

	private static final class FloatFieldGreaterDoublePredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.value>2.9d;
		}
	}

	// SODA coercion is broken for greater/smaller comparisons
	public void _testFloatFieldGreaterDouble() throws Exception {
		assertNQResult(new FloatFieldGreaterDoublePredicate());
	}

	private static final class DataGetterEqIdentityStaticMemberPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getPrev() == ConstantHolder._prevData;
		}
	}

	public void testDataGetterEqIdentityStaticMember() throws Exception {
		assertNQResult(new DataGetterEqIdentityStaticMemberPredicate());
	}

	private static final class DataGetterEqIdentityStaticGetterPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.getPrev() == ConstantHolder.prevData();
		}
	}

	public void testDataGetterEqIdentityStaticGetter() throws Exception {
		assertNQResult(new DataGetterEqIdentityStaticGetterPredicate());
	}

	@decaf.Remove(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	private static final class NameFieldContainsLowerCaseAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.name.contains("a");
		}
	}
	
	@decaf.Ignore(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	public void testNameFieldContainsLowerCaseA() throws Exception {
		assertNQResult(new NameFieldContainsLowerCaseAPredicate());
	}

	@decaf.Remove(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	private static final class NameFieldContainsUpperCaseAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 1;}

		public boolean match(Data candidate) {
			return candidate.name.contains("A");
		}
	}

	@decaf.Ignore(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	public void testNameFieldContainsUpperCaseA() throws Exception {
		assertNQResult(new NameFieldContainsUpperCaseAPredicate());
	}

	@decaf.Remove(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	private static final class NameFieldContainsUpperCaseCPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 2;}

		public boolean match(Data candidate) {
			return candidate.name.contains("C");
		}
	}

	@decaf.Ignore(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	public void testNameFieldContainsUpperCaseC() throws Exception {
		assertNQResult(new NameFieldContainsUpperCaseCPredicate());
	}

	@decaf.Remove(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	private static final class NameFieldNotContainsUpperCaseAPredicate extends ExpectingPredicate<Data> {
		public int expected() { return 3;}

		public boolean match(Data candidate) {
			return !candidate.name.contains("A");
		}
	}

	@decaf.Ignore(platforms={decaf.Platform.JDK11, decaf.Platform.JDK12})
	public void testNameFieldNotContainsUpperCaseA() throws Exception {
		assertNQResult(new NameFieldNotContainsUpperCaseAPredicate());
	}

	
	private void assertNQResult(final ExpectingPredicate predicate) throws Exception {
		ObjectContainer db=db();
		ConstantHolder._prevData = (Data)db.queryByExample(ConstantHolder._prevData).next();
		Db4oQueryExecutionListener listener = new Db4oQueryExecutionListener() {
			private int run=0;
			
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				if(run<2) {
					Assert.areEqual(info.predicate(),predicate);
				}
				String expMsg=null;
				switch(run) {
					case 0:
						expMsg=NativeQueryHandler.UNOPTIMIZED;
						Assert.isNull(info.optimized());
						break;
					case 1:
						expMsg=NativeQueryHandler.DYNOPTIMIZED;
						Assert.isTrue(info.optimized() instanceof Expression);
						break;
					case 2:
						expMsg=NativeQueryHandler.PREOPTIMIZED;
						Assert.isNull(info.optimized());
						break;
				}
				Assert.areEqual(expMsg,info.message());
				run++;
			}
		};
		((InternalObjectContainer)db).getNativeQueryHandler().addListener(listener);
		db.ext().configure().optimizeNativeQueries(false);
		ObjectSet raw=db.query(predicate);
		db.ext().configure().optimizeNativeQueries(true);
		ObjectSet optimized=db.query(predicate);
		if(!raw.equals(optimized)) {
			System.out.println("RAW");
			raw.reset();
			while(raw.hasNext()) {
				System.out.println(raw.next());
			}
			raw.reset();
			System.out.println("OPT");
			optimized.reset();
			while(optimized.hasNext()) {
				System.out.println(optimized.next());
			}
			optimized.reset();
		}
		Assert.areEqual(raw,optimized);
		Assert.areEqual(predicate.expected(),raw.size());

		if(RUN_LOADTIME) {
			runLoadTimeTest(db, predicate, raw, optimized);
		} 
		((InternalObjectContainer)db).getNativeQueryHandler().clearListeners();
		db.ext().configure().optimizeNativeQueries(true);
	}

	private void runLoadTimeTest(ObjectContainer db, final ExpectingPredicate predicate, ObjectSet raw, ObjectSet optimized) throws Exception {
		db.ext().configure().optimizeNativeQueries(false);
		NQEnhancingClassloader loader=new NQEnhancingClassloader(getClass().getClassLoader()) {
			@Override
			protected boolean mustDelegate(String name) {
				return super.mustDelegate(name) 
					|| name.equals(ConstantHolder.class.getName())
					|| name.equals(Base.class.getName())
					|| name.equals(Data.class.getName());
			}
		};
		Class filterClass=loader.loadClass(predicate.getClass().getName());
		Constructor constr=null;
		Object[] args=null;
		try {
			constr=filterClass.getDeclaredConstructor(new Class[0]);
			args=new Object[0];
		} catch(NoSuchMethodException exc) {
			constr=filterClass.getDeclaredConstructor(new Class[]{Class.class});
			args=new Object[]{Data.class};
		}
		constr.setAccessible(true);
		Predicate clPredicate=(Predicate)constr.newInstance(args);
		
		ObjectSet preoptimized=db.query(clPredicate);
		
		Assert.areEqual(predicate.expected(),preoptimized.size());
		Assert.areEqual(raw,preoptimized);
		Assert.areEqual(optimized,preoptimized);
	}
}
