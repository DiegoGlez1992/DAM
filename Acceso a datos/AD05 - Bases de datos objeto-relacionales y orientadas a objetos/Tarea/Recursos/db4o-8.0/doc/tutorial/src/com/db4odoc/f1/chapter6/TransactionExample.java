package com.db4odoc.f1.chapter6;

import java.io.*;
import com.db4o.*;
import com.db4odoc.f1.*;


public class TransactionExample extends Util {
	
	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";
	
    public static void main(String[] args) {
        new File(DB4OFILENAME).delete();
        ObjectContainer db=Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB4OFILENAME);
        try {
            storeCarCommit(db);
            db.close();
            db=Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB4OFILENAME);
            listAllCars(db);
            storeCarRollback(db);
            db.close();
            db=Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB4OFILENAME);
            listAllCars(db);
            carSnapshotRollback(db);
            carSnapshotRollbackRefresh(db);
        }
        finally {
            db.close();
        }
    }
    
    public static void storeCarCommit(ObjectContainer db) {
        Pilot pilot=new Pilot("Rubens Barrichello",99);
        Car car=new Car("BMW");
        car.setPilot(pilot);
        db.store(car);
        db.commit();
    }

    public static void listAllCars(ObjectContainer db) {
        ObjectSet result=db.queryByExample(Car.class);
        listResult(result);
    }
    
    public static void storeCarRollback(ObjectContainer db) {
        Pilot pilot=new Pilot("Michael Schumacher",100);
        Car car=new Car("Ferrari");
        car.setPilot(pilot);
        db.store(car);
        db.rollback();
    }

    public static void carSnapshotRollback(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.store(car);
        db.rollback();
        System.out.println(car);
    }

    public static void carSnapshotRollbackRefresh(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new Car("BMW"));
        Car car=(Car)result.next();
        car.snapshot();
        db.store(car);
        db.rollback();
        db.ext().refresh(car,Integer.MAX_VALUE);
        System.out.println(car);
    }
}
