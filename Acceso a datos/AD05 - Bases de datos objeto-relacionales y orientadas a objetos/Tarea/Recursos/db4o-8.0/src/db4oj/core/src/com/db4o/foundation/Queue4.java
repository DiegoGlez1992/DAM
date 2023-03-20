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


public interface Queue4<E> {

	void add(E obj);

	E next();

	boolean hasNext();
	
	/**
	 * Returns the next object in the queue that matches the specified condition.
	 * 
	 * The operation is always NON-BLOCKING.
	 *  
	 * @param condition the object must satisfy to be returned
	 * 
	 * @return the object satisfying the condition or null if none does
	 */
	E nextMatching(Predicate4<E> condition);

	Iterator4 iterator();
}