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
package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.ext.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
public class CompositeDb4oExceptionTestCase implements TestCase{
	
	public void test(){
		try{
			throwCompositeException();
		} catch( CompositeDb4oException ex){
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			ex.printStackTrace(printWriter);
			String stackTrace = stringWriter.toString();
			StringAssert.contains("throwCompositeException", stackTrace);
			StringAssert.contains("method1", stackTrace);
			StringAssert.contains("method2", stackTrace);
			ex.printStackTrace();
		}
	}

	private void throwCompositeException() {
		Exception ex1 = null;
		Exception ex2 = null;
		
		try{
			method1();
		}catch(Exception ex){
			ex1 = ex;
		}
		
		try{
			method2();
		}catch(Exception ex){
			ex2 = ex;
		}
		
		throw new CompositeDb4oException(ex1, ex2);
	}
	
	private void method1(){
		throw new RuntimeException();
	}
	
	private void method2 () {
		throw new RuntimeException();
	}

}
