package com.db4odoc.f1.chapter6;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4odoc.f1.Util;

import java.io.File;

public class DeepExample extends Util {
	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";

	public static void main(String[] args) {
		new File(DB4OFILENAME).delete();
		ObjectContainer db = Db4oEmbedded.openFile(DB4OFILENAME);
		storeCar(db);
		db.close();
		db = Db4oEmbedded.openFile(DB4OFILENAME);
		db.close();
		takeManySnapshots();
		db = Db4oEmbedded.openFile(DB4OFILENAME);
		retrieveAllSnapshots(db);
		db.close();
		db = Db4oEmbedded.openFile(DB4OFILENAME);
		retrieveSnapshotsSequentially(db);
		retrieveSnapshotsSequentiallyImproved(db);
		db.close();
		retrieveSnapshotsSequentiallyCascade();
	}

	public static void storeCar(ObjectContainer db) {
		Pilot pilot = new Pilot("Rubens Barrichello", 99);
		Car car = new Car("BMW");
		car.setPilot(pilot);
		db.store(car);
	}

	public static void takeManySnapshots() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Car.class).cascadeOnUpdate(true);
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();
			for (int i = 0; i < 5; i++) {
				car.snapshot();
			}
			db.store(car);
		}
		db.close();
	}

	public static void retrieveAllSnapshots(ObjectContainer db) {
		ObjectSet result = db.queryByExample(SensorReadout.class);
		while (result.hasNext()) {
			System.out.println(result.next());
		}
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

	public static void retrieveSnapshotsSequentiallyCascade() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(TemperatureSensorReadout.class).cascadeOnActivate(
				true);
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

	public static void retrieveSnapshotsSequentiallyImproved(ObjectContainer db) {
		ObjectSet result = db.queryByExample(Car.class);
		Car car = (Car) result.next();
		SensorReadout readout = car.getHistory();
		while (readout != null) {
			db.activate(readout, 1);
			System.out.println(readout);
			readout = readout.getNext();
		}
	}

}
