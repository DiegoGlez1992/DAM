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
package com.db4o.events;

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * Arguments for commit time related events.
 * 
 * @see EventRegistry
 */
public class CommitEventArgs extends TransactionalEventArgs {
	
	private final CallbackObjectInfoCollections _collections;
	private final boolean _isOwnCommit;

	public CommitEventArgs(Transaction transaction, CallbackObjectInfoCollections collections, boolean isOwnCommit) {
		super(transaction);
		_collections = collections;
		_isOwnCommit = isOwnCommit;
	}
	
	/**
	 * Returns a iteration
	 * 
	 * @sharpen.property
	 */
	public ObjectInfoCollection added() {
		return _collections.added;
	}
	
	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection deleted() {
		return _collections.deleted;
	}

	/**
	 * @sharpen.property
	 */
	public ObjectInfoCollection updated() {
		return _collections.updated;
	}

	public boolean isOwnCommit() {
		return _isOwnCommit;
	}
}
