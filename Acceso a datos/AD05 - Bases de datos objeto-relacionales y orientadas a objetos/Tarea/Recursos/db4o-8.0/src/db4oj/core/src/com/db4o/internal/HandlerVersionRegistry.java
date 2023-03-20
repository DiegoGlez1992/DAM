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
package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class HandlerVersionRegistry {
    
    private final HandlerRegistry _registry;
    
    private final Hashtable4 _versions = new Hashtable4();
    
    public HandlerVersionRegistry(HandlerRegistry registry){
        _registry = registry;
    }

    public void put(TypeHandler4 handler, int version, TypeHandler4 replacement) {
        _versions.put(new HandlerVersionKey(handler, version), replacement);
    }

    public TypeHandler4 correctHandlerVersion(final TypeHandler4 originalHandler, final int version) {
        if(version >= HandlerRegistry.HANDLER_VERSION){
            return originalHandler;
        }
        if(originalHandler == null){
        	return null;  // HandlerVersionKey with null key will throw NPE.
        }
        TypeHandler4 replacement = (TypeHandler4) _versions.get(new HandlerVersionKey(genericTemplate(originalHandler), version));
        if(replacement == null){
            return correctHandlerVersion(originalHandler, version + 1);    
        }
        if(replacement instanceof VersionedTypeHandler){
            return (TypeHandler4) ((VersionedTypeHandler)replacement).deepClone(new TypeHandlerCloneContext(_registry, originalHandler,  version));
        };
        return replacement;
    }

    private TypeHandler4 genericTemplate(final TypeHandler4 handler) {
        if (handler instanceof VersionedTypeHandler){
            return ((VersionedTypeHandler)handler).unversionedTemplate(); 
        }
        return handler;
    }
    
    private class HandlerVersionKey {
        
        private final TypeHandler4 _handler;
        
        private final int _version;
        
        public HandlerVersionKey(TypeHandler4 handler, int version){
            _handler = handler;
            _version = version;
        }

        public int hashCode() {
            return _handler.hashCode() + _version * 4271;
        }

        public boolean equals(Object obj) {
            HandlerVersionKey other = (HandlerVersionKey) obj;
            return _handler.equals(other._handler) && _version == other._version;
        }

    }
    
}
