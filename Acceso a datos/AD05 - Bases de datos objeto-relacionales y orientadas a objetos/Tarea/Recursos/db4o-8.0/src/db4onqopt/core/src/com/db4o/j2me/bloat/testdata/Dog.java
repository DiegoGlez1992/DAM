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
package com.db4o.j2me.bloat.testdata;

public class Dog extends Animal {
	private Dog[] _parents;
	private int _age;
	private int[] _prices;
	
	public Dog(String name,int age,Dog[] parents,int[] prices) {
		super(name);
		_age=age;
		_parents=parents;
		_prices=prices;
	}

	public int age() {
		return _age;
	}

	public Dog[] parents() {
		return _parents;
	}
	
	public int[] prices() {
		return _prices;
	}
	
	public String toString() {
		return "DOG: "+name()+"/"+age()+"/"+(_parents!=null ? String.valueOf(_parents.length) : "null")+" parents/"+(_prices!=null ? String.valueOf(_prices.length) : "null")+" prices";
	}
}
