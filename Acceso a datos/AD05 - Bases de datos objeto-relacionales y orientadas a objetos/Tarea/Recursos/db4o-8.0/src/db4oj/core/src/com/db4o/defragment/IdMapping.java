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
import com.db4o.internal.slots.*;

/**
 * The ID mapping used internally during a defragmentation run.
 * 
 * @see Defragment
 */
public interface IdMapping {

	/**
	 * Returns a previously registered mapping ID for the given ID if it exists.
	 * @param origID The original ID
	 * 
	 * @return The mapping ID for the given original ID or 0, if none has been registered.
	 */
	int mappedId(int origId);

	/**
	 * Registers a mapping for the given IDs.
	 * 
	 * @param origID The original ID
	 * @param mappedID The ID to be mapped to the original ID.
	 * @param isClassID true if the given original ID specifies a class slot, false otherwise.
	 */
	void mapId(int origId, int mappedId, boolean isClassId);
	
	
	/**
	 * Maps an ID to a slot
	 * @param id
	 * @param slot
	 */
	void mapId(int id, Slot slot);
	
	
	/**
	 * provides a Visitable of all mappings of IDs to slots.
	 */
	Visitable<SlotChange> slotChanges();
	

	/**
	 * Prepares the mapping for use.
	 */
	void open() throws IOException;	
	
	/**
	 * Shuts down the mapping after use.
	 */
	void close();
	
	/**
	 * returns the slot address for an ID 
	 */
	int addressForId(int id);
	
	void commit();
	
}