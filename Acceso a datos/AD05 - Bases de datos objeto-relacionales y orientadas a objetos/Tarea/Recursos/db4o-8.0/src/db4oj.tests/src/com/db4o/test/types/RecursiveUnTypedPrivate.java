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

public class RecursiveUnTypedPrivate extends RTest
{
	private Object recurse;
	private String depth;
	
	public void set(int ver){
		set(ver, 10);
	}
	
	private void set(int ver, int a_depth){
		depth = "s" + ver + ":" +  a_depth;	
		if(a_depth > 0){
			recurse = new RecursiveUnTypedPrivate();
			((RecursiveUnTypedPrivate)recurse).set(ver, a_depth - 1);
		}
	}
	
	public boolean jdk2(){
		return true;
	}

}
