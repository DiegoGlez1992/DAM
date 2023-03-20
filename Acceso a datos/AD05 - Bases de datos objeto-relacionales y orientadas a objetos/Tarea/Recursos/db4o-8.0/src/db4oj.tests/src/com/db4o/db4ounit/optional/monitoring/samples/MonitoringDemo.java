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
package com.db4o.db4ounit.optional.monitoring.samples;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;

public class MonitoringDemo {
	
	private static final boolean CLIENT_SERVER = true;
	
	private static final String DATABASE_FILE_NAME = "mydb.db4o";

	private static final int PERMANENT_OBJECT_COUNT = 10000;
	
	private static final int TEMPORARY_OBJECT_COUNT = 1000;
	
	private static final int QUERY_COUNT = 10;

	private ObjectServer _server;

	public static class Item {
		
		public String name;

		public Item(String name) {
			this.name = name;
		}

	}

	public static void main(String[] args) {
		new MonitoringDemo().run();
	}
	
	public void run(){
		System.out.println("MonitoringDemo will run forever to allow you to see JMX/Perfmon statistics.");
		System.out.println("Cancel running with CTRL + C");

		File4.delete(DATABASE_FILE_NAME);

		ObjectContainer objectContainer = openContainer();
		
		storePermanentObjects(objectContainer);
		
		try{
			while(true){
				storeTemporaryObjects(objectContainer);
				executeQueries(objectContainer);
				deleteTemporaryObjects(objectContainer);
			}
		} finally{
			close(objectContainer);
		}
	}

	private void close(ObjectContainer objectContainer) {
		objectContainer.close();
		if(_server != null){
			_server.close();
			_server = null;
		}
	}

	private ObjectContainer openContainer() {
		if(CLIENT_SERVER){
			String user = "db4o";
			String password = "db4o";
			_server = Db4oClientServer.openServer(configure(Db4oClientServer.newServerConfiguration(), "db4o server(" + DATABASE_FILE_NAME + ")"), DATABASE_FILE_NAME, Db4oClientServer.ARBITRARY_PORT);
			_server.grantAccess(user, password);
			return Db4oClientServer.openClient(configure(Db4oClientServer.newClientConfiguration(), "db4o client(localhost:" + _server.ext().port() + ")"), "localhost", _server.ext().port(), user, password);
		}
		
		return Db4oEmbedded.openFile(configure(Db4oEmbedded.newConfiguration(), "db4o(" + DATABASE_FILE_NAME + ")"), DATABASE_FILE_NAME);
	}

	private void executeQueries(ObjectContainer objectContainer) {
		for (int i = 0; i < QUERY_COUNT; i++) {
			executeSodaQuery(objectContainer);
			executeOptimizedNativeQuery(objectContainer);
			executeUnOptimizedNativeQuery(objectContainer);
		}
	}

	private void executeSodaQuery(ObjectContainer objectContainer) {
		Query query = objectContainer.query();
		query.constrain(Item.class);
		query.descend("name").constrain("1");
		query.execute();
	}
	
	private void executeOptimizedNativeQuery(ObjectContainer objectContainer) {
		objectContainer.query(new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return candidate.name.equals("name1");
			}
		});
	}
	
	private void executeUnOptimizedNativeQuery(ObjectContainer objectContainer) {
		objectContainer.query(new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return candidate.name.charAt(0) == 'q';
			}
		});
	}
	
	private void deleteTemporaryObjects(ObjectContainer objectContainer) {
		Query query = objectContainer.query();
		query.constrain(Item.class);
		query.descend("name").constrain("temp");
		ObjectSet<Item> objectSet = query.execute();
		while(objectSet.hasNext()){
			objectContainer.delete(objectSet.next());
		}
		objectContainer.commit();
	}

	private void storeTemporaryObjects(ObjectContainer objectContainer) {
		for (int i = 0; i < TEMPORARY_OBJECT_COUNT; i++) {
			objectContainer.store(new Item("temp"));
		}
		objectContainer.commit();
	}

	private void storePermanentObjects(ObjectContainer objectContainer) {
		for (int i = 0; i < PERMANENT_OBJECT_COUNT; i++) {
			objectContainer.store(new Item("" + i));
		}
		objectContainer.commit();
	}

	private <T extends CommonConfigurationProvider> T configure (T config, String name) {
		config.common().objectClass(Item.class).objectField("name").indexed(true);
		config.common().nameProvider(new SimpleNameProvider(name));
		new AllMonitoringSupport().apply(config);
		return config;
	}
	
}
