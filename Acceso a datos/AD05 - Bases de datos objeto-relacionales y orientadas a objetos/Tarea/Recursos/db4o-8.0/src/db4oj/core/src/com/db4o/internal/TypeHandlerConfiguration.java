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

import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class TypeHandlerConfiguration {
    
    protected final Config4Impl _config;
    
    private TypeHandler4 _listTypeHandler;
    
    private TypeHandler4 _mapTypeHandler;
    
    public abstract void apply();
    
    public TypeHandlerConfiguration(Config4Impl config){
        _config = config;
    }
    
    protected void listTypeHandler(TypeHandler4 listTypeHandler){
    	_listTypeHandler = listTypeHandler;
    }
    
    protected void mapTypeHandler(TypeHandler4 mapTypehandler){
    	_mapTypeHandler = mapTypehandler;
    }
    
    protected void registerCollection(Class clazz){
        registerListTypeHandlerFor(clazz);    
    }
    
    protected void registerMap(Class clazz){
        registerMapTypeHandlerFor(clazz);    
    }
    
    protected void ignoreFieldsOn(Class clazz){
    	registerTypeHandlerFor(clazz, IgnoreFieldsTypeHandler.INSTANCE);
    }
    
    protected void ignoreFieldsOn(String className){
    	registerTypeHandlerFor(className, IgnoreFieldsTypeHandler.INSTANCE);
    }
    
    private void registerListTypeHandlerFor(Class clazz){
        registerTypeHandlerFor(clazz, _listTypeHandler);
    }
    
    private void registerMapTypeHandlerFor(Class clazz){
        registerTypeHandlerFor(clazz, _mapTypeHandler);
    }
    
    protected void registerTypeHandlerFor(Class clazz, TypeHandler4 typeHandler){
        _config.registerTypeHandler(new SingleClassTypeHandlerPredicate(clazz), typeHandler);
    }
    
    protected void registerTypeHandlerFor(String className, TypeHandler4 typeHandler){
        _config.registerTypeHandler(new SingleNamedClassTypeHandlerPredicate(className), typeHandler);
    }


}
