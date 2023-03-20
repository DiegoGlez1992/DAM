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
package com.db4o.db4ounit.common.internal;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ClassMetadataTypeHandlerIntegrationTestCase extends AbstractDb4oTestCase {
    
    public static class Item {
    }
    
    public static class MyReferenceType {
    }

    public static class MyReferenceTypeHandler implements ReferenceTypeHandler {

		public void activate(ReferenceActivationContext context) {
		}

		public void defragment(DefragmentContext context) {
		}

		public void delete(DeleteContext context) throws Db4oIOException {
		}

		public void write(WriteContext context, Object obj) {
		}
    }
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(MyReferenceType.class), 
            new MyReferenceTypeHandler());
    }
    
    protected void store() throws Exception {
        store(new Item());
        store(new MyReferenceType());
    }
    
    public void testIsValueType(){
        for (Pair<Object, Boolean> typeDescriptor : typeDescriptors()) { 
            ClassMetadata classMetadata = container().classMetadataForObject(typeDescriptor.first);
            Assert.areEqual(typeDescriptor.second.booleanValue(), classMetadata.isValueType(), classMetadata.toString());
        }
    }

	private Pair<Object, Boolean>[] typeDescriptors() {
		 return new Pair[] {
			 pair(new Integer(1), true),
			 pair(new Date(), true),
			 pair("astring", true),
			 pair(new Item(), false),
			 pair((new int[] {1}), false),
			 pair((new Date[] {new Date()}), false),
			 pair((new Item[] {new Item()}), false),
			 pair(new MyReferenceType(), false),
		 };
	}
	
	private <TFirst, TSecond> Pair<TFirst, TSecond> pair(TFirst first, TSecond second) {
		return new Pair<TFirst, TSecond>(first, second);
	}

}
