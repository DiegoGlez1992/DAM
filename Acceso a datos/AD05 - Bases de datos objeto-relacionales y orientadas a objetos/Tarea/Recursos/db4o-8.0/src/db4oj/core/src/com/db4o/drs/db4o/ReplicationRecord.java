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
package com.db4o.drs.db4o;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * tracks the version of the last replication between
 * two Objectcontainers.
 * 
 * @exclude
 * @persistent
 */
public class ReplicationRecord implements Internal4{
   
    public Db4oDatabase _youngerPeer;
    
    public Db4oDatabase _olderPeer;
    
    public long _version;
    
    public long[] _concurrentTimestamps;
    
    public ReplicationRecord(){
    }
    
    public ReplicationRecord(Db4oDatabase younger, Db4oDatabase older){
        _youngerPeer = younger;
        _olderPeer = older;
    }
    
    public void setVersion(long version){
        _version = version;
    }
    
    public void store(ObjectContainerBase container){
    	store(container.checkTransaction());
    }
    
    public void store(Transaction trans){
    	ObjectContainerBase container = trans.container();
        container.showInternalClasses(true);
        try {
	        container.storeAfterReplication(trans, this, container.updateDepthProvider().forDepth(Integer.MAX_VALUE), false);
        } finally {
        	container.showInternalClasses(false);
        }
    }
    
    public static ReplicationRecord beginReplication(Transaction transA, Transaction  transB){
        
        ObjectContainerBase peerA = transA.container();
        ObjectContainerBase peerB = transB.container();
        
        Db4oDatabase dbA = ((InternalObjectContainer)peerA).identity();
        Db4oDatabase dbB = ((InternalObjectContainer)peerB).identity();
        
        dbB.bind(transA);
        dbA.bind(transB);
        
        Db4oDatabase younger = null;
        Db4oDatabase older = null;
        
        if(dbA.isOlderThan(dbB)){
            younger = dbB;
            older = dbA;
        }else{
            younger = dbA;
            older = dbB;
        }
        
        ReplicationRecord rrA = queryForReplicationRecord(peerA, transA, younger, older);
        ReplicationRecord rrB = queryForReplicationRecord(peerB, transB, younger, older);
        if(rrA == null){
            if(rrB == null){
                return new ReplicationRecord(younger, older);
            }
            rrB.store(peerA);
            return rrB;
        }
        
        if(rrB == null){
            rrA.store(peerB);
            return rrA;
        }
        
        if(rrA != rrB){
            peerB.showInternalClasses(true);
            try {
	            int id = peerB.getID(transB, rrB);
	            peerB.bind(transB, rrA, id);
	        } finally {
            	peerB.showInternalClasses(false);
            }
        }
        
        return rrA;
    }
    
    public static ReplicationRecord queryForReplicationRecord(ObjectContainerBase container, Transaction trans, Db4oDatabase younger, Db4oDatabase older) {
        container.showInternalClasses(true);
        try {
	        Query q = container.query(trans);
	        q.constrain(ReplicationRecord.class);
	        q.descend("_youngerPeer").constrain(younger).identity();
	        q.descend("_olderPeer").constrain(older).identity();
	        ObjectSet objectSet = q.execute();
	        if(objectSet.hasNext()){
	        	ReplicationRecord replicationRecord = (ReplicationRecord) objectSet.next();
	        	container.activate(replicationRecord, Integer.MAX_VALUE);
	        	return replicationRecord;
	        }
	        return null;
        } finally {
        	container.showInternalClasses(false);
        }
    }

	public void concurrentTimestamps(List<Long> concurrentTimestamps) {
		_concurrentTimestamps = Arrays4.toLongArray(concurrentTimestamps);
	}
	
	public long[] concurrentTimestamps(){
		return _concurrentTimestamps;
	}
	
}

