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

import com.db4o.internal.*;
import com.db4o.internal.references.*;

import db4ounit.extensions.*;


public class ReferenceSystemIntegrationTestCase extends AbstractDb4oTestCase{
	
	private static final int[] IDS = new int[] {100,134,689, 666, 775};
	
	private static final Object[] REFERENCES = createReferences();

	public static void main(String[] args) {
		new ReferenceSystemIntegrationTestCase().runSolo();
	}
	
	public void testTransactionalReferenceSystem(){
		ReferenceSystem transactionalReferenceSystem = new TransactionalReferenceSystem();
		assertAllRerefencesAvailableOnNew(transactionalReferenceSystem);
		transactionalReferenceSystem.rollback();
		assertEmpty(transactionalReferenceSystem);
		assertAllRerefencesAvailableOnCommit(transactionalReferenceSystem);
	}
	
	public void testHashCodeReferenceSystem(){
		ReferenceSystem referenceSystem = new HashcodeReferenceSystem();
		assertAllRerefencesAvailableOnNew(referenceSystem);
	}
	
	private void assertAllRerefencesAvailableOnCommit(ReferenceSystem referenceSystem){
		fillReferenceSystem(referenceSystem);
		referenceSystem.commit();
		assertAllReferencesAvailable(referenceSystem);
	}
	
	private void assertAllRerefencesAvailableOnNew(ReferenceSystem referenceSystem){
		fillReferenceSystem(referenceSystem);
		assertAllReferencesAvailable(referenceSystem);
	}
	
	private void assertEmpty(ReferenceSystem referenceSystem){
		assertContains(referenceSystem, new Object[]{});
	}
	
	private void assertAllReferencesAvailable(ReferenceSystem referenceSystem){
		assertContains(referenceSystem, REFERENCES);
	}

	private void assertContains(ReferenceSystem referenceSystem, final Object[] objects) {
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(objects);
		referenceSystem.traverseReferences(expectingVisitor);
		expectingVisitor.assertExpectations();
	}
	
	private void fillReferenceSystem(ReferenceSystem referenceSystem){
		for (int i = 0; i < REFERENCES.length; i++) {
			referenceSystem.addNewReference((ObjectReference)REFERENCES[i]);
		}
	}
	
	private static Object[] createReferences() {
		Object[] references = new Object[IDS.length];
		for (int i = 0; i < IDS.length; i++) {
			ObjectReference ref = new ObjectReference(IDS[i]);
			ref.setObject(new Integer(IDS[i]).toString());
			references[i]= ref; 
		}
		return references;
	}

}
