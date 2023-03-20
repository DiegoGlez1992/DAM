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
package com.db4o.db4ounit.common.tp;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CommitTimeStampsWithTPTestCase extends AbstractDb4oTestCase{

    public void testWorksWithoutTP() {
        assertUpdate(false);
    }

	private void assertUpdate(boolean isTP) {
		final NamedItem item = new NamedItem();
        store(item);
        commit();
        final long firstTS = commitTimestampFor(item);
        item.setName("New Name");
        if(! isTP){
        	store(item);
        }
        commit();
        final long secondTS = commitTimestampFor(item);
        assertChangesHaveBeenStored(db());
        Assert.isTrue(secondTS>firstTS);
	}

	private long commitTimestampFor(final NamedItem item) {
		return db().ext().getObjectInfo(item).getCommitTimestamp();
	}

    public void testWorksWithTP() throws Exception {
		fixture().configureAtRuntime(new RuntimeConfigureAction() {
			public void apply(Configuration config) {
				config.add(new TransparentPersistenceSupport());	
			}
		});
		reopen();
		assertUpdate(true);
    }

    private void assertChangesHaveBeenStored(ObjectContainer container) {
        ObjectContainer session = container.ext().openSession();
        try{
            final NamedItem item = session.query(NamedItem.class).get(0);
            Assert.areEqual("New Name", item.getName());
        } finally {
            session.close();
        }
    }
    
    @Override
    protected void configure(Configuration config) throws Exception {
    	config.generateCommitTimestamps(true);
    	config.storage(new MemoryStorage());
    }

    public static class NamedItem implements Activatable {

        private transient Activator _activator;

        private String name = "default";

        public void setName(String name) {
            activate(ActivationPurpose.WRITE);
            this.name = name;
        }

        public String getName() {
            activate(ActivationPurpose.READ);
            return this.name;
        }

        public void activate(ActivationPurpose purpose) {
            if (_activator != null) {
                _activator.activate(purpose);
            }
        }

        public void bind(Activator activator) {
            if (_activator == activator) {
                return;
            }
            if (activator != null && _activator != null) {
                throw new IllegalStateException();
            }
            _activator = activator;

        }
    }
}
