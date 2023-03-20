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
package com.db4o.instrumentation.classfilter;

import com.db4o.instrumentation.core.*;

/**
 * Filter by class name/prefix.
 */

public class ByNameClassFilter implements ClassFilter {

	private final String[] _names;
	private final boolean _prefixMatch;
	
	public ByNameClassFilter(String fullyQualifiedName) {
		this(fullyQualifiedName, false);
	}

	public ByNameClassFilter(String name, boolean prefixMatch) {
		this(new String[]{ name }, prefixMatch);
	}

	public ByNameClassFilter(String[] fullyQualifiedNames) {
		this(fullyQualifiedNames, false);
	}

	public ByNameClassFilter(String[] names, boolean prefixMatch) {
		_names = names;		
		_prefixMatch = prefixMatch;
	}

	public boolean accept(Class clazz) {
		for (int idx = 0; idx < _names.length; idx++) {
			boolean match = (_prefixMatch ? clazz.getName().startsWith(_names[idx]) : _names[idx].equals(clazz.getName()));
			if(match) {
				return true;
			}
		}
		return false;
	}

}
