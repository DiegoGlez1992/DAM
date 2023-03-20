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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.monitoring.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.OptOutNotSupportedJavaxManagement;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class ObjectLifecycleMonitoringSupportTestCase extends MBeanTestCaseBase implements CustomClientServerConfiguration, OptOutNotSupportedJavaxManagement {
	
	public static class Item {
		
		public Item _child;
		
		public Item(Item child) {
			_child = child;
		}
		
	}

	public void testObjectsStored(){
		Assert.areEqual(0.0, bean().getAttribute("ObjectsStoredPerSec"));
		Item item = new Item(new Item(null));
		store(item);
		_clock.advance(1000);
		Assert.areEqual(2.0, bean().getAttribute("ObjectsStoredPerSec"));
		store(item);
		_clock.advance(1000);
		Assert.areEqual(1.0, bean().getAttribute("ObjectsStoredPerSec"));
	}
	
	public void testObjectsDeleted() {
		Item item = new Item(new Item(null));
		store(item);
		db().commit();
		db().delete(item);
		db().commit();
		_clock.advance(1000);
		Assert.areEqual(2.0, fileSessionBean().getAttribute("ObjectsDeletedPerSec"));
	}
	
	public void testObjectsActivated() throws Exception{
		ObjectSet<Item> objectSet = storedItems();
		while(objectSet.hasNext()){
			objectSet.next();
		}
		_clock.advance(1000);
		Assert.areEqual(2.0, bean().getAttribute("ObjectsActivatedPerSec"));
	}
	
	public void testObjectsDeactivated() throws Exception{
		ObjectSet<Item> objectSet = storedItems();
		while(objectSet.hasNext()){
			db().deactivate(objectSet.next());
		}
		_clock.advance(1000);
		Assert.areEqual(2.0, bean().getAttribute("ObjectsDeactivatedPerSec"));
	}

	private ObjectSet<Item> storedItems() throws Exception {
		Item item = new Item(new Item(null));
		store(item);
		reopen();
		Query query = newQuery(Item.class);
		return query.<Item>execute();
	}

	@Override
	protected String beanID() {
		return Db4oMBeans.mBeanIDForContainer(container());
	}
	
	@Override
	protected Class<?> beanInterface() {
		return ObjectLifecycleMBean.class;
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.add(new ObjectLifecycleMonitoringSupport());
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	public void configureClient(Configuration config) throws Exception {
		configure(config);
	}
	
	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}
	
}
