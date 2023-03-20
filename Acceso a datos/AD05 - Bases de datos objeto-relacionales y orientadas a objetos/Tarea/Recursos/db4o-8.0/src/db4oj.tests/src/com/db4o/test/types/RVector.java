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
package com.db4o.test.types;

import java.util.*;

import com.db4o.*;
import com.db4o.test.*;

public class RVector implements RTestable{

	public Object newInstance(){
		return new Vector();
	}

	public Object set(Object obj, int ver){
		TEntry[] arr = new TEntry().test(ver);
		Vector vt = (Vector)obj;
		vt.removeAllElements();
		for(int i = 0; i < arr.length; i ++){
			vt.addElement(arr[i].key);
		}
		return obj;
	}

	public void compare(ObjectContainer con, Object obj, int ver){
		Vector vt = (Vector)obj;
		TEntry[] entries = new TEntry[vt.size()];
		Enumeration enu = vt.elements();
		int i = 0;
		while(enu.hasMoreElements()){
			entries[i] = new TEntry();
			entries[i].key = enu.nextElement();
			i++;
		}
		new TEntry().compare(entries, ver, true);
	}

	public void specific(ObjectContainer con, int step){
		TEntry entry = new TEntry().firstElement();
		Vector vt = (Vector)newInstance();
		if(step > 0){
			vt.addElement(entry.key);
			ObjectSet set = con.queryByExample(vt);
			if(set.size() != step){
				Regression.addError("Vector member query not found");
			}else{
				Vector res = (Vector)set.next();
				if(! (res.firstElement().equals(new TEntry().firstElement().key))){
					Regression.addError("Vector order changed.");
				}
			}
		}
		entry = new TEntry().noElement();
		vt.addElement(entry.key);
		if(con.queryByExample(vt).size() != 0){
			Regression.addError("Vector member query found too many");
		}
	}


	public boolean jdk2(){
		return false;
	}
	
	public boolean ver3(){
		return false;	}

}
