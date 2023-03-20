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
 * Argument for object related events which can be cancelled.
 * 
 * @see EventRegistry
 * @see CancellableEventArgs
 */
public class CancellableObjectEventArgs extends ObjectInfoEventArgs implements CancellableEventArgs {
	private boolean _cancelled;
	private Object _object;

	/**
	 * Creates a new instance for the specified object.
	 */
	public CancellableObjectEventArgs(Transaction transaction, ObjectInfo objectInfo, Object obj) {
		super(transaction, objectInfo);
		_object = obj;
	}
	
	/**
	 * @see CancellableEventArgs#cancel()
	 */
	public void cancel() {
		_cancelled = true;
	}

	/**
	 * @see CancellableEventArgs#isCancelled()
	 */
	public boolean isCancelled() {
		return _cancelled;
	}

	@Override
    public Object object() {
		return _object;
    }
	
	@Override
	public ObjectInfo info() {
		final ObjectInfo info = super.info();
		if (null == info) {
			throw new IllegalStateException();
		}
		
		return info;
	}
}
