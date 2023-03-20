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
package com.db4o.internal.metadata;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class HierarchyAnalyzer {
	
	public static class Diff {
		
		private final ClassMetadata _classMetadata;

		public Diff(ClassMetadata classMetadata) {
			if(classMetadata == null){
				throw new ArgumentNullException();
			}
			_classMetadata = classMetadata;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(getClass() != obj.getClass()){
				return false;
			}
			Diff other = (Diff)obj;
			return _classMetadata == other._classMetadata;
		}
		
		@Override
		public String toString() {
			return ReflectPlatform.simpleName(getClass()) + "(" + _classMetadata.getName() + ")";
		}

		public ClassMetadata classMetadata() {
			return _classMetadata;
		}

		public boolean isRemoved() {
			return false;
		}

	}

	public static class Same extends Diff {

		public Same(ClassMetadata classMetadata) {
			super(classMetadata);
		}
	}
	
	public static class Removed extends Diff {
		public Removed(ClassMetadata classMetadata) {
			super(classMetadata);
		}
		
		@Override
		public boolean isRemoved() {
			return true;
		}
	}

	private ClassMetadata _storedClass;
	private ReflectClass _runtimeClass;
	
	private final ReflectClass _objectClass;

	public HierarchyAnalyzer(ClassMetadata storedClass,
			ReflectClass runtimeClass) {
		if(storedClass == null || runtimeClass == null){
			throw new ArgumentNullException();
		}
		_storedClass = storedClass;
		_runtimeClass = runtimeClass;
		_objectClass = runtimeClass.reflector().forClass(Object.class);
	}

	public List<Diff> analyze() {
		List<Diff> ancestors = new ArrayList<Diff>();
		ClassMetadata storedAncestor = _storedClass.getAncestor();
		ReflectClass runtimeAncestor = _runtimeClass.getSuperclass();
		while(storedAncestor != null){
			if(runtimeAncestor == storedAncestor.classReflector()){
				ancestors.add(new Same(storedAncestor));
			}else{
				do {
					ancestors.add(new Removed(storedAncestor));
					storedAncestor = storedAncestor.getAncestor();
					if (null == storedAncestor) {
						if (isObject(runtimeAncestor)) {
							return ancestors;
						}
						throwUnsupportedAdd(runtimeAncestor); 
					}
					if (runtimeAncestor == storedAncestor.classReflector()) {
						ancestors.add(new Same(storedAncestor));
						break;
					}
				} while(storedAncestor != null);
			}
			storedAncestor = storedAncestor.getAncestor();
			runtimeAncestor = runtimeAncestor.getSuperclass();
		}
		if(runtimeAncestor != null && (! isObject(runtimeAncestor))){
			throwUnsupportedAdd(runtimeAncestor);
		}
		return ancestors;
	}

	private void throwUnsupportedAdd(ReflectClass runtimeAncestor) {
		throw new IllegalStateException("Unsupported class hierarchy change. Class " + runtimeAncestor.getName() + " was added to hierarchy of " + _runtimeClass.getName());
	}

	private boolean isObject(ReflectClass clazz) {
		return _objectClass == clazz;
	}
	
}