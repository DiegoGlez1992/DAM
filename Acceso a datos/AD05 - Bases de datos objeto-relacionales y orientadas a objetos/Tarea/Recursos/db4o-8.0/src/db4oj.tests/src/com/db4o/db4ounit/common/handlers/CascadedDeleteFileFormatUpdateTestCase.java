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
package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;

import db4ounit.*;

/**
 * @exclude
 */
public class CascadedDeleteFileFormatUpdateTestCase extends FormatMigrationTestCaseBase {
	
	private boolean _failed;
	
	protected void configureForStore(Configuration config) {
		config.objectClass(ParentItem.class).cascadeOnDelete(true);
	}
	
	protected void configureForTest(Configuration config) {
		configureForStore(config);
		config.diagnostic().addListener(new DiagnosticListener() {
			public void onDiagnostic(Diagnostic d) {
				if(d instanceof DeletionFailed){
					// Can't assert directly here, db4o eats the exception. :/
					_failed = true;
				}
			}
		});
	}
	
	@Override
	protected void deconfigureForTest(Configuration config) {
		config.diagnostic().removeAllListeners();
	}
	public static class ParentItem {

		public ChildItem[] _children;
		
		public static ParentItem newTestInstance(){
			ParentItem item = new ParentItem();
			item._children = new ChildItem[]{
				new ChildItem(),
				new ChildItem(),
			};
			return item;
		}
		
	}
	
	public static class ChildItem {
		
	}

	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		ParentItem parentItem = (ParentItem) retrieveInstance(objectContainer, ParentItem.class);
		Assert.isNotNull(parentItem._children);
		Assert.isNotNull(parentItem._children[0]);
		Assert.isNotNull(parentItem._children[1]);
		objectContainer.delete(parentItem);
		Assert.isFalse(_failed);
		
		objectContainer.store(ParentItem.newTestInstance());
	}

	private Object retrieveInstance(ExtObjectContainer objectContainer, Class clazz) {
		return objectContainer.query(clazz).next();
	}

	protected String fileNamePrefix() {
		return "migrate_cascadedelete_" ;
	}

	protected void store(ObjectContainerAdapter objectContainer) {
		objectContainer.store(ParentItem.newTestInstance());
	}

}
