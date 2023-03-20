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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DatabaseUnicityTest extends AbstractDb4oTestCase  {

	public void test() {
        Hashtable4 ht = new Hashtable4();
        ObjectContainerBase container = container();
        container.showInternalClasses(true);
        Query q = db().query();
        q.constrain(Db4oDatabase.class);
        ObjectSet objectSet = q.execute();
        while (objectSet.hasNext()) {
        	Db4oDatabase identity = (Db4oDatabase) objectSet.next();
        	Assert.isFalse(ht.containsKey(identity.i_signature));
        	ht.put(identity.i_signature, "");
        }
        container.showInternalClasses(false);
    }

}
