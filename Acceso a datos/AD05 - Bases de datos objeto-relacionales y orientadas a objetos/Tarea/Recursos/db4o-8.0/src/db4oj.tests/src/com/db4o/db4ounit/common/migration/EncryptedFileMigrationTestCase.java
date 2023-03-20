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
package com.db4o.db4ounit.common.migration;

import com.db4o.config.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;

import db4ounit.*;

public class EncryptedFileMigrationTestCase extends HandlerUpdateTestCaseBase{
	
	public static class Item{
		public String _name;
		
		public Item(String name){
			_name = name;
		}
	}

	@Override
	protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
		// do nothing
		
	}

	@Override
	protected void assertValues(ExtObjectContainer objectContainer,
			Object[] values) {
		Item item = (Item) values[0];
		Assert.areEqual("one", item._name);
	}

	@Override
	protected Object createArrays() {
		return null;
	}

	@Override
	protected Object[] createValues() {
		return new Object[]{new Item("one")};
	}

	@Override
	protected String typeName() {
		return "encrypted";
	}
	
	@Override
	protected void configureForStore(Configuration config) {
		configureInternal(config);
	}
	
	@Override
	protected void configureForTest(Configuration config) {
		configureInternal(config);
	}

	private void configureInternal(Configuration config) {
		config.encrypt(true);
		config.password("encrypted");
		
	}
	
	@Override
	protected void deconfigureForStore(Configuration config) {
		deconfigureInternal(config);
	}

	@Override
	protected void deconfigureForTest(Configuration config) {
		deconfigureInternal(config);
	}

	private void deconfigureInternal(Configuration config) {
		config.encrypt(false);
	}
	
}
