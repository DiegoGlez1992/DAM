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
package com.db4o.internal.reflect.generic;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

public class KnownClassesCollector {

	private final ObjectContainerBase _container;
	private final KnownClassesRepository _repository;

	public KnownClassesCollector(ObjectContainerBase container, KnownClassesRepository repository) {
		_container = container;
		_repository = repository;
	}

	public ReflectClass[] collect() {
        Collection4 classes = new Collection4();
		collectKnownClasses(classes);
		
		return (ReflectClass[])classes.toArray(new ReflectClass[classes.size()]);
	}
	
	private void collectKnownClasses(final Collection4 classes) {
		final Listener4<ReflectClass> collectingListener = newCollectingClassListener(classes);
		_repository.addListener(collectingListener);
		try { 
			collectKnownClasses(classes, Iterators.copy(_repository.classes()));
		} finally { 
			_repository.removeListener(collectingListener);
		}
	}

	private Listener4<ReflectClass> newCollectingClassListener(final Collection4 classes) {
		return new Listener4<ReflectClass>() {		
			public void onEvent(ReflectClass addedClass) {
				collectKnownClass(classes, addedClass);
			}
		};
	}

	private void collectKnownClasses(Collection4 collector, Iterator4 knownClasses) {
		while(knownClasses.moveNext()){
            ReflectClass clazz = (ReflectClass) knownClasses.current();
            collectKnownClass(collector, clazz);
		}
	}

	private void collectKnownClass(Collection4 classes, ReflectClass clazz) {
		if(isInternalClass(clazz))
			return;
		
		if(!hasIdentity(clazz))
			return;
		
		if(clazz.isArray())
			return;
		
		classes.add(clazz);
	}

	private boolean isInternalClass(ReflectClass clazz) {
		return _container._handlers.ICLASS_INTERNAL.isAssignableFrom(clazz);
	}

	private boolean hasIdentity(ReflectClass clazz) {
		ClassMetadata clazzMeta = _container.classMetadataForReflectClass(clazz);
		return clazzMeta == null || clazzMeta.hasIdentity();
	}
}
