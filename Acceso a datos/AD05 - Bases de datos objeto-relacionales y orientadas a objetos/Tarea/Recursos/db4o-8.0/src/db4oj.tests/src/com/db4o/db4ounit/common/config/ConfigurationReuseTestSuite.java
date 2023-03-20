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
import com.db4o.cs.*;
import com.db4o.cs.internal.config.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

/**
 * Tests all combinations of configuration use/reuse scenarios.
 */
@SuppressWarnings("deprecation")
public class ConfigurationReuseTestSuite extends FixtureTestSuiteDescription {

	{
		fixtureProviders(
			new SimpleFixtureProvider(
				CONFIGURATION_USE_FUNCTION, // each function returns a block that disposes of any containers
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final ObjectContainer container = Db4o.openFile(config, ".");
					return new Runnable() { public void run() {
						container.close();
					}};
				}},
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final ObjectServer server = openServer(config, ".", 0);
					return new Runnable() { public void run() {
						server.close();
					}};
				}},
				new Function4<Configuration, Runnable>() { public Runnable apply(Configuration config) {
					final Configuration serverConfig = Db4o.newConfiguration();
					serverConfig.storage(new MemoryStorage());
					final ObjectServer server = openServer(serverConfig, ".", -1);
					server.grantAccess("user", "password");
					final ObjectContainer client = openClient(config, "localhost", server.ext().port(), "user", "password");
					return new Runnable() { public void run() {
						client.close();
						server.close();
					}};
				}}
			),
			new SimpleFixtureProvider(
				CONFIGURATION_REUSE_PROCEDURE,
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					Db4o.openFile(config, "..");
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					openServer(config, "..", 0);
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					final ObjectServer server = openServer(newInMemoryConfiguration(), "..", 0);
					try {
						openClient(config, "localhost", server.ext().port(), "user", "password");
					} finally {
						server.close();
					}
				}},
				new Procedure4<Configuration>() { public void apply(Configuration config) {
					openClient(config, "localhost", 0xdb40, "user", "password");
				}}
			)
		);
		
		testUnits(ConfigurationReuseTestUnit.class);
	}

	static final FixtureVariable<Function4<Configuration, Runnable>> CONFIGURATION_USE_FUNCTION = FixtureVariable.newInstance("Successul configuration use");
	static final FixtureVariable<Procedure4<Configuration>> CONFIGURATION_REUSE_PROCEDURE = FixtureVariable.newInstance("Configuration reuse attempt");
	
	public static class ConfigurationReuseTestUnit implements TestCase {
		
		public void test() {
			final Configuration config = newInMemoryConfiguration();
			final Runnable tearDownBlock = CONFIGURATION_USE_FUNCTION.value().apply(config);
			try {
				Assert.expect(IllegalArgumentException.class, new CodeBlock() {
					public void run() throws Throwable {
						CONFIGURATION_REUSE_PROCEDURE.value().apply(config);
					}
				});
			} finally {
				tearDownBlock.run();
			}
		}

	}
	
	static Configuration newInMemoryConfiguration() {
		final Configuration config = Db4o.newConfiguration();
		config.storage(new MemoryStorage());
		return config;
	}

	protected ObjectServer openServer(Configuration config, String databaseFileName, int port) {
		return Db4oClientServer.openServer(Db4oClientServerLegacyConfigurationBridge.asServerConfiguration(config), databaseFileName, port);
	}

	protected ObjectContainer openClient(Configuration config, String host, int port,
			String user, String password) {
		return Db4oClientServer.openClient(Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(config), host, port, user, password);
	}
}