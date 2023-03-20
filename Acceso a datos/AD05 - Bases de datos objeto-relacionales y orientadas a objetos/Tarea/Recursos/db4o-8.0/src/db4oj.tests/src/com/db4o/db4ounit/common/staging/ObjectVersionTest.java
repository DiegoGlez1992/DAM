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
package com.db4o.db4ounit.common.staging;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ObjectVersionTest extends AbstractDb4oTestCase {
	
	protected void configure(Configuration config) {
		config.generateUUIDs(ConfigScope.GLOBALLY);
		config.generateCommitTimestamps(true);
	}

	public void test() {
		final ExtObjectContainer oc = this.db();
		Item object = new Item("c1");
		
		oc.store(object);
		
		ObjectInfo objectInfo1 = oc.getObjectInfo(object);
		long oldVer = objectInfo1.getCommitTimestamp();

		//Update
		object.setName("c3");
		oc.store(object);

		ObjectInfo objectInfo2 = oc.getObjectInfo(object);
		long newVer = objectInfo2.getCommitTimestamp();

		Assert.isNotNull(objectInfo1.getUUID());
		Assert.isNotNull(objectInfo2.getUUID());

		Assert.isTrue(oldVer > 0);
		Assert.isTrue(newVer > 0);

		Assert.areEqual(objectInfo1.getUUID(), objectInfo2.getUUID());
		Assert.isTrue(newVer > oldVer);
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

}
