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
package com.db4o.typehandlers.internal;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public abstract class TreeSetTypeHandler
	implements InstantiatingTypeHandler, QueryableTypeHandler {
	
	public boolean descendsIntoMembers() {
		return true;
    }

	public void writeInstantiation(WriteContext context, Object obj) {
		final Comparator comparator = ((TreeSet)obj).comparator();
		context.writeObject(comparator);
	}
	
	public Object instantiate(ReadContext context) {
		final Comparator comparator = (Comparator)context.readObject();
		return create(comparator);
	}

	protected abstract TreeSet create(final Comparator comparator);

	public void activate(ReferenceActivationContext context) {
		// already handled by CollectionTypeHandler
	}
	
	public void write(WriteContext context, Object obj) {
		// already handled by CollectionTypeHandler
	}

	public void defragment(DefragmentContext context) {
		context.copyID();
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		// TODO: when the TreeSet is deleted
		// the comparator should be deleted too
		// TODO: What to do about shared comparators?
		// context.deleteObject();
	}
}
