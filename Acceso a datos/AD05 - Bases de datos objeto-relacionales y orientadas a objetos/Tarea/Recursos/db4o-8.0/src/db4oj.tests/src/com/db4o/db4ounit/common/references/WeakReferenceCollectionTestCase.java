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
package com.db4o.db4ounit.common.references;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class WeakReferenceCollectionTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
	}

    //COR-1839
	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	public void test() throws InterruptedException{
		if(! Platform4.hasWeakReferences()){
			return;
		}
		Item item = new Item();
		store(item);
		commit();
		final ByRef<ObjectReference> reference = new ByRef();
		referenceSystem().traverseReferences(new Visitor4<ObjectReference>() {
			public void visit(ObjectReference ref) {
				if(ref.getObject() instanceof Item){
					reference.value = ref;
				}
			}
		});
		Assert.isNotNull(reference.value);
		item = null;
		
		long timeout = 10000;
		
		long startTime = System.currentTimeMillis();
		while(true){
			long currentTime = System.currentTimeMillis();
			if(currentTime - startTime >= timeout){
				Assert.fail("Timeout waiting for WeakReference collection.");
			}
			System.gc();
			System.runFinalization();
			Thread.sleep(1);
			if(reference.value.getObject() == null){
				break;
			}
		}
		
		startTime = System.currentTimeMillis();
		while(true){
			long currentTime = System.currentTimeMillis();
			if(currentTime - startTime >= timeout){
				Assert.fail("Timeout waiting for removal of ObjectReference from ReferenceSystem.");
			}
			final BooleanByRef found = new BooleanByRef();
			referenceSystem().traverseReferences(new Visitor4<ObjectReference>() {
				public void visit(ObjectReference ref) {
					if(ref == reference.value){
						found.value = true;
					}
				}
			});
			if(! found.value){
				return;
			}
			Thread.sleep(10);
		}
		
	}

	private ReferenceSystem referenceSystem() {
		return trans().referenceSystem();
	}

}
