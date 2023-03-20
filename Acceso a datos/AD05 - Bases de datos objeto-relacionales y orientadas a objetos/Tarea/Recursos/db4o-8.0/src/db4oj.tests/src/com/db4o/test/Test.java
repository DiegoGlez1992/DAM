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
package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.internal.config.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.tools.*;

public class Test extends AllTests {
	
    private static ObjectServer objectServer;
    private static ExtObjectContainer oc;
    private static ExtObjectContainer _replica;

    static AllTests currentRunner;
    public static boolean clientServer = true;
    static boolean runServer = true;
    static int errorCount = 0;
    public static int assertionCount = 0;
    static int run;
    
    static byte[] memoryFileContent;
    
    public static final boolean COMPARE_INTERNAL_OK = false;

    public static boolean canCheckFileSize() {
    	if(Deploy.debug){
    		return false;
    	}
        if (currentRunner != null) {
            return !clientServer || !currentRunner.REMOTE_SERVER;    
            
        }
        return false;
    }
    
    public static void beginTesting(){
    	File file = new File(BLOB_PATH);
    	file.mkdirs();
    	if(! file.exists()) {
			System.out.println("Unable to create blob directory: " + BLOB_PATH);
    	}
    }
    
    private static Class classOf(Object obj){
    	if(obj == null){
    		return null;
    	}
    	if(obj instanceof Class){
    		return (Class)obj;
    	}
    	return obj.getClass();
    }

    public static void close() {
		closeClient();
		closeServer();
        closeReplica();
    }

	private static void closeReplica() {
	    if(_replica != null){
            while(!_replica.close()) {
            }
            _replica = null;
        }
    }

	private static void closeServer() {
	    if (null != objectServer) {
			objectServer.close();
			objectServer = null;
		}
    }

	private static void closeClient() {
	    if (null != oc) {
	        while (!oc.close()) {
	        }
			oc = null;
		}
    }

    public static void commit() {
        oc.commit();
    }
    
    public static void configureMessageLevel(){
    	Db4o.configure().messageLevel(-1);
    }
    
    public static ObjectServer currentServer(){
    	if(clientServer && runServer){
    		return objectServer;
    	}
    	return null;
    }

    public static void defragment(){
    	defragment(false);
    }

    public static void defragment(boolean excludeSerialized){
        String fileName = FILE_SOLO;
        close();
        if (isClientServer()) {
            server().close();
            fileName = FILE_SERVER;
            objectServer=null;
        }
        try {
            
            String targetFile = fileName + ".defrag.backup";
            DefragmentConfig defragConfig = new DefragmentConfig(fileName, targetFile);
            defragConfig.forceBackupDelete(true);
            // super ugly hack to avoid trying to defrag serialized classes without having the translator installed
            if(excludeSerialized) {
	            defragConfig.storedClassFilter(new StoredClassFilter() {
					public boolean accept(StoredClass storedClass) {
						StoredField[] fields = storedClass.getStoredFields();
						return fields.length < 2 || !fields[0].getName().endsWith(TSerializable.class.getName());
					}
	            });
            }
			com.db4o.defragment.Defragment.defrag(defragConfig);
           
        } catch(Exception e){
            e.printStackTrace();
        }
        open();
    }

    public static void delete() {
        new File(FILE_SOLO).delete();
        new File(FILE_SERVER).delete();
        new File(replicatedFileName(false)).delete();
        new File(replicatedFileName(true)).delete();
    }

    public static void delete(Object obj) {
        objectContainer().delete(obj);
    }
    
    public static void deleteAll(ObjectContainer container) {
		deleteObjectSet(container, container.queryByExample(null));
	}

	public static void deleteObjectSet(ObjectContainer container, ObjectSet all) {
		while (all.hasNext()) {
			container.delete(all.next());
		}
	}

    public static void deleteAllInstances(Object obj) {
    	try {
			Query q = oc.query();
			q.constrain(classOf(obj));
			deleteObjectSet(oc, q.execute());
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

	public static void end() {
        if (oc != null) {
            while (!oc.close()) {
            }
        }
        if (objectServer != null) {
            Runtime4.sleep(1000);
            objectServer.close();
            objectServer = null;
        }
    }

    public static boolean ensure(boolean condition,String msg) {
        assertionCount++;
        if (!condition) {
            error(msg);
            return false;
        }
        return true;
    }

    public static boolean ensure(boolean condition) {
    	return ensure(condition,null);
    }

    public static boolean ensureEquals(Object exp,Object actual) {
    	return ensureEquals(exp,actual,null);
    }

    public static boolean ensureEquals(Object exp,Object actual,String msg) {
        assertionCount++;
        if (!exp.equals(actual)) {
            String errMsg = "Expected "+exp+" but was "+actual;
            if(msg!=null) {
            	errMsg=msg+"\n"+errMsg;
            }
			error(errMsg);
            return false;
        }
        return true;
    }

    public static boolean ensureEquals(int exp,int actual) {
    	return ensureEquals(exp,actual,null);
    }

    public static boolean ensureEquals(int exp,int actual,String msg) {
    	return ensureEquals(new Integer(exp),new Integer(actual),msg);
    }

    public static void ensureOccurrences(Object obj, int count) {
        assertionCount++;
		int occ = occurrences(obj);
		if(occ != count) {
			error("Expected count: " + count + " Count was:" + occ);
		}
    }
	
	public static void error(String msg) {
        errorCount++;
		if(msg != null) {
			new Exception(msg).printStackTrace();
		}else {
			new Exception().printStackTrace();
		}
	}

    public static void error() {
		error(null);
    }

    public static int fileLength() {
        String fileName = clientServer ? FILE_SERVER : FILE_SOLO;
        return (int) new File(fileName).length();
    }

    public static void forEach(Object obj, Visitor4 vis) {
        ObjectContainer con = objectContainer();
        con.deactivate(obj, Integer.MAX_VALUE);
        ObjectSet set = oc.queryByExample(obj);
        while (set.hasNext()) {
            vis.visit(set.next());
        }
    }

    public static Object getOne(Object obj) {
		Query q = oc.query();
		q.constrain(classOf(obj));
		ObjectSet set = q.execute();
		if (set.size() != 1) {
			error();
		}
        return set.next();
    }
    
    public static boolean isClientServer(){
    	return currentServer() != null;
    }

    public static void log(Query q) {
        ObjectSet set = q.execute();
        while (set.hasNext()) {
            Logger.log(oc, set.next());
        }
    }

    public static void logAll() {
        ObjectSet set = oc.queryByExample(null);
        while (set.hasNext()) {
            Logger.log(oc, set.next());
        }
    }

    public static ExtObjectContainer objectContainer() {
        if (oc == null) {
            open();
        }
        return oc;
    }

    public static int occurrences(Object obj) {
        Query q = oc.query();
        q.constrain(classOf(obj));
        return q.execute().size();
    }

    public static ExtObjectContainer open() {
        if (runServer && clientServer && objectServer == null) {
            objectServer = Db4oClientServer.openServer(Db4oClientServerLegacyConfigurationBridge.asServerConfiguration(Db4o.cloneConfiguration()), FILE_SERVER, SERVER_PORT);
            
            // Null can happen, for EncryptionWrongPassword            
            if(objectServer != null){
                objectServer.grantAccess(DB4O_USER, DB4O_PASSWORD);
                objectServer.ext().configure().messageLevel(0);
            }
            else {
            	throw new RuntimeException("Couldn't open server.");
            }
        }
        if (clientServer) {
            oc = openClient();
        } else {
            oc = Db4o.openFile(FILE_SOLO).ext();
        }
        return oc;
    }
    
    public static ExtObjectContainer openClient(){
        if (clientServer) {
            try {
                
                if(EMBEDDED_CLIENT){
                    return objectServer.openClient().ext();
                }
                
                return Db4oClientServer.openClient(Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(Db4o.cloneConfiguration()), SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                // oc = objectServer.openClient().ext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Query query() {
        return objectContainer().query();
    }

    public static ObjectContainer reOpen() {
        close();
        return open();
    }
    
    public static ObjectContainer reOpenServer(){
		if(runServer && clientServer){
			close();
			if(objectServer!=null) {
				objectServer.close();
				objectServer = null;
			}
			Runtime4.sleep(500);
			return open();
		}else{
			return reOpen();
		}
    }
    
    public static ExtObjectContainer replica(){
        if(_replica != null){
            while(!_replica.close());
        }
        _replica = Db4o.openFile(replicatedFileName(isClientServer())).ext();
        return _replica;
    }
    
    private static String replicatedFileName(boolean clientServer){
        if(clientServer){
            return "replicated_" + FILE_SERVER;
        }
        return "replicated_" + FILE_SOLO;
        
    }

    public static void rollBack() {
        objectContainer().rollback();
    }
    
    public static ObjectServer server(){
    	return objectServer;
    }
    

    public static void store(Object obj) {
        objectContainer().store(obj);
    }

    public static void statistics() {
        Statistics.main(new String[] { FILE_SOLO });
    }

	public static void commitSync(ExtObjectContainer client1, ExtObjectContainer client2) {
		client1.setSemaphore("sem", 0);
		client1.commit();
		client1.releaseSemaphore("sem");
		ensure(client2.setSemaphore("sem", 5000));
		client2.releaseSemaphore("sem");
	}

}
