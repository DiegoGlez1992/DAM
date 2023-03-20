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

public class SetDeactivated {
	
	public String foo;
	
	public SetDeactivated(){
	}
	
	public SetDeactivated(String foo){
		this.foo = foo;
	}
	
	public void store(){
		Test.deleteAllInstances(this);
		Test.store(new SetDeactivated("hi"));
		Test.commit();
	}
	
	public void test(){
		SetDeactivated sd = (SetDeactivated)Test.getOne(this);
		Test.objectContainer().deactivate(sd, 1);
		Test.store(sd);
		Test.objectContainer().purge(sd);
		sd = (SetDeactivated)Test.getOne(this);
		Test.objectContainer().activate(sd, 1);
		Test.ensure(sd.foo.equals("hi"));
	}
}
