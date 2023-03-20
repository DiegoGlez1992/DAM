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
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

import db4ounit.*;

public class FieldIndexAssert {
	
	private final Class _clazz;
	
	private final String _name;
	
	public FieldIndexAssert(Class clazz, String name){
		_clazz = clazz;
		_name = name;
	}
	
	public void assertSingleEntry(LocalObjectContainer container, final long id) {
		final BooleanByRef called = new BooleanByRef();
		index(container).traverseKeys(container.systemTransaction(), new Visitor4<FieldIndexKey>() {
			public void visit(FieldIndexKey key) {
				Assert.areEqual(id, key.parentID());
				Assert.isFalse(called.value);
				called.value = true;
			}
		});
		Assert.isTrue(called.value);
	}

	private BTree index(LocalObjectContainer container) {
		return fieldMetadata(container).getIndex(null);
	}

	private FieldMetadata fieldMetadata(LocalObjectContainer container) {
		return classMetadata(container).fieldMetadataForName(_name);
	}

	private ClassMetadata classMetadata(LocalObjectContainer container) {
		return container.classMetadataForReflectClass(container.reflector().forClass(_clazz));
	}


}
