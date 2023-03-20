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


import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

public abstract class HandlerUpdateTestCaseBase extends FormatMigrationTestCaseBase {
    
    public static class Holder{
        
        public Object[] _values;
        
        public Object _arrays;
        
    }
    
    private int _handlerVersion;
    
    protected String fileNamePrefix() {
        return "migrate_" + typeName() + "_" ;
    }
    
    protected void store(ObjectContainerAdapter objectContainer) {
        Holder holder = new Holder();
        holder._values = createValues();
        holder._arrays = createArrays();
        objectContainer.store(holder);
    }
    
    protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
        Holder holder = retrieveHolderInstance(objectContainer);
        objectContainer.activate(holder, Integer.MAX_VALUE);
        assertValues(objectContainer, holder._values);
        assertArrays(objectContainer, holder._arrays);
        assertQueries(objectContainer);
    }

    private Holder retrieveHolderInstance(ExtObjectContainer objectContainer) {
        Query q = objectContainer.query();
        q.constrain(Holder.class);
        ObjectSet objectSet = q.execute();
        Holder holder = (Holder) objectSet.next();
        investigateHandlerVersion(objectContainer, holder);
        return holder;
    }
    
    protected void update(ExtObjectContainer objectContainer) {
        Holder holder = retrieveHolderInstance(objectContainer);
        updateValues(holder._values);
        updateArrays(holder._arrays);
        objectContainer.store(holder, Integer.MAX_VALUE);
    }
    
    protected void assertObjectsAreUpdated(ExtObjectContainer objectContainer) {
        Holder holder = retrieveHolderInstance(objectContainer);
        assertUpdatedValues(holder._values);
        assertUpdatedArrays(holder._arrays);
    }
    
    private void investigateHandlerVersion(ExtObjectContainer objectContainer, Object obj){
        _handlerVersion = VersionServices.slotHandlerVersion(objectContainer, obj);
    }

    protected abstract String typeName();

    protected abstract Object[] createValues();
    
    protected abstract Object createArrays();
    
    protected abstract void assertValues(ExtObjectContainer objectContainer, Object[] values);
    
    protected abstract void assertArrays(ExtObjectContainer objectContainer, Object obj);
    
    protected void assertQueries(ExtObjectContainer objectContainer) {
        // override on demand to check queries
    }

    protected int[] castToIntArray(Object obj){
        ObjectByRef byRef = new ObjectByRef(obj);
        castToIntArrayJavaOnly(byRef);
        return (int[]) byRef.value;
    }

    /**
     * @sharpen.remove
     */
    private void castToIntArrayJavaOnly(ObjectByRef byRef) {
        if(db4oHeaderVersion() != VersionServices.HEADER_30_40){
            return;
        }
            
        // Bug in the oldest format: 
        // It accidentally converted int[] arrays to Integer[] arrays.
        
        Integer[] wrapperArray = (Integer[])byRef.value;
        int[] res = new int[wrapperArray.length];
        for (int i = 0; i < wrapperArray.length; i++) {
            if(wrapperArray[i] != null){
                res[i] = wrapperArray[i].intValue();
            }
        }
        byRef.value = res;
    }
    
    /**
     * On .NET there are no primitive wrappers, so the primitives have 
     * their default value. Since default values are tested OK with the 
     * other values test, we don't have to test again, so it's safe to: 
     * @sharpen.remove
     */
    protected void assertPrimitiveWrapperIsNullJavaOnly(Object obj) {
        Assert.isNull(obj);
    }

    protected int db4oHandlerVersion() {
        return _handlerVersion;
    }
    
    protected void updateValues(Object[] values){
        // Override to check updates also
    }
    
    protected void updateArrays(Object obj){
        // Override to check updates also
    }

    protected void assertUpdatedValues(Object[] values){
        // Override to check updates also
    }
    
    protected void assertUpdatedArrays(Object obj){
        // Override to check updates also
    }
    
    protected boolean usesNullMarkerValue() {
    	return db4oHandlerVersion() == 0;
    }
    

}
