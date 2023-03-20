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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.query.*;

import db4ounit.*;

public class IndexedNodeTestCase extends FieldIndexProcessorTestCaseBase {
	
	public static void main(String[] args) {
		new IndexedNodeTestCase().runSolo();
	}
	
	protected void store() {
		storeItems(new int[] { 3, 4, 7, 9 });
		storeComplexItems(
						new int[] { 3, 4, 7, 9 },
						new int[] { 2, 2, 8, 8 });
	}
	
	public void testTwoLevelDescendOr() {
    	Query query = createComplexItemQuery();
        Constraint c1 = query.descend("child").descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("child").descend("foo").constrain(new Integer(4)).greater();        
        c1.or(c2);
        
        assertSingleOrNode(query);
	}
	
	public void testMultipleOrs() {
    	Query query = createComplexItemQuery();    	
        Constraint c1 = query.descend("foo").constrain(new Integer(4)).smaller();
        for (int i = 0; i < 5; i++) {
        	Constraint c2 = query.descend("foo").constrain(new Integer(4)).greater();        
        	c1 = c1.or(c2);
        }
        assertSingleOrNode(query);
	}
	
	public void testDoubleDescendingOnIndexedNodes() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("foo").constrain(new Integer(3));
		query.descend("bar").constrain(new Integer(2));
		
		final IndexedNode index = selectBestIndex(query);
		assertComplexItemIndex("foo", index);
		
		Assert.isFalse(index.isResolved());
		
		IndexedNode result = index.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		Assert.isTrue(result.isResolved());
		Assert.isNull(result.resolve());
		
		assertComplexItems(new int[] { 4 }, result.toTreeInt());
	}	
	
	public void testTripleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("child").descend("foo").constrain(new Integer(3));
		
		final IndexedNode index = selectBestIndex(query);
		assertComplexItemIndex("foo", index);
		
		Assert.isFalse(index.isResolved());
		
		IndexedNode result = index.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		Assert.isFalse(result.isResolved());
		result = result.resolve();
		Assert.isNotNull(result);
		assertComplexItemIndex("child", result);
		
		assertComplexItems(new int[] { 7 } , result.toTreeInt());
	}
	
	private void assertComplexItems(final int[] expectedFoos, final TreeInt found) {
		Assert.isNotNull(found);
		assertTreeInt(
				mapToObjectIds(createComplexItemQuery(), expectedFoos),
				found);
	}
	
	private void assertSingleOrNode(Query query) {
		Iterator4 nodes = createProcessor(query).collectIndexedNodes();
        Assert.isTrue(nodes.moveNext());
        
        OrIndexedLeaf node = (OrIndexedLeaf)nodes.current();
        Assert.isNotNull(node);
        
        Assert.isFalse(nodes.moveNext());
	}
}
