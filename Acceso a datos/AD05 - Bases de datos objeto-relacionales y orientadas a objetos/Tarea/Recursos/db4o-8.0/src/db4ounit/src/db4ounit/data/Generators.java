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

import com.db4o.foundation.*;

/**
 * @sharpen.partial
 */
public class Generators {

	public static Iterable4 arbitraryValuesOf(Class type) {
		final Iterable4 platformSpecific = platformSpecificArbitraryValuesOf(type);
		if (null != platformSpecific) {
			return platformSpecific;
		}
		if (type == Integer.class) {
			return take(10, Streams.randomIntegers());
		}
		if (type == String.class) {
			return take(10, Streams.randomStrings());
		}
		throw new NotImplementedException("No generator for type " + type);
    }

	/**
	 * @sharpen.ignore
	 */
	private static Iterable4 platformSpecificArbitraryValuesOf(Class type) {
		return null;
    }

	static Iterable4 trace(Iterable4 source) {
		return Iterators.map(source, new Function4() {
			public Object apply(Object value) {
				System.out.println(value);
				return value;
            }
		});
    }

	public static Iterable4 take(final int count, final Iterable4 source) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return new Iterator4() {
					private int _taken = 0;
					private Iterator4 _delegate = source.iterator();

					public Object current() {
						if (_taken > count) {
							throw new IllegalStateException();
						}
						return _delegate.current();
                    }

					public boolean moveNext() {
						if (_taken < count) {
							if (!_delegate.moveNext()) {
								_taken = count;
								return false;
							}
							++_taken;
							return true;
						}
						return false;
                    }

					public void reset() {
						_taken = 0;
						_delegate = source.iterator();
                    }
				};
			}
		};
    }

}
