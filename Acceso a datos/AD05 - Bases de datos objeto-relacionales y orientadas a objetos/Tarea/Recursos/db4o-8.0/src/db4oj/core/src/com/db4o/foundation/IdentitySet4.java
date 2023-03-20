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
package com.db4o.foundation;


/**
 * @exclude
 */
public class IdentitySet4 extends HashtableBase implements Iterable4 {
	
	public IdentitySet4(){
	}
	
	public IdentitySet4(int size){
		super(size);
	}
	
	public boolean contains(Object obj){
		return findWithSameKey(new HashtableIdentityEntry(obj)) != null;
	}
	
	public void add(Object obj){
		if(null == obj){
			throw new ArgumentNullException();
		}
		putEntry(new HashtableIdentityEntry(obj));
	}
	
	public void remove(Object obj) {
		if(null == obj){
			throw new ArgumentNullException();
		}
		
		removeIntEntry(System.identityHashCode(obj));
	}
	
	public Iterator4 iterator() {
		return valuesIterator();
	}
}
