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
package com.db4o.ext;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.encoding.*;
import com.db4o.query.*;
import com.db4o.types.*;


/**
 * Class to identify a database by it's signature.
 * <br><br>db4o UUID handling uses a reference to the Db4oDatabase object, that
 * represents the database an object was created on.
 * 
 * @persistent
 * @exclude
 */
public class Db4oDatabase implements Db4oType, Internal4{
    
    public static final Db4oDatabase STATIC_IDENTITY = Debug4.staticIdentity ? new Db4oDatabase(new byte[] {(byte)'d' , (byte)'e', (byte)'b', (byte)'u', (byte)'g'}, 1) : null;
    
    public static final int STATIC_ID = -1;

    /**
     * Field is public for implementation reasons, DO NOT TOUCH!
     */
    public byte[] i_signature;
    
    /**
     * Field is public for implementation reasons, DO NOT TOUCH!
     * 
     * This field is badly named, it really is the creation time.
     */
    // TODO: change to _creationTime with PersistentFormatUpdater
    public long i_uuid;
    
    private static final String CREATIONTIME_FIELD = "i_uuid"; 

    
    /**
     * cached ObjectContainer for getting the own ID.
     */
    private transient ObjectContainerBase i_stream;
    
    /**
     * cached ID, only valid in combination with i_objectContainer
     */
    private transient int i_id;
    
    /**
     * constructor for persistence
     */
    public Db4oDatabase(){
    }
    
    /**
     * constructor for comparison and to store new ones
     */
    public Db4oDatabase(byte[] signature, long creationTime){
    	// FIXME: make sure signature is null
        i_signature = signature;
        i_uuid = creationTime;
    }
    
    /**
     * generates a new Db4oDatabase object with a unique signature.
     */
    public static Db4oDatabase generate() {
        if(Debug4.staticIdentity){
            return STATIC_IDENTITY;
        }
        StatefulBuffer writer = new StatefulBuffer(null, 300);
        new LatinStringIO().write(writer, SignatureGenerator.generateSignature());
        return new Db4oDatabase(
                writer.getWrittenBytes(),
                System.currentTimeMillis());

    }
    
    /**
     * comparison by signature.
     */
    public boolean equals(Object obj) {
    	if(obj==this) {
    		return true;
    	}
    	if(obj==null||this.getClass()!=obj.getClass()) {
    		return false;
    	}
        Db4oDatabase other = (Db4oDatabase)obj;
        if (null == other.i_signature || null == this.i_signature) {
        	return false;
        }
		return Arrays4.equals(other.i_signature, this.i_signature);
    }

    public int hashCode() {
    	return i_signature.hashCode();
    }
    
	/**
	 * gets the db4o ID, and may cache it for performance reasons.
	 * 
	 * @return the db4o ID for the ObjectContainer
	 */
    public int getID(Transaction trans) {
        if(Debug4.staticIdentity){
            return STATIC_ID; 
        }
        ObjectContainerBase stream = trans.container();
        if(stream != i_stream) {
            i_stream = stream;
            i_id = bind(trans);
        }
        return i_id;
    }
    
    public long getCreationTime(){
        return i_uuid;
    }
    
    /**
     * returns the unique signature 
     */
    public byte[] getSignature(){
        return i_signature;
    }
    
    public String toString(){
        return "db " + i_signature;
    }
    
    public boolean isOlderThan(Db4oDatabase peer){
		
		if(peer == this) 
			throw new IllegalArgumentException(); 
        
        if(i_uuid != peer.i_uuid){
            return i_uuid < peer.i_uuid;
        }
        
        // the above logic has failed, both are the same
        // age but we still want to distinguish in some 
        // way, to have an order in the ReplicationRecord
        
        // The following is arbitrary, it only needs to
        // be repeatable.
        
        // Let's distinguish by signature length 
        
        if(i_signature.length != peer.i_signature.length ){
            return i_signature.length < peer.i_signature.length;
        }
        
        for (int i = 0; i < i_signature.length; i++) {
            if(i_signature[i] != peer.i_signature[i]){
                return i_signature[i] < peer.i_signature[i];
            }
        }
        
        // This should never happen.
        
        // FIXME: Add a message and move to Messages.
        // 
        throw new RuntimeException();
    }
    
    /**
     * make sure this Db4oDatabase is stored. Return the ID.  
     */
    public int bind(Transaction trans){
        ObjectContainerBase stream = trans.container();
        Db4oDatabase stored = (Db4oDatabase)stream.db4oTypeStored(trans,this);
        if (stored == null) {
        	return storeAndGetId(trans);
        }
        if(stored == this){
            return stream.getID(trans, this);
        }
        if(i_uuid == 0){
            i_uuid = stored.i_uuid;
        }
        stream.showInternalClasses(true);
        try {
	        int id = stream.getID(trans, stored);
	        stream.bind(trans, this, id);
	        return id;
	    } finally {
            stream.showInternalClasses(false);
        }
    }

	private int storeAndGetId(Transaction trans) {
		ObjectContainerBase stream = trans.container();
		stream.showInternalClasses(true);
		try {
		    stream.store2(trans,this, stream.updateDepthProvider().forDepth(2), false);
		    return stream.getID(trans, this);
		} finally {
			stream.showInternalClasses(false);
		}
	}
    
    /**
     * find a Db4oDatabase with the same signature as this one
     */
    public Db4oDatabase query(Transaction trans){
        // showInternalClasses(true);  has to be set for this method to be successful
        if(i_uuid > 0){
            // try fast query over uuid (creation time) first
            Db4oDatabase res = query(trans, true);
            if(res != null){
                return res;
            }
        }
        // if not found, try to find with signature
        return query(trans, false);
    }
    
    private Db4oDatabase query(Transaction trans, boolean constrainByUUID){
        ObjectContainerBase stream = trans.container();
        Query q = stream.query(trans);
        q.constrain(getClass());
        if(constrainByUUID){
            q.descend(CREATIONTIME_FIELD).constrain(new Long(i_uuid));
        }
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            Db4oDatabase storedDatabase = (Db4oDatabase) objectSet.next();
            stream.activate(null, storedDatabase, new FixedActivationDepth(4));
            if (storedDatabase.equals(this)) {
                return storedDatabase;
            }
        }
        return null;
    }
}
    
    
