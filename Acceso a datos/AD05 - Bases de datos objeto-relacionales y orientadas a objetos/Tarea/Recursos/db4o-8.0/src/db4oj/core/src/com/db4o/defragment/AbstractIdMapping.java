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

import com.db4o.foundation.*;


/**
 * Base class for defragment ID mappings.
 * 
 * @see Defragment
 */
public abstract class AbstractIdMapping implements IdMapping {

	private Hashtable4	_classIDs = new Hashtable4();

	public final void mapId(int origID, int mappedID, boolean isClassID) {
		if(isClassID) {
			mapClassIDs(origID, mappedID);
			return;
		}
		mapNonClassIDs(origID, mappedID);
	}

	protected int mappedClassID(int origID) {
		Object obj = _classIDs.get(origID);
		if(obj == null){
			return 0;
		}
		return ((Integer)obj).intValue();
	}

	private void mapClassIDs(int oldID, int newID) {
		_classIDs.put(oldID,new Integer(newID));
	}

	protected abstract void mapNonClassIDs(int origID,int mappedID);
}
