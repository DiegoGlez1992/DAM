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
package com.db4o.cluster;

import com.db4o.*;
import com.db4o.internal.cluster.*;
import com.db4o.query.*;

/**
 * allows running Queries against multiple ObjectContainers.
 * @exclude   
 */
public class Cluster {
    
    public final ObjectContainer[] _objectContainers;
    
    /**
     * use this constructor to create a Cluster and call
     * add() to add ObjectContainers
     */
    public Cluster(ObjectContainer[] objectContainers){
        if(objectContainers == null){
            throw new NullPointerException();
        }
        if(objectContainers.length < 1){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < objectContainers.length; i++) {
            if(objectContainers[i] == null){
                throw new IllegalArgumentException();
            }
        }
        _objectContainers = objectContainers;
    }
    
    /**
     * starts a query against all ObjectContainers in 
     * this Cluster.
     * @return the Query
     */
    public Query query(){
        synchronized(this){
            Query[] queries = new Query[_objectContainers.length];
            for (int i = 0; i < _objectContainers.length; i++) {
                queries[i] = _objectContainers[i].query(); 
            }
            return new ClusterQuery(this, queries);
        }
    }
    
    /**
     * returns the ObjectContainer in this cluster where the passed object
     * is stored or null, if the object is not stored to any ObjectContainer
     * in this cluster
     * @param obj the object
     * @return the ObjectContainer
     */
    public ObjectContainer objectContainerFor(Object obj){
        synchronized(this){
            for (int i = 0; i < _objectContainers.length; i++) {
                if(_objectContainers[i].ext().isStored(obj)){
                    return _objectContainers[i];
                }
            }
        }
        return null;
    }
    
}
