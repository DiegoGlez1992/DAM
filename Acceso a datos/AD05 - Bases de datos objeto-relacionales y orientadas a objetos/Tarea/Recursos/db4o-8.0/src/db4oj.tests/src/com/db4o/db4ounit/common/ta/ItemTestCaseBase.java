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
package com.db4o.db4ounit.common.ta;

import com.db4o.ext.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public abstract class ItemTestCaseBase
	extends TransparentActivationTestCaseBase
	implements OptOutDefragSolo {
    
	private Class _clazz;
	protected long id;
	protected Db4oUUID uuid;
	
    protected void store() throws Exception {
        Object value = createItem();
        _clazz = value.getClass();
        store(value);
        id = db().ext().getID(value);
        uuid = db().ext().getObjectInfo(value).getUUID();
    }
    
    public void testQuery() throws Exception {
        Object item = retrieveOnlyInstance();
        assertRetrievedItem(item);
        assertItemValue(item);
    }
    
    public void testDeactivate() throws Exception {	
    	Object item = retrieveOnlyInstance();
    	db().deactivate(item, 1);
    	assertNullItem(item);  
    	
    	db().activate(item, 42);
    	db().deactivate(item, 1);
    	assertNullItem(item);
	}

	protected Object retrieveOnlyInstance() {
		return retrieveOnlyInstance(_clazz);
	}
    
    protected void assertNullItem(Object obj) throws Exception {
    	ReflectClass claxx = reflector().forObject(obj);
        ReflectField[] fields = claxx.getDeclaredFields();
    	for(int i = 0; i < fields.length; ++i) {
    		ReflectField field = fields[i];
    		if(field.isStatic() || field.isTransient()) {
    			continue;
    		}
    		ReflectClass type = field.getFieldType();
    		if(container().classMetadataForReflectClass(type).isValueType()) {
    			continue;
    		}
    		Object value = field.get(obj);
    		Assert.isNull(value);
    			
    	}
    }

	protected abstract void assertItemValue(Object obj) throws Exception;
     
    protected abstract Object createItem() throws Exception;
    
    protected abstract void assertRetrievedItem(Object obj) throws Exception;
}
