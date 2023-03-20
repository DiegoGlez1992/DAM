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
package com.db4o.db4ounit.jre12.staging;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * Jira #COR-1373
 */
public class SerializableConstructorTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new SerializableConstructorTestCase().runAll();
	}

	static class ExceptionalListHolder {

		public ExceptionalList<String> list;

		public ExceptionalListHolder(ExceptionalList<String> exceptionalList) {
			list = exceptionalList;
		}
		
	}
	
	static class ExceptionalList<E> extends ArrayList<E> {

		public ExceptionalList(String name) {
			if (null == name) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new ExceptionalListHolder(new ExceptionalList<String>("foo")));
	}
	
	public void test() {
		ExceptionalListHolder instance = (ExceptionalListHolder) retrieveOnlyInstance(ExceptionalListHolder.class);
		Assert.isNotNull(instance.list);
	}
	
}
