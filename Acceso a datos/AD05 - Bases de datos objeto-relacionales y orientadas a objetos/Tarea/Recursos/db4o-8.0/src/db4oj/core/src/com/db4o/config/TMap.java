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
package com.db4o.config;

import java.util.*;

import com.db4o.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TMap implements ObjectTranslator {
	
	public Object onStore(ObjectContainer con, Object object){
		Map map = (Map)object;
		Entry[] entries = new Entry[map.size()];
		Iterator it = map.keySet().iterator();
		int i = 0;
		while(it.hasNext()){
			entries[i] = new Entry();
			entries[i].key = it.next();
			entries[i].value = map.get(entries[i].key);
			i++;
		}
		return entries;
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		Map map = (Map)object;
		map.clear();
		if(members != null){
			Entry[] entries = (Entry[]) members;
			for(int i = 0; i < entries.length; i++){
                if(entries[i] != null){
    				if(entries[i].key != null && entries[i].value != null){
    					map.put(entries[i].key,entries[i].value);
    				}
                }
			}
		}
	}

	public Class storedClass(){
		return Entry[].class;
	}
}
