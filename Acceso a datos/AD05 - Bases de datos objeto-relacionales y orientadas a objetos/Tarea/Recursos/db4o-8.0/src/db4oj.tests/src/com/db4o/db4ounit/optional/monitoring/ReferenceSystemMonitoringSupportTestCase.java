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
import com.db4o.monitoring.*;

import db4ounit.*;
import db4ounit.extensions.OptOutNotSupportedJavaxManagement;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class ReferenceSystemMonitoringSupportTestCase extends MBeanTestCaseBase implements CustomClientServerConfiguration, OptOutDefragSolo, OptOutNotSupportedJavaxManagement {
	
	public static void main(String[] args) {
		new ReferenceSystemMonitoringSupportTestCase().runNetworking();
	}
	
	public static class Item{
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.add(new ReferenceSystemMonitoringSupport());
	}

	public void configureClient(Configuration config) throws Exception {
		configure(config);
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}

	@Override
	protected Class<?> beanInterface() {
		return ReferenceSystemMBean.class;
	}

	@Override
	protected String beanID() {
		return Db4oMBeans.mBeanIDForContainer(isEmbedded() ? fileSession() : db());
	}
	
	public void testObjectReferenceCount(){
		int objectCount = 10;
		Item[] items = new Item[objectCount];
		for (int i = 0; i < objectCount; i++) {
			Assert.areEqual(referenceCountForDb4oDatabase() + i, objectReferenceCount());
			items[i] = new Item();
			store(items[i]);
		}
		db().purge(items[0]);
		Assert.areEqual(referenceCountForDb4oDatabase() + objectCount -1, objectReferenceCount());
	}
	
	private Object objectReferenceCount() {
		return bean().getAttribute("ObjectReferenceCount");
	}
	
	private int referenceCountForDb4oDatabase(){
		if(isNetworking()){
			return 0;
		}
		return 1;
	}
	

}
