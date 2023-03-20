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
public class THashtable implements ObjectTranslator {
	
	public Object onStore(ObjectContainer con, Object object){
		Hashtable ht = (Hashtable)object;
		Entry[] entries = new Entry[ht.size()];
		Enumeration enumeration = ht.keys();
		int i = 0;
		while(enumeration.hasMoreElements()){
			entries[i] = new Entry();
			entries[i].key = enumeration.nextElement();
			entries[i].value = ht.get(entries[i].key);
			i++;
		}
		return entries;
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		Hashtable ht = (Hashtable)object;
		ht.clear();
		if(members != null){
			Entry[] entries = (Entry[]) members;
			for(int i = 0; i < entries.length; i++){
                if(entries[i] != null){
    				if(entries[i].key != null && entries[i].value != null){
    					ht.put(entries[i].key,entries[i].value);
    				}
                }
			}
		}
	}

	public Class storedClass(){
		return Entry[].class;
	}
}
