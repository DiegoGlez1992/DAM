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
package com.db4o.monitoring;


import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.InternalObjectContainer;
import com.db4o.internal.query.*;

/**
 * Publishes native query statistics to JMX.  
 */
@decaf.Ignore
public class NativeQueryMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {	
		final NativeQueries queries = Db4oMBeans.newNativeQueriesMBean(container);
		container.getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				queries.notifyNativeQuery(info);
			}
		});
	}

	public void prepare(Configuration configuration) {

	}

}
