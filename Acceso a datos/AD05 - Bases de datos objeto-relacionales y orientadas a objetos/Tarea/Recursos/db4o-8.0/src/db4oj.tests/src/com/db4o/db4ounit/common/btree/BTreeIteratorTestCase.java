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
package com.db4o.db4ounit.common.btree;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.data.*;

/**
 * @exclude
 */
public class BTreeIteratorTestCase extends BTreeTestCaseBase {
	
	public void testEmpty(){
		Iterator4 iterator = _btree.iterator(trans());
		Assert.isNotNull(iterator);
		Assert.isFalse(iterator.moveNext());
	}
	
	public void testOneKey(){
		_btree.add(trans(), new Integer(1));
		Iterator4 iterator = _btree.iterator(trans());
		Assert.isTrue(iterator.moveNext());
		Assert.areEqual(new Integer(1), iterator.current());
		Assert.isFalse(iterator.moveNext());
	}
	
	public void testManyKeys(){
		for (int keyCount = 50; keyCount < 70; keyCount++) {
			_btree = newBTree();
			Iterable4 keys = randomPositiveIntegersWithoutDuplicates(keyCount);
			Iterator4 keyIterator = keys.iterator();
			while(keyIterator.moveNext()){
				Integer currentKey = (Integer) keyIterator.current();
				_btree.add(trans(), currentKey);
			}
			Iterator4Assert.sameContent(keys.iterator(), _btree.iterator(trans()));
		}
	}

	private Iterable4 randomPositiveIntegersWithoutDuplicates(int keyCount) {
		Iterable4 generator = Generators.take(keyCount, Streams.randomIntegers());
		Collection4 res = new Collection4();
		Iterator4 i = generator.iterator();
		while(i.moveNext()){
			Integer currentInteger = (Integer) i.current();
			if(currentInteger.intValue() < 0){
				currentInteger = new Integer(- currentInteger.intValue());
			}
			if(! res.contains(currentInteger)){
				res.add(currentInteger);
			}
		}
		return res;
	}

}
