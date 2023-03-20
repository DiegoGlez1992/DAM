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

import com.db4o.activation.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;

import db4ounit.*;

/**
 * 
 * @sharpen.partial
 *
 */
public class TransparentActivationSupportTestCase extends TransparentActivationTestCaseBase {

	public static void main(String[] args) {
		new TransparentActivationSupportTestCase().runAll();
	}
	
	public void testActivationDepth() {
		Assert.isInstanceOf(TransparentActivationDepthProviderImpl.class, stream().configImpl().activationDepthProvider());
	}
	
	/**
	 * 
	 * @sharpen.partial
	 *
	 */
	public static final class Item extends ActivatableImpl {
		public void update() {
			activate(ActivationPurpose.WRITE);
		}
	}
	
	public void testTransparentActivationDoesNotImplyTransparentUpdate() {
		final Item item = new Item();
		db().store(item);
		db().commit();
		
		item.update();
		final Collection4 updated = commitCapturingUpdatedObjects(db());
		Assert.areEqual(0, updated.size());
	}
	
	private Collection4 commitCapturingUpdatedObjects(
			final ExtObjectContainer container) {
		final Collection4 updated = new Collection4();
		eventRegistryFor(container).updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs objectArgs = (ObjectEventArgs)args;
				updated.add(objectArgs.object());
			}
		});
		container.commit();
		return updated;
	}
}
