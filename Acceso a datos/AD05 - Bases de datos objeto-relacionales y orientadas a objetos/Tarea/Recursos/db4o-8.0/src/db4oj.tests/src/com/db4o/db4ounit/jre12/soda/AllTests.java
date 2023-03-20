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
package com.db4o.db4ounit.jre12.soda;

import com.db4o.db4ounit.common.util.*;
import com.db4o.db4ounit.jre12.soda.collections.*;
import com.db4o.db4ounit.jre12.soda.deepOR.*;
import com.db4o.db4ounit.jre12.soda.experiments.*;

import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class AllTests  extends Db4oTestSuite {
	protected Class[] testCases() {
		 
		return Db4oUnitTestUtil.mergeClasses(new Class[]{
				HashtableModifiedUpdateDepthTestCase.class,
				ObjectSetListAPITestSuite.class,
				NullElementsInArrayTestCase.class,
				STArrayListTTestCase.class,
				STArrayListUTestCase.class,
				STHashSetTTestCase.class,
				STHashSetUTestCase.class,
				STHashtableETTestCase.class,
				STHashtableEUTestCase.class,
				STHashtableTTestCase.class,
				STHashtableUTestCase.class,
				STLinkedListTTestCase.class,
				STLinkedListUTestCase.class,
				STOwnCollectionTTestCase.class,
				STTreeSetTTestCase.class,
				STTreeSetUTestCase.class,
				STVectorTTestCase.class,
				STVectorUTestCase.class,
				STOrContainsTestCase.class,
				STCurrentTestCase.class,
				STIdentityEvaluationTestCase.class,
				STNullOnPathTestCase.class,
		}, vectorQbeTestCases());
	}
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}
	
	private Class[] vectorQbeTestCases () {
		
		if(true){
			
			//  QBE with vector and Hashtable is not expressible as SODA and 
			//  it will no longer work with new collection Typehandlers

			return new Class[] {};
		}
		
		return new Class[] {

				STVectorDTestCase.class,
				STVectorEDTestCase.class,
				STHashtableDTestCase.class,
				STHashtableEDTestCase.class,

		};
	}
	
}
