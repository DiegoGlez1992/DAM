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

import com.db4o.internal.activation.*;


/**
 * @exclude
 */
public class HardObjectReference {
	
	public static final HardObjectReference INVALID = new HardObjectReference(null, null);
	
	public final ObjectReference _reference;
	
	public final Object _object;

	public HardObjectReference(ObjectReference ref, Object obj) {
		_reference = ref;
		_object = obj;
	}
	
	public static HardObjectReference peekPersisted(Transaction trans, int id, int depth) {
	    Object obj = trans.container().peekPersisted(trans, id, activationDepthProvider(trans).activationDepth(depth, ActivationMode.PEEK), true);
	    if(obj == null){
	        return null;
	    }
	    ObjectReference ref = trans.referenceForId(id);
		return new HardObjectReference(ref, obj);
	}

	private static ActivationDepthProvider activationDepthProvider(Transaction trans) {
		return trans.container().activationDepthProvider();
	}
}
