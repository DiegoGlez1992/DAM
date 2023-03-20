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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class SetCollectionOnUpdateTestCase extends AbstractDb4oTestCase {
	private static final String OLDNAME = "A";

	public static class Data {
		public String name;

		public Data(String name) {
			this.name = name;
		}
		
		public String name() {
			return name;
		}
		
		public void name(String name) {
			this.name=name;
		}
	}

	public static class DataList {
		public List list;
		
		public DataList(Data data) {
			list=new ArrayList(1);
			list.add(data);
		}

		public Data data() {
			return (Data)list.get(0);
		}
		
		public void objectOnUpdate(ObjectContainer container) {
			container.ext().store(this.list, 1);
		}
	}

    protected void store() {
		Data data=new Data(OLDNAME);
		DataList list=new DataList(data);
		db().store(list);
    }
    
    public void testUpdateAndReread() throws Exception{
		DataList list=readDataList();
		db().ext().activate(list,Integer.MAX_VALUE);
		list.data().name(OLDNAME+"X");
		db().store(list);
		db().commit();
        
        reopen();
        
		list=readDataList();
		Assert.areEqual(OLDNAME, list.data().name());
    }

	private DataList readDataList() {
		ObjectSet result = db().queryByExample(DataList.class);
		return (DataList)result.next();
	}

}
