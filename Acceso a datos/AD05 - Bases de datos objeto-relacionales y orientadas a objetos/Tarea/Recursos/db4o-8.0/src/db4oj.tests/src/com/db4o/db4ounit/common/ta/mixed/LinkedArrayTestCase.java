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
package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.db4ounit.common.ta.LinkedArrays.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.extensions.*;


public class LinkedArrayTestCase extends AbstractDb4oTestCase implements OptOutTA {
    
    static int TESTED_DEPTH = 7;

    public static void main(String[] args) {
        new LinkedArrayTestCase().runAll();
    }
    
    private Db4oUUID _linkedArraysUUID; 
    
    protected void configure(Configuration config) throws Exception {
        config.generateUUIDs(ConfigScope.GLOBALLY);
        config.add(new TransparentActivationSupport());
    }
    
    protected void store() throws Exception {
        LinkedArrays linkedArrays = LinkedArrays.newLinkedArrayRoot(TESTED_DEPTH);
        store(linkedArrays);
        _linkedArraysUUID = db().getObjectInfo(linkedArrays).getUUID();
    }
    
    public void testTheTest(){
        for (int depth = 1; depth < TESTED_DEPTH; depth++) {
            LinkedArrays linkedArrays = LinkedArrays.newLinkedArrays(depth);
            linkedArrays.assertActivationDepth(depth - 1, false);
        }
    }
    
    public void testActivateFixedDepth(){
        LinkedArrays linkedArrays = root();
        for (int depth = 0; depth < TESTED_DEPTH; depth++) {
            db().activate(linkedArrays, depth);
            linkedArrays.assertActivationDepth(depth, false);
            db().deactivate(linkedArrays, Integer.MAX_VALUE);
        }
    }
    
    public void testActivatingActive(){
        LinkedArrays linkedArrays = root();
        for (int secondActivationDepth = 2; secondActivationDepth < TESTED_DEPTH; secondActivationDepth++) {
            for (int firstActivationDepth = 1; firstActivationDepth < secondActivationDepth; firstActivationDepth++) {
                db().activate(linkedArrays, firstActivationDepth);
                db().activate(linkedArrays, secondActivationDepth);
                linkedArrays.assertActivationDepth(secondActivationDepth, false);
                db().deactivate(linkedArrays, Integer.MAX_VALUE);
            }
        }
    }
    
    public void testActivateDefaultMode(){
        LinkedArrays linkedArrays = root();
        db().activate(linkedArrays);
        linkedArrays.assertActivationDepth(TESTED_DEPTH - 1, true);
    }
    
    public void testPeekPersisted(){
        LinkedArrays linkedArrays = root();
        for (int depth = 0; depth < TESTED_DEPTH; depth++) {
            LinkedArrays peeked = (LinkedArrays) db().peekPersisted(linkedArrays, depth, true);
            peeked.assertActivationDepth(depth, false);
        }
    }
    
    public void testTransparentActivationQuery(){
        LinkedArrays linkedArray = queryForRoot();
        linkedArray.assertActivationDepth(TESTED_DEPTH - 1, true);
    }
    
    public void testTransparentActivationTraversal(){
        LinkedArrays root = queryForRoot();
        ActivatableItem activatableItem = root._activatableItemArray[0];
        activatableItem.activate(ActivationPurpose.READ);
        LinkedArrays descendant = activatableItem._linkedArrays; 
        descendant.assertActivationDepth(TESTED_DEPTH - 3, true);
        db().deactivate(activatableItem, 1);
        activatableItem.activate(ActivationPurpose.READ);
        descendant.assertActivationDepth(TESTED_DEPTH - 3, true);
    }
    
    private LinkedArrays queryForRoot(){
        Query q = db().query();
        q.constrain(LinkedArrays.class);
        q.descend("_isRoot").constrain(new Boolean(true));
        return (LinkedArrays) q.execute().next();
    }
    
    private LinkedArrays root(){
        return (LinkedArrays) db().getByUUID(_linkedArraysUUID);
    }

}
