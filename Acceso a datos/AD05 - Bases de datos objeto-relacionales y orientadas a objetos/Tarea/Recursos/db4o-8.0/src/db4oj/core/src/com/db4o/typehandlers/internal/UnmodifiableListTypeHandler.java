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
package com.db4o.typehandlers.internal;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class UnmodifiableListTypeHandler implements InstantiatingTypeHandler, QueryableTypeHandler, CascadingTypeHandler {

	public boolean descendsIntoMembers() {
		return true;
    }

	public void writeInstantiation(WriteContext context, Object obj) {
		Object list = Reflection4.getFieldValue(obj, "list");
		context.writeObject(list);
	}
	
	public Object instantiate(ReadContext context) {
		List list = (List) context.readObject();
		return Collections.unmodifiableList(list);
	}

	public void activate(ReferenceActivationContext context) {
		UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context;
		Object list = Reflection4.getFieldValue(context.persistentObject(), "list");
		unmarshallingContext.container().activate(context.transaction(),list, unmarshallingContext.activationDepth());
	}
	
	public void write(WriteContext context, Object obj) {
		
	}

	public void defragment(DefragmentContext context) {
		context.copyID();
	}

	public void delete(final DeleteContext context) throws Db4oIOException {
		if (!context.cascadeDelete()) {
			return;
		}
		final Transaction transaction = context.transaction();
		final LocalObjectContainer container = (LocalObjectContainer) transaction.container();
		int listId = context.readInt();
		IdObjectCollector collector = new IdObjectCollector();
		collectIds(transaction, container, listId, collector);
        collector.ids().traverse(new Visitor4<TreeInt>() {
			public void visit(TreeInt treeInt) {
				container.deleteByID(transaction, treeInt._key, context.cascadeDeleteDepth());
			}
		});
	}

	private void collectIds(final Transaction transaction,
			final LocalObjectContainer container, int listId,
			IdObjectCollector collector) {
		final CollectIdContext subContext =  CollectIdContext.forID(transaction, collector, listId);
		ByteArrayBuffer arrayElementBuffer = container.readBufferById(transaction, listId);
        ObjectHeader objectHeader = ObjectHeader.scrollBufferToContent(container, arrayElementBuffer);
        objectHeader.classMetadata().collectIDs(subContext);
	}

	
	public void cascadeActivation(ActivationContext context) {
		Object list = Reflection4.getFieldValue(context.targetObject(), "list");
		context.cascadeActivationToChild(list);
	}

	
	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
		return this;
	}

	public void collectIDs(QueryingReadContext context) {
		collectIds(context.transaction(), (LocalObjectContainer)context.container(), context.readInt(), context.collector());
	}

}
