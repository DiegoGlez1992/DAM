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
package com.db4o.qlin;

import com.db4o.*;

/**
 * a node in a QLin ("Coolin") query.
 * QLin is a new experimental query interface.
 * We would really like to have LINQ for Java instead. 
 * @since 8.0
 */
public interface QLin<T> {
	
	/**
	 * adds a where node to this QLin query.
	 * @param expression can be any of the following:
	 * 
	 */
	public QLin<T> where(Object expression);
	
	/**
	 * executes the QLin query and returns the result
	 * as an {@link ObjectSet}.
	 * Note that ObjectSet extends List and Iterable
	 * on the platforms that support these interfaces. 
	 * You may want to use these interfaces instead of
	 * working directly against an ObjectSet.
	 */
	// FIXME: The return value should not be as closely bound to db4o.
	// Collection is mutable, it's not nice.
	// Discuss !!!
	public ObjectSet<T> select ();
	
	
	public QLin<T> equal(Object obj);

	public QLin<T> startsWith(String string);

	public QLin<T> limit(int size);

	public QLin<T> smaller(Object obj);

	public QLin<T> greater(Object obj);
	
	
	/**
	 * orders the query by the expression.
	 * Use the {@link QLinSupport#ascending()} and {@link QLinSupport#descending()}
	 * helper methods to set the direction.
	 */
	public QLin<T> orderBy(Object expression, QLinOrderByDirection direction);
	
	public T singleOrDefault(T defaultValue);

	public T single();

}
