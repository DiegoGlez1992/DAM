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
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * Publishes statistics about freespace to JMX.
 */
@decaf.Ignore
public class FreespaceMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		if(! (container instanceof LocalObjectContainer) || container.configImpl().isReadOnly()){
			return;
		}
		LocalObjectContainer localObjectContainer = (LocalObjectContainer) container;
		FreespaceManager freespaceManager = localObjectContainer.freespaceManager();
		final Freespace freespace = Db4oMBeans.newFreespaceMBean(container);
		freespaceManager.listener(freespace);
		freespaceManager.traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				freespace.slotAdded(slot.length());
			}
		});
	}
	
	public void prepare(Configuration configuration) {
		// do nothing
	}



}
