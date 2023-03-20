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
package db4ounit.tests.fixtures;

import db4ounit.*;
import db4ounit.fixtures.*;

public class Set4TestUnit implements TestLifeCycle {
	
	private final Set4 subject = (Set4)SubjectFixtureProvider.value();
	private final Object[] data = MultiValueFixtureProvider.value();

	public void setUp() {
		for (int i=0; i<data.length; ++i) {
			Object element = data[i];
			subject.add(element);
		}
	}
	
	public void testSize() {
		Assert.areEqual(data.length, subject.size());
	}
	
	public void testContains() {
		for (int i=0; i<data.length; ++i) {
			Object element = data[i];
			Assert.isTrue(subject.contains(element));
		}
	}

	public void tearDown() throws Exception {
	}
}