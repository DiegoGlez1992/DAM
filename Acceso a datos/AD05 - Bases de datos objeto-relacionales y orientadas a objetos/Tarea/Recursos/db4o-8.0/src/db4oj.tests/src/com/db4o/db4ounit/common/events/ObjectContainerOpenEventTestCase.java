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
package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ObjectContainerOpenEventTestCase implements TestCase {

	private BooleanByRef eventReceived = new BooleanByRef(false);

	final class OpenListenerConfigurationItem implements ConfigurationItem {
		private BooleanByRef _eventReceived;
		
		OpenListenerConfigurationItem(BooleanByRef eventReceived) {
			_eventReceived = eventReceived;
		}
		
		public void prepare(Configuration configuration) {
		}
		
		public void apply(InternalObjectContainer container) {
			EventRegistryFactory.forObjectContainer(container).opened().addListener(new EventListener4<ObjectContainerEventArgs>() {
				public void onEvent(Event4<ObjectContainerEventArgs> event, ObjectContainerEventArgs args) {
					_eventReceived.value = true;
				}
			});
		}
	}
	
	public void test() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		config.common().add(new OpenListenerConfigurationItem(eventReceived));
		Assert.isFalse(eventReceived.value);
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, "");
		Assert.isTrue(eventReceived.value);
		db.close();
	}
	
}
