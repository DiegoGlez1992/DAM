package com.db4odoc.f1.chapter5;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4odoc.f1.*;


public class InheritanceExample extends Util {
	
	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";
	
    public static void main(String[] args) {
        new File(DB4OFILENAME).delete();
        ObjectContainer db=Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
        try {
            storeFirstCar(db);
            storeSecondCar(db);
            retrieveTemperatureReadoutsQBE(db);
            retrieveAllSensorReadoutsQBE(db);
            retrieveAllSensorReadoutsQBEAlternative(db);
            retrieveAllSensorReadoutsQuery(db);
            retrieveAllObjectsQBE(db);
        }
        finally {
            db.close();
        }
    }

    public static void storeFirstCar(ObjectContainer db) {
        Car car1=new Car("Ferrari");
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        car1.setPilot(pilot1);
        db.store(car1);
    }
    
    public static void storeSecondCar(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        Car car2=new Car("BMW");
        car2.setPilot(pilot2);
        car2.snapshot();
        car2.snapshot();
        db.store(car2);
    }

    public static void retrieveAllSensorReadoutsQBE(
            ObjectContainer db) {
        SensorReadout proto=new SensorReadout(null,null,null);
        ObjectSet result=db.queryByExample(proto);
        listResult(result);
    }

    public static void retrieveTemperatureReadoutsQBE(
            ObjectContainer db) {
        SensorReadout proto=
            new TemperatureSensorReadout(null,null,null,0.0);
        ObjectSet result=db.queryByExample(proto);
        listResult(result);
    }

    public static void retrieveAllSensorReadoutsQBEAlternative(
            ObjectContainer db) {
        ObjectSet result=db.queryByExample(SensorReadout.class);
        listResult(result);
    }

    public static void retrieveAllSensorReadoutsQuery(
            ObjectContainer db) {
        Query query=db.query();
        query.constrain(SensorReadout.class);
        ObjectSet result=query.execute();
        listResult(result);
    }
    
    public static void retrieveAllObjectsQBE(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new Object());
        listResult(result);
    }
    
}
