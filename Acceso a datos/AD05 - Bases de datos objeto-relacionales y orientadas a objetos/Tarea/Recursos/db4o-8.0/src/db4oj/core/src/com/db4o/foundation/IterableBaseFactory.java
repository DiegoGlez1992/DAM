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

import java.util.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class IterableBaseFactory {

	/**
	 * @sharpen.unwrap
	 */
	public static IterableBase coerce(Object obj) {
		if(obj instanceof Collection) {
			return new CollectionIterableBase((Collection) obj);
		}
		try {
			return new ReflectionIterableBase(obj);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
	
	public static Object unwrap(IterableBase iterable) {
		if(iterable instanceof IterableBaseWrapper) {
			return ((IterableBaseWrapper)iterable).delegate();
		}
		return iterable;
	}
	
	private IterableBaseFactory() {
	}

}
