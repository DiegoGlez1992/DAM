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
package com.db4o.ta.instrumentation.test.collections;

import java.util.*;

public class ArrayListFactory {

	public List createArrayList() {
		return new ArrayList();
	}

	public List createSizedArrayList() {
		return new ArrayList(42);
	}

	public List createNestedArrayList() {
		return new ArrayList(new ArrayList(42));
	}

	public List createMethodArgArrayList() {
		return new ArrayList(size());
	}

	public List createConditionalArrayList() {
		return new ArrayList(size() > 41 ? size() : 42);
	}

	private int size() {
		return 42;
	}
}
