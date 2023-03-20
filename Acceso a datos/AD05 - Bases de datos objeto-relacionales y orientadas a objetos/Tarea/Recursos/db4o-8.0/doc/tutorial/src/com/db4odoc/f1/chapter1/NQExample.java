package com.db4odoc.f1.chapter1;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4odoc.f1.Util;

import java.util.List;

public class NQExample extends Util {

	final static String DB4OFILENAME = System.getProperty("user.home") + "/formula1.db4o";
	
    public static void main(String[] args) {
        ObjectContainer db=Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
        try {
            storePilots(db);
            retrieveComplexSODA(db);
            retrieveComplexNQ(db);
            retrieveArbitraryCodeNQ(db);
            clearDatabase(db);
        }
        finally {
            db.close();
        }
    }

    public static void storePilots(ObjectContainer db) {
        db.store(new Pilot("Michael Schumacher",100));
        db.store(new Pilot("Rubens Barrichello",99));
    }

    public static void retrieveComplexSODA(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Query pointQuery=query.descend("points");
        query.descend("name").constrain("Rubens Barrichello")
        	.or(pointQuery.constrain(99).greater()
        	    .and(pointQuery.constrain(199).smaller()));
        ObjectSet result=query.execute();
        listResult(result);
    }
    
    public static void retrieveComplexNQ(ObjectContainer db) {
        List<Pilot> result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot) {
        		return pilot.getPoints()>99
        			&& pilot.getPoints()<199
        			|| pilot.getName().equals("Rubens Barrichello");
			}
        });
        listResult(result);
    }

    public static void retrieveArbitraryCodeNQ(ObjectContainer db) {
    	final int[] points={1,100};
        List<Pilot> result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot) {
                for (int point : points) {
                    if (pilot.getPoints() == point) {
                        return true;
                    }
                }
        		return pilot.getName().startsWith("Rubens");
			}
        });
        listResult(result);
    }

    public static void clearDatabase(ObjectContainer db) {
        ObjectSet result=db.queryByExample(Pilot.class);
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
}
