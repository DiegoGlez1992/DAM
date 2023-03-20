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
package com.db4o.db4ounit.common.diagnostics;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class MissingClassDiagnosticsTestCase implements TestCase, TestLifeCycle, OptOutMultiSession {

	private static final String DB_URI = "test_db";

	private static final int PORT = 0xdb40;

	private static final String USER = "user";
	private static final String PASSWORD = "password";

	private transient MemoryStorage _storage = new MemoryStorage();

	public static void main(String[] args) {
		new ConsoleTestRunner(MissingClassDiagnosticsTestCase.class).run();
	}

	public static class AcceptAllPredicate extends Predicate<Object> {
		@Override
		public boolean match(Object candidate) {
			return true;
		}
	}

	public static class Pilot {
		public String name;
		public List cars = new ArrayList();

		public Pilot(String name) {
			super();
			this.name = name;
		}

		public List getCars() {
			return cars;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return "Pilot[" + name + "]";
		}
	}

	public static class Car {
		public String model;

		public Car(String model) {
			this.model = model;
		}

		public String getModel() {
			return model;
		}

		public String toString() {
			return "Car[" + model + "]";
		}
	}

	private void prepareHost(FileConfiguration fileConfig, CommonConfiguration commonConfig, final List classesNotFound) {
		fileConfig.storage(_storage);
		prepareCommon(commonConfig, classesNotFound);
	}

	private void prepareCommon(CommonConfiguration commonConfig, final List classesNotFound) {
		commonConfig.reflectWith(Platform4.reflectorForType(Pilot.class));
		prepareDiagnostic(commonConfig, classesNotFound);
	}

	private void prepareDiagnostic(CommonConfiguration common, final List classesNotFound) {
		common.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic d) {
				if (d instanceof MissingClass) {
					classesNotFound.add(((MissingClass) d).reason());
				}
			}
		});
	}

	public void testEmbedded() {

		List missingClasses = new ArrayList();

		EmbeddedConfiguration excludingConfig = Db4oEmbedded.newConfiguration();
		prepareHost(excludingConfig.file(), excludingConfig.common(), missingClasses);

		excludeClasses(excludingConfig.common(), Pilot.class, Car.class);

		EmbeddedObjectContainer excludingContainer = Db4oEmbedded.openFile(excludingConfig, DB_URI);

		try {
			excludingContainer.query(new AcceptAllPredicate());
		} finally {
			excludingContainer.close();
		}

		assertPilotAndCarMissing(missingClasses);
	}

	private void assertPilotAndCarMissing(List classesNotFound) {

		List<String> excluded = Arrays.asList(
										ReflectPlatform.fullyQualifiedName(Pilot.class), 
										ReflectPlatform.fullyQualifiedName(Car.class));

		Assert.areEqual(excluded.size(), classesNotFound.size());
		for(String candidate : excluded) {
			Assert.isTrue(classesNotFound.contains(candidate));
		}
	}

	public void testMissingClassesInServer() {

		List serverMissedClasses = new ArrayList();
		List clientMissedClasses = new ArrayList();

		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		prepareHost(serverConfig.file(), serverConfig.common(), serverMissedClasses);

		excludeClasses(serverConfig.common(), Pilot.class, Car.class);

		ObjectServer server = Db4oClientServer.openServer(serverConfig, DB_URI, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			prepareCommon(clientConfig.common(), clientMissedClasses);
			ObjectContainer client = Db4oClientServer.openClient(clientConfig, "localhost", PORT, USER, PASSWORD);

			client.query(new AcceptAllPredicate());

			client.close();
		} finally {
			server.close();
		}

		assertPilotAndCarMissing(serverMissedClasses);
		Assert.areEqual(0, clientMissedClasses.size());

	}

	public void testMissingClassesInClient() {

		List serverMissedClasses = new ArrayList();
		List clientMissedClasses = new ArrayList();

		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		prepareHost(serverConfig.file(), serverConfig.common(), serverMissedClasses);

		ObjectServer server = Db4oClientServer.openServer(serverConfig, DB_URI, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			prepareCommon(clientConfig.common(), clientMissedClasses);
			excludeClasses(clientConfig.common(), Pilot.class, Car.class);
			ObjectContainer client = Db4oClientServer.openClient(clientConfig, "localhost", PORT, USER, PASSWORD);

			ObjectSet result = client.query(new AcceptAllPredicate());
			
			iterateOver(result);

			client.close();
		} finally {
			server.close();
		}

		Assert.areEqual(0, serverMissedClasses.size());
		assertPilotAndCarMissing(clientMissedClasses);
	}

	private void iterateOver(ObjectSet result) {
		while (result.hasNext()) {
			result.next();
		}			
	}

	private void excludeClasses(CommonConfiguration commonConfiguration, Class<?>... classes) {
		commonConfiguration.reflectWith(new ExcludingReflector(ByRef.<Class<?>>newInstance(Pilot.class), classes));
	}
	
	public void testClassesFound() throws IOException {

		List missingClasses = new ArrayList();
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		prepareHost(config.file(), config.common(), missingClasses);
		populateContainer(config);
		Assert.areEqual(0, missingClasses.size());

	}

	private void populateContainer(EmbeddedConfiguration config) {
		config.file().storage(_storage);
		ObjectContainer container = Db4oEmbedded.openFile(config, DB_URI);

		try {
			Pilot pilot = new Pilot("Barrichello");
			pilot.getCars().add(new Car("BMW"));
			container.store(pilot);
		} finally {
			container.close();
		}
	}

	public void setUp() throws Exception {

		populateContainer(Db4oEmbedded.newConfiguration());
	}

	public void tearDown() throws Exception {
	}

}
