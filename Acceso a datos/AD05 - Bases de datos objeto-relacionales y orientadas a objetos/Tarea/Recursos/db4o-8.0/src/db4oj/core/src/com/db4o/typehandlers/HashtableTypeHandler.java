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
package com.db4o.typehandlers;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.internal.*;

/**
 * Typehandler for java.util.Hashtable
 * @sharpen.ignore
 */
public class HashtableTypeHandler implements ReferenceTypeHandler , CascadingTypeHandler, VariableLengthTypeHandler{
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

	public void write(WriteContext context, Object obj) {
        Hashtable hashTable = (Hashtable)obj;
        KeyValueHandlerPair handlers = detectKeyValueTypeHandlers(container(context), hashTable);
        writeClassMetadataIds(context, handlers);
        writeElementCount(context, hashTable);
        writeElements(context, hashTable, handlers);
    }
    
    public void activate(ReferenceActivationContext context) {
    	UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context; 
    	Hashtable hashtable = (Hashtable) unmarshallingContext.persistentObject();
    	hashtable.clear();
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(unmarshallingContext, unmarshallingContext);
        int elementCount = unmarshallingContext.readInt();
        for (int i = 0; i < elementCount; i++) {
            Object key = unmarshallingContext.readFullyActivatedObjectForKeys(handlers._keyHandler);
            Object value = unmarshallingContext.readObject(handlers._valueHandler);
            hashtable.put(key, value);
        }
    }
    
    private void writeElementCount(WriteContext context, Hashtable hashtable) {
        context.writeInt(hashtable.size());
    }

    private void writeElements(WriteContext context, Hashtable hashtable, KeyValueHandlerPair handlers) {
        final Enumeration elements = hashtable.keys();
        while (elements.hasMoreElements()) {
            Object key = elements.nextElement();
            context.writeObject(handlers._keyHandler, key);
            context.writeObject(handlers._valueHandler, hashtable.get(key));
        }
    }

    private ObjectContainerBase container(Context context) {
        return ((InternalObjectContainer)context.objectContainer()).container();
    }
    
    public void delete(final DeleteContext context) throws Db4oIOException {
        if (! context.cascadeDelete()) {
            return;
        }
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt();
        for (int i = elementCount; i > 0; i--) {
            handlers._keyHandler.delete(context);
            handlers._valueHandler.delete(context);
        }
    }

    public void defragment(DefragmentContext context) {
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt(); 
        for (int i = elementCount; i > 0; i--) {
            context.defragment(handlers._keyHandler);
            context.defragment(handlers._valueHandler);
        }
    }
    
    public final void cascadeActivation(ActivationContext context) {
    	Hashtable hashtable = (Hashtable) context.targetObject();
        Enumeration keys = (hashtable).keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            context.cascadeActivationToChild(key);
            context.cascadeActivationToChild(hashtable.get(key));
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        return this;
    }
    
    public void collectIDs(final QueryingReadContext context) {
    	KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt();
        for (int i = 0; i < elementCount; i++) {
            context.readId(handlers._keyHandler);
            context.skipId(handlers._valueHandler);
        }
    }

	private void writeClassMetadataIds(WriteContext context, KeyValueHandlerPair handlers) {
		context.writeInt(0);
		context.writeInt(0);
	}

	private KeyValueHandlerPair readKeyValueTypeHandlers(ReadBuffer buffer, Context context) {
		buffer.readInt();
		buffer.readInt();
		TypeHandler4 untypedHandler = (TypeHandler4) container(context).handlers().openTypeHandler();
		return new KeyValueHandlerPair(untypedHandler, untypedHandler);
	}

	private KeyValueHandlerPair detectKeyValueTypeHandlers(InternalObjectContainer container, Hashtable hashTable) {
		TypeHandler4 untypedHandler = (TypeHandler4) container.handlers().openTypeHandler();
		return new KeyValueHandlerPair(untypedHandler, untypedHandler);
	}

}
