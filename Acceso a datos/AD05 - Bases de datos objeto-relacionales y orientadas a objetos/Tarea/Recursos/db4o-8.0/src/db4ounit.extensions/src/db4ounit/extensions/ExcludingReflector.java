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
package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

/**
 * @sharpen.extends Db4objects.Db4o.Reflect.Net.NetReflector
 */
public class ExcludingReflector extends JdkReflector {

	private final Collection4 _excludedClasses;
	
	/**
	 * @sharpen.remove.first
	 */
	public ExcludingReflector(Class<?>... excludedClasses) {
		super(ExcludingReflector.class.getClassLoader());
		
		_excludedClasses = new Collection4();
		for(Class<?> claxx : excludedClasses) {
			_excludedClasses.add(claxx.getName());
		}
	}

	/**
	 * @sharpen.remove.first
	 */
	public ExcludingReflector(ByRef<Class<?>> loaderClass, Class<?>... excludedClasses) {
		super(loaderClass.value.getClassLoader());
		
		_excludedClasses = new Collection4();
		for(Class<?> claxx : excludedClasses) {
			_excludedClasses.add(claxx.getName());
		}
	}

	/**
	 * @sharpen.remove.first
	 */
	public ExcludingReflector(Collection4 excludedClasses) {
		super(ExcludingReflector.class.getClassLoader());
		
		_excludedClasses = excludedClasses;
    }

	/**
	 * @sharpen.remove.first
	 */
	public ExcludingReflector(ByRef<Class<?>> loaderClass, Collection4 excludedClasses) {
		super(loaderClass.value.getClassLoader());
		
		_excludedClasses = excludedClasses;
    }

	@Override
	public Object deepClone(Object obj) {
		return new ExcludingReflector(_excludedClasses);
	}
	
	@Override
	public ReflectClass forName(String className) {
		if (_excludedClasses.contains(className)) {
			return null;
		}
		return super.forName(className);
	}
	
	@Override
	public ReflectClass forClass(Class clazz) {
		if (_excludedClasses.contains(clazz.getName())) {
			return null;
		}
		return super.forClass(clazz);
	}
}
