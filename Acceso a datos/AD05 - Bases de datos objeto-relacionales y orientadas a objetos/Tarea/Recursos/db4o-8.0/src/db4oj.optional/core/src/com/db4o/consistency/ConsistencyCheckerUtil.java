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
package com.db4o.consistency;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;

/**
 * @exclude
 */
public final class ConsistencyCheckerUtil {

	public static Map<Integer, ClassMetadata> typesFor(LocalObjectContainer db, Set<Integer> ids) {
		Map<Integer, Set<ClassMetadata>> id2clazzes = new HashMap<Integer, Set<ClassMetadata>>();
		ClassMetadataIterator iter = db.classCollection().iterator();
		while(iter.moveNext()) {
			for(int id : ids) {
				ClassMetadata clazz = iter.currentClass();
				BTree btree = BTreeClassIndexStrategy.btree(clazz);
				if(btree.search(db.systemTransaction(), id) != null) {
					Set<ClassMetadata> clazzes = id2clazzes.get(id);
					if(clazzes == null) {
						clazzes = new HashSet<ClassMetadata>();
						id2clazzes.put(id, clazzes);
					}
					clazzes.add(clazz);
				}
			}
		}
		Map<Integer, ClassMetadata> id2clazz = new HashMap<Integer, ClassMetadata>();
		for(int id : id2clazzes.keySet()) {
			Set<ClassMetadata> clazzes = id2clazzes.get(id);
			ClassMetadata mostSpecific = null;
			OUTER:
			for(ClassMetadata curClazz : clazzes) {
				for(ClassMetadata cmpClazz : clazzes) {
					if(curClazz.equals(cmpClazz._ancestor)) {
						continue OUTER;
					}
				}
				mostSpecific = curClazz;
				break;
			}
			id2clazz.put(id, mostSpecific);
		}
		return id2clazz;
	}

	
	private ConsistencyCheckerUtil() {
	}
}
