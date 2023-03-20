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
package com.db4o.db4ounit.common.ta.nested;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * TODO: This test case will fail when run against JDK1.3/JDK1.4 (though it will run green against
 * JDK1.2 and JDK1.5+) because the synthetic "this$0" field is final.
 * See http://developer.db4o.com/Resources/view.aspx/Reference/Implementation_Strategies/Type_Handling/Final_Fields/Final_Fields_Specifics
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class NestedClassesTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA { 

	public static void main(String[] args) {
		new NestedClassesTestCase().runSolo();
	}
	
	protected void store() throws Exception {
		OuterClass outerObject = new OuterClass();
		outerObject._foo = 42;
		
		final Activatable objOne = (Activatable)outerObject.createInnerObject();
		store(objOne);
		
		final Activatable objTwo = (Activatable)outerObject.createInnerObject();
		store(objTwo);
	}

	
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
	}
	
	public void test() throws Exception {
		String property = System.getProperty("java.version");
        if (property != null && property.startsWith("1.3")) {
			System.err.println("IGNORED: " + getClass() + " will fail when run against JDK1.3/JDK1.4");
			return;
		}
		ObjectSet query = db().query(OuterClass.InnerClass.class);
		while(query.hasNext()){
			OuterClass.InnerClass innerObject = (OuterClass.InnerClass) query.next();
			Assert.isNull(innerObject.getOuterObjectWithoutActivation());
			Assert.areEqual(42, innerObject.getOuterObject().foo());
		}
	}
	
}
