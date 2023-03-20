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
package com.db4o.db4ounit.common.internal.metadata;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.metadata.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClassMetadataIntegrationTestCase extends AbstractDb4oTestCase {
	
	public static class SuperClazz {
		public int _id;
		public String _name;
	}

	public static class SubClazz extends SuperClazz {
		public int _age;
	}

	protected void store() throws Exception {
		store(new SubClazz());
	}
	
	public void testFieldTraversal() {		
		final Collection4 expectedNames=new Collection4(new ArrayIterator4(new String[]{"_id","_name","_age"}));
		ClassMetadata classMetadata = classMetadataFor(SubClazz.class);
		
        classMetadata.traverseAllAspects(new TraverseFieldCommand() {
    		
			@Override
			protected void process(FieldMetadata field) {
				Assert.isNotNull(expectedNames.remove(field.getName()));
			}
		});

		Assert.isTrue(expectedNames.isEmpty());
	}
	
	
	public void testPrimitiveArrayMetadataIsPrimitiveTypeMetadata() {
		ClassMetadata byteArrayMetadata = container().produceClassMetadata(reflectClass(byte[].class));
		Assert.isInstanceOf(PrimitiveTypeMetadata.class, byteArrayMetadata);
	}
}
