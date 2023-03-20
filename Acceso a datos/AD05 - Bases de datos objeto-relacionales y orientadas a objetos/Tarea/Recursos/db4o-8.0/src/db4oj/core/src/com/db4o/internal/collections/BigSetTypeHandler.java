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
package com.db4o.internal.collections;

import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class BigSetTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler {

	public void defragment(DefragmentContext context) {
		int pos = context.offset();
		int id = context.readInt();
		BTree bTree = newBTree(context, id);
		DefragmentServicesImpl services = (DefragmentServicesImpl) context.services();
		IDMappingCollector collector = new IDMappingCollector();
		services.registerBTreeIDs(bTree, collector);
		collector.flush(services);
		context.seek(pos);
		context.copyID();
		bTree.defragBTree(services);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		invalidBigSet(context);
		int id = context.readInt();
		freeBTree(context, id);
	}

	private void invalidBigSet(DeleteContext context) {
	    BigSetPersistence bigSet = (BigSetPersistence) context.transaction().objectForIdFromCache(context.objectId());
		if(bigSet != null){
			bigSet.invalidate();
		}
    }

	private void freeBTree(DeleteContext context, int id) {
	    BTree bTree = newBTree(context, id);
		bTree.free(systemTransaction(context));
		bTree = null;
    }

	private static LocalTransaction systemTransaction(Context context) {
		return (LocalTransaction)context.transaction().systemTransaction();
	}

	private BTree newBTree(Context context, int id) {
		return new BTree(systemTransaction(context), id, new IDHandler());
	}

	public void write(WriteContext context, Object obj) {
		BigSetPersistence bigSet = (BigSetPersistence) obj;
		bigSet.write(context);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public void activate(ReferenceActivationContext context) {
		BigSetPersistence bigSet = (BigSetPersistence) context.persistentObject();
		bigSet.read(context);
	}

	public void cascadeActivation(ActivationContext context) {
		// TODO Auto-generated method stub
		
	}

	public void collectIDs(QueryingReadContext context) {
		// TODO Auto-generated method stub
		
	}

	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
