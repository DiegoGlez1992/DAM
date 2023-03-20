package com.db4odoc.f1.chapter6;

import java.io.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4odoc.f1.*;

public class ClientServerExample extends Util {
	final static String DB4OFILENAME = System.getProperty("user.home")
			+ "/formula1.db4o";

	private final static int PORT = 0xdb40;
	private final static String USER = "user";
	private final static String PASSWORD = "password";

	public static void main(String[] args) throws IOException {
		new File(DB4OFILENAME).delete();
		accessLocalServer();
		new File(DB4OFILENAME).delete();
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		try {
			setFirstCar(db);
			setSecondCar(db);
		} finally {
			db.close();
		}
		ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		config.common().objectClass(Car.class).updateDepth(3);
		ObjectServer server = Db4oClientServer.openServer(config, DB4OFILENAME,
				0);
		try {
			queryLocalServer(server);
			demonstrateLocalReadCommitted(server);
			demonstrateLocalRollback(server);
		} finally {
			server.close();
		}
		accessRemoteServer();
		server = Db4oClientServer.openServer(Db4oClientServer
				.newServerConfiguration(), DB4OFILENAME, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			queryRemoteServer(PORT, USER, PASSWORD);
			demonstrateRemoteReadCommitted(PORT, USER, PASSWORD);
			demonstrateRemoteRollback(PORT, USER, PASSWORD);
		} finally {
			server.close();
		}
	}

	public static void setFirstCar(ObjectContainer db) {
		Pilot pilot = new Pilot("Rubens Barrichello", 99);
		Car car = new Car("BMW");
		car.setPilot(pilot);
		db.store(car);
	}

	public static void setSecondCar(ObjectContainer db) {
		Pilot pilot = new Pilot("Michael Schumacher", 100);
		Car car = new Car("Ferrari");
		car.setPilot(pilot);
		db.store(car);
	}

	public static void accessLocalServer() {
		ObjectServer server = Db4oClientServer.openServer(Db4oClientServer
				.newServerConfiguration(), DB4OFILENAME, 0);
		try {
			ObjectContainer client = server.openClient();
			// Do something with this client, or open more clients
			client.close();
		} finally {
			server.close();
		}
	}

	public static void queryLocalServer(ObjectServer server) {
		ObjectContainer client = server.openClient();
		listResult(client.queryByExample(new Car(null)));
		client.close();
	}

	public static void demonstrateLocalReadCommitted(ObjectServer server) {
		ObjectContainer client1 = server.openClient();
		ObjectContainer client2 = server.openClient();
		Pilot pilot = new Pilot("David Coulthard", 98);
		ObjectSet result = client1.queryByExample(new Car("BMW"));
		Car car = (Car) result.next();
		car.setPilot(pilot);
		client1.store(car);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.commit();
		listResult(client1.queryByExample(Car.class));
		listRefreshedResult(client2, client2.queryByExample(Car.class), 2);
		client1.close();
		client2.close();
	}

	public static void demonstrateLocalRollback(ObjectServer server) {
		ObjectContainer client1 = server.openClient();
		ObjectContainer client2 = server.openClient();
		ObjectSet result = client1.queryByExample(new Car("BMW"));
		Car car = (Car) result.next();
		car.setPilot(new Pilot("Someone else", 0));
		client1.store(car);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.rollback();
		client1.ext().refresh(car, 2);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.close();
		client2.close();
	}

	public static void accessRemoteServer() throws IOException {
		ObjectServer server = Db4oClientServer.openServer(Db4oClientServer
				.newServerConfiguration(), DB4OFILENAME, PORT);
		server.grantAccess(USER, PASSWORD);
		try {
			ObjectContainer client = Db4oClientServer.openClient(
					Db4oClientServer.newClientConfiguration(), "localhost",
					PORT, USER, PASSWORD);
			// Do something with this client, or open more clients
			client.close();
		} finally {
			server.close();
		}
	}

	public static void queryRemoteServer(int port, String user, String password)
			throws IOException {
		ObjectContainer client = Db4oClientServer.openClient(Db4oClientServer
				.newClientConfiguration(), "localhost", port, user, password);
		listResult(client.queryByExample(new Car(null)));
		client.close();
	}

	public static void demonstrateRemoteReadCommitted(int port, String user,
			String password) throws IOException {
		ObjectContainer client1 = Db4oClientServer.openClient(Db4oClientServer
				.newClientConfiguration(), "localhost", port, user, password);
		ObjectContainer client2 = Db4oClientServer.openClient(Db4oClientServer
				.newClientConfiguration(), "localhost", port, user, password);
		Pilot pilot = new Pilot("Jenson Button", 97);
		ObjectSet result = client1.queryByExample(new Car(null));
		Car car = (Car) result.next();
		car.setPilot(pilot);
		client1.store(car);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.commit();
		listResult(client1.queryByExample(new Car(null)));
		listRefreshedResult(client2, client2.queryByExample(Car.class), 2);
		client1.close();
		client2.close();
	}

	public static void demonstrateRemoteRollback(int port, String user,
			String password) throws IOException {
		ObjectContainer client1 = Db4oClientServer.openClient(Db4oClientServer
				.newClientConfiguration(), "localhost", port, user, password);
		ObjectContainer client2 = Db4oClientServer.openClient(Db4oClientServer
				.newClientConfiguration(), "localhost", port, user, password);
		ObjectSet result = client1.queryByExample(new Car(null));
		Car car = (Car) result.next();
		car.setPilot(new Pilot("Someone else", 0));
		client1.store(car);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.rollback();
		client1.ext().refresh(car, 2);
		listResult(client1.queryByExample(new Car(null)));
		listResult(client2.queryByExample(new Car(null)));
		client1.close();
		client2.close();
	}
}
