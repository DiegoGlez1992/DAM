package com.db4odoc.f1.chapter3;


import java.io.*;

import com.db4o.*;
import com.db4odoc.f1.*;


public class OMEExample extends Util {    
	final static String DB4OFILENAME = System.getProperty("user.home") + "/ome.db4o";
	
    public static void main(String[] args) {
        deleteDatabase();
        storePilots();
    }

	public static void deleteDatabase() {
		new File(DB4OFILENAME).delete();
	}
    
    public static void storePilots() {
    	ObjectContainer db=Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
        try {
        	Pilot pilot1=new Pilot("Michael Schumacher",100);
            db.store(pilot1);
            System.out.println("Stored "+pilot1);
            Pilot pilot2=new Pilot("Rubens Barrichello",99);
            db.store(pilot2);
            System.out.println("Stored "+pilot2);
        }
        finally {
            db.close();
        }
        
    }

    }
