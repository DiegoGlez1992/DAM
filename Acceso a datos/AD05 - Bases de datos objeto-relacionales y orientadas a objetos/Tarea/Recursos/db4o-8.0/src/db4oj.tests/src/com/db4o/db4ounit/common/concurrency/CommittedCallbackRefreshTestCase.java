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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class CommittedCallbackRefreshTestCase extends Db4oClientServerTestCase {
	
	private final Object _lock = new Object();
	
	private final int COUNT = 1;
	
	public static class Item {
		
		public String _name;
		
		public SubItem _subItem;
		
		public int _updates;

		public Item(String name, SubItem subItem) {
			_name = name;
			_subItem = subItem;
		}
		
		public void update(){
			_updates++;
			_subItem._updates ++;
		}
		
		public void check(){
			Assert.isNotNull(_name);
			Assert.areEqual(_name, _subItem._name);
			Assert.areEqual(_updates, _subItem._updates);
			// System.out.println(_updates);
		}
		
	}
	
	public static class SubItem {
		
		public String _name;
		
		public int _updates;

		public SubItem(String name) {
			_name = name;
		}
		
	}

	public static void main(String[] arguments) {
		new CommittedCallbackRefreshTestCase().runConcurrency();
	}
	
	
	protected void store() {
		for (int i = 0; i < COUNT; i++) {
			String name = "original" + i;
			store(new Item(name, new SubItem(name)));
		}
	}
	
	
	public void conc(final ExtObjectContainer oc, int seq) {
		
		eventRegistry(oc).committed().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				if(oc.isClosed()){
					return;
				}
				ObjectInfoCollection updated = ((CommitEventArgs)args).updated();
				Iterator4 infos = updated.iterator();
				while(infos.moveNext()){
					ObjectInfo info = (ObjectInfo) infos.current();
					Object obj = info.getObject();
					oc.refresh(obj, 2);
				}
			}
		});
		
		Item[] items = new Item[COUNT];
		ObjectSet objectSet = newQuery(Item.class).execute();
		int count = 0;
		while(objectSet.hasNext()){
			synchronized(_lock){
				items[count] = (Item) objectSet.next();
				items[count].check();
				count++;
			}
		}
		
		for (int i = 0; i < items.length; i++) {
			synchronized(_lock){
				items[i].update();
				store(items[i]._subItem);
				store(items[i]);
			}
			db().commit();
			
		}
		
		Runtime4.sleep(1000);
		
		for (int  i= 0; i < items.length; i++) {
			synchronized(_lock){
				items[i].check();
			}
		}
		
		Runtime4.sleep(3000);
	}


	private EventRegistry eventRegistry(final ExtObjectContainer oc) {
		return EventRegistryFactory.forObjectContainer(oc);
	}
}
