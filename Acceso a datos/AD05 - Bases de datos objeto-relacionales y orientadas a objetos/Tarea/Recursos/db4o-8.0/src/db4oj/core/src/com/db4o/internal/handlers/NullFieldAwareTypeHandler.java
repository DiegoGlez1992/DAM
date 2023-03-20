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
package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class NullFieldAwareTypeHandler implements FieldAwareTypeHandler{

	public static final FieldAwareTypeHandler INSTANCE = new NullFieldAwareTypeHandler();

	public void addFieldIndices(ObjectIdContextImpl context) {
	}

	public void classMetadata(ClassMetadata classMetadata) {
	}

	public void collectIDs(CollectIdContext context, Predicate4<ClassAspect> predicate) {
	}

	public void deleteMembers(DeleteContextImpl deleteContext, boolean isUpdate) {
	}

	public void readVirtualAttributes(ObjectReferenceContext context) {
	}

	public boolean seekToField(ObjectHeaderContext context, ClassAspect aspect) {
		return false;
	}

	public void defragment(DefragmentContext context) {
	}

	public void delete(DeleteContext context) throws Db4oIOException {
	}

	public void activate(ReferenceActivationContext context) {
	}

	public void write(WriteContext context, Object obj) {
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		return null;
	}

	public TypeHandler4 unversionedTemplate() {
		return null;
	}

	public Object deepClone(Object context) {
		return null;
	}

	public void cascadeActivation(ActivationContext context) {
		
	}

	public void collectIDs(QueryingReadContext context) {
		
	}

	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
		return null;
	}
}
