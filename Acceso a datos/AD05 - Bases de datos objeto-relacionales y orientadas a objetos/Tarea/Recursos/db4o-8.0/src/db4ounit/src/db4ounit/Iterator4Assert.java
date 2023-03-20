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

import com.db4o.foundation.*;


public class Iterator4Assert {
	
	public static void areEqual(Iterable4 expected, Iterable4 actual) {
		areEqual(expected.iterator(), actual.iterator());
	}

	public static void areEqual(Iterator4 expected, Iterator4 actual) {
		if (null == expected) {
			Assert.isNull(actual);
			return;
		}
		Assert.isNotNull(actual);		
		while (expected.moveNext()) {
			assertNext(expected.current(), actual);
		}
		if (actual.moveNext()) {
			unexpected(actual.current());
		}
	}

	private static void unexpected(Object element) {
		Assert.fail("Unexpected element: " + element);
	}

	public static void assertNext(final Object expected, Iterator4 iterator) {
		Assert.isTrue(iterator.moveNext(), "'" + expected + "' expected.");
		Assert.areEqual(expected, iterator.current());
	}

	public static void areEqual(Object[] expected, Iterator4 iterator) {
		areEqual(new ArrayIterator4(expected), iterator);
	}

	public static void sameContent(Object[] expected, Iterator4 actual) {
		sameContent(new ArrayIterator4(expected), actual);
	}

	public static void sameContent(Iterator4 expected, Iterator4 actual) {
		final Collection4 allExpected = new Collection4(expected);
		while (actual.moveNext()) {
			final Object current = actual.current();
			final boolean removed = allExpected.remove(current);
			if (! removed) {
				unexpected(current);
			}
		}
		Assert.isTrue(allExpected.isEmpty(), "Still missing: " + allExpected.toString());
	}

	public static void areInstanceOf(Class expectedType, final Iterable4 values) {
        for (final Iterator4 i = values.iterator(); i.moveNext();) {
    		Assert.isInstanceOf(expectedType, i.current());
    	}	
    }

	public static void all(Iterable4 values, Predicate4 condition) {
		final Iterator4 iterator = values.iterator();
		while (iterator.moveNext()) {
			if (!condition.match(iterator.current())) {
				Assert.fail("Condition does not hold for for value '" + iterator.current() + "'.");
			}
		}
    }

}
