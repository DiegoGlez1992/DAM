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
package com.db4o.filestats;

import java.util.*;

import com.db4o.internal.*;

/**
* @exclude
*/
@decaf.Ignore(decaf.Platform.JDK11)
public class ClassNode {
	
	public static Set<ClassNode> buildHierarchy(ClassMetadataRepository repository) {
		ClassMetadataIterator classIter = repository.iterator();
		Map<String, ClassNode> nodes = new HashMap<String, ClassNode>();
		Set<ClassNode> roots = new HashSet<ClassNode>();
		while(classIter.moveNext()) {
			ClassMetadata clazz = classIter.currentClass();
			ClassNode node = new ClassNode(clazz);
			nodes.put(clazz.getName(), node);
			if(clazz.getAncestor() == null) {
				roots.add(node);
			}
		}
		for (ClassNode node : nodes.values()) {
			ClassMetadata ancestor = node.classMetadata().getAncestor();
			if(ancestor != null) {
				nodes.get(ancestor.getName()).addSubClass(node);
			}
		}
		return roots;
	}
	
	private final ClassMetadata _clazz;
	private final Set<ClassNode> _subClasses = new HashSet<ClassNode>();

	public ClassNode(ClassMetadata clazz) {
		_clazz = clazz;
	}
	
	public ClassMetadata classMetadata() {
		return _clazz;
	}
	
	void addSubClass(ClassNode node) {
		_subClasses.add(node);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return _clazz.getName().equals(((ClassNode)obj)._clazz.getName());
	}
	
	@Override
	public int hashCode() {
		return _clazz.getName().hashCode();
	}

	public Iterable<ClassNode> subClasses() {
		return _subClasses;
	}
}