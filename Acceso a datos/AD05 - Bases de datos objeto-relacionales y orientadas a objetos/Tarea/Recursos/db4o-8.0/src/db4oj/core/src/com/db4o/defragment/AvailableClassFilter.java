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
package com.db4o.defragment;

import com.db4o.ext.*;

/**
 * Filter that accepts only StoredClass instances whose corresponding Java
 * class is currently known.
 * @sharpen.ignore
 */
public class AvailableClassFilter implements StoredClassFilter {
	
	private ClassLoader _loader;

	/**
	 * Will accept only classes that are known to the classloader that loaded
	 * this class.
	 */
	public AvailableClassFilter() {
		this(AvailableClassFilter.class.getClassLoader());
	}

	/**
	 * Will accept only classes that are known to the given classloader.
	 * 
	 * @param loader The classloader to check class names against
	 */
	public AvailableClassFilter(ClassLoader loader) {
		_loader = loader;
	}

	/**
	 * Will accept only classes whose corresponding platform class is known
	 * to the configured classloader.
	 * 
	 * @param storedClass The class instance to be checked
	 * @return true if the corresponding platform class is known to the configured classloader, false otherwise
	 */
	public boolean accept(StoredClass storedClass) {
		try {
			_loader.loadClass(storedClass.getName());
			return true;
		} catch (ClassNotFoundException exc) {
			return false;
		}
	}
}
