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

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.metadata.*;
		
/**
 * First step in the defragmenting process: Allocates pointer slots in the target file for
 * each ID (but doesn't fill them in, yet) and registers the mapping from source pointer address
 * to target pointer address.
 * 
 * @exclude
 */
public final class FirstPassCommand implements PassCommand {
	
	private IDMappingCollector _collector = new IDMappingCollector();
	
	public void processClass(final DefragmentServicesImpl context, ClassMetadata classMetadata,int id,int classIndexID) {
		_collector.createIDMapping(context,id, true);
		
        classMetadata.traverseAllAspects(new TraverseFieldCommand() {
    		
			@Override
			protected void process(FieldMetadata field) {
                if(!field.isVirtual()&&field.hasIndex()) {
                    processBTree(context,field.getIndex(context.systemTrans()));
                }
			}
		});

	}

	public void processObjectSlot(DefragmentServicesImpl context, ClassMetadata classMetadata, int sourceID) {
		_collector.createIDMapping(context,sourceID, false);
	}

	public void processClassCollection(DefragmentServicesImpl context) throws CorruptionException {
		_collector.createIDMapping(context,context.sourceClassCollectionID(), false);
	}

	public void processBTree(final DefragmentServicesImpl context, final BTree btree) {
		context.registerBTreeIDs(btree, _collector);
	}

	public void flush(DefragmentServicesImpl context) {
		_collector.flush(context);
	}

}