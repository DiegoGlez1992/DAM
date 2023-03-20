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
import com.db4o.db4ounit.common.api.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;

import db4ounit.*;

public class ConfigurationItemTestCase extends Db4oTestWithTempFile {
	
	static final class ConfigurationItemStub implements ConfigurationItem {

		private InternalObjectContainer _container;
		private Configuration _configuration;

		public void apply(InternalObjectContainer container) {
			Assert.isNotNull(container);
			_container = container;
		}

		public void prepare(Configuration configuration) {
			Assert.isNotNull(configuration);
			_configuration = configuration;
		}
		
		public Configuration preparedConfiguration() {
			return _configuration;
		}
		
		public InternalObjectContainer appliedContainer() {
			return _container;
		}
		
	}

	public void test() {
		EmbeddedConfiguration configuration = newConfiguration();
		
		ConfigurationItemStub item = new ConfigurationItemStub();
		configuration.common().add(item);
		
		Assert.areSame(legacyConfigFor(configuration), item.preparedConfiguration());
		Assert.isNull(item.appliedContainer());
		
		File4.delete(tempFile());
		
		ObjectContainer container = Db4oEmbedded.openFile(configuration, tempFile());
		container.close();
		
		Assert.areSame(container, item.appliedContainer());
	}

	private Configuration legacyConfigFor(EmbeddedConfiguration configuration) {
		EmbeddedConfigurationImpl configImpl = (EmbeddedConfigurationImpl) configuration;
		return configImpl.legacy();
	}
}
