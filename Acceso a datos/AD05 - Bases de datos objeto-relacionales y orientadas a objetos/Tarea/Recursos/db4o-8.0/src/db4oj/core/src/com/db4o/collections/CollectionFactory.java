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
package com.db4o.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.collections.*;

/**
 * Collection factory with methods to create collections with behaviour
 * that is optimized for db4o.<br/><br/> 
 * Example usage:<br/>
 * <code>CollectionFactory.forObjectContainer(objectContainer).newBigSet();</code>
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class CollectionFactory {
	
	private final ObjectContainer _objectContainer;
	
	private CollectionFactory(ObjectContainer objectContainer){
		_objectContainer = objectContainer;
	}
	
	/**
	 * returns a collection factory for an ObjectContainer
	 * @param objectContainer - the ObjectContainer
	 * @return the CollectionFactory
	 */
	public static CollectionFactory forObjectContainer(ObjectContainer objectContainer){
		if(isClient(objectContainer)){
			throw new UnsupportedOperationException("CollectionFactory is not yet available for Client/Server.");
		}
		return new CollectionFactory(objectContainer);
	}
	
	/**
	 * creates a new BigSet.<br/><br/>
	 * Characteristics of BigSet:<br/>
	 * - It is optimized by using a BTree of IDs of persistent objects.<br/> 
	 * - It can only hold persistent first class objects (no primitives, no strings, no objects that are not persistent)<br/>
	 * - Objects are activated upon getting them from the BigSet.
	 * <br/><br/>
	 * BigSet is recommend whenever one object references a huge number of other objects and sorting is not required.
	 * @return
	 */
	public <E> Set<E> newBigSet(){
		return new BigSet<E>((LocalObjectContainer) _objectContainer);
	}
	
	private static boolean isClient(ObjectContainer oc){
		return ((InternalObjectContainer)oc).isClient();
	}

}
