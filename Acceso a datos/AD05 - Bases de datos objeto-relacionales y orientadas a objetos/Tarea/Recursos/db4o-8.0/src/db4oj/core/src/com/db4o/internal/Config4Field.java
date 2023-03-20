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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;


public class Config4Field extends Config4Abstract implements ObjectField, DeepClone {
    
    private final Config4Class _configClass;
    
    private boolean _used;
    
	private final static KeySpec INDEXED_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
    
	protected Config4Field(Config4Class a_class, KeySpecHashtable4 config) {
		super(config);
        _configClass = a_class;
	}
	
    Config4Field(Config4Class a_class, String a_name) {
        _configClass = a_class;
        setName(a_name);
    }

    private Config4Class classConfig() {
    	return _configClass;
    }
    
    String className() {
        return classConfig().getName();
    }

    public Object deepClone(Object param) {
        return new Config4Field((Config4Class)param, _config);
    }

    public void rename(String newName) {
        classConfig().config().rename(Renames.forField(className(), getName(), newName));
        setName(newName);
    }

    public void indexed(boolean flag) {
    	putThreeValued(INDEXED_KEY, flag);
    }

    public void initOnUp(Transaction systemTrans, FieldMetadata fieldMetadata) {
    	
        ObjectContainerBase anyStream = systemTrans.container();
        if (!anyStream.maintainsIndices()) {
        	return;
        }
        if(Debug4.indexAllFields){
            indexed(true);
        }
        if (! fieldMetadata.supportsIndex()) {
            indexed(false);
        }
        
        TernaryBool indexedFlag=_config.getAsTernaryBool(INDEXED_KEY);        
        if (indexedFlag.definiteNo()) {
            fieldMetadata.dropIndex((LocalTransaction)systemTrans);
            return;
        }
        
        if (useExistingIndex(systemTrans, fieldMetadata)) {
        	return;
        }
        
        if (!indexedFlag.definiteYes()) {
        	return;
        }
        
        fieldMetadata.createIndex();
    }

	private boolean useExistingIndex(Transaction systemTrans, FieldMetadata fieldMetadata) {
	    return fieldMetadata.getIndex(systemTrans) != null;
	}

	public void used(boolean flag) {
		_used = flag;
	}
	
	public boolean used(){
		return _used;
	}
	
	
	

}
