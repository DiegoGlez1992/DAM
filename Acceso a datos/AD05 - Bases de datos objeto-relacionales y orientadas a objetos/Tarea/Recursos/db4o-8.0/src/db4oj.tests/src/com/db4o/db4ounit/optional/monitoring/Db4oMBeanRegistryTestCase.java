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
package com.db4o.db4ounit.optional.monitoring;

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.monitoring.*;

import db4ounit.*;
import db4ounit.extensions.OptOutNotSupportedJavaxManagement;

@decaf.Remove
public class Db4oMBeanRegistryTestCase implements TestCase, OptOutNotSupportedJavaxManagement {

	public static interface Mock1MBean {
	}

	public static interface Mock2MBean {
	}

	private static abstract class MockMBean extends MBeanRegistrationSupport {
		public boolean registered = false;

		public MockMBean(ObjectContainer db, Class<?> type) {
			super(db, type);
		}

		@Override
		public void register() throws JMException {
			super.register();
			registered = true;
		}
		
		@Override
		public void unregister() {
			super.unregister();
			registered = false;
		}
	}

	public static class Mock1 extends MockMBean implements Mock1MBean {
		public Mock1(ObjectContainer db, Class<?> type) {
			super(db, type);
		}
	}

	public static class Mock2 extends MockMBean implements Mock2MBean {
		public Mock2(ObjectContainer db, Class<?> type) {
			super(db, type);
		}
	}

	public void test() {
		final MockMBean[] beans = new MockMBean[2];
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		config.common().add(new ConfigurationItem() {
			public void prepare(Configuration configuration) {
			}
			
			public void apply(InternalObjectContainer container) {
					beans[0] = new Mock1(container, Mock1MBean.class);
					beans[1] = new Mock2(container, Mock2MBean.class);
			}
		});
		final ExternalObjectContainer db = (ExternalObjectContainer) Db4oEmbedded.openFile(config, "");
		assertRegistered(beans, true);
		db.close();
		assertRegistered(beans, false);
	}
	
	private void assertRegistered(MockMBean[] beans, boolean expected) {
		for (MockMBean bean: beans) {
			Assert.areEqual(expected, bean.registered);
		}
	}
}
