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
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ActivationExceptionBubblesUpTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ActivationExceptionBubblesUpTestCase().runAll();
	}
	
	public static class AcceptAllEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
			candidate.include(true);
		}
	}

	public static final class ItemTranslator implements ObjectTranslator {

		public void onActivate(ObjectContainer container,
				Object applicationObject, Object storedObject) {
			
			throw new ItemException();
		}

		public Object onStore(ObjectContainer container, Object applicationObject) {
			return applicationObject;
		}

		public Class storedClass() {
			return Item.class;
		}
		
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).translate(new ItemTranslator());
	}
	
	protected void store() throws Exception {
		store(new Item());
	}
	
	public void test() {
		Assert.expect(ReflectException.class, ItemException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						final Query q = db().query();
						q.constrain(Item.class);
						q.constrain(new AcceptAllEvaluation());
						q.execute().next();
					}
				});
	}

}
