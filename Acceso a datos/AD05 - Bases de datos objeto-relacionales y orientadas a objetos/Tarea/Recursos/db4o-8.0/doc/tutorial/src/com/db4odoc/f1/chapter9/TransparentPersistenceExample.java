package com.db4odoc.f1.chapter9;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;
import com.db4odoc.f1.*;

public class TransparentPersistenceExample extends Util {

	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";

	public static void main(String[] args) throws Exception {
		new File(DB4OFILENAME).delete();
		storeCarAndSnapshots();
		modifySnapshotHistory();
		readSnapshotHistory();

	}

	public static void storeCarAndSnapshots() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		Car car = new Car("Ferrari");
		for (int i = 0; i < 3; i++) {
			car.snapshot();
		}
		db.store(car);
		db.close();
	}

	public static void modifySnapshotHistory() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);

		System.out.println("Read all sensors and modify the description:");
		ObjectSet result = db.queryByExample(Car.class);
		if (result.hasNext()) {
			Car car = (Car) result.next();
			SensorReadout readout = car.getHistory();
			while (readout != null) {
				System.out.println(readout);
				readout.setDescription("Modified: " + readout.getDescription());
				readout = readout.getNext();
			}
			db.commit();
		}
		db.close();
	}

	public static void readSnapshotHistory() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentPersistenceSupport());
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);

		System.out.println("Read all modified sensors:");
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

}
