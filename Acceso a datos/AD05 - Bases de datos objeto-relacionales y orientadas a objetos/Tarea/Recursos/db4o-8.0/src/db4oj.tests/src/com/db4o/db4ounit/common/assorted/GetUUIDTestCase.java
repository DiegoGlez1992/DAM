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
package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class GetUUIDTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new GetUUIDTestCase().runAll();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.generateUUIDs(ConfigScope.GLOBALLY);
    }
    
    protected void store() throws Exception {
        store(new Item("Item to be deleted"));
    }
    
    /*
     * Regression test for COR-546
     */
    public void testGetUUIDInCommittedCallbacks() {
    	
    	final Db4oUUID itemUUID = getItemUUID();
    	
        serverEventRegistry().committed().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                CommitEventArgs commitEventArgs = (CommitEventArgs) args;
                Iterator4 deletedObjectInfoCollection = commitEventArgs.deleted()
                        .iterator();
                while (deletedObjectInfoCollection.moveNext()) {
                    ObjectInfo objectInfo = (ObjectInfo) deletedObjectInfoCollection.current();
                    Assert.areEqual(itemUUID, objectInfo.getUUID());
                }
            }
        });
        
        deleteAll(Item.class);
        db().commit();
    }

	private Db4oUUID getItemUUID() {
		return getItemInfo().getUUID();
	}

	private ObjectInfo getItemInfo() {
		return db().ext().getObjectInfo(retrieveOnlyInstance(Item.class));
	}

    public void testGetUUIDInCommittingCallbacks() {
        serverEventRegistry().committing().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                CommitEventArgs commitEventArgs = (CommitEventArgs) args;
                Iterator4 deletedObjectInfoCollection = commitEventArgs.deleted()
                        .iterator();
                while (deletedObjectInfoCollection.moveNext()) {
                    ObjectInfo objectInfo = (ObjectInfo) deletedObjectInfoCollection.current();
                    Assert.isNotNull(objectInfo.getUUID());
                }
            }
        });
        
        deleteAll(Item.class);
        db().commit();
    }
    
    public static class Item {
        public String _name;

        public Item(String name) {
            _name = name;
        }
        
        public String toString() {
            return _name;
        }
    }
}
