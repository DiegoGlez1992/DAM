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
public class TransactionalReferenceSystem extends TransactionalReferenceSystemBase implements ReferenceSystem {

	@Override
	public void commit() {
		traverseNewReferences(new Visitor4() {
			public void visit(Object obj) {
				ObjectReference oref = (ObjectReference)obj;
				if(oref.getObject() != null){
					_committedReferences.addExistingReference(oref);
				}
			}
		});
		createNewReferences();
	}

	@Override
	public void addExistingReference(ObjectReference ref) {
		_committedReferences.addExistingReference(ref);
	}

	@Override
	public void addNewReference(ObjectReference ref) {
		_newReferences.addNewReference(ref);
	}

	@Override
	public void removeReference(ObjectReference ref) {
		_newReferences.removeReference(ref);
		_committedReferences.removeReference(ref);
	}

	@Override
	public void rollback() {
		createNewReferences();
	}

	public void discarded() {
		// do nothing;
	}
	
}
