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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.query.processor.QQueryBase.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public abstract class FieldIndexProcessorTestCaseBase extends
		FieldIndexTestCaseBase {

	public FieldIndexProcessorTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
		super.configure(config);
		indexField(config,ComplexFieldIndexItem.class, "foo");
		indexField(config,ComplexFieldIndexItem.class, "bar");
		indexField(config,ComplexFieldIndexItem.class, "child");
	}

	protected Query createComplexItemQuery() {
		return createQuery(ComplexFieldIndexItem.class);
	}

	protected IndexedNode selectBestIndex(final Query query) {
		final FieldIndexProcessor processor = createProcessor(query);		
		return processor.selectBestIndex();
	}

	protected FieldIndexProcessor createProcessor(final Query query) {
		final QCandidates candidates = getQCandidates(query);		
		return new FieldIndexProcessor(candidates);
	}

	private QCandidates getQCandidates(final Query query) {
		final CreateCandidateCollectionResult result = ((QQuery)query).createCandidateCollection();
		 ((QQuery)query).checkConstraintsEvaluationMode();
		QCandidates candidates = (QCandidates)result.candidateCollection._element;
		return candidates;
	}

	protected void assertComplexItemIndex(String expectedFieldIndex, IndexedNode node) {
		Assert.areSame(complexItemIndex(expectedFieldIndex), node.getIndex());
	}

	protected BTree fieldIndexBTree(Class clazz, String fieldName) {
		return classMetadataFor(clazz).fieldMetadataForName(fieldName).getIndex(null);
	}

	protected BTree classIndexBTree(Class clazz) {
		return ((BTreeClassIndexStrategy)classMetadataFor(clazz).index()).btree();
	}

	private BTree complexItemIndex(String fieldName) {
		return fieldIndexBTree(ComplexFieldIndexItem.class, fieldName);
	}

	protected int[] mapToObjectIds(Query itemQuery, int[] foos) {
		final Transaction trans = transactionFromQuery(itemQuery);
		int[] lookingFor = IntArrays4.clone(foos);
		
		int[] objectIds = new int[foos.length];
		final ObjectSet set = itemQuery.execute();
		while (set.hasNext()) {
			HasFoo item = (HasFoo)set.next();
			for (int i = 0; i < lookingFor.length; i++) {
				if(lookingFor[i] == item.getFoo()){
					lookingFor[i] = -1;
					objectIds[i] = (int) ((ObjectContainerBase)db()).getID(trans, item);
					break;
				}
			}
		}		
		
		int index = indexOfNot(lookingFor, -1);
		if (-1 != index) {
			throw new IllegalArgumentException("Foo '" + lookingFor[index] + "' not found!");
		}
		
		return objectIds;
	}

	public static int indexOfNot(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			if (value != array[i]) {
				return i;
			}
		}
		return -1;
	}

	protected void storeComplexItems(int[] foos, int[] bars) {
		ComplexFieldIndexItem last = null;
		for (int i = 0; i < foos.length; i++) {
			last = new ComplexFieldIndexItem(foos[i], bars[i], last);
			store(last);
	    }
	}

	protected void assertTreeInt(final int[] expectedValues, final TreeInt treeInt) {
		final ExpectingVisitor visitor = ExpectingVisitor.createExpectingVisitor(expectedValues);
		treeInt.traverse(new Visitor4() {
			public void visit(Object obj) {
				visitor.visit(new Integer(((TreeInt)obj)._key));
			}
		});
		visitor.assertExpectations();
	}
	
	protected Transaction transactionFromQuery(Query query) {
		return ((QQuery)query).transaction();
	}



}