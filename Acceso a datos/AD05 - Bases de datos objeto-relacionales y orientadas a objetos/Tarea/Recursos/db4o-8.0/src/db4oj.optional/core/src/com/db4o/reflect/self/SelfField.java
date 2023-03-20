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
package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfField implements ReflectField {

	private String _name;

	private ReflectClass _type;

	private SelfClass _selfclass;

	private SelfReflectionRegistry _registry;

	public SelfField(String name, ReflectClass type, SelfClass selfclass,
			SelfReflectionRegistry registry) {
		_name = name;
		_type = type;
		_selfclass = selfclass;
		_registry = registry;
	}

	public Object get(Object onObject) {
		if (onObject instanceof SelfReflectable) {
			return ((SelfReflectable) onObject).self_get(_name);
		}
		return null;
	}

	public String getName() {
		return _name;
	}

	public ReflectClass getFieldType() {
		return _type;
	}

	public boolean isPublic() {
		return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name)
				.isPublic();
	}

	public boolean isStatic() {
		return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name)
				.isStatic();
	}

	public boolean isTransient() {
		return _registry.infoFor(_selfclass.getJavaClass()).fieldByName(_name)
				.isTransient();
	}

	public void set(Object onObject, Object value) {
		if (onObject instanceof SelfReflectable) {
			((SelfReflectable) onObject).self_set(_name, value);
		}
	}

	public Object indexEntry(Object orig) {
		return orig;
	}

	public ReflectClass indexType() {
		return getFieldType();
	}

}
