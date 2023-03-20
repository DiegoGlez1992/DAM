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
package com.db4o.cs.internal.objectexchange;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

public class StandardReferenceCollector implements ReferenceCollector {

	private Transaction _transaction;
	
	public StandardReferenceCollector(Transaction transaction) {
	    _transaction = transaction;
    }

	public Iterator4<Integer> referencesFrom(int id) {
	    final CollectIdContext context = CollectIdContext.forID(_transaction, id);
	    final ClassMetadata classMetadata = context.classMetadata();
	    if (null == classMetadata) {
	    	// most probably ClassMetadata reading
	    	return Iterators.EMPTY_ITERATOR;
	    }
	    if (!classMetadata.hasIdentity()) {
	    	throw new IllegalStateException(classMetadata.toString());
	    }
	    if (!Handlers4.isCascading(classMetadata.typeHandler())) {
	    	return Iterators.EMPTY_ITERATOR;
	    }
		classMetadata.collectIDs(context);
		return new TreeKeyIterator(context.ids());
    }

}
