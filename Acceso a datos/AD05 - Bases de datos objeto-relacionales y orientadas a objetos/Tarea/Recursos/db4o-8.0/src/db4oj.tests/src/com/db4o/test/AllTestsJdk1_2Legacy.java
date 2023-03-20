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
package com.db4o.test;

import com.db4o.test.legacy.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class AllTestsJdk1_2Legacy extends AllTestsLegacy {
	
    public static void main(String[] args) {
        new AllTestsJdk1_2Legacy(new String[]{}).runWithException();
    }
    
    public AllTestsJdk1_2Legacy(String[] testcasenames) {
    	super(testcasenames);
    }
    
    protected void addTestSuites(TestSuite suites) {
    	super.addTestSuites(suites);
    	suites.add(new TestSuite() {
			public Class[] tests() {
				return new Class[]{
					ArrayListInHashMap.class,
					CascadeToHashMap.class,
				   
				    ExtendsHashMap.class,
				    ExternalBlobs.class,
		            KeepCollectionContent.class,

		            TransientClone.class,
				    TreeSetCustomComparable.class,
				};
			}
    	});
	}

}
