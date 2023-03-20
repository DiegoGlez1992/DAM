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

import com.db4o.test.*;

public class TEntry {

	public Object key;
	public Object value;

	public TEntry(){
	}

    public TEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
    }

	public TEntry firstElement(){
		return new TEntry("first", "firstvalue");
	}

	public TEntry lastElement(){
		return new TEntry(new ObjectSimplePublic("lastKey"), new ObjectSimplePublic("lastValue"));
	}

	public TEntry noElement(){
		return new TEntry("NO", "babe");
	}

	public TEntry[] test(int ver){
		if(ver == 1){
			return new TEntry[]{
				firstElement(),
				new TEntry(new Integer(111), new ObjectSimplePublic("111")),
				new TEntry(new Long(9999111), new Double(0.4566)),
				/*
				
				need to extend compare for the following
				
				new Entry(
					new ObjectSimplePublic[]{
						new ObjectSimplePublic("killer1"), new ObjectSimplePublic("killer2")
					},
					new ObjectSimplePublic[]{
						new ObjectSimplePublic("killer3"), null, new ObjectSimplePublic("killer4")
					}
				),
				*/
				lastElement()
			};
		}
		return new TEntry[]{
			new TEntry(new Integer(222), new ObjectSimplePublic("111")),
			new TEntry("222", "TrippleTwo"),
			new TEntry(new ObjectSimplePublic("2222"), new ObjectSimplePublic("222")),
		};
	}

	public void compare(TEntry[] a_cmp, int oneOrTwo, boolean keysOnly){
		TEntry[] tests = test(oneOrTwo);
		TEntry[] cmp = new TEntry[a_cmp.length];
		System.arraycopy(a_cmp,0, cmp, 0, a_cmp.length);
		if(cmp == null){
			Regression.addError("Entry:argument is null");
			return;
		}
		if(cmp.length  != tests.length){
			Regression.addError("Entry:arrays of different length");
			return;
		}
		for(int i = 0; i < tests.length; i ++){
			boolean found = false;
			for(int j=0; j < cmp.length; j++){
				if(cmp[j] != null){
					if(tests[i].key.equals(cmp[j].key)){
						if(!keysOnly){
							if(! tests[i].value.equals(cmp[j].value)){
								Regression.addError("Entry:inequality");
								return;
							}
						}
						cmp[j] = null;
						found = true;
						break;
					}
				}
			}
			if(! found){
				
				Regression.addError("element not found");
			}
		}
	}
}