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
package com.db4o.monitoring.internal;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;
import com.db4o.monitoring.*;

/**
 * @exclude
 */
public class MonitoringReferenceSystem extends TransactionalReferenceSystemBase implements com.db4o.internal.references.ReferenceSystem {
	
	private final ReferenceSystemListener _referenceSystemListener;
	
	private int _referenceCount ;
	
	public MonitoringReferenceSystem(ReferenceSystemListener referenceSystem) {
		_referenceSystemListener = referenceSystem;
	}


	@Override
	public void commit() {
		final IntByRef removedReferenceCount = new IntByRef();
		traverseNewReferences(new Visitor4() {
			public void visit(Object obj) {
				ObjectReference oref = (ObjectReference)obj;
				if(oref.getObject() != null){
					_committedReferences.addExistingReference(oref);
				} else{
					removedReferenceCount.value ++;
				}
			}
		});
		createNewReferences();
		referenceCountChanged(- removedReferenceCount.value);
	}

	@Override
	public void addExistingReference(ObjectReference ref) {
		_committedReferences.addExistingReference(ref);
		referenceCountChanged(1);
	}

	@Override
	public void addNewReference(ObjectReference ref) {
		_newReferences.addNewReference(ref);
		referenceCountChanged(1);
	}

	@Override
	public void removeReference(ObjectReference ref) {
		if(_newReferences.referenceForId(ref.getID()) != null){
			_newReferences.removeReference(ref);
			referenceCountChanged(-1);	
		}
		if(_committedReferences.referenceForId(ref.getID()) != null){
			_committedReferences.removeReference(ref);
			referenceCountChanged(-1);
		}
	}

	@Override
	public void rollback() {
		final IntByRef newReferencesCount = new IntByRef();
		traverseNewReferences(new Visitor4() {
			public void visit(Object obj) {
				newReferencesCount.value ++;
			}
		});
		createNewReferences();
		referenceCountChanged(- newReferencesCount.value);
	}
	
	private void referenceCountChanged(int changedBy) {
		if(changedBy == 0){
			return;
		}
		_referenceCount += changedBy;
		_referenceSystemListener.notifyReferenceCountChanged(changedBy);
	}

	public void discarded() {
		referenceCountChanged(- _referenceCount);
	}
}
