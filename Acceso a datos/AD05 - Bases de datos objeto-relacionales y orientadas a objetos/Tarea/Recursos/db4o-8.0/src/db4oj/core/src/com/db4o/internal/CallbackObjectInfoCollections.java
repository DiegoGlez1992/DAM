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

import com.db4o.ext.*;

/**
 * @exclude
 */
public class CallbackObjectInfoCollections {
	
	public final ObjectInfoCollection added;
	
	public final ObjectInfoCollection updated;
	
	public final ObjectInfoCollection deleted;

	public static final CallbackObjectInfoCollections EMTPY = empty(); 
		
	public CallbackObjectInfoCollections(ObjectInfoCollection added_, ObjectInfoCollection updated_,
		ObjectInfoCollection deleted_) {
		added = added_;
		updated = updated_;
		deleted = deleted_;
	}
	
	private static final CallbackObjectInfoCollections empty(){
		return new CallbackObjectInfoCollections(ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY, ObjectInfoCollectionImpl.EMPTY); 
	}

}
