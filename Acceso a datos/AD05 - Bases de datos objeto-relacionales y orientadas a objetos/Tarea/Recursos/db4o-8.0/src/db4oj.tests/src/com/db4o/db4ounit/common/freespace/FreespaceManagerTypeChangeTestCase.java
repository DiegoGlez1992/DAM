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
package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.config.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public class FreespaceManagerTypeChangeTestCase extends FreespaceManagerTestCaseBase implements OptOutMultiSession, OptOutDefragSolo, OptOutNonStandardBlockSize {
    
    private static final boolean VERBOSE = false;

    /**
     * The magic numbers for the limits were found empirically 
     * using "what we have" and adding a reserve.
     * 
     * Settings may need to be higher if we add new complexity
     * to how our engine works.
     */
    private static final long USED_SPACE_CREEP_LIMIT = 1200;

    private static final long FRAGMENTATION_CREEP_LIMIT = 10;

	private static final long TOTAL_USED_SPACE_CREEP_LIMIT = 12000;

	private static final long TOTAL_FRAGMENTATION_CREEP_LIMIT = 100;
    
    private Configuration configuration;

    private static String ITEM_NAME = "one";
    
    int[] _initialUsedSpace = new int[2];
    
    int[] _initialFragmentation = new int[2];
    
    int[] _usedSpace = new int[2];
    
    int[] _fragmentation = new int[2];
    
    int _maxUsedSpaceCreep; 
    
    int _maxFragmentationCreep;
    
    
    
    private static int BTREE = 0;
    
    private static int RAM = 1;
    
    public static class Item{
        
        public String _name;
        
        public Item(String name){
            _name = name;
        }
        
    }
    
    public static void main(String[] args) {
        new FreespaceManagerTypeChangeTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        super.configure(config);
        config.freespace().useBTreeSystem();
        configuration = config;
        Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useInMemorySystem();
    }
    
    public void testSwitchingBackAndForth() throws Exception{
        produceSomeFreeSpace();
        printStatus();
        db().commit();
        printStatus();
        storeItem();
        printStatus();
        db().commit();
        
        for (int run = 0; run < 50; run++) {
        	
        	// produceSomeFreeSpace();
        	// db().commit();
            
            printStatus();
            
            assertFreespace(BTREE, run);
            
            configuration.freespace().useRamSystem();
            reopen();
            assertFreespaceManagerClass(InMemoryFreespaceManager.class);

            assertItemAvailable();
            deleteItem();
            storeItem();
            
            printStatus();
            assertFreespace(RAM, run);
            
            configuration.freespace().useBTreeSystem();
            reopen();
            assertFreespaceManagerClass(BTreeFreespaceManager.class);
            
            assertItemAvailable();
            deleteItem();
            storeItem();
        }

    }

    private void storeItem() {
        store(new Item(ITEM_NAME));
    }
    
    private void deleteItem(){
        db().delete(retrieveOnlyInstance(Item.class));
    }

    private void assertItemAvailable() {
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.areEqual(ITEM_NAME, item._name);
    }

    private void assertFreespace(int system, int run) {
    	int calculatedFreespaceSize = calculatedFreespaceSize();
    	long fileSize = fileSize();
    	int usedSpace = (int) (fileSize - calculatedFreespaceSize);
    	
    	int fragmentation = freespaceSlots().size();
    	
    	if(run  == 0){
    		_usedSpace[system] = usedSpace;
    		_fragmentation[system] = fragmentation;
    		
    		_initialFragmentation[system] = fragmentation;
    		_initialUsedSpace[system] = usedSpace;
    		return;
    	}
    	
    	if(usedSpace > _usedSpace[system]){
    		int usedSpaceCreep = usedSpace - _usedSpace[system];
    		_usedSpace[system] = usedSpace;
    		if(usedSpaceCreep > _maxUsedSpaceCreep){
    			_maxUsedSpaceCreep = usedSpaceCreep;
    		}
    	}
    	print("Max space CREEP " + _maxUsedSpaceCreep);
    	
    	if(fragmentation > _fragmentation[system]){
    		int fragmentationCreep = fragmentation - _fragmentation[system];
    		_fragmentation[system] = fragmentation;
    		if(fragmentationCreep > _maxFragmentationCreep){
    			_maxFragmentationCreep = fragmentationCreep;
    		}
    	}
    	print("Max Fragmentation CREEP " + _maxFragmentationCreep);
    	
    	int totalUsedSpaceCreep = usedSpace - _initialUsedSpace[system];
    	int totalFragmentationCreep = fragmentation - _initialFragmentation[system];
    	
    	print("Total space CREEP " + totalUsedSpaceCreep);
    	print("Total Fragmentation CREEP " + totalFragmentationCreep);

    	Assert.isSmaller(FRAGMENTATION_CREEP_LIMIT, _maxFragmentationCreep);
    	Assert.isSmaller(TOTAL_FRAGMENTATION_CREEP_LIMIT, totalFragmentationCreep);
    	Assert.isSmaller(USED_SPACE_CREEP_LIMIT, _maxUsedSpaceCreep);
    	Assert.isSmaller(TOTAL_USED_SPACE_CREEP_LIMIT, totalUsedSpaceCreep);
    }

    private void printStatus() {
        if(! VERBOSE){
            return;
        }
        print("fileSize " + fileSize());
        print("slot count " + currentFreespaceManager().slotCount());
        print("current freespace " + currentFreespace());
        Collection4 freespaceSlots = freespaceSlots();
        Iterator4 iterator = freespaceSlots.iterator();
        while(iterator.moveNext()) {
        	print(iterator.current().toString());
        }
        print("calculated freespace size " + calculatedFreespaceSize());
    }

	private long fileSize() {
		return fileSession().fileLength();
	}

    private Collection4 freespaceSlots() {
        final Collection4 collectionOfSlots = new Collection4();
        currentFreespaceManager().traverse(new Visitor4() {
            public void visit(Object obj) {
                collectionOfSlots.add(obj);
            }
        });
        return collectionOfSlots;
    }
    
    private int calculatedFreespaceSize(){
    	int size = 0;
    	Iterator4 i = freespaceSlots().iterator();
    	while(i.moveNext()){
    		Slot slot = (Slot) i.current();
    		size += slot.length();
    	}
    	return size;
    }

    private void assertFreespaceManagerClass(Class clazz) {
        Assert.isInstanceOf(clazz, currentFreespaceManager());
    }

    private int currentFreespace() {
        return currentFreespaceManager().totalFreespace();
    }
    
    private static void print(String str){
        if(VERBOSE){
            System.out.println(str);
        }
    }

}
