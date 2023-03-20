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
public abstract class RCollection implements RTestable{

	abstract public Object newInstance();

	TEntry entry(){
		return new TEntry();
	}

	public Object set(Object obj, int ver){
		TEntry[] arr = entry().test(ver);
		Collection col = (Collection)obj;
		col.clear();
		for(int i = 0; i < arr.length; i ++){
			col.add(arr[i].key);
		}
		return obj;
	}

	public void compare(ObjectContainer con, Object obj, int ver){
		Collection col = (Collection)obj;
		TEntry[] entries = new TEntry[col.size()];
		Iterator it = col.iterator();
		int i = 0;
		while(it.hasNext()){
			entries[i] = new TEntry();
			entries[i].key = it.next();
			i++;
		}
		entry().compare(entries, ver, true);
	}

	public void specific(ObjectContainer con, int step){
		TEntry entry = entry().firstElement();
		Collection col = (Collection)newInstance();
		if(step > 0){
			col.add(entry.key);
						ObjectSet set = con.queryByExample(col);
			Collection4 sizeCalc = new Collection4();
			while(set.hasNext()){
				Object obj = set.next();
				if(obj.getClass() == col.getClass()){
					sizeCalc.add(obj);
				}
			}
			if(sizeCalc.size() != step){
				Regression.addError("Collection member query not found");
			}
		}
		entry = entry().noElement();
		col.add(entry.key);
		if(con.queryByExample(col).size() != 0){
			Regression.addError("Collection member query found too many");
		}
	}


	public boolean jdk2(){
		return true;
	}

	public boolean ver3(){
		return false;
	}
}