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
 * Shared implementation for a paged collection.
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public 
class PagedBackingStore extends ActivatableImpl {

	public final static int INITIAL_PAGE_COUNT = 16;	
	
	private Page[] _pages = new Page[INITIAL_PAGE_COUNT];
	private int _top = 0;

	public PagedBackingStore() {
		addNewPage();
	}
	
	public boolean add(Object item) {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return getPageForAdd().add(item);
	}
	
	public int size() {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		return _top * Page.PAGESIZE - lastPage().capacity();
	}
	
	public Object get(int itemIndex) {
		// TA BEGIN
		activate(ActivationPurpose.READ);
		// TA END
		Page page = pageHolding(itemIndex);
		return page.get(indexInPage(itemIndex));
	}

	private Page lastPage() {
		return _pages[_top - 1];
	}

	private Page getPageForAdd() {
		Page lastPage = lastPage();
		if (lastPage.atCapacity()) {
			lastPage = addNewPage();
		}
		return lastPage;
	}
	
	private Page addNewPage() {
		final Page page = new Page(_top);
		if(_top == _pages.length) {
			growPages();
		}
		_pages[_top] = page;
		_top++;
		return page;
	}
	
	private void growPages() {
		Page[] grown = new Page[_pages.length*2];
		System.arraycopy(_pages, 0, grown, 0, _pages.length);
		_pages = grown;
	}

	/**
	 * Will return the page that holds the index passed in.
	 * For example, if pagesize == 100 and index == 525, then this will return page 5.
	 *
	 * @param itemIndex
	 * @return
	 */
	private Page pageHolding(int itemIndex) {
		return _pages[pageIndex(itemIndex)];
	}
	
	private int pageIndex(int itemIndex) {
		return itemIndex / Page.PAGESIZE;
	}
	
	private int indexInPage(int itemIndex) {
		return itemIndex % Page.PAGESIZE;
	}
}
