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
package db4ounit;

import java.util.*;

import com.db4o.foundation.*;

@decaf.Ignore(decaf.Platform.JDK11)
public class IteratorAssert {
    
    public static void areEqual(Iterable expected, Iterable actual) {
    	areEqual(expected.iterator(), actual.iterator());
    }
    
    public static void areEqual(Iterator expected, Iterator actual) {
        if (null == expected) {
            Assert.isNull(actual);
            return;
        }
        Assert.isNotNull(actual);       
        while (expected.hasNext()) {
            Assert.isTrue(actual.hasNext());
            Assert.areEqual(expected.next(), actual.next());
        }
        Assert.isFalse(actual.hasNext());
    }

    public static void areEqual(Object[] expected, Iterator iterator) {
        Vector v = new Vector();
        for (int i = 0; i < expected.length; i++) {
            v.add(expected[i]);
        }
        areEqual(v.iterator(), iterator);
    }
    
    public static void sameContent(Object[] expected, Iterable actual){
    	List expectedList = new ArrayList();
    	for (Object expectedObject : expected) {
			expectedList.add(expectedObject);
		}
    	sameContent(expectedList, actual);
    }
    
    public static void sameContent(Iterable expected, Iterable actual) {
    	sameContent(expected.iterator(), actual.iterator());
    }	
    
	public static void sameContent(Iterator expected, Iterator actual) {
		final Collection4 allExpected = new Collection4();
		while(expected.hasNext()){
			allExpected.add(expected.next());
		}
		while (actual.hasNext()) {
			final Object current = actual.next();
			final boolean removed = allExpected.remove(current);
			if (! removed) {
				unexpected(current);
			}
		}
		Assert.isTrue(allExpected.isEmpty(), allExpected.toString());
	}
	
	private static void unexpected(Object element) {
		Assert.fail("Unexpected element: " + element);
	}
}
