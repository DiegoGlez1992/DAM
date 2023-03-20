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

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.slots.*;

/**
 * Encapsulates services involving source and target database files during defragmenting.
 * 
 * @exclude
 */
public interface DefragmentServices extends IDMapping {
	
	ByteArrayBuffer sourceBufferByAddress(int address,int length) throws IOException;
	ByteArrayBuffer targetBufferByAddress(int address,int length) throws IOException;

	ByteArrayBuffer sourceBufferByID(int sourceID) ;

	Slot allocateTargetSlot(int targetLength);

	void targetWriteBytes(ByteArrayBuffer targetPointerReader, int targetAddress);

	Transaction systemTrans();

	void targetWriteBytes(DefragmentContextImpl context, int targetAddress);

	void traverseAllIndexSlots(BTree tree, Visitor4 visitor4);	
	
	void registerBTreeIDs(BTree tree, IDMappingCollector collector);
	
	ClassMetadata classMetadataForId(int id);

	int mappedID(int id);

	void registerUnindexed(int id);
	
	IdSource unindexedIDs();
	
	int sourceAddressByID(int sourceID);
	
	int targetAddressByID(int sourceID);
	
	int targetNewId();
	
	public IdMapping mapping();
	
	public void commitIds();
	
}