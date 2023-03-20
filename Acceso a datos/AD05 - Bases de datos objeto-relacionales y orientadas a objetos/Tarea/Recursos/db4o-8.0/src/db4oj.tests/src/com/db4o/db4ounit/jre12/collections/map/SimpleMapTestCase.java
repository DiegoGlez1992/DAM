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
package com.db4o.db4ounit.jre12.collections.map;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class SimpleMapTestCase extends AbstractDb4oTestCase{
	
	protected void configure(Configuration config) {
		config.generateUUIDs(ConfigScope.GLOBALLY);
	}

	public static void main(String[] args) {
        new SimpleMapTestCase().runNetworking();
    }
	
	public void testGetByUUID() {
		MapContent c1 = new MapContent("c1");
		db().store(c1);	//comment me bypass the bug

		//db().getObjectInfo(c1).getUUID();	//Uncomment me bypass the bug

		MapHolder mh = new MapHolder("h1");
		mh.map.put("key1", c1);

		db().store(mh);	//comment me bypass the bug

		Db4oUUID uuid = db().getObjectInfo(c1).getUUID();

		Assert.isNotNull(db().getByUUID(uuid));	//This line fails when Test.clientServer = true;
	}
}
