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
public class LazyObjectReference implements ObjectInfo{
	
	private final Transaction _transaction;
	
	private final int _id;	
	
	public LazyObjectReference(Transaction transaction, int id){
		_transaction = transaction;
		_id = id;
	}

	public long getInternalID() {
		return _id;
	}

	public Object getObject() {
		synchronized(containerLock()){
			return reference().getObject();
		}
	}

	public Db4oUUID getUUID() {
		synchronized(containerLock()){
			return reference().getUUID();
		}
	}

	public long getVersion() {
		return getCommitTimestamp();
	}
	
	public long getCommitTimestamp() {
		synchronized(containerLock()){
			return reference().getCommitTimestamp();
		}
	}
	
	public ObjectReference reference() {
		final HardObjectReference hardRef = _transaction.container().getHardObjectReferenceById(_transaction, _id);
		return hardRef._reference;
	}
	
	private Object containerLock(){
		_transaction.container().checkClosed();
		return _transaction.container().lock();
	}

}
