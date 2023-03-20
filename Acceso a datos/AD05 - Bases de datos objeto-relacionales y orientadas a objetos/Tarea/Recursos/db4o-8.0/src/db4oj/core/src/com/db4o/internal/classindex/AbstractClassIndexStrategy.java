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
package com.db4o.internal.classindex;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class AbstractClassIndexStrategy implements ClassIndexStrategy {

	protected final ClassMetadata _classMetadata;

	public AbstractClassIndexStrategy(ClassMetadata classMetadata) {
		_classMetadata = classMetadata;
	}

	protected int classMetadataID() {
		return _classMetadata.getID();
	}

	public int ownLength() {
		return Const4.ID_LENGTH;
	}

	protected abstract void internalAdd(Transaction trans, int id);

	public final void add(Transaction trans, int id) {
		if (DTrace.enabled) {
	        DTrace.ADD_TO_CLASS_INDEX.log(id);
	    }
		checkId(id);
		internalAdd(trans, id);
	}	

	protected abstract void internalRemove(Transaction ta, int id);

	public final void remove(Transaction ta, int id) {
	    if (DTrace.enabled){
	        DTrace.REMOVE_FROM_CLASS_INDEX.log(id);
	    }
	    checkId(id);
	    internalRemove(ta, id);
	}
	
	private void checkId(int id) {
		if (Deploy.debug) {
            if (id == 0) {
                throw new IllegalArgumentException("id can't be zero");
            }
        }
	}
}