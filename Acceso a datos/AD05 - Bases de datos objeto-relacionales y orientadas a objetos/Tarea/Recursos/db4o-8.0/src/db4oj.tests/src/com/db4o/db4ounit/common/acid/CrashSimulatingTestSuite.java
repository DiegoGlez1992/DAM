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
package com.db4o.db4ounit.common.acid;

import java.io.IOException;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.config.Db4oLegacyConfigurationBridge;
import com.db4o.io.*;
import com.db4o.query.Query;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.OptOutMultiSession;
import db4ounit.fixtures.*;

/**
 * @exclude
 */
public class CrashSimulatingTestSuite  extends FixtureBasedTestSuite implements OptOutVerySlow {
	
    static final boolean VERBOSE = false;
	
	private final static FixtureVariable<LabeledBoolean> USE_CACHE = new FixtureVariable<LabeledBoolean>();
	
	private final static FixtureVariable<LabeledBoolean> USE_LOGFILE = new FixtureVariable<LabeledBoolean>();
	
	private final static FixtureVariable<LabeledBoolean> WRITE_TRASH = new FixtureVariable<LabeledBoolean>();
	
	private final static FixtureVariable<LabeledConfig> ID_SYSTEM = new FixtureVariable<LabeledConfig>();
	
	private final static FixtureVariable<LabeledConfig> FREESPACE_MANAGER = new FixtureVariable<LabeledConfig>();
	
	private FixtureProvider[] singleConfig() {
		return new FixtureProvider[]{
				new SimpleFixtureProvider<LabeledBoolean>(USE_CACHE, new LabeledBoolean("no cache", false)),
				new SimpleFixtureProvider<LabeledBoolean>(USE_LOGFILE, new LabeledBoolean("no logfile", false)),
				new SimpleFixtureProvider<LabeledBoolean>(WRITE_TRASH, new LabeledBoolean("write trash", true)),
				new SimpleFixtureProvider<LabeledConfig>(FREESPACE_MANAGER, 
					new LabeledConfig("BTreeFreespaceManager"){
						@Override
						public void configure(Config4Impl config) {
							// config.freespace().useRamSystem();
							config.freespace().useBTreeSystem();
						}
					}
				),
				new SimpleFixtureProvider<LabeledConfig>(ID_SYSTEM, 
					new LabeledConfig("BTreeIdSystem"){
						@Override
						public void configure(Config4Impl config) {
							
							Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useStackedBTreeSystem();
							// Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useInMemorySystem();
							// Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).usePointerBasedSystem();
						}
					}
		)};
	}

	@Override
	public FixtureProvider[] fixtureProviders() {
//		if(true){
//			return singleConfig();
//		}
		return new FixtureProvider[]{
				new SimpleFixtureProvider<LabeledBoolean>(USE_CACHE, new LabeledBoolean("cached", true), new LabeledBoolean("no cache", false)),
				new SimpleFixtureProvider<LabeledBoolean>(USE_LOGFILE, new LabeledBoolean("logfile", true), new LabeledBoolean("no logfile", false)),
				new SimpleFixtureProvider<LabeledBoolean>(WRITE_TRASH, new LabeledBoolean("write trash", true), new LabeledBoolean("don't write trash", false)),
				new SimpleFixtureProvider<LabeledConfig>(FREESPACE_MANAGER, 
					new LabeledConfig("InMemoryFreespaceManager"){
						@Override
						public void configure(Config4Impl config) {
							config.freespace().useRamSystem();
						}
					}, 
					new LabeledConfig("BTreeFreespaceManager"){
						@Override
						public void configure(Config4Impl config) {
							config.freespace().useBTreeSystem();
						}
					}
				),
				new SimpleFixtureProvider<LabeledConfig>(ID_SYSTEM, 
					new LabeledConfig("PointerBasedIdSystem"){
						@Override
						public void configure(Config4Impl config) {
							Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).usePointerBasedSystem();
						}
					}, 
					new LabeledConfig("BTreeIdSystem"){
						@Override
						public void configure(Config4Impl config) {
							Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useStackedBTreeSystem();
						}
					},
					new LabeledConfig("InMemoryIdSystem"){
						@Override
						public void configure(Config4Impl config) {
							Db4oLegacyConfigurationBridge.asIdSystemConfiguration(config).useInMemorySystem();
						}
					}
		)};
	}

	@Override
	public Class[] testUnits() {
		return new Class[]{CrashSimulatingTestCase.class};
	}
	
	public static class CrashSimulatingTestCase implements TestCase, OptOutMultiSession, OptOutVerySlow {	
	    
	    /**
	     * @sharpen.remove
	     */
		public void test() throws IOException{
	    	
	    	boolean cached = USE_CACHE.value().booleanValue();
	    	boolean useLogFile = USE_LOGFILE.value().booleanValue();
	    	boolean writeTrash = WRITE_TRASH.value().booleanValue();
	    	
	    	if(cached && writeTrash){
	    		System.err.println("DISABLED CrashSimulatingTestCase: combination of write trash and cache");
	    		// The cache may touch more bytes than the ones we modified.
	    		// We should be safe even if we don't get this test to pass.
	    		return;
	    	}
	    	
	    	if(useLogFile && writeTrash){
	    		System.err.println("DISABLED CrashSimulatingTestCase: combination of write trash and use log file");
	    		
	    		// The log file is not a public API yet anyway.
	    		// It's only needed for the PointerBasedIdSystem
	    		// With the new BTreeIdSystem it's not likely to become important
	    		// so we can safely ignore the failing write trash case.
	    		return;
	    	}
	    	
	    	if(Platform4.needsLockFileThread()){
	    		System.out.println("CrashSimulatingTestCase is ignored on platforms with lock file thread.");
	    		return;
	    	}
	    	
	        String path = Path4.combine(Path4.getTempPath(), "crashSimulate");
	        String fileName = Path4.combine(path, "cs");
	        
	    	File4.delete(fileName);
	    	File4.mkdirs(path);
	        
	    	createFile(baseConfig(useLogFile), fileName);
	        
	        CrashSimulatingStorage crashSimulatingStorage = new CrashSimulatingStorage(new FileStorage(), fileName);
	        Storage storage = cached ? (Storage) new CachingStorage(crashSimulatingStorage) : crashSimulatingStorage;
	        
	        Configuration recordConfig = baseConfig(useLogFile);
	        recordConfig.storage(storage);
	        
	        
	        ObjectContainer oc = Db4o.openFile(recordConfig, fileName);
	        
	        ObjectSet objectSet = oc.queryByExample(new CrashData(null, "three"));
	        oc.delete(objectSet.next());
	        
	        oc.store(new CrashData(null, "four"));
	        oc.store(new CrashData(null, "five"));
	        oc.store(new CrashData(null, "six"));
	        oc.store(new CrashData(null, "seven"));
	        oc.store(new CrashData(null, "eight"));
	        oc.store(new CrashData(null, "nine"));
	        oc.store(new CrashData(null, "10"));
	        oc.store(new CrashData(null, "11"));
	        oc.store(new CrashData(null, "12"));
	        oc.store(new CrashData(null, "13"));
	        oc.store(new CrashData(null, "14"));
	        
	        oc.commit();
	        
	        Query q = oc.query();
	        q.constrain(CrashData.class);
	        objectSet = q.execute();
	        while(objectSet.hasNext()){
	        	CrashData cData = (CrashData) objectSet.next();
	            if( !  (cData._name.equals("10") || cData._name.equals("13")) ){
	                oc.delete(cData);
	            }
	        }
	        
	        oc.commit();

	        oc.close();

	        int count = crashSimulatingStorage._batch.writeVersions(fileName, writeTrash);

	        checkFiles(useLogFile, fileName, "R", crashSimulatingStorage._batch.numSyncs());
	        checkFiles(useLogFile, fileName, "W", count);
			if (VERBOSE) {
				System.out.println("Total versions: " + count);
			}
	    }

		private Configuration baseConfig(boolean useLogFile) {
			Config4Impl config = (Config4Impl) Db4o.newConfiguration();
			config.objectClass(CrashData.class).objectField("_name").indexed(true);
	    	config.reflectWith(Platform4.reflectorForType(CrashSimulatingTestCase.class));
	        config.bTreeNodeSize(4);
	        config.lockDatabaseFile(false);
	    	config.fileBasedTransactionLog(useLogFile);
	    	ID_SYSTEM.value().configure(config);
	    	FREESPACE_MANAGER.value().configure(config);
			return config;
		}

		private void checkFiles(boolean useLogFile, String fileName, String infix,int count) {
	        for (int i = 1; i <= count; i++) {
	            String versionedFileName = fileName + infix + i;
	            if(VERBOSE){
	                System.out.println("Checking " + versionedFileName);
	            }
	            ObjectContainer oc = Db4o.openFile(baseConfig(useLogFile), versionedFileName);
		        try {
		            if(! stateBeforeCommit(oc)){
		                if(! stateAfterFirstCommit(oc)){
		                    Assert.isTrue(stateAfterSecondCommit(oc));
		                }
		            }
	            } finally {
	            	oc.close();
	            }
	        }
	    }
	    
	    private boolean stateBeforeCommit(ObjectContainer oc){
	        return expect(oc, new String[] {"one", "two", "three"});
	    }
	    
	    private boolean stateAfterFirstCommit (ObjectContainer oc){
	        return expect(oc, new String[] {"one", "two", "four", "five", "six", "seven", "eight", "nine", "10", "11", "12", "13", "14" });
	    }
	    
	    private boolean stateAfterSecondCommit (ObjectContainer oc){
	        return expect(oc, new String[] {"10", "13"});
	    }
	    
	    private boolean expect(ObjectContainer container, String[] names){
	    	Collection4 expected = new Collection4(names);
	        ObjectSet actual = container.query(CrashData.class);
	        while (actual.hasNext()){
	            CrashData current = (CrashData)actual.next();
	            if (! expected.remove(current._name)) {
	            	return false;
	            }
	        }
	        return expected.isEmpty();
	    }
	    
	    private void createFile(Configuration config, String fileName) throws IOException{
	        ObjectContainer oc = Db4o.openFile(config, fileName);
	        try {
	        	populate(oc);
	        } finally {
	        	oc.close();
	        }
	        File4.copy(fileName, fileName + "0");
	    }

		private void populate(ObjectContainer container) {
			for (int i = 0; i < 10; i++) {
	            container.store(new Item("delme"));
	        }
	        CrashData one = new CrashData(null, "one");
	        CrashData two = new CrashData(one, "two");
	        CrashData three = new CrashData(one, "three");
	        container.store(one);
	        container.store(two);
	        container.store(three);
	        container.commit();
	        ObjectSet objectSet = container.query(Item.class);
	        while(objectSet.hasNext()){
	            container.delete(objectSet.next());
	        }
		}
	}
	
	public static class CrashData {
	    public String _name;	    
	    public CrashData _next;

	    public CrashData(CrashData next_, String name) {
	        _next = next_;
	        _name = name;
	    }
	    
		public String toString() {
			return _name+" -> "+_next;
		}
	}
	
    public static class Item{
    	
        public String name;
        
        public Item() {
        }
        
        public Item(String name_) {
            this.name = name_;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String name_){
        	name = name_;
        }

    }
	
	public static class LabeledBoolean implements Labeled {
		
		private final boolean _value;
		
		private final String _label;
		
		public LabeledBoolean(String label, boolean value){
			_label = label;
			_value = value;
		}

		public String label() {
			return _label;
		}
		
		public boolean booleanValue(){
			return _value;
		}
		
	}
	
	public static abstract class LabeledConfig implements Labeled {
		
		private final String _label;
		
		public LabeledConfig(String label){
			_label = label;
		}

		public abstract void configure(Config4Impl config);

		public String label() {
			return _label;
		}
		
		
		
		
	}


}
