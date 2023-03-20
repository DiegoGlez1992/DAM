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

import com.db4o.marshall.*;

public interface InstantiatingTypeHandler extends ReferenceTypeHandler {

	Object instantiate(ReadContext context);
	
	/**
	 * gets called when an object is to be written to the database.
	 * 
	 * The method must only write data necessary to re instantiate the object, usually
	 * the immutable bits of information held by the object. For value
	 * types that means their complete state.
	 * 
	 * Mutable state (only allowed in reference types) must be handled
	 * during {@link ReferenceTypeHandler#activate(WriteContext)}
	 *  
	 * @param context
	 * @param obj the object
	 */
    void writeInstantiation(WriteContext context, Object obj);

}
