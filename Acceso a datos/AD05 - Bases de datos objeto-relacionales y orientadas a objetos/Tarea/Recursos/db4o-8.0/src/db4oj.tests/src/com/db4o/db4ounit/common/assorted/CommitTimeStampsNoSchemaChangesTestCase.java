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
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CommitTimeStampsNoSchemaChangesTestCase extends AbstractDb4oTestCase{
    
    @Override
    protected void configure(Configuration config) throws Exception {
    	config.generateCommitTimestamps(true);
    }

    @Override
    protected void store() throws Exception {
    	store(new Holder());
    	intializeCSClasses();
    }

	private void intializeCSClasses() {
		for (Holder holder : db().query(Holder.class)) {
			db().getID(holder);
    	}
	}

    public void testCommitTimestampsNoSchemaDetection() throws Exception{
    	fixture().configureAtRuntime(new RuntimeConfigureAction() {
			public void apply(Configuration config) {
				config.detectSchemaChanges(false);
				config.generateCommitTimestamps(true);
			}
		});
    	reopen();
        store(new Holder());
        commit();
        
        for (Holder holder : db().query(Holder.class)) {
            final ObjectInfo objectInfo = db().ext().getObjectInfo(holder);
            final long ts = objectInfo.getCommitTimestamp();
            Assert.isGreater(0, ts);
        }
    }

    public static class Holder{
        public String data = "data";
    }
}
