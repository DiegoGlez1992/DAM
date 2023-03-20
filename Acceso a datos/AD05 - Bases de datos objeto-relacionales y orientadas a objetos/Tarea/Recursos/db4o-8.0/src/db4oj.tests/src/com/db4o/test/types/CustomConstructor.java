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

public class CustomConstructor extends RTest
{
	public String name;
	transient String tname;

	public CustomConstructor(){
	}
	
	public CustomConstructor(String transientName){
		tname = transientName;
	}
	
	public boolean equals(Object obj){
		if(obj != null){
			if(obj instanceof CustomConstructor){
				CustomConstructor cc = (CustomConstructor)obj;
				if(name != null){
					if (! name.equals(cc.name)){
						return false;
					}
					if(cc.name != null){
						return false;
					}
				}
				if(tname != null){
					if(! tname.equals(cc.tname)){
						return false;
					}
					if(cc.tname != null){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}


	public void set(int ver){
		if(ver == 1){
			name = "OneONEOneONEOneONEOneONEOneONEOneONE";	
		}else{
			name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";	
		}
		tname = name;
	}
}
