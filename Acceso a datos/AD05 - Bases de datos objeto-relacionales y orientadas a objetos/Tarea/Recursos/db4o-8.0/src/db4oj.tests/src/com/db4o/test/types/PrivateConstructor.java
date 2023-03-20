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

import com.db4o.*;
import com.db4o.test.*;

public class PrivateConstructor implements RTestable
{
	public String s;

    private PrivateConstructor() {
    }

	public void compare(ObjectContainer con, Object obj, int ver){
		Compare.compare(con, set(newInstance(), ver), obj,"", null);
	}
	
	static public PrivateConstructor construct(){
		return new PrivateConstructor();	}

	public boolean equals(Object obj){
		if(obj != null){
			if(obj instanceof PrivateConstructor){
				return s.equals(((PrivateConstructor)obj).s);
			}
		} 
		return false;
	}
	
	public boolean jdk2(){
		return true;
	}
	
	public Object newInstance(){
		return new PrivateConstructor();	}
	
	public Object set(Object obj, int ver){		((PrivateConstructor)obj).set(ver);		return obj;
	}
	
	public void set(int ver){
		s = "set" + ver;
	}
	
	public boolean ver3(){
		return false;	}

}
