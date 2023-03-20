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
package com.db4o.internal.qlin;

import com.db4o.*;
import com.db4o.qlin.*;

/**
 * @exclude
 */
public abstract class QLinNode <T> implements QLin<T> {
	
	
	public QLin<T> equal(Object obj) {
		throw new QLinException("#equal() is not supported on this node");
	}
	
	public QLin<T> startsWith(String string) {
		throw new QLinException("#startsWith() is not supported on this node");
	}
	
	public QLin<T> smaller(Object obj) {
		throw new QLinException("#smaller() is not supported on this node");
	}
	
	public QLin<T> greater(Object obj) {
		throw new QLinException("#greater() is not supported on this node");
	}
	
	public T singleOrDefault(T defaultValue){
		ObjectSet<T> collection = select();
		// TODO: Change to #isEmpty here after decafs, so the size doesn#t need to be calculated
		if(collection.size() == 0){
			return defaultValue;
		}
		if(collection.size() > 1){
			// Consider: Use a more specific exception if a query does not return
			//           the expected result
			throw new QLinException("Expected one or none. Found: " + collection.size());
		}
		
		// The following would be the right way to work against
		// a collection but for now it won't decaf.
		// return collection.iterator().next();
		
		// This is the ugly old db4o interface, where a Collection is
		// an iterator directly. For now it's convenient but we don't
		// really want to use this in the future.
		
		// Update #single() in the same way.
		return collection.next();
	}
	
	public T single(){
		ObjectSet<T> collection = select();
		if(collection.size() != 1){
			throw new QLinException("Expected exactly one. Found: " + collection.size());
		}
		return collection.next();
	}

}
