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

public class Atom {
	
	public Atom child;
	public String name;
	
	public Atom(){
	}
	
	public Atom(Atom child){
		this.child = child;
	}
	
	public Atom(String name){
		this.name = name;
	}
	
	public Atom(Atom child, String name){
		this(child);
		this.name = name;
	}
	
	public int compareTo(Object obj){
		return 0;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Atom){
			Atom other = (Atom)obj;
			if(name == null){
				if(other.name != null){
					return false;
				}
			}else{
				if(! name.equals(other.name)){
					return false;
				}
			}
			if(child != null){
				return child.equals(other.child);
			}
			return other.child == null;
		}
		return false;
	}
	
	public String toString(){
		String str = "Atom(" + name + ")";
		if(child != null){
			return str + "." + child.toString();
		}
		return str;
	}
	
}
