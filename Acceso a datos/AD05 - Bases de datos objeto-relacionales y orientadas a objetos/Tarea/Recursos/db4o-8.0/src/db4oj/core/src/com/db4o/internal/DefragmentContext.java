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

import java.io.*;

import com.db4o.defragment.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

public interface DefragmentContext extends BufferContext, MarshallingInfo, HandlerVersionContext{
	
	public TypeHandler4 typeHandlerForId(int id);

	public int copyID();

	public int copyIDReturnOriginalID();
	
	public int copySlotlessID();

	public int copyUnindexedID();
	
	public void defragment(TypeHandler4 handler);
	
	public int handlerVersion();

	public void incrementOffset(int length);

	boolean isLegacyHandlerVersion();
	
	public int mappedID(int origID);
	
	public ByteArrayBuffer sourceBuffer();
	
	public ByteArrayBuffer targetBuffer();

	public Slot allocateTargetSlot(int length);

	public Slot allocateMappedTargetSlot(int sourceAddress, int length);

	public int copySlotToNewMapped(int sourceAddress, int length) throws IOException;

	public ByteArrayBuffer sourceBufferByAddress(int sourceAddress, int length) throws IOException;
	
	public ByteArrayBuffer sourceBufferById(int sourceId) throws IOException;
	
	public void targetWriteBytes(int address, ByteArrayBuffer buffer);
	
	public DefragmentServices services();
	
	public ObjectContainerBase container();
}
