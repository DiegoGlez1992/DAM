package com.db4odoc.f1.chapter8;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;
import com.db4odoc.f1.*;

public class TransparentActivationExample extends Util {

	final static String DB4OFILENAME = System.getProperty("user.home")
			+ "/formula1.db4o";

	public static void main(String[] args) throws Exception {
		new File(DB4OFILENAME).delete();
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		try {
			storeCarAndSnapshots(db);
			db.close();
			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
					DB4OFILENAME);
			retrieveSnapshotsSequentially(db);
			db.close();
			retrieveSnapshotsSequentiallyTA();
			demonstrateTransparentActivation();

		} finally {
			db.close();
		}
	}

	public static void storeCarAndSnapshots(ObjectContainer db) {
		Pilot pilot = new Pilot("Kimi Raikkonen", 110);
		Car car = new Car("Ferrari");
		car.setPilot(pilot);
		for (int i = 0; i < 5; i++) {
			car.snapshot();
		}
		db.store(car);
	}

	public static void retrieveSnapshotsSequentially(ObjectContainer db) {
		ObjectSet result = db.queryByExample(Car.class);
		Car car = (Car) result.next();
		SensorReadout readout = car.getHistory();
		while (readout != null) {
			System.out.println(readout);
			readout = readout.getNext();
		}
	}

	public static void retrieveSnapshotsSequentiallyTA() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentActivationSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();
			SensorReadout readout = car.getHistory();
			while (readout != null) {
				System.out.println(readout);
				readout = readout.getNext();
			}
		}
		db.close();
	}

	public static void demonstrateTransparentActivation() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentActivationSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);

		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();

			System.out
					.println("#getPilotWithoutActivation() before the car is activated");
			System.out.println(car.getPilotWithoutActivation());

			System.out.println("calling #getPilot() activates the car object");
			System.out.println(car.getPilot());

			System.out
					.println("#getPilotWithoutActivation() after the car is activated");
			System.out.println(car.getPilotWithoutActivation());

		}
		db.close();
	}

}
