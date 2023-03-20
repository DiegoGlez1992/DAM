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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;

import db4ounit.*;

public class EmbeddedConfigurationItemIntegrationTestCase implements TestCase {

	public void test() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		DummyConfigurationItem item = new DummyConfigurationItem();
		config.addConfigurationItem(item);
		EmbeddedObjectContainer container = Db4oEmbedded.openFile(config, "");		
		item.verify(config, container);		
		container.close();
	}

	private final class DummyConfigurationItem implements EmbeddedConfigurationItem {
		private int _prepareCount = 0;
		private int _applyCount = 0;
		private EmbeddedConfiguration _config;
		private EmbeddedObjectContainer _container;
		
		public void prepare(EmbeddedConfiguration configuration) {
			_config = configuration;
			_prepareCount++;
		}

		public void apply(EmbeddedObjectContainer container) {
			_container = container;
			_applyCount++;
		}
		
		void verify(EmbeddedConfiguration config, EmbeddedObjectContainer container) {
			Assert.areSame(config, _config);
			Assert.areSame(container, _container);
			Assert.areEqual(1, _prepareCount);
			Assert.areEqual(1, _applyCount);
		}
	}

}
