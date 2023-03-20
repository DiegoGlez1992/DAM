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
package com.db4o.internal;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.internal.collections.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;
import com.db4o.typehandlers.internal.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TypeHandlerConfigurationJDK_1_2 extends TypeHandlerConfiguration{
	
	public TypeHandlerConfigurationJDK_1_2(Config4Impl config) {
		super(config);
        listTypeHandler(new CollectionTypeHandler());
        mapTypeHandler(new MapTypeHandler());
	}

	public void apply(){
        registerCollection(AbstractCollection.class);
		ignoreFieldsOn(AbstractList.class);
		ignoreFieldsOn(AbstractSequentialList.class);
		ignoreFieldsOn(LinkedList.class);
		ignoreFieldsOn(ArrayList.class);
		ignoreFieldsOn(Vector.class);
		ignoreFieldsOn(Stack.class);
		ignoreFieldsOn(AbstractSet.class);
		ignoreFieldsOn(HashSet.class);
		
		registerMap(AbstractMap.class);
		registerMap(Hashtable.class);
		
		ignoreFieldsOn(HashMap.class);
		ignoreFieldsOn(WeakHashMap.class);
		
		registerTypeHandlerFor(BigSet.class, new BigSetTypeHandler());
		registerTypeHandlerFor(TreeSet.class, new TreeSetTypeHandler() {
			@Override
			protected TreeSet create(Comparator comparator) {
				return new TreeSet(comparator);
			}
		});
		registerTypeHandlerFor(ActivatableTreeSet.class, new TreeSetTypeHandler() {
			@Override
			protected TreeSet create(Comparator comparator) {
				return new ActivatableTreeSet(comparator);
			}
		});
		registerTypeHandlerFor("java.util.Collections$UnmodifiableRandomAccessList", new UnmodifiableListTypeHandler());
		ignoreFieldsOn("java.util.Collections$UnmodifiableList");
		ignoreFieldsOn("java.util.Collections$UnmodifiableCollection");
	}

}
