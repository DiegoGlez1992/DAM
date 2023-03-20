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
package com.db4o.internal.references;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class TransactionalReferenceSystemBase {
	
	protected final ReferenceSystem _committedReferences;
	
	protected ReferenceSystem _newReferences;
	
	public TransactionalReferenceSystemBase() {
		createNewReferences();
		_committedReferences = newReferenceSystem();
	}
	
	private ReferenceSystem newReferenceSystem(){
	    return new HashcodeReferenceSystem();
	}

	public abstract void addExistingReference(ObjectReference ref);

	public abstract void addNewReference(ObjectReference ref);
	
	public abstract void commit();

	protected void traverseNewReferences(final Visitor4 visitor) {
		_newReferences.traverseReferences(visitor);
	}
	
	protected void createNewReferences(){
		_newReferences = newReferenceSystem();
	}

	public ObjectReference referenceForId(int id) {
		ObjectReference ref = _newReferences.referenceForId(id);
		if(ref != null){
			return ref;
		}
		return _committedReferences.referenceForId(id);
	}

	public ObjectReference referenceForObject(Object obj) {
		ObjectReference ref = _newReferences.referenceForObject(obj);
		if(ref != null){
			return ref;
		}
		return _committedReferences.referenceForObject(obj);
	}

	public abstract void removeReference(ObjectReference ref);
	
	public abstract void rollback();
	
	public void traverseReferences(Visitor4 visitor) {
		traverseNewReferences(visitor);
		_committedReferences.traverseReferences(visitor);
	}



}
