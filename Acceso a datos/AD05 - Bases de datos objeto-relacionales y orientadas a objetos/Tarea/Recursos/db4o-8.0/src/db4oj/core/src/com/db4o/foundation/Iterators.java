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
package com.db4o.foundation;

/**
 * Iterator primitives (concat, map, reduce, filter, etc...).
 * 
 * @exclude
 * 
 * @sharpen.partial
 */
public class Iterators {
	
	/**
	 * Constant indicating that the current element in a {@link #map} operation
	 * should be skipped.
	 */
	public static final Object SKIP = new Object();

	public static final Iterator4 EMPTY_ITERATOR = new Iterator4() {
		public Object current() {
			throw new IllegalStateException();
		}

		public boolean moveNext() {
			return false;
		}

		public void reset() {
			// do nothing
		}
	};
	
	public static final Iterable4 EMPTY_ITERABLE = new Iterable4() {
		public Iterator4 iterator() {
			return EMPTY_ITERATOR;
		}
	};
	
	static final Object NO_ELEMENT = new Object();

	/**
	 * Generates {@link EnumerateIterator.Tuple} items with indexes starting at 0.
	 * 
	 * @param iterable the iterable to be enumerated
	 */
	public static Iterable4 enumerate(final Iterable4 iterable) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return new EnumerateIterator(iterable.iterator());
			}
		};
	}
	
	public static boolean any(Iterator4 iterator, Predicate4 condition) {
		while (iterator.moveNext()) {
			if (condition.match(iterator.current())) {
				return true;
			}
		}
		return false;
	}
	
	public static Iterator4 concat(Iterator4... array) {
		return concat(iterate((Object[])array));
	}
	
	public static Iterator4 concat(Iterator4 iterators) {
		return new CompositeIterator4(iterators);
	}
	
	public static Iterable4 concat(Iterable4... iterables) {
		return concat(iterable(iterables));
	}
	
	public static Iterable4 concat(final Iterable4 iterables) {
		return new CompositeIterable4(iterables);
	}
	
	public static Iterator4 concat(Iterator4 first, Iterator4 second) {
		return concat(new Iterator4[] { first, second });
	}
	
	public static Iterable4 concatMap(Iterable4 iterable, Function4 function) {
		return concat(map(iterable, function));
	}
	
	/**
	 * Returns a new iterator which yields the result of applying the function
	 * to every element in the original iterator.
	 * 
	 * {@link Iterators#SKIP} can be returned from function to indicate the current
	 * element should be skipped. 
	 * 
	 * @param iterator
	 * @param function
	 * @return
	 */
	public static Iterator4 map(Iterator4 iterator, Function4 function) {
		return new FunctionApplicationIterator(iterator, function);
	}
	
	public static Iterator4 map(Object[] array, Function4 function) {
		return map(new ArrayIterator4(array), function);
	}
	
	public static <T> Iterator4<T> filter(T[] array, Predicate4<T> predicate) {
		return filter(new ArrayIterator4(array), predicate);
	}
	
	public static Iterable4 filter(final Iterable4 source, final Predicate4 predicate) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return filter(source.iterator(), predicate);
			}
		};
	}
	
	public static Iterator4 filter(Iterator4 iterator, Predicate4 predicate) {
		return new FilteredIterator(iterator, predicate);
	}
	
	public static Iterable4 singletonIterable(final Object element) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return singletonIterator(element);
			}
		};
	}
	
	public static Iterable4 append(Iterable4 front, Object last) {
		return concat(iterable(new Object[] { front, singletonIterable(last) }));
	}
	
	/**
	 * @sharpen.ignore
	 */
	@decaf.Ignore(decaf.Platform.JDK11)
	public static <T> Iterator4 iterator(java.util.Collection<T> c) {
		return new JdkCollectionIterator4(c);
	}
	
	public static Iterator4 iterator(Iterable4 iterable) {
		return iterable.iterator();
	}
	
	/**
	 * @sharpen.unwrap
	 * @sharpen.ignore
	 */
	@decaf.ReplaceFirst(value="return iterator;", platform=decaf.Platform.JDK11)
	public static <T> java.util.Iterator<T> platformIterator(Iterator4 iterator) {
		return new Iterator4JdkIterator(iterator);
	}
	
	public static <T> Iterator4 iterate(T... array) {
		return new ArrayIterator4(array);
	}

	public static <T> Iterator4 revert(Iterator4<T> iterator) {
		iterator.reset();
		List4 tail = null;
		while(iterator.moveNext()){
			tail = new List4<T>(tail, iterator.current());
		}
		return iterate(tail);
	}
	
	public static <T> Iterator4 iterate(List4 list) {
		if(list == null){
			return EMPTY_ITERATOR;
		}
		Collection4 collection = new Collection4();
		while(list != null){
			collection.add(list._element);
			list = list._next;
		}
		return collection.iterator();
	}

	public static int size(Iterable4 iterable) {
		return size(iterable.iterator());
	}
	
	public static Object next(Iterator4 iterator) {
		if (!iterator.moveNext()) {
			throw new IllegalStateException();
		}
		return iterator.current();
	}

	public static int size(Iterator4 iterator) {
		int count=0;
		while (iterator.moveNext()) {
			++count;
		}
		return count;
	}
	
	public static String toString(Iterable4 i) {
		return toString(i.iterator());
	}

	public static String toString(Iterator4 i) {
		return join(i, "[", "]", ", ");
	}
	
	public static String join(Iterable4 i, String separator) {
		return join(i.iterator(), separator);
	}
	
	public static String join(Iterator4 i, String separator) {
		return join(i, "", "", separator);
	}

	public static String join(Iterator4 i, final String prefix,
			final String suffix, final String separator) {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		if (i.moveNext()) {
			sb.append(i.current());
			while (i.moveNext()) {
				sb.append(separator);
				sb.append(i.current());
			}
		}
		sb.append(suffix);
		return sb.toString();
	}
	
	public static Object[] toArray(Iterator4 tests) {
		return toArray(tests, new ArrayFactory() {
			public Object[] newArray(int size) {
				return new Object[size];
			}
		});
	}

	public static Object[] toArray(Iterator4 tests, ArrayFactory factory) {
		Collection4 elements = new Collection4(tests);
		return elements.toArray(factory.newArray(elements.size()));
	}
	
	/**
	 * Yields a flat sequence of elements. Any {@link Iterable4} or {@link Iterator4}
	 * found in the original sequence is recursively flattened.
	 * 
	 * @param iterator original sequence
	 */
	public static Iterator4 flatten(Iterator4 iterator) {
		return new FlatteningIterator(iterator);
	}
	
	public static Iterable4 map(final Iterable4 iterable, final Function4 function) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return map(iterable.iterator(), function);
			}
		};
	}
	
	public static Iterable4 crossProduct(Iterable4 iterables) {
		return crossProduct((Iterable4[])toArray(iterables.iterator(), new ArrayFactory() {
			public Object[] newArray(int size) {
				return new Iterable4[size];
			}
		}));
	}

	public static Iterable4 crossProduct(Iterable4... iterables) {
		return crossProduct(iterables, 0, Iterators.EMPTY_ITERABLE);
	}

	private static Iterable4 crossProduct(final Iterable4[] iterables, final int level, final Iterable4 row) {
		if (level == iterables.length - 1) {
			return map(
				iterables[level],
				new Function4() {
					public Object apply(Object arg) {
						return append(row, arg);
					}
				}
			);
		}
		return concatMap(iterables[level],
				new Function4() {
					public Object apply(Object arg) {
						return crossProduct(iterables, level+1, append(row, arg));
					}
				});
	}

	public static <T> Iterable4 iterable(final T... objects) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return iterate(objects);
			}
		};
	}

	public static Iterator4 singletonIterator(final Object element) {
		return new SingleValueIterator(element);
	}

	public static Iterable4 iterable(final Iterator4 iterator) {
		return new Iterable4() {
			public Iterator4 iterator() {
				return iterator;
			}
		};
	}

	public static Iterator4 copy(final Iterator4 iterator) {
		return new Collection4(iterator).iterator();
	}

	public static <T> Iterator4<T> take(final int count, final Iterator4<T> iterator) {
		return new Iterator4<T>() {
			private int _taken = 0;

			public T current() {
				if (_taken > count) {
					throw new IllegalStateException();
				}
				return iterator.current();
            }

			public boolean moveNext() {
				if (_taken < count) {
					if (!iterator.moveNext()) {
						_taken = count;
						return false;
					}
					++_taken;
					return true;
				}
				return false;
            }

			public void reset() {
				throw new NotImplementedException();
            }
		};
	}

	public static Iterator4<Integer> range(int fromInclusive, int toExclusive) {
		if (toExclusive < fromInclusive) {
			throw new IllegalArgumentException();
		}
		return take(
					toExclusive - fromInclusive,
					series(fromInclusive - 1, new Function4<Integer, Integer>() { public Integer apply(Integer i) {
						return i + 1;
                    }}).iterator());
    }

	public static <T> Iterable4<T> series(final T seed, final Function4<T, T> function) {
        return new Iterable4() {
        	public Iterator4<T> iterator() {
        		return new Iterator4<T>() {
        			private T _current = seed;
        			
    				public T current() {
    					return _current;
                    }
    
    				public boolean moveNext() {
    					_current = function.apply(_current);
    					return true;
                    }
    
    				public void reset() {
    					_current = seed;
                    }
        		};
        	}
        };
    }
}
