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
package com.db4o.db4ounit.common.soda.ordered;

public class OrderTestSubject {
	public String _name;
	public int _seniority;
	public int _age;
		 
	public OrderTestSubject(String name,int age, int seniority){
		this._name=name;
		this._age=age;
		this._seniority=seniority;
	}
		 
	public String toString(){
		return _name + " " + _age + " " + _seniority;
	}
		
	public boolean equals(Object o){
		if (o == null){
			return false;
		}		
			
		if (o.getClass() != getClass()){
			return false;
		}
		
		OrderTestSubject ots = (OrderTestSubject) o;
		boolean ret  =(_age == ots._age) && (_name.equals(ots._name)) && (_seniority == ots._seniority);
		return ret;
	}
}
