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
package com.db4o.typehandlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;


/**
 * handles reading, writing, deleting, defragmenting and 
 * comparisons for types of objects.<br><br>
 * Custom Typehandlers can be implemented to alter the default 
 * behaviour of storing all non-transient fields of an object.<br><br>
 * @see {@link Configuration#registerTypeHandler(TypeHandlerPredicate, TypeHandler4)} 
 */
public interface TypeHandler4 {
	
	/**
	 * gets called when an object gets deleted.
	 * @param context 
	 * @throws Db4oIOException
	 */
	void delete(DeleteContext context) throws Db4oIOException;
	
	/**
	 * gets called when an object gets defragmented.
	 * @param context
	 */
	void defragment(DefragmentContext context);
	
	/**
	 * gets called when an object is to be written to the database.

	 * @param context
	 * @param obj the object
	 */
    void write(WriteContext context, Object obj);
	
}
