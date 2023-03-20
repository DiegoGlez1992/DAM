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
package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.foundation.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public final class CollectionFactories {

	
	private CollectionFactories() {
	}

	public static Closure4<ArrayList<CollectionElement>> plainArrayListFactory() {
		return new Closure4<ArrayList<CollectionElement>>() {
				public ArrayList<CollectionElement> run() {
					return new ArrayList<CollectionElement>();
				}
		};
	}

	public static Closure4<ArrayList<CollectionElement>> activatableArrayListFactory() {
		return new Closure4<ArrayList<CollectionElement>>() {
				public ArrayList<CollectionElement> run() {
					return new ActivatableArrayList<CollectionElement>();
				}
		};
	}

	public static Closure4<LinkedList<CollectionElement>> plainLinkedListFactory() {
		return new Closure4<LinkedList<CollectionElement>>() {
				public LinkedList<CollectionElement> run() {
					return new LinkedList<CollectionElement>();
				}
		};
	}

	public static Closure4<LinkedList<CollectionElement>> activatableLinkedListFactory() {
		return new Closure4<LinkedList<CollectionElement>>() {
				public LinkedList<CollectionElement> run() {
					return new ActivatableLinkedList<CollectionElement>();
				}
		};
	}

	public static Closure4<Stack<CollectionElement>> plainStackFactory() {
		return new Closure4<Stack<CollectionElement>>() {
				public Stack<CollectionElement> run() {
					return new Stack<CollectionElement>();
				}
		};
	}

	public static Closure4<Stack<CollectionElement>> activatableStackFactory() {
		return new Closure4<Stack<CollectionElement>>() {
			public Stack<CollectionElement> run() {
				return new ActivatableStack<CollectionElement>();
			}			
		};
	}

	public static Closure4<HashSet<CollectionElement>> plainHashSetFactory() {
		return new Closure4<HashSet<CollectionElement>>() {
				public HashSet<CollectionElement> run() {
					return new HashSet<CollectionElement>();
				}
		};
	}

	public static Closure4<HashSet<CollectionElement>> activatableHashSetFactory() {
		return new Closure4<HashSet<CollectionElement>>() {
			public HashSet<CollectionElement> run() {
				return new ActivatableHashSet<CollectionElement>();
			}			
		};
	}
	
	public static Closure4<TreeSet<CollectionElement>> plainTreeSetFactory() {
		return new Closure4<TreeSet<CollectionElement>>() {
				public TreeSet<CollectionElement> run() {
					return new TreeSet<CollectionElement>();
				}
		};
	}

	public static Closure4<TreeSet<CollectionElement>> activatableTreeSetFactory() {
		return new Closure4<TreeSet<CollectionElement>>() {
			public TreeSet<CollectionElement> run() {
				return new ActivatableTreeSet<CollectionElement>();
			}			
		};
	}


}
