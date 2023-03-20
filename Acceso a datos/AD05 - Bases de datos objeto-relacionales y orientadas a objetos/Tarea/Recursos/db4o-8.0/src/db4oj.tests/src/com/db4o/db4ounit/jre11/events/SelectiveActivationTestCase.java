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
package com.db4o.db4ounit.jre11.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SelectiveActivationTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA {
	
	private boolean debug = false;

    protected void configure(Configuration config) {
        enableCascadeOnDelete(config);
        config.activationDepth(1);
    }

    protected void store() {
        Item c = new Item("C", null);
        Item b = new Item("B", c);
        Item a = new Item("A", b);
        db().store(a);
    }

    public void testActivateFullTree() throws Exception {
        reopen();
        print("test activate full tree");
        Assert.areEqual(3, queryItems().size());

        reopen();
        
        Item a = queryItem("A");
        Assert.isNull(a.child.child); // only A and B should be activated.

        reopen(); // start fresh

        addActivationListener();

        a = queryItem("A");        
        Assert.isNotNull(a.child.child); // this time, they should all be activated.  
    }

    public void testActivateEventsAndSort() throws Exception {
    	reopen(); // start fresh

    	print("test activate events and sorting");
    	
        addActivationListener();
        
        ObjectSet queryItems = queryItems();
        
        // check items are sorted the right way
        Item a = (Item)queryItems.next();
        Assert.areEqual(a.id, "A");
        a = (Item)queryItems.next();
        Assert.areEqual(a.id, "B");
        a = (Item)queryItems.next();
        Assert.areEqual(a.id, "C");
    }
    
    private void addActivationListener() {
		// Add listener for activated event
        eventRegistry().activated().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                try {
                    print("event occured");
                    ObjectEventArgs a = (ObjectEventArgs) args;
                    if (a.object() instanceof Item) {
                        Item item = ((Item) a.object());
                        print("Activated item: " + item.id);
                        if (item.child == null) {
                        	print("skipping null child...");
                        	return;
                        }
                        boolean isChildActive = db().isActive(item.child);
                        print("child is active? " + isChildActive);
                        if (!isChildActive) {
                            print("Activating child manually...");
                            db().activate(item.child, 1);
                        }
                    } else {
                        print("got object: " + a.object());
                    }
                } catch (Throwable e1) {
                    // todo: Classcast exceptions weren't breaking out?  silently hidden
                    e1.printStackTrace();
                }
            }
        });
	}

    private void print(String s) {
        if(debug) System.out.println(s);
    }

    private ObjectSet queryItems() {
        return createItemQuery().execute();
    }

    private Query createItemQuery() {
        Query q = db().query();
        q.constrain(Item.class);
        // todo: having this here will not fire the activation events!
        q.sortBy(new ItemQueryComparator());
        return q;
    }

    private void enableCascadeOnDelete(Configuration config) {
        config.objectClass(Item.class).cascadeOnDelete(true);
    }

    private Item queryItem(final String id) {
        Query q = createItemQuery();
        q.descend("id").constrain(id);
        return (Item) q.execute().next();
    }

    public static class ItemQueryComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Item)first).id.compareTo(((Item)second).id);
		}
	}

    public static void main(String[] args) {
    	new SelectiveActivationTestCase().runAll();
    }
}
