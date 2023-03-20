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
package com.db4o.db4ounit.optional.monitoring;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;
import com.db4o.monitoring.*;

import db4ounit.*;
import db4ounit.extensions.OptOutNotSupportedJavaxManagement;
import db4ounit.extensions.fixtures.*;


@decaf.Remove
public class FreespaceMonitoringSupportTestCase extends MBeanTestCaseBase implements CustomClientServerConfiguration, OptOutNotSupportedJavaxManagement {
	
	public static class Item{
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.add(new FreespaceMonitoringSupport());
	}

	public void configureClient(Configuration config) throws Exception {
		configure(config);
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}
	
	public void test(){
		// ensure client is fully connected to the server already
		db().commit();
		assertMonitoredFreespaceIsCorrect();
		Item item = new Item();
		store(item);
		db().commit();
		assertMonitoredFreespaceIsCorrect();
		db().delete(item);
		db().commit();
		assertMonitoredFreespaceIsCorrect();
	}

	private void assertMonitoredFreespaceIsCorrect() {
		final IntByRef totalFreespace = new IntByRef();
		final IntByRef slotCount = new IntByRef();
		FreespaceManager freespaceManager = fileSession().freespaceManager();
		freespaceManager.traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				totalFreespace.value += slot.length();
				slotCount.value ++;
			}
		});
		Assert.areEqual(totalFreespace.value, totalFreespace());
		Assert.areEqual(slotCount.value, slotCount());
	}
	
	private Object totalFreespace() {
		return bean().getAttribute("TotalFreespace");
	}
	
	private Object slotCount() {
		return bean().getAttribute("SlotCount");
	}
	

	@Override
	protected Class<?> beanInterface() {
		return FreespaceMBean.class;
	}

	@Override
	protected String beanID() {
		return Db4oMBeans.mBeanIDForContainer(fileSession());
	}


}
