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
package com.db4o.db4ounit.common.uuid;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UUIDTestCase extends AbstractDb4oTestCase {
    
    private static long storeStartTime;
    
    private static long storeEndTime;
	
	public static void main(String[] args) {
		new UUIDTestCase().runAll();
	}
	
    public static class Item {
        
        public String name;

        public Item(String name_) {
            this.name = name_;
        }
        
    }
	
	protected void configure(Configuration config) {
		config.objectClass(Item.class).generateUUIDs(true);
	}

	protected void store() {
	    storeStartTime = System.currentTimeMillis();
		db().store(new Item("one"));
		db().commit();
		storeEndTime = System.currentTimeMillis();
		db().store(new Item("two"));
	}

	public void testRetrieve() throws Exception {
		Hashtable4 uuidCache = new Hashtable4();
		assertItemsCanBeRetrievedByUUID(uuidCache);
		reopen();
		assertItemsCanBeRetrievedByUUID(uuidCache);
	}
	
	public void testTimeStamp(){
	    Query q = newItemQuery();
	    q.descend("name").constrain("one");
	    Item item = (Item) q.execute().next();
	    Db4oUUID uuid = uuid(item);
	    long longPart = uuid.getLongPart();
	    long creationTime = TimeStampIdGenerator.idToMilliseconds(longPart);
	    Assert.isGreaterOrEqual(storeStartTime, creationTime);
	    Assert.isSmallerOrEqual(storeEndTime, creationTime);
	}

    protected void assertItemsCanBeRetrievedByUUID(Hashtable4 uuidCache) {
        Query q = newItemQuery();
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
            Item item = (Item) objectSet.next();
            Db4oUUID uuid = uuid(item);            
            Assert.isNotNull(uuid);
            Assert.areSame(item, db().getByUUID(uuid));
            final Db4oUUID cached = (Db4oUUID) uuidCache.get(item.name);
            if (cached != null) {
                Assert.areEqual(cached, uuid);
            } else {
                uuidCache.put(item.name, uuid);
            }
        }
    }

    private Db4oUUID uuid(Object obj) {
        return db().getObjectInfo(obj).getUUID();
    }

    private Query newItemQuery() {
        return newQuery(Item.class);
    }

}
