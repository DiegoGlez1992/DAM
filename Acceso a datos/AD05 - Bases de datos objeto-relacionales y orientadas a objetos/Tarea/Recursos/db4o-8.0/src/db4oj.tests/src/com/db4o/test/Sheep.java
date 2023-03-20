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
package com.db4o.test;

import com.db4o.config.annotations.*;



/**
 */
@decaf.Ignore
public class Sheep {

	@Indexed 
	private String name;
	private boolean constructorCalled=false;

	Sheep parent;

	public Sheep(String name, Sheep parent) {
		this.name = name;
		this.parent = parent;
		constructorCalled=true;
	}

	public boolean constructorCalled() {
		return constructorCalled;
	}
	
	@Override 
	public String toString() {
		return name + " " + parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
