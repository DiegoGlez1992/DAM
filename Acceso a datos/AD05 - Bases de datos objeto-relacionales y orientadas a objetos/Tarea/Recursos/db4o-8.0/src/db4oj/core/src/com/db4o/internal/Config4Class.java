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

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class Config4Class extends Config4Abstract implements ObjectClass,
    DeepClone {
    
    private final Config4Impl _configImpl;

	private final static KeySpec CALL_CONSTRUCTOR_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
	
	private final static KeySpec CLASS_INDEXED_KEY = new KeySpec(true);
	
	private final static KeySpec EXCEPTIONAL_FIELDS_KEY=new KeySpec(null);

	private final static KeySpec GENERATE_UUIDS_KEY=new KeySpec(TernaryBool.UNSPECIFIED);
    
    /**
     * We are running into cyclic dependancies on reading the PBootRecord
     * object, if we maintain MetaClass information there 
     */
	private final static KeySpec MAINTAIN_METACLASS_KEY=new KeySpec(true);
	
	private final static KeySpec MAXIMUM_ACTIVATION_DEPTH_KEY=new KeySpec(0);

	private final static KeySpec MINIMUM_ACTIVATION_DEPTH_KEY=new KeySpec(0);

	private final static KeySpec PERSIST_STATIC_FIELD_VALUES_KEY=new KeySpec(false);
    
	private final static KeySpec QUERY_ATTRIBUTE_PROVIDER_KEY=new KeySpec(null);
    
	private final static KeySpec STORE_TRANSIENT_FIELDS_KEY=new KeySpec(false);
    
	private final static KeySpec TRANSLATOR_KEY=new KeySpec(null);

	private final static KeySpec TRANSLATOR_NAME_KEY=new KeySpec((String)null);
    
	private final static KeySpec UPDATE_DEPTH_KEY=new KeySpec(Const4.UNSPECIFIED);
    
	private final static KeySpec WRITE_AS_KEY=new KeySpec((String)null);
    
    protected Config4Class(Config4Impl configuration, KeySpecHashtable4 config) {
    	super(config);
        _configImpl = configuration;
    }

	Config4Class(Config4Impl a_configuration, String a_name) {
        _configImpl = a_configuration;
        setName(a_name);
    }

    public int adjustActivationDepth(int depth) {
		TernaryBool cascadeOnActivate = cascadeOnActivate();
		if (cascadeOnActivate.definiteYes() && depth < 2) {
			depth = 2;
		}
		if (cascadeOnActivate.definiteNo() && depth > 1) {
			depth = 1;
		}
		if (config().classActivationDepthConfigurable()) {
			int minimumActivationDepth = minimumActivationDepth();
			if (minimumActivationDepth != 0 && depth < minimumActivationDepth) {
				depth = minimumActivationDepth;
			}
			int maximumActivationDepth = maximumActivationDepth();
			if (maximumActivationDepth != 0 && depth > maximumActivationDepth) {
				depth = maximumActivationDepth;
			}
		}
		return depth;
	}
    
    public void callConstructor(boolean flag){
    	putThreeValued(CALL_CONSTRUCTOR_KEY, flag);
    }

    String className() {
        return getName();
    }
    
    ReflectClass classReflector() {
    	return config().reflector().forName(getName());
    }

    /**
     * @deprecated
     */
    public void compare(ObjectAttribute comparator) {
        _config.put(QUERY_ATTRIBUTE_PROVIDER_KEY,comparator);
    }

    Config4Field configField(String fieldName) {
    	Hashtable4 exceptionalFields=exceptionalFieldsOrNull();
        if (exceptionalFields == null) {
            return null;
        }
        Config4Field config4Field = (Config4Field) exceptionalFields.get(fieldName);
        if (config4Field == null) {
        	return null;
        }
        config4Field.used(true);
		return config4Field;
    }
    
    public Object deepClone(Object param){
    	Config4Impl parentConfig = ((Config4Impl.ConfigDeepCloneContext)param)._cloned;
        return new Config4Class(parentConfig, _config);
    }

	public void enableReplication(boolean setting) {
		throw new UnsupportedOperationException("See documentation");
	}

	public void generateUUIDs(boolean setting) {
    	_config.put(GENERATE_UUIDS_KEY, TernaryBool.forBoolean(setting));
    }

    public void generateVersionNumbers(boolean setting) {
		throw new UnsupportedOperationException("See documentation");
	}
    
    public ObjectTranslator getTranslator() {
    	ObjectTranslator translator = (ObjectTranslator) _config
				.get(TRANSLATOR_KEY);
		if (translator != null) {
			return translator;
		}

		String translatorName = _config.getAsString(TRANSLATOR_NAME_KEY);
		if (translatorName == null) {
			return null;
		}
		try {
			translator = newTranslatorFromReflector(translatorName);
		} catch (RuntimeException t) {
			try {
				translator = newTranslatorFromPlatform(translatorName);
			} catch (Exception e) {
				throw new Db4oException(e);
			} 
		}
		translate(translator);
        return translator;
    }


	private ObjectTranslator newTranslatorFromPlatform(String translatorName) throws InstantiationException, IllegalAccessException{
		return (ObjectTranslator) ReflectPlatform.forName(translatorName).newInstance();
	}
   
	private ObjectTranslator newTranslatorFromReflector(String translatorName) {
		return (ObjectTranslator) config().reflector().forName(
		    translatorName).newInstance();
	}
    
	public void indexed(boolean flag) {
		_config.put(CLASS_INDEXED_KEY, flag);
	}
	
	public boolean indexed() {
		return _config.getAsBoolean(CLASS_INDEXED_KEY);
	}
	
    public void maximumActivationDepth(int depth) {
    	_config.put(MAXIMUM_ACTIVATION_DEPTH_KEY,depth);
    }
    
    int maximumActivationDepth() {
    	return _config.getAsInt(MAXIMUM_ACTIVATION_DEPTH_KEY);
    }

    public void minimumActivationDepth(int depth) {
    	_config.put(MINIMUM_ACTIVATION_DEPTH_KEY,depth);
    }
    
    public int minimumActivationDepth() {
    	return _config.getAsInt(MINIMUM_ACTIVATION_DEPTH_KEY);
    }
    
    public TernaryBool callConstructor() {
        if(_config.get(TRANSLATOR_KEY) != null){
            return TernaryBool.YES;
        }
        return _config.getAsTernaryBool(CALL_CONSTRUCTOR_KEY);
    }

    Hashtable4 exceptionalFieldsOrNull() {
    	return (Hashtable4)_config.get(EXCEPTIONAL_FIELDS_KEY);
    }
    
    private Hashtable4 exceptionalFields() {
    	Hashtable4 exceptionalFieldsCollection=exceptionalFieldsOrNull();
        if (exceptionalFieldsCollection == null) {
            exceptionalFieldsCollection = new Hashtable4(16);
            _config.put(EXCEPTIONAL_FIELDS_KEY,exceptionalFieldsCollection);
        }
        return exceptionalFieldsCollection;
    }
    
    public ObjectField objectField(String fieldName) {
    	Hashtable4 exceptionalFieldsCollection=exceptionalFields();
        Config4Field c4f = (Config4Field) exceptionalFieldsCollection.get(fieldName);
        if (c4f == null) {
            c4f = new Config4Field(this, fieldName);
            exceptionalFieldsCollection.put(fieldName, c4f);
        }
        return c4f;
    }

    public void persistStaticFieldValues() {
        _config.put(PERSIST_STATIC_FIELD_VALUES_KEY, true);
    }

    public void rename(String newName) {
        config().rename(Renames.forClass(getName(), newName));
        setName(newName);
    }

    public void storeTransientFields(boolean flag) {
    	_config.put(STORE_TRANSIENT_FIELDS_KEY, flag);
    }

    public void translate(ObjectTranslator translator) {
        if (translator == null) {
            _config.put(TRANSLATOR_NAME_KEY, null);
        }
        _config.put(TRANSLATOR_KEY, translator);
    }

    void translateOnDemand(String a_translatorName) {
        _config.put(TRANSLATOR_NAME_KEY,a_translatorName);
    }

    public void updateDepth(int depth) {
    	if(depth < 0) {
    		throw new IllegalArgumentException("update depth must not be negative");
    	}
    	_config.put(UPDATE_DEPTH_KEY, depth);
    }

	Config4Impl config() {
		return _configImpl;
	}

	TernaryBool generateUUIDs() {
		return (TernaryBool) _config.get(GENERATE_UUIDS_KEY);
	}

	TernaryBool generateVersionNumbers() {
		return TernaryBool.NO;
	}

	void maintainMetaClass(boolean flag){
		_config.put(MAINTAIN_METACLASS_KEY,flag);
	}

	boolean staticFieldValuesArePersisted() {
		return _config.getAsBoolean(PERSIST_STATIC_FIELD_VALUES_KEY);
	}

	public ObjectAttribute queryAttributeProvider() {
		return (ObjectAttribute)_config.get(QUERY_ATTRIBUTE_PROVIDER_KEY);
	}

	public boolean storeTransientFields() {
		return _config.getAsBoolean(STORE_TRANSIENT_FIELDS_KEY);
	}

	int updateDepth() {
		return _config.getAsInt(UPDATE_DEPTH_KEY);
	}

	String writeAs() {
		return _config.getAsString(WRITE_AS_KEY);
	}



}