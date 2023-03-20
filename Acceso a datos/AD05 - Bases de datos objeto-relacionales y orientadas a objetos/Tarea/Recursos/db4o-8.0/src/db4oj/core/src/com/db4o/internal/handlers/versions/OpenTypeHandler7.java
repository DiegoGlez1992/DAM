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
package com.db4o.internal.handlers.versions;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

public class OpenTypeHandler7 extends OpenTypeHandler {

	public OpenTypeHandler7(ObjectContainerBase container) {
		super(container);
	}

	public Object read(ReadContext readContext) {
		InternalReadContext context = (InternalReadContext) readContext;
		int payloadOffset = context.readInt();
		if (payloadOffset == 0) {
			context.notifyNullReferenceSkipped();
			return null;
		}
		int savedOffSet = context.offset();
		try {
			TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
			if (typeHandler == null) {
				return null;
			}
			if (isPlainObject(typeHandler)) {
				return readPlainObject(readContext);
			}
			seekSecondaryOffset(context, typeHandler);
			return context.readAtCurrentSeekPosition(typeHandler);
		} finally {
			context.seek(savedOffSet);
		}
	}

	@Override
	public void defragment(DefragmentContext context) {
		int payLoadOffSet = context.readInt();
		if (payLoadOffSet == 0) {
			return;
		}
		int savedOffSet = context.offset();
		context.seek(payLoadOffSet);

		int classMetadataId = context.copyIDReturnOriginalID();
		TypeHandler4 typeHandler = correctTypeHandlerVersionFor(context, classMetadataId);
		if (typeHandler != null) {
			if (isPlainObject(typeHandler)) {
				context.copySlotlessID();
			} else {
				seekSecondaryOffset(context, typeHandler);
				context.defragment(typeHandler);
			}
		}
		context.seek(savedOffSet);
	}

	private Object readPlainObject(ReadContext context) {
		int id = context.readInt();
		Transaction transaction = context.transaction();
		Object obj = transaction.objectForIdFromCache(id);
		if (obj != null) {
			return obj;
		}
		obj = new Object();
		addReference(context, obj, id);
		return obj;
	}

	private void addReference(Context context, Object obj, int id) {
		final Transaction transaction = context.transaction();
		final ObjectReference ref = new ObjectReference(id) {

			boolean _firstUpdate = true;

			@Override
			public void writeUpdate(Transaction transaction, UpdateDepth updatedepth) {
				if (!_firstUpdate) {
					super.writeUpdate(transaction, updatedepth);
					return;
				}

				_firstUpdate = false;

				ObjectContainerBase container = transaction.container();
				setStateClean();

				MarshallingContext context = new MarshallingContext(transaction, this, updatedepth, false);
				Handlers4.write(classMetadata().typeHandler(), context, getObject());
				
				int length = container().blockConverter().blockAlignedBytes(context.marshalledLength());
		        Slot slot = context.allocateNewSlot(length);
		        
				Pointer4 pointer = new Pointer4(getID(), slot);
				ByteArrayBuffer buffer = context.toWriteBuffer(pointer);

				container.writeUpdate(transaction, pointer, classMetadata(), ArrayType.NONE, buffer);

				if (isActive()) {
					setStateClean();
				}

			}
		};
		ref.classMetadata(transaction.container().classMetadataForID(Handlers4.UNTYPED_ID));
		ref.setObjectWeak(transaction.container(), obj);
		transaction.addNewReference(ref);
	}
	


}
