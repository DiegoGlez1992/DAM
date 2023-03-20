/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

public class TransactionsPerSecond {
    
    public static void main(String[] args) {
		gc();
		new TransactionsPerSecond().run(pointerBasedIdSystem());
		gc();
		new TransactionsPerSecond().run(stackedBTreeSystem(false));
		gc();
		new TransactionsPerSecond().run(singleBTreeSystem(false));
		gc();
		new TransactionsPerSecond().run(stackedBTreeSystem(true));
		gc();
		new TransactionsPerSecond().run(singleBTreeSystem(true));
    	
    }
    
    public static void gc(){
    	for (int i = 0; i < 10; i++) {
    		System.gc();
    		System.runFinalization();
		}
    }

	private static EmbeddedConfiguration pointerBasedIdSystem() {
		System.out.println("PointerBasedIdSystem");
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().usePointerBasedSystem();
		return config;
	}
	
	private static EmbeddedConfiguration stackedBTreeSystem(boolean asynchronousSync) {
		print("StackedBTreeSystem", asynchronousSync);
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().useStackedBTreeSystem();
    	if(asynchronousSync){
    		config.file().asynchronousSync(true);
    	}
		return config;
	}
	
	private static EmbeddedConfiguration singleBTreeSystem(boolean asynchronousSync) {
		print("SingleBTreeSystem", asynchronousSync);
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().useSingleBTreeSystem();
    	if(asynchronousSync){
    		config.file().asynchronousSync(true);
    	}
		return config;
	}
    
	private static void print(String systemType, boolean asynchronousSync) {
		String syncType = asynchronousSync ? "asynchronous sync" : "serialized sync";
		String msg = systemType + " " + syncType;
		System.out.println(msg);
	}
	
    public static class Item{
        public int _int;
        public Item(){
        }
        public Item(int int_){
            _int = int_;
        }
    }
    
    private static final String FILE = "tps.db4o";
    
    private static final long TOTAL_COUNT = 5000;
    
    public void run(EmbeddedConfiguration config){
        
        new File(FILE).delete();
        
        ObjectContainer objectContainer = Db4oEmbedded.openFile(config, FILE).ext();
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_COUNT; i++) {
            objectContainer.store(new Item(i));
            objectContainer.commit();
        }
        
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        objectContainer.close();
        
        System.out.println("Time to store " + TOTAL_COUNT + " objects: " + duration + "ms");
        
        double seconds = ((double)duration) / ((double)1000); 
        double tps = TOTAL_COUNT / seconds;
        
        System.out.println("Transactions per second: " + tps);
    }

}
