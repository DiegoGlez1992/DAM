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

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class HashcodeReferenceSystem implements ReferenceSystem {
	
	private ObjectReference       _hashCodeTree;
	
	private ObjectReference       _idTree;
	
	public void addNewReference(ObjectReference ref){
		addReference(ref);
	}

	public void addExistingReference(ObjectReference ref){
		addReference(ref);
	}

	private void addReference(ObjectReference ref){
		ref.ref_init();
		idAdd(ref);
		hashCodeAdd(ref);
	}
	
	public void commit() {
		// do nothing
	}

	private void hashCodeAdd(ObjectReference ref){
		if (Deploy.debug) {
		    Object obj = ref.getObject();
		    if (obj != null) {
		        ObjectReference existing = referenceForObject(obj);
		        if (existing != null) {
		            System.out.println("Duplicate alarm hc_Tree");
		        }
		    }
		}
		if(_hashCodeTree == null){
			_hashCodeTree = ref;
			return;
		}
		_hashCodeTree = _hashCodeTree.hc_add(ref);
	}
	
	private void idAdd(ObjectReference ref){
		if(DTrace.enabled){
		    DTrace.ID_TREE_ADD.log(ref.getID());
		}
		if (Deploy.debug) {
		    ObjectReference existing = referenceForId(ref.getID());
		    if (existing != null) {
		        System.out.println("Duplicate alarm id_Tree:" + ref.getID());
		    }
		}
		if(_idTree == null){
			_idTree = ref;
			return;
		}
		_idTree = _idTree.id_add(ref);
	}
	
	public ObjectReference referenceForId(int id){
        if(DTrace.enabled){
            DTrace.GET_YAPOBJECT.log(id);
        }
        if(_idTree == null){
        	return null;
        }
        if(! ObjectReference.isValidId(id)){
            return null;
        }
        return _idTree.id_find(id);
	}
	
	public ObjectReference referenceForObject(Object obj) {
		if(_hashCodeTree == null){
			return null;
		}
		return _hashCodeTree.hc_find(obj);
	}

	public void removeReference(ObjectReference ref) {
        if(DTrace.enabled){
            DTrace.REFERENCE_REMOVED.log(ref.getID());
        }
        if(_hashCodeTree != null){
        	_hashCodeTree = _hashCodeTree.hc_remove(ref);
        }
        if(_idTree != null){
        	_idTree = _idTree.id_remove(ref);
        }
	}

	public void rollback() {
		// do nothing
	}
	
	public void traverseReferences(final Visitor4 visitor) {
		if(_hashCodeTree == null){
			return;
		}
		_hashCodeTree.hc_traverse(visitor);
	}
	
	@Override
	public String toString() {
		final BooleanByRef found = new BooleanByRef();
		final StringBuffer str = new StringBuffer("HashcodeReferenceSystem {");
		traverseReferences(new Visitor4() {
			public void visit(Object obj) {
				if(found.value){
					str.append(", ");
				}
				ObjectReference ref = (ObjectReference) obj;
				str.append(ref.getID());
				found.value = true;
			}
		});
		str.append("}");
		return str.toString();
	}

	public void discarded() {
		// do nothing
	}
	
}
