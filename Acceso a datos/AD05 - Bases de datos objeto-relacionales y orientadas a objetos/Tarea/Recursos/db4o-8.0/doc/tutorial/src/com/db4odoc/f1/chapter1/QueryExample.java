package com.db4odoc.f1.chapter1;

import com.db4o.*;
import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.db4odoc.f1.*;


public class QueryExample extends Util {
	
	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";
	
    public static void main(String[] args) {
        ObjectContainer db=Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
        try {
            storeFirstPilot(db);
            storeSecondPilot(db);
            retrieveAllPilots(db);
            retrievePilotByName(db);
            retrievePilotByExactPoints(db);
            retrieveByNegation(db);
            retrieveByConjunction(db);
            retrieveByDisjunction(db);
            retrieveByComparison(db);
            retrieveByDefaultFieldValue(db);
            retrieveSorted(db); 
            clearDatabase(db);
        }
        finally {
            db.close();
        }
    }

    public static void storeFirstPilot(ObjectContainer db) {
        Pilot pilot1=new Pilot("Michael Schumacher",100);
        db.store(pilot1);
        System.out.println("Stored "+pilot1);
    }

    public static void storeSecondPilot(ObjectContainer db) {
        Pilot pilot2=new Pilot("Rubens Barrichello",99);
        db.store(pilot2);
        System.out.println("Stored "+pilot2);
    }

    public static void retrieveAllPilots(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrievePilotByName(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").constrain("Michael Schumacher");
        ObjectSet result=query.execute();
        listResult(result);
    }
    
    public static void retrievePilotByExactPoints(
            ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points").constrain(100);
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveByNegation(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").constrain("Michael Schumacher").not();
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveByConjunction(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Constraint constr=query.descend("name")
                .constrain("Michael Schumacher");
        query.descend("points")
                .constrain(99).and(constr);
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveByDisjunction(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Constraint constr=query.descend("name")
                .constrain("Michael Schumacher");
        query.descend("points")
                .constrain(99).or(constr);
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveByComparison(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points")
                .constrain(99).greater();
        ObjectSet result=query.execute();
        listResult(result);
    }

    public static void retrieveByDefaultFieldValue(
                    ObjectContainer db) {
        Pilot somebody=new Pilot("Somebody else",0);
        db.store(somebody);
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("points").constrain(0);
        ObjectSet result=query.execute();
        listResult(result);
        db.delete(somebody);
    }
    
    public static void retrieveSorted(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        query.descend("name").orderAscending();
        ObjectSet result=query.execute();
        listResult(result);
        query.descend("name").orderDescending();
        result=query.execute();
        listResult(result);
    }

    public static void clearDatabase(ObjectContainer db) {
        ObjectSet result=db.queryByExample(Pilot.class);
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
}
