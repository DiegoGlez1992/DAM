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
 * Typehandler for classes that implement java.util.Map.
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class MapTypeHandler implements ReferenceTypeHandler, CascadingTypeHandler, VariableLengthTypeHandler{
    
    public PreparedComparison prepareComparison(Context context, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

	public void write(WriteContext context, Object obj) {
        Map map = (Map)obj;
        KeyValueHandlerPair handlers = detectKeyValueTypeHandlers(container(context), map);
        writeClassMetadataIds(context, handlers);
        writeElementCount(context, map);
        writeElements(context, map, handlers);
    }
    
    public void activate(ReferenceActivationContext context) {
    	UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context; 
        Map map = (Map)unmarshallingContext.persistentObject();
        map.clear();
        KeyValueHandlerPair handlers = readKeyValueTypeHandlers(context, context);
        int elementCount = context.readInt();
        for (int i = 0; i < elementCount; i++) {
            Object key = unmarshallingContext.readFullyActivatedObjectForKeys(handlers._keyHandler);
            if(key == null && !unmarshallingContext.lastReferenceReadWasReallyNull()) {
            	continue;
            }
            Object value = context.readObject(handlers._valueHandler);
            map.put(key, value);
        }
    }
    
    private void writeElementCount(WriteContext context, Map map) {
        context.writeInt(map.size());
    }

    private void writeElements(WriteContext context, Map map, KeyValueHandlerPair handlers) {
        final Iterator elements = map.entrySet().iterator();
        while (elements.hasNext()) {
            Map.Entry entry = (Map.Entry) elements.next();
            context.writeObject(handlers._keyHandler, entry.getKey());
            context.writeObject(handlers._valueHandler, entry.getValue());
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
        Map map = (Map) context.targetObject();
        Iterator keys = (map).keySet().iterator();
        while (keys.hasNext()) {
            final Object key = keys.next();
            context.cascadeActivationToChild(key);
            context.cascadeActivationToChild(map.get(key));
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

	private KeyValueHandlerPair detectKeyValueTypeHandlers(InternalObjectContainer container, Map map) {
		TypeHandler4 untypedHandler = (TypeHandler4) container.handlers().openTypeHandler();
		return new KeyValueHandlerPair(untypedHandler, untypedHandler);
	}
}
