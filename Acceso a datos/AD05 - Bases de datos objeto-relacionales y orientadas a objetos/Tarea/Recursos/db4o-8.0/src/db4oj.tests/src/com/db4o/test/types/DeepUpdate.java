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

/**
 * note the special configuration for this class
 * in Regression.openContainer()
 */
public class DeepUpdate implements RTestable
{
	public ObjectSimplePublic d1;
	public DeepHelper d2;
	public DeepHelper[] d3;


	public DeepUpdate(){
	}

	public void compare(ObjectContainer con, Object obj, int ver){
		Compare.compare(con, set(newInstance(), ver), obj,"", null);
	}

	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(! (obj instanceof DeepUpdate) ){
			return false;
		}
		DeepUpdate with = (DeepUpdate) obj;
		if(with.d1 != null && d1 != null){
			if(d1.equals(with.d1)){
				if(with.d2 != null && d2 != null){
					if(d2.d1.equals(with.d2.d1)){
						if(with.d3 != null && d3 != null){
							if(with.d3.length == d3.length){
								if(with.d3[0].equals(d3[0])){
									if(with.d3[1].equals(d3[1])){
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public Object newInstance(){
		return new DeepUpdate();
	}


	public Object set(Object obj, int ver){
		((DeepUpdate)obj).set(ver);
		return obj;
	}

	public void set(int ver){
		d1 = new ObjectSimplePublic();
		d2 = new DeepHelper();
		d3 = new DeepHelper[2];
		d3[0] = new DeepHelper();
		d3[1] = new DeepHelper();
		if(ver == 1){
			d1.name = "OneONEOneONEOneONEOneONEOneONEOneONE";
		}else{
			d1.name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
		}
		d2.set(ver);
		d3[0].set(ver);
		d3[1].set(ver);
	}

	public boolean jdk2(){
		return false;
	}
	
	public boolean ver3(){
		return false;	}
}
