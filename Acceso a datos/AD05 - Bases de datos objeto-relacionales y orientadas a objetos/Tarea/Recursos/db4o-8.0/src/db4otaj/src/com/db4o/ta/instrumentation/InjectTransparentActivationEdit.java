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
package com.db4o.ta.instrumentation;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.ta.*;

/**
 * Instrumentation step for injecting Transparent Activation awareness by
 * implementing {@link Activatable}.
 */
public class InjectTransparentActivationEdit extends CompositeBloatClassEdit {

	public InjectTransparentActivationEdit(ClassFilter filter) {
		this(filter, true);
	}

	public InjectTransparentActivationEdit(ClassFilter filter, boolean withCollections) {
		super(createEdits(filter, withCollections));
	}
	
	private static BloatClassEdit[] createEdits(ClassFilter filter, boolean withCollections) {
		BloatClassEdit[] firstSet = new BloatClassEdit[] {
				new CheckApplicabilityEdit(),
		};
		BloatClassEdit[] secondSet = new BloatClassEdit[] {
				new InjectTAInfrastructureEdit(filter), 
				new InstrumentFieldAccessEdit(filter),
		};
		BloatClassEdit[] edits = firstSet;
		if(withCollections) {
			BloatClassEdit[] collectionEdit = new BloatClassEdit[]{
					new ReplaceClassOnInstantiationEdit(new ClassReplacementSpec[] {
							new ClassReplacementSpec(ArrayList.class, ActivatableArrayList.class),
							new ClassReplacementSpec(HashMap.class, ActivatableHashMap.class),
							new ClassReplacementSpec(Hashtable.class, ActivatableHashtable.class),
							new ClassReplacementSpec(LinkedList.class, ActivatableLinkedList.class),
							new ClassReplacementSpec(Stack.class, ActivatableStack.class),
							new ClassReplacementSpec(HashSet.class, ActivatableHashSet.class),
							new ClassReplacementSpec(TreeSet.class, ActivatableTreeSet.class),
							
					}),
			};
			edits = (BloatClassEdit[]) Arrays4.merge(edits, collectionEdit, BloatClassEdit.class);
		}
		edits = (BloatClassEdit[]) Arrays4.merge(edits, secondSet, BloatClassEdit.class);
		return edits;
	}
}
