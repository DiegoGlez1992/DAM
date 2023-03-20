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
package com.db4o.db4ounit.common.config;

import com.db4o.config.*;
import com.db4o.diagnostic.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ObjectFieldDoesNotExistTestCase extends AbstractDb4oTestCase{
	
	private static final String BOGUS_FIELD_NAME = "bogusField";
	
	private boolean _diagnosticCalled = false; 
	
	public static class Item{
		
		public String _name;
		
	}
	
	@Override
	protected void configure(final Configuration config) throws Exception {
		config.diagnostic().addListener(new DiagnosticListener(){
			public void onDiagnostic(Diagnostic d) {
				if(d instanceof ObjectFieldDoesNotExist){
					ObjectFieldDoesNotExist message = (ObjectFieldDoesNotExist) d;
					Assert.areEqual(BOGUS_FIELD_NAME, message._fieldName);
					_diagnosticCalled = true;
				}
			}
		});
		config.objectClass(Item.class).objectField(BOGUS_FIELD_NAME).indexed(true);
		config.objectClass(Item.class).objectField("_name").indexed(true);
	}
	
	public void test(){
		store(new Item());
		Assert.isTrue(_diagnosticCalled);
	}
	
	public static void main(String[] args) {
		new ObjectFieldDoesNotExistTestCase().runNetworking();
	}

}
