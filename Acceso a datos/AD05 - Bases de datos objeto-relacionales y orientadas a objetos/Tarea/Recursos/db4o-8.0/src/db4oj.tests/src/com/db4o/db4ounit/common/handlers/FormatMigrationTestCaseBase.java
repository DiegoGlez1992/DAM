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
package com.db4o.db4ounit.common.handlers;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.util.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 * @sharpen.partial
 */
public abstract class FormatMigrationTestCaseBase implements TestLifeCycle, OptOutNoFileSystemData, OptOutMultiSession, OptOutWorkspaceIssue {
	
    private static final String USERNAME = "db4o";

    private static final String PASSWORD = USERNAME;
    
    private String _db4oVersion;
    
    public void configure(){
        Configuration config = Db4o.configure();
        config.allowVersionUpdates(true);
        configureForTest(config);
    }

    private final void deconfigure(){
        Configuration config = Db4o.configure();
        config.allowVersionUpdates(false);
        deconfigureForTest(config);
    }
    /**
     * @sharpen.ignore
     */
	private static String getTempPath() {
		return Path4.getTempPath();
	}
    
    private byte _db4oHeaderVersion;
    
    
    public void createDatabase() {
        createDatabase(fileName());
    }
    
    public void createDatabaseFor(String versionName) {
        _db4oVersion = versionName;
        Configuration config = Db4o.configure();
        try{
        	configureForStore(config);
        } catch(Throwable t){
        	// Some old database engines may throw NoSuchMethodError
        	// for configuration methods they don't know yet. Ignore,
        	// but tell the implementor:
        	
        	// System.out.println("Exception in configureForStore for " + versionName + " in " + getClass().getName());
        }
        try {
        	createDatabase(fileName(versionName));
        }
        finally {
        	deconfigureForStore(config);
        }
    }
    
    public void setUp() throws Exception {
        configure();
        createDatabase();
    }
    
    public void test() throws IOException{
        for(int i = 0; i < versionNames().length; i ++){
            final String versionName = versionNames()[i];
			test(versionName);
        }
    }

	public void test(final String versionName) throws IOException {
	    _db4oVersion = versionName;
	    if (!isApplicableForDb4oVersion()) {
			return;
		}
		String testFileName = fileName(versionName); 
		if(! File4.exists(testFileName)){
		    System.out.println("Version upgrade check failed. File not found:" + testFileName);
		    
		    // FIXME: The following fails the CC build since not all files are there on .NET.
		    //        Change back when we have all files.
		    // Assert.fail("Version upgrade check failed. File not found:" + testFileName);
		    
		    return;
		}
		
//      System.out.println("Checking database file: " + testFileName);
		
	    investigateFileHeaderVersion(testFileName);
	    
	    prepareClientServerTest(testFileName);
	    
	    try{
	    
		    runDeletionTests(testFileName);
		    
			defragmentSoloAndCS(testFileName);
	
		    checkDatabaseFile(testFileName);
		    // Twice, to ensure everything is fine after opening, converting and closing.
		    checkDatabaseFile(testFileName);
		    
		    updateDatabaseFile(testFileName);
		    
		    checkUpdatedDatabaseFile(testFileName);
	
			defragmentSoloAndCS(testFileName);
	
		    checkUpdatedDatabaseFile(testFileName);
	    } finally {
	    	tearDownClientServer(testFileName);
	    }
		    
	}

	private void defragmentSoloAndCS(String fileName) throws IOException {
		runDefrag(fileName);
		runDefrag(clientServerFileName(fileName));
	}

	private void tearDownClientServer(String testFileName) {
		File4.delete(clientServerFileName(testFileName));
	}

	private void prepareClientServerTest(String fileName)
			throws IOException {
		File4.copy(fileName, clientServerFileName(fileName));
	}
    
	private String clientServerFileName(String fileName) {
		return fileName + ".CS";
	}

	private void runDeletionTests(String testFileName) throws IOException {
		withDatabase(testFileName, new Function4<ObjectContainer, Object>() { public Object apply(ObjectContainer db) {
			assertObjectDeletion(db.ext());
			return null;
		}});
			
		checkDatabaseFile(testFileName);
	}

	/**
	 * Override to provide tests for deletion.
	 */
	protected void assertObjectDeletion(ExtObjectContainer objectContainer) {
	}

	/**
	 * Can be overridden to disable the test for specific db4o versions.
	 */
    protected boolean isApplicableForDb4oVersion() {
    	return true;
	}

	private void checkDatabaseFile(String testFile) {
        withDatabase(testFile, new Function4() {
            public Object apply(Object objectContainer) {
                assertObjectsAreReadable((ExtObjectContainer) objectContainer);
                return null;
            }
        });
    }
    
    private void checkUpdatedDatabaseFile(String testFile) {
        withDatabase(testFile, new Function4() {
            public Object apply(Object objectContainer) {
                assertObjectsAreUpdated((ExtObjectContainer) objectContainer);
                return null;
            }
        });
    }

	private void createDatabase(String file) {
		
		if (!isApplicableForDb4oVersion()) {
			return;
		}
		
		File4.mkdirs(databasePath());
        if(File4.exists(file)){
            File4.delete(file);
        }
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        
        ObjectContainerAdapter adapter = ObjectContainerAdapterFactory.forVersion(db4oMajorVersion(), db4oMinorVersion()).forContainer(objectContainer);
        
        try {
            store(adapter);
        } finally {
            objectContainer.close();
        }
	}

	/**
	 * @sharpen.property
	 */
	private String databasePath() {
		return Path4.combine(getTempPath(), "test/db4oVersions");
	}
    
    private void investigateFileHeaderVersion(String testFile) throws IOException{
        _db4oHeaderVersion = VersionServices.fileHeaderVersion(testFile); 
    }
    
    private void runDefrag(String testFileName) throws IOException {
		Configuration config = Db4o.newConfiguration();
		config.allowVersionUpdates(true);
		configureForTest(config);
		ObjectContainer oc = Db4o.openFile(config, testFileName);
		oc.close();
		
		String backupFileName = Path4.getTempFileName();
		try{
			DefragmentConfig defragConfig = new DefragmentConfig(testFileName, backupFileName);
			defragConfig.forceBackupDelete(true);
			configureForTest(defragConfig.db4oConfig());
			defragConfig.readOnly(! defragmentInReadWriteMode());
			Defragment.defrag(defragConfig);
		} finally{
			File4.delete(backupFileName);
		}
	}
    
    public void tearDown() throws Exception {
    	deconfigure();
    }
    
    private void updateDatabaseFile(String testFile) {
        withDatabase(testFile, new Function4() {
            public Object apply(Object objectContainer) {
                update((ExtObjectContainer) objectContainer);
                return null;
            }
        });
    }
    
    private void withDatabase(String file, Function4 function){
        configure();
        ExtObjectContainer objectContainer = Db4o.openFile(file).ext();
        try {
            function.apply(objectContainer);
        } finally {
            objectContainer.close();
        }
        ObjectServer server = Db4o.openServer(clientServerFileName(file), -1);
        server.grantAccess(USERNAME, PASSWORD);
        objectContainer = Db4o.openClient("localhost", server.ext().port(), USERNAME, PASSWORD).ext();
        
        try {
        	function.apply(objectContainer);
        } finally {
        	objectContainer.close();
        	server.close();
        }
    }    

    
    protected abstract void assertObjectsAreReadable(ExtObjectContainer objectContainer);
    
    protected void assertObjectsAreUpdated(ExtObjectContainer objectContainer) {
        // Override to check updates also
    }
    
    protected void configureForStore(Configuration config){
    	// Override for special storage configuration.
    }
    
    protected void configureForTest(Configuration config){
    	// Override for special testing configuration.
    }
    
    protected byte db4oHeaderVersion() {
        return _db4oHeaderVersion;
    }
    
    protected int db4oMajorVersion(){
        if(_db4oVersion != null){
            return new Integer (_db4oVersion.substring(0, 1)).intValue();
        }
        return new Integer(Db4o.version().substring(5, 6)).intValue();
    }
    
    protected int db4oMinorVersion(){
        if(_db4oVersion != null){
            return new Integer (_db4oVersion.substring(2, 3)).intValue();
        }
        return new Integer(Db4o.version().substring(7, 8)).intValue();
    }

    /**
     * override and return true for database updates that produce changed class metadata 
     */
    protected boolean defragmentInReadWriteMode() {
        return false;
    }
    
    protected String fileName(){
        _db4oVersion = Db4oVersion.NAME;
        return fileName(_db4oVersion);
    }
    
    protected String fileName(String versionName){
        return oldVersionFileName(versionName) + ".db4o";
    }

    protected void deconfigureForStore(Configuration config){
    	// Override for special storage deconfiguration.
    }

    protected void deconfigureForTest(Configuration config){
    	// Override for special storage deconfiguration.
    }

    protected abstract String fileNamePrefix();
    
    protected String oldVersionFileName(String versionName){
        return Path4.combine(databasePath(), fileNamePrefix() + versionName.replace(' ', '_'));
    }

    protected abstract void store(ObjectContainerAdapter objectContainer);   
    
    protected void update(ExtObjectContainer objectContainer) {
        // Override to do updates also
    }
    
    protected String[] versionNames(){
        return new String[] { Db4o.version().substring(5) };
    }

}

