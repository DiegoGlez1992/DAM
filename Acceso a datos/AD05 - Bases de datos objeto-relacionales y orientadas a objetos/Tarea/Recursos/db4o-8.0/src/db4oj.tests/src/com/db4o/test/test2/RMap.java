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
package com.db4o.test.test2;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.test.*;
import com.db4o.test.types.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public abstract class RMap implements RTestable{

	abstract public Object newInstance();

	TEntry entry(){
		return new TEntry();
	}

	public Object set(Object obj, int ver){
		TEntry[] arr = entry().test(ver);
		Map map = (Map)obj;
		map.clear();
		for(int i = 0; i < arr.length; i ++){
			map.put(arr[i].key, arr[i].value);
		}
		return obj;
	}

	public void compare(ObjectContainer con, Object obj, int ver){
		Map map = (Map)obj;
		TEntry[] entries = new TEntry[map.size()];
		Iterator it = map.keySet().iterator();
		int i = 0;
		while(it.hasNext()){
			entries[i] = new TEntry();
			entries[i].key = it.next();
			i++;
		}
		for(i = 0; i < entries.length; i ++){
			entries[i].value = map.get(entries[i].key);
		}
		entry().compare(entries, ver, false);
	}

	public void specific(ObjectContainer con, int step){
		TEntry entry = entry().firstElement();
		Map map = (Map)newInstance();
		if(step > 0){
			map.put(entry.key, entry.value);
			ObjectSet set = con.queryByExample(map);
			Collection4 col = new Collection4();
			while(set.hasNext()){
				Object obj = set.next();
				if(obj.getClass() == map.getClass()){
					col.add(obj);
				}
			}
			if(col.size() != step){
				Regression.addError("Map member query not found" );
			}
		}
		entry = entry().noElement();
		map.put(entry.key, entry.value);
		if(con.queryByExample(map).size() != 0){
			Regression.addError("Map member query found too many");
		}
	}


	public boolean jdk2(){
		return true;
	}


	public boolean ver3(){
		return false;
	}
}