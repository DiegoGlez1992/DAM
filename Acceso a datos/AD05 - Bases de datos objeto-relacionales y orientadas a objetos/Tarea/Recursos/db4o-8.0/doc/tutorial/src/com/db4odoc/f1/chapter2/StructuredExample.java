package com.db4odoc.f1.chapter2;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4odoc.f1.Util;

import java.io.File;
import java.util.List;

public class StructuredExample extends Util {

	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";

	public static void main(String[] args) {
		new File(DB4OFILENAME).delete();
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		storeFirstCar(db);
		storeSecondCar(db);
		retrieveAllCarsQBE(db);
		retrieveAllPilotsQBE(db);
		retrieveCarByPilotQBE(db);
		retrieveCarByPilotNameQuery(db);
		retrieveCarByPilotProtoQuery(db);
		retrievePilotByCarModelQuery(db);
		updateCar(db);
		updatePilotSingleSession(db);
		updatePilotSeparateSessionsPart1(db);
		db.close();
		db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		updatePilotSeparateSessionsPart2(db);
		db.close();
		updatePilotSeparateSessionsImprovedPart1();
		updatePilotSeparateSessionsImprovedPart2();
		db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);

		deleteFlat(db);
		db.close();
		deleteDeep();
		deleteDeepRevisited();
	}

	public static void storeFirstCar(ObjectContainer db) {
		Car car1 = new Car("Ferrari");
		Pilot pilot1 = new Pilot("Michael Schumacher", 100);
		car1.setPilot(pilot1);
		db.store(car1);
	}

	public static void storeSecondCar(ObjectContainer db) {
		Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
		db.store(pilot2);
		Car car2 = new Car("BMW");
		car2.setPilot(pilot2);
		db.store(car2);
	}

	public static void retrieveAllCarsQBE(ObjectContainer db) {
		Car proto = new Car(null);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrieveAllPilotsQBE(ObjectContainer db) {
		Pilot proto = new Pilot(null, 0);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrieveAllPilots(ObjectContainer db) {
		ObjectSet result = db.queryByExample(Pilot.class);
		listResult(result);
	}

	public static void retrieveCarByPilotQBE(ObjectContainer db) {
		Pilot pilotproto = new Pilot("Rubens Barrichello", 0);
		Car carproto = new Car(null);
		carproto.setPilot(pilotproto);
		ObjectSet result = db.queryByExample(carproto);
		listResult(result);
	}

	public static void retrieveCarByPilotNameQuery(ObjectContainer db) {
		Query query = db.query();
		query.constrain(Car.class);
		query.descend("pilot").descend("name").constrain("Rubens Barrichello");
		ObjectSet result = query.execute();
		listResult(result);
	}

	public static void retrieveCarByPilotProtoQuery(ObjectContainer db) {
		Query query = db.query();
		query.constrain(Car.class);
		Pilot proto = new Pilot("Rubens Barrichello", 0);
		query.descend("pilot").constrain(proto);
		ObjectSet result = query.execute();
		listResult(result);
	}

	public static void retrievePilotByCarModelQuery(ObjectContainer db) {
		Query carquery = db.query();
		carquery.constrain(Car.class);
		carquery.descend("model").constrain("Ferrari");
		Query pilotquery = carquery.descend("pilot");
		ObjectSet result = pilotquery.execute();
		listResult(result);
	}

	public static void retrieveAllPilotsNative(ObjectContainer db) {
		List<Pilot> results = db.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return true;
			}
		});
		listResult(results);
	}

	public static void retrieveAllCars(ObjectContainer db) {
		ObjectSet results = db.queryByExample(Car.class);
		listResult(results);
	}

	public static void retrieveCarsByPilotNameNative(ObjectContainer db) {
		final String pilotName = "Rubens Barrichello";
		List<Car> results = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getPilot().getName().equals(pilotName);
			}
		});
		listResult(results);
	}

	public static void updateCar(ObjectContainer db) {
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = (Car) result.get(0);
		found.setPilot(new Pilot("Somebody else", 0));
		db.store(found);
		result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	public static void updatePilotSingleSession(ObjectContainer db) {
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = result.get(0);
		found.getPilot().addPoints(1);
		db.store(found);
		result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	public static void updatePilotSeparateSessionsPart1(ObjectContainer db) {
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = result.get(0);
		found.getPilot().addPoints(1);
		db.store(found);
	}

	public static void updatePilotSeparateSessionsPart2(ObjectContainer db) {
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		listResult(result);
	}

	public static void updatePilotSeparateSessionsImprovedPart1() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Car.class).cascadeOnUpdate(true);
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		if (result.size() > 0) {
			Car found = result.get(0);
			found.getPilot().addPoints(1);
			db.store(found);
		}
		db.close();
	}

	public static void updatePilotSeparateSessionsImprovedPart2() {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);

		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car car = result.get(0);
		listResult(result);
		db.close();
	}

	public static void deleteFlat(ObjectContainer db) {
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("Ferrari");
			}
		});
		Car found = result.get(0);
		db.delete(found);
		result = db.queryByExample(new Car(null));
		listResult(result);
	}

	public static void deleteDeep() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Car.class).cascadeOnDelete(true);
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		List<Car> result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return car.getModel().equals("BMW");
			}
		});
		if (result.size() > 0) {
			Car found = result.get(0);
			db.delete(found);
		}
		result = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return true;
			}
		});
		listResult(result);
		db.close();
	}

	public static void deleteDeepRevisited() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Car.class).cascadeOnDelete(true);
		ObjectContainer db = Db4oEmbedded.openFile(config, DB4OFILENAME);
		ObjectSet<Pilot> result = db.query(new Predicate<Pilot>() {
			public boolean match(Pilot pilot) {
				return pilot.getName().equals("Michael Schumacher");
			}
		});
		if (!result.hasNext()) {
			System.out.println("Pilot not found!");
			db.close();
			return;
		}
		Pilot pilot = (Pilot) result.next();
		Car car1 = new Car("Ferrari");
		Car car2 = new Car("BMW");
		car1.setPilot(pilot);
		car2.setPilot(pilot);
		db.store(car1);
		db.store(car2);
		db.delete(car2);
		List<Car> cars = db.query(new Predicate<Car>() {
			public boolean match(Car car) {
				return true;
			}
		});
		listResult(cars);
		db.close();
	}

}
