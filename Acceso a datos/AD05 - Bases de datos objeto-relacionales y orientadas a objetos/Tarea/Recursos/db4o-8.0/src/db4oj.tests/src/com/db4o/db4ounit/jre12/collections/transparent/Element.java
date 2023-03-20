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
package com.db4o.db4ounit.jre12.collections.transparent;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class Element implements CollectionElement, Comparable{
	
	public String _name;
	
	public Element(String name){
		_name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof Element)){
			return false;
		}
		Element other = (Element)obj;
		return _name.equals(other._name);
	}
	
	@Override
	public String toString() {
		return "Element " + _name;
	}
	
	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	public int compareTo(Object o) {
		CollectionElement other = (CollectionElement) o;
		if(_name == null){
			if(other.name() == null){
				return 0;
			}else{
				return -1;
			}
		}
		return _name.compareTo(other.name());
	}

	public String name() {
		return _name;
	}
	
}
