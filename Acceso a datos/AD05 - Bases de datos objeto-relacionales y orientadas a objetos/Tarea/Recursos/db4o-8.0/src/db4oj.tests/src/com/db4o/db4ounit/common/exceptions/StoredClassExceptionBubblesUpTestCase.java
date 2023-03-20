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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class StoredClassExceptionBubblesUpTestCase extends AbstractDb4oTestCase implements OptOutMultiSession, OptOutDefragSolo {
	
	public static void main(String[] args) {
		new StoredClassExceptionBubblesUpTestCase().runSolo();
	}
	
	public static final class ItemTranslator implements ObjectTranslator {

		public void onActivate(ObjectContainer container,
				Object applicationObject, Object storedObject) {		
		}

		public Object onStore(ObjectContainer container,
				Object applicationObject) {
			return null;
		}

		public Class storedClass() {
			throw new ItemException();
		}
		
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).translate(new ItemTranslator());
	}
	
	public void test() {
		Assert.expect(ReflectException.class, ItemException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						store(new Item());
					}
				});
	}

}
