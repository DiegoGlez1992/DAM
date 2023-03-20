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
public class ReferenceSystemRegistry {
    
    private final Collection4 _referenceSystems = new Collection4();
    
    public void removeId(final int id){
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return referenceSystem.referenceForId(id);
			}
    	});
    }

    public void removeObject(final Object obj){
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return referenceSystem.referenceForObject(obj);
			}
    	});
    }
    
    public void removeReference(final ObjectReference reference) {
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return reference;
			}
    	});
    }

    private void removeReference(ReferenceSource referenceSource) {
        Iterator4 i = _referenceSystems.iterator();
        while(i.moveNext()){
            ReferenceSystem referenceSystem = (ReferenceSystem) i.current();
            ObjectReference reference = referenceSource.referenceFrom(referenceSystem);
            if(reference != null){
                referenceSystem.removeReference(reference);
            }
        }
    }

    public void addReferenceSystem(ReferenceSystem referenceSystem) {
        _referenceSystems.add(referenceSystem);
    }

    public boolean removeReferenceSystem(ReferenceSystem referenceSystem) {
        boolean res = _referenceSystems.remove(referenceSystem);
        referenceSystem.discarded();
        return res;
    }

    private static interface ReferenceSource {
    	ObjectReference referenceFrom(ReferenceSystem referenceSystem);
    }
}
