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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.metadata.*;
import com.db4o.internal.metadata.HierarchyAnalyzer.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class HierarchyAnalyzerTestCase extends AbstractDb4oTestCase{
	
	public static class A {
		
	}
	
	public static class BA extends A {
		
	}
	
	public static class CBA extends BA {
		
	}
	
	public static class DA extends A {
		
	}
	
	public static class E {
		
	}
	
	
	public void testRemovedImmediateSuperclass(){
		assertDiffBetween(DA.class, CBA.class,
				new Removed(produceClassMetadata(BA.class)),
				new Same(produceClassMetadata(A.class)));
	}
	
	public void testRemoveTopLevelSuperclass(){
		assertDiffBetween(E.class, BA.class,
				new Removed(produceClassMetadata(A.class)));
	}
	
	public void testAddedImmediateSuperClass(){
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				assertDiffBetween(CBA.class, DA.class);
			}
		});
	}
	
	public void testAddedTopLevelSuperClass(){
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				assertDiffBetween(BA.class, E.class);
			}
		});
	}


	private void assertDiffBetween(Class<?> runtimeClass, Class<?> storedClass,
			Diff... expectedDiff) {
		ClassMetadata classMetadata = produceClassMetadata(storedClass);
		ReflectClass reflectClass = reflectClass(runtimeClass);
		List<Diff> ancestors = new HierarchyAnalyzer(classMetadata, reflectClass).analyze();
		assertDiff(ancestors, expectedDiff );
	}


	private ClassMetadata produceClassMetadata(Class<?> storedClass) {
		return container().produceClassMetadata(reflectClass(storedClass));
	}


	private void assertDiff(List<Diff> actual, Diff...expected) {
		Iterator4Assert.areEqual(Iterators.iterate(expected), Iterators.iterator(actual) );
	}

}
