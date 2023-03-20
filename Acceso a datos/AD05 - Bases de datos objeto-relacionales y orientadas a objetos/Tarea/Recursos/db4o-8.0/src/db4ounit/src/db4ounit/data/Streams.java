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
package db4ounit.data;

import java.util.*;

import com.db4o.foundation.*;

/**
 * Factory for infinite sequences of values.
 */
public class Streams {

	private static final Random random = new Random();
	
	public static Iterable4 randomIntegers() {
		return Iterators.series(null, new Function4() {
			public Object apply(Object arg) {
	            return new Integer(random.nextInt());
            }
		});
    }
	
	public static Iterable4 randomNaturals(final int ceiling) {
		return Iterators.series(null, new Function4() {
			public Object apply(Object arg) {
	            return new Integer(random.nextInt(ceiling));
            }
		});
	}
	
	public static Iterable4 randomStrings() {
		final int maxLength = 42;
		return Iterators.map(randomNaturals(maxLength), new Function4() {
			public Object apply(Object arg) {
				final int length = ((Integer)arg).intValue();
				return randomString(length);
            }
		});
	}

	private static String randomString(int length) {
		return Iterators.join(Generators.take(length, printableCharacters()), "");
    }

	public static Iterable4 printableCharacters() {
		return Iterators.filter(randomCharacters(), new Predicate4() {
			public boolean match(Object candidate) {
	            final Character character = (Character)candidate;
	            return isPrintable(character.charValue());
            }

			private boolean isPrintable(final char value) {
	            if (value >= 'a' && value <= 'z') {
	            	return true;
	            }
	            if (value >= 'A' && value <= 'Z') {
	            	return true;
	            }
	            if (value >= '0' && value <= '9') {
	            	return true;
	            }
	            switch (value) {
	            case '_':
	            case ' ':
	            case '\r':
	            case '\n':
	            	return true;
	            }
	            return false;
            }
		});
    }

	public static Iterable4 randomCharacters() {
	    final char maxCharInclusive = 'z';
		return Iterators.map(randomNaturals(1 + (int)maxCharInclusive), new Function4() {
			public Object apply(final Object value) {
				return new Character((char)((Integer)value).intValue());
			}
		});
    }	
}
