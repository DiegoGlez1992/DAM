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
import com.db4o.internal.*;

public class IDMappingCollector {
	
	private final static int ID_BATCH_SIZE=4096;

	private TreeInt _ids;
	
	void createIDMapping(DefragmentServicesImpl context, int objectID, boolean isClassID) {
		if(batchFull()) {
			flush(context);
		}
		_ids=TreeInt.add(_ids,(isClassID ? -objectID : objectID));
	}

	private boolean batchFull() {
		return _ids!=null&&_ids.size()==ID_BATCH_SIZE;
	}

	public void flush(DefragmentServicesImpl context) {
		if(_ids==null) {
			return;
		}
		Iterator4 idIter=new TreeKeyIterator(_ids);
		while(idIter.moveNext()) {
			int objectID=((Integer)idIter.current()).intValue();
			boolean isClassID=false;
			if(objectID<0) {
				objectID=-objectID;
				isClassID=true;
			}
			
			if(DefragmentConfig.DEBUG){
				int mappedID = context.mappedID(objectID, -1);
				// seen object ids don't come by here anymore - any other candidates?
				if(mappedID>=0) {
					throw new IllegalStateException();
				}
			}
			context.mapIDs(objectID,context.targetNewId(), isClassID);
		}
		context.mapping().commit();
		_ids=null;
	}
}