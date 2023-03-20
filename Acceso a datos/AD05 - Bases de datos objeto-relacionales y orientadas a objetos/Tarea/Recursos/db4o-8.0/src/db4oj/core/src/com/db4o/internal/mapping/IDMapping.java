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
package com.db4o.internal.mapping;

/**
 * A mapping from db4o file source IDs/addresses to target IDs/addresses, used for defragmenting.
 * 
 * @exclude
 */
public interface IDMapping {
	/**
	 * @return a mapping for the given id. if it does refer to a system handler or the empty reference (0), returns the given id.
	 * @throws MappingNotFoundException if the given id does not refer to a system handler or the empty reference (0) and if no mapping is found
	 */
	int strictMappedID(int oldID) throws MappingNotFoundException;
	void mapIDs(int oldID,int newID, boolean isClassID);
}
