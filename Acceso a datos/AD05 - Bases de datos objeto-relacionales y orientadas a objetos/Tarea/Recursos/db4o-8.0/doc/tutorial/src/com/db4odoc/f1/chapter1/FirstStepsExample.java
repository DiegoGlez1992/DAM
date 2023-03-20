package com.db4odoc.f1.chapter1;

import java.io.*;

import com.db4o.*;
import com.db4odoc.f1.*;

public class FirstStepsExample extends Util {
	final static String DB4OFILENAME = System.getProperty("user.home")
			+ "/formula1.db4o";

	public static void main(String[] args) {
		new File(DB4OFILENAME).delete();
		accessDb4o();
		new File(DB4OFILENAME).delete();
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		try {
			storeFirstPilot(db);
			storeSecondPilot(db);
			retrieveAllPilots(db);
			retrievePilotByName(db);
			retrievePilotByExactPoints(db);
			updatePilot(db);
			deleteFirstPilotByName(db);
			deleteSecondPilotByName(db);
		} finally {
			db.close();
		}
	}

	public static void accessDb4o() {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		try {
			// do something with db4o
		} finally {
			db.close();
		}
	}

	public static void storeFirstPilot(ObjectContainer db) {
		Pilot pilot1 = new Pilot("Michael Schumacher", 100);
		db.store(pilot1);
		System.out.println("Stored " + pilot1);
	}

	public static void storeSecondPilot(ObjectContainer db) {
		Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
		db.store(pilot2);
		System.out.println("Stored " + pilot2);
	}

	public static void retrieveAllPilotQBE(ObjectContainer db) {
		Pilot proto = new Pilot(null, 0);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrieveAllPilots(ObjectContainer db) {
		ObjectSet result = db.queryByExample(Pilot.class);
		listResult(result);
	}

	public static void retrievePilotByName(ObjectContainer db) {
		Pilot proto = new Pilot("Michael Schumacher", 0);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrievePilotByExactPoints(ObjectContainer db) {
		Pilot proto = new Pilot(null, 100);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void updatePilot(ObjectContainer db) {
		ObjectSet result = db
				.queryByExample(new Pilot("Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		found.addPoints(11);
		db.store(found);
		System.out.println("Added 11 points for " + found);
		retrieveAllPilots(db);
	}

	public static void deleteFirstPilotByName(ObjectContainer db) {
		ObjectSet result = db
				.queryByExample(new Pilot("Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		db.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(db);
	}

	public static void deleteSecondPilotByName(ObjectContainer db) {
		ObjectSet result = db
				.queryByExample(new Pilot("Rubens Barrichello", 0));
		Pilot found = (Pilot) result.next();
		db.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(db);
	}
}
