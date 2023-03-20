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
package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class EventRegistryTestCase extends AbstractDb4oTestCase {
	
	public void testForObjectContainerReturnsSameInstance() {
		Assert.areSame(
				EventRegistryFactory.forObjectContainer(db()),
				EventRegistryFactory.forObjectContainer(db()));
	}

	public void testQueryEvents() {

		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());

		EventRecorder recorder = new EventRecorder(fileSession().lock());
		
		registry.queryStarted().addListener(recorder);
		registry.queryFinished().addListener(recorder);

		
		Assert.areEqual(0, recorder.size());
		
		Query q = db().query();
		q.execute();
		
		Assert.areEqual(2, recorder.size());
		EventRecord e1 = recorder.get(0);
		Assert.areSame(registry.queryStarted(), e1.e);
		Assert.areSame(q, ((QueryEventArgs)e1.args).query());

		EventRecord e2 = recorder.get(1);
		Assert.areSame(registry.queryFinished(), e2.e);
		Assert.areSame(q, ((QueryEventArgs)e2.args).query());

		recorder.clear();

		registry.queryStarted().removeListener(recorder);
		registry.queryFinished().removeListener(recorder);

		db().query().execute();

		Assert.areEqual(0, recorder.size());
	}
}
