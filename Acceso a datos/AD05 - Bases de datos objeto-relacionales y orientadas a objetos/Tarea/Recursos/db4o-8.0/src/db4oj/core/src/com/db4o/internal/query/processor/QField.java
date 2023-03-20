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
package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public class QField implements Visitor4, Unversioned{
	
	transient Transaction i_trans;
	
	@decaf.Public
    private String i_name;
	
	transient FieldMetadata _fieldMetadata;
	
	@decaf.Public
    private int i_classMetadataID;
	
	@decaf.Public
    private int _fieldHandle;
	
	public QField(){
		// C/S only	
	}
	
	public QField(Transaction a_trans, String name, FieldMetadata fieldMetadata, int classMetadataID, int a_index){
		i_trans = a_trans;
		i_name = name;
		_fieldMetadata = fieldMetadata;
		i_classMetadataID = classMetadataID;
		_fieldHandle = a_index;
		if(_fieldMetadata != null){
		    if(! _fieldMetadata.alive()){
		        _fieldMetadata = null;
		    }
		}
	}
	
	public String name() {
	    return i_name;
    }

	Object coerce(Object a_object){
	    ReflectClass claxx = null;
	    if(a_object != null){
	        if(a_object instanceof ReflectClass){
	            claxx = (ReflectClass)a_object;
	        }else{
	            claxx = i_trans.reflector().forObject(a_object);
	        }
	    }else{
	        
	        // TODO: Review this line for NullableArrayHandling 
	        
			if(Deploy.csharp){
				return a_object;
			}
			
	    }
        if(_fieldMetadata == null){
            return a_object;
        }
        return _fieldMetadata.coerce(claxx, a_object);
	}
    
	
	ClassMetadata getFieldType(){
		if(_fieldMetadata != null){
			return _fieldMetadata.fieldType();
		}
		return null;
	}
	
	public FieldMetadata getFieldMetadata() {
		return _fieldMetadata;
	}
	
	boolean isArray(){
		return _fieldMetadata != null && Handlers4.handlesArray(_fieldMetadata.getHandler());
	}
	
	boolean isClass(){
		return _fieldMetadata == null ||  Handlers4.handlesClass(_fieldMetadata.getHandler());
	}
	
	boolean isQueryLeaf(){
		return _fieldMetadata != null &&  Handlers4.isQueryLeaf(_fieldMetadata.getHandler());
	}
	
	PreparedComparison prepareComparison(Context context, Object obj){
		if(_fieldMetadata != null){
			return _fieldMetadata.prepareComparison(context, obj);
		}
		if(obj == null){
			return Null.INSTANCE;
		}
		ClassMetadata yc = i_trans.container().produceClassMetadata(i_trans.reflector().forObject(obj));
		FieldMetadata yf = yc.fieldMetadataForName(name());
		if(yf != null){
			return yf.prepareComparison(context, obj);
		}
		return null;
	}

	
	void unmarshall(Transaction a_trans){
		if(i_classMetadataID != 0){
			ClassMetadata yc = a_trans.container().classMetadataForID(i_classMetadataID);
			_fieldMetadata = (FieldMetadata) yc._aspects[_fieldHandle];
		}
	}
	
	public void visit(Object obj) {
		((QCandidate) obj).useField(this);
	}
	
	public String toString() {
		if(_fieldMetadata != null){
			return "QField " + _fieldMetadata.toString();
		}
		return super.toString();
	}
}

