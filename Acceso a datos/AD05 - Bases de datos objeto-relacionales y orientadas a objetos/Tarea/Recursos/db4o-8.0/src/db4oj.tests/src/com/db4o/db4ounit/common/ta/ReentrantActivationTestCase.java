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
package com.db4o.db4ounit.common.ta;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.ta.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ReentrantActivationTestCase extends AbstractDb4oTestCase {
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(ReentratActivatableItem.class), new ReentrantActivationTypeHandler());
	}
	
	@Override
	protected void store() throws Exception {
		store(new ReentratActivatableItem());
	}
	
	public void test() {
		ReentratActivatableItem item = retrieveOnlyInstance(ReentratActivatableItem.class);
		Assert.isFalse(item.activated());
		
		item.activateForRead();		
		Assert.isTrue(item.activated());
		
		assertNotActivatedForWrite(item);
	}

	private void assertNotActivatedForWrite(ReentratActivatableItem item) {
		commit();
		Assert.isFalse(item.written());
	}
	
	public static class ReentratActivatableItem implements Activatable {

		private Activator _activator;
		
		private transient boolean _activated;		
		private transient boolean _written;

		public void activate(ActivationPurpose purpose) {
			_activator.activate(purpose);
			_activated = true;
		}
		
		public void objectOnUpdate(ObjectContainer container) {
			_written = true;
		}

		public void bind(Activator activator) {
			_activator = activator;
		}
		
		public void activateForRead() {
			activate(ActivationPurpose.READ);
		}
		
		public void activateForWrite() {		
			activate(ActivationPurpose.WRITE);
		}
		
		public boolean activated() {
			return _activated;
		}
		
		public boolean written() {
			return _written;
		}
	}
	
	public static class ReentrantActivationTypeHandler implements ReferenceTypeHandler {

		public void activate(ReferenceActivationContext context) {
			ReentratActivatableItem item = (ReentratActivatableItem) context.persistentObject();
			item.activateForWrite();
		}

		public void defragment(DefragmentContext context) {
		}

		public void delete(DeleteContext context) throws Db4oIOException {
		}

		public void write(WriteContext context, Object obj) {
		}	
	}
}
