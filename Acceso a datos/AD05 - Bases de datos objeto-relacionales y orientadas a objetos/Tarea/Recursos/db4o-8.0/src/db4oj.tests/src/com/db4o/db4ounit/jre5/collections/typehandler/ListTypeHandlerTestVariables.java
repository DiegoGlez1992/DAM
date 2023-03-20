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
package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import db4ounit.fixtures.*;

/**
 */
@decaf.Ignore
public final class ListTypeHandlerTestVariables {
	
	public final static FixtureVariable LIST_IMPLEMENTATION = new FixtureVariable("list");
	
	public final static FixtureVariable ELEMENTS_SPEC = new FixtureVariable("elements");
	
	public final static FixtureVariable LIST_TYPEHANDER = new FixtureVariable("typehandler");
	
	public final static FixtureProvider LIST_FIXTURE_PROVIDER = 
			new SimpleFixtureProvider(
				LIST_IMPLEMENTATION,
				new Object[] {
						new ArrayListItemFactory(),
						new LinkedListItemFactory(),
						new ListItemFactory(),
						new NamedArrayListItemFactory(),
				}
			);
	
	public final static FixtureProvider TYPEHANDLER_FIXTURE_PROVIDER =  
			new SimpleFixtureProvider(LIST_TYPEHANDER,
			        new Object[]{
			    		null, 
			        }
			    );

	public final static ListTypeHandlerTestElementsSpec STRING_ELEMENTS_SPEC = 
		new ListTypeHandlerTestElementsSpec(new Object[]{ "zero", "one" }, "two", "zzz");
	public final static ListTypeHandlerTestElementsSpec INT_ELEMENTS_SPEC =
		new ListTypeHandlerTestElementsSpec(new Object[]{ new Integer(0), new Integer(1) }, new Integer(2), new Integer(Integer.MAX_VALUE));
	public final static ListTypeHandlerTestElementsSpec OBJECT_ELEMENTS_SPEC =
		new ListTypeHandlerTestElementsSpec(new Object[]{ new ReferenceElement(0), new ReferenceElement(1) }, new ReferenceElement(2), null);
	
	private ListTypeHandlerTestVariables() {
	}

	public static class ReferenceElement {

		public int _id;
		
		public ReferenceElement(int id) {
			_id = id;
		}
		
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null || getClass() != obj.getClass()) {
				return false;
			}
			ReferenceElement other = (ReferenceElement) obj;
			return _id == other._id;
		}
		
		public int hashCode() {
			return _id;
		}
		
		public String toString() {
			return "FCE#" + _id;
		}

	}
	
	private static class ArrayListItemFactory extends AbstractListItemFactory implements Labeled {
		private static class Item {
			public ArrayList _list = new ArrayList();
		}
		
		public Object newItem() {
			return new Item();
		}

		public Class itemClass() {
			return Item.class;
		}

		public Class containerClass() {
			return ArrayList.class;
		}

		public String label() {
			return "ArrayList";
		}
	}

	private static class LinkedListItemFactory extends AbstractListItemFactory implements Labeled {
		private static class Item {
			public LinkedList _list = new LinkedList();
		}
		
		public Object newItem() {
			return new Item();
		}

		public Class itemClass() {
			return Item.class;
		}

		public Class containerClass() {
			return LinkedList.class;
		}

		public String label() {
			return "LinkedList";
		}
	}

	private static class ListItemFactory extends AbstractListItemFactory implements Labeled {
		private static class Item {
			public List _list = new LinkedList();
		}
		
		public Object newItem() {
			return new Item();
		}

		public Class itemClass() {
			return Item.class;
		}

		public Class containerClass() {
			return LinkedList.class;
		}

		public String label() {
			return "[Linked]List";
		}
	}
	
	private static class NamedArrayListItemFactory extends AbstractListItemFactory implements Labeled {
	    
	    private static class Item {
	        public ArrayList _list = new NamedArrayList();
	    }
	    
	    public Object newItem() {
	        return new Item();
	    }

	    public Class itemClass() {
	        return NamedArrayListItemFactory.Item.class;
	    }

	    public Class containerClass() {
	        return NamedArrayList.class;
	    }

	    public String label() {
	        return "NamedArrayList";
	    }
	}

}
