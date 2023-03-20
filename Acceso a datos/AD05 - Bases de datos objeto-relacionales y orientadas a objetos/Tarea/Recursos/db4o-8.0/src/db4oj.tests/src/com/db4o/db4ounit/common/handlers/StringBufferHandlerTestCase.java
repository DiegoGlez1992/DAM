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
package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StringBufferHandlerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new StringBufferHandlerTestCase().runAll();
    }

    public static class Item {
        public StringBuffer buffer;

        public Item(StringBuffer contents) {
            buffer = contents;
        }
    }

    static String _bufferValue = "42"; //$NON-NLS-1$

    protected void configure(Configuration config) throws Exception {
        config.exceptionsOnNotStorable(true);
        config.registerTypeHandler(new SingleClassTypeHandlerPredicate(
                StringBuffer.class), new StringBufferHandler());
        config.diagnostic().addListener(new DiagnosticListener() {

            public void onDiagnostic(Diagnostic d) {
                if (d instanceof DeletionFailed)
                    throw new Db4oException();
            }
        });
    }

    protected void store() throws Exception {
        store(new Item(new StringBuffer(_bufferValue)));
    }

    public void testRetrieve() {
        Item item = retrieveItem();
        Assert.areEqual(_bufferValue, item.buffer.toString());
    }

    public void testTopLevelStore() {
        Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
            public void run() throws Throwable {
                store(new StringBuffer("a")); //$NON-NLS-1$
            }
        });
    }
    
    public void testStringBufferQuery() {
    	final Query query = newItemQuery();
		query.descend("buffer").constrain(new StringBuffer(_bufferValue));
		Assert.areEqual(1, query.execute().size());
    }

    public void testDelete() {
        Item item = retrieveItem();
        Assert.areEqual(_bufferValue, item.buffer.toString());
        db().delete(item);
        Query query = newItemQuery();
        Assert.areEqual(0, query.execute().size());
    }

	private Query newItemQuery() {
	    Query query = newQuery();
        query.constrain(Item.class);
	    return query;
    }

    public void testPrepareComparison() {
        StringBufferHandler handler = new StringBufferHandler();
        PreparedComparison preparedComparison = handler.prepareComparison(trans().context(), _bufferValue);
        Assert.isGreater(preparedComparison.compareTo("43"), 0); //$NON-NLS-1$
    }
    
    public void testStoringStringBufferDirectly(){
    	Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
			public void run() throws Throwable {
		    	StringBuffer stringBuffer = new StringBuffer(_bufferValue);
		    	store(stringBuffer);
			}
		});
    }

    private Item retrieveItem() {
        return (Item) retrieveOnlyInstance(Item.class);
    }

}
