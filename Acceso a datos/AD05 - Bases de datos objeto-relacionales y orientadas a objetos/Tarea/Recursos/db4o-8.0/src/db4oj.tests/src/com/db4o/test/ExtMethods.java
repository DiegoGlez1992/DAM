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
package com.db4o.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

public class ExtMethods {
	
    public void configure(){
        Db4o.configure().generateUUIDs(ConfigScope.GLOBALLY);
        Db4o.configure().generateCommitTimestamps(true);
    }

	public void test(){
		
		ExtMethods em = new ExtMethods();
		Test.store(em);
		
		ExtObjectContainer eoc = Test.objectContainer();
		
		Test.ensure(! eoc.isClosed());
		
		Test.ensure(eoc.isActive(em));
		Test.ensure(eoc.isStored(em));
		
		eoc.deactivate(em, 1);
		Test.ensure(! eoc.isActive(em));
		
		eoc.activate(em, 1);
		Test.ensure(eoc.isActive(em));
		
		long id = eoc.getID(em);
		
		Test.ensure(eoc.isCached(id));
		
		eoc.purge(em);
		
		Test.ensure(! eoc.isCached(id));
		Test.ensure(! eoc.isStored(em));
		Test.ensure(! eoc.isActive(em));
		
		eoc.bind(em, id);
		
		Test.ensure(eoc.isCached(id));
		Test.ensure(eoc.isStored(em));
		Test.ensure(eoc.isActive(em));
		
		ExtMethods em2 = (ExtMethods)eoc.getByID(id);
		
		Test.ensure(em == em2);
		
		// Purge all and try again
		eoc.purge();  
		
		Test.ensure(eoc.isCached(id));
		Test.ensure(eoc.isStored(em));
		Test.ensure(eoc.isActive(em));
		
		em2 = (ExtMethods)eoc.getByID(id);
		Test.ensure(em == em2);
		
		Test.delete(em2);
		Test.commit();
		Test.ensure(! eoc.isCached(id));
		Test.ensure(! eoc.isStored(em2));
		Test.ensure(! eoc.isActive(em2));
		
		// Null checks
		Test.ensure(! eoc.isStored(null));
		Test.ensure(! eoc.isActive(null));
		Test.ensure(! eoc.isCached(0));
		
	}

}
