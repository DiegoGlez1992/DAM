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
package com.db4o.db4ounit.common.ta.collections;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Page extends ActivatableImpl {

	public static final int PAGESIZE = 100;
	
	private Object[] _data = new Object[PAGESIZE];
	private int _top = 0;

	private int _pageIndex;

	private transient boolean _dirty = false;

	public Page(int pageIndex) {
		_pageIndex = pageIndex;
	}

	public boolean add(Object obj) {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		_dirty = true;
		_data[_top++] = obj;
		return true;
	}

	public int size() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return _top;
	}

	public Object get(int indexInPage) {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
//		System.out.println("got from page: " + _pageIndex);
		_dirty = true; // just to be safe, we'll mark things as dirty if they are used.
		return _data[indexInPage];
	}

	public boolean isDirty() {
		// TA BEGIN
//		activate();
		// TA END
		return _dirty;
	}

	public void setDirty(boolean dirty) {
		// TA BEGIN
//		activate();
		// TA END
		_dirty = dirty;
	}

	public int getPageIndex() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return _pageIndex;
	}

	public boolean atCapacity() {
		return capacity() == 0;
	}
	
	public int capacity() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return Page.PAGESIZE - size();
	}
}
