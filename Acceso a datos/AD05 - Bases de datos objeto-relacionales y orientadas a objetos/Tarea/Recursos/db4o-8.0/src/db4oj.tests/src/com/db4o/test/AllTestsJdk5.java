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

import com.db4o.*;
import com.db4o.test.nativequery.*;


/**
 */
@decaf.Ignore
public class AllTestsJdk5 extends AllTestsJdk1_2{
	
    public static void main(String[] args) {
    	if(args!=null&&args.length==1&&args[0].equals("-withExceptions")) {
            new AllTestsJdk5().runWithException();
            return;
    	}
        new AllTestsJdk5().run();
    }
    
    @Override
    protected void addTestSuites(TestSuite suites) {
    	super.addTestSuites(suites);
    	suites.add(new Jdk5TestSuite());
        if(Db4oVersion.MAJOR >= 5){
            suites.add(new NativeQueryTestSuite());
        }
	}

}
