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
import com.db4o.query.*;
import com.db4o.reflect.*;


/**
 *
 * Class constraint on queries
 * 
 * @exclude
 */
public class QConClass extends QConObject{
	
	private transient ReflectClass _claxx;
	
	@decaf.Public
    private String _className;
	
	@decaf.Public
    private boolean i_equal;
	
	public QConClass(){
		// C/S
	}
	
	QConClass(Transaction trans, QCon parent, QField field, ReflectClass claxx){
		super(trans, parent, field, null);
		if(claxx != null){
			ObjectContainerBase container = trans.container();
			_classMetadata = container.classMetadataForReflectClass(claxx);
			if(_classMetadata == null){
				// could be an aliased class, try to resolve.
				String className = claxx.getName();
				String aliasRunTimeName = container.config().resolveAliasStoredName(className);
				if(! className.equals(aliasRunTimeName)){
					_classMetadata = container.classMetadataForName(aliasRunTimeName);
				}
			}
			if(claxx.equals(container._handlers.ICLASS_OBJECT)){
				_classMetadata = (ClassMetadata)_classMetadata.typeHandler();
			}
		}
		_claxx = claxx;
	}
	
	QConClass(Transaction trans, ReflectClass claxx){
	    this(trans ,null, null, claxx);
	}
	
	public String getClassName() {
		return _claxx == null ? null : _claxx.getName();
	}
    
    public boolean canBeIndexLeaf(){
        return false;
    }
	
	boolean evaluate(QCandidate a_candidate){
		boolean res = true;
		ReflectClass claxx = a_candidate.classReflector();
		if(claxx == null){
			res = false;
		}else{
			res = i_equal ? _claxx.equals(claxx) : _claxx.isAssignableFrom(claxx);
		}
		return i_evaluator.not(res);
	}
	
	void evaluateSelf() {
		
		// optimization for simple class queries: 
		// No instantiation of objects, if not necessary.
		// Does not handle the special comparison of the
		// Compare interface.
		//
		if(i_candidates.wasLoadedFromClassIndex()){
			if(i_evaluator.isDefault()){
				if(! hasJoins()){
					if(_classMetadata != null  && i_candidates.i_classMetadata != null){
						if(_classMetadata.getHigherHierarchy(i_candidates.i_classMetadata) == _classMetadata){
							return;
						}
					}
				}
			}
		}
		i_candidates.filter(this);
	}
	
	public Constraint equal (){
		synchronized(streamLock()){
			i_equal = true;
			return this;
		}
	}
	
	boolean isNullConstraint() {
		return false;
	}
    
    String logObject() {
        if (Debug4.queries) {
            if(_claxx != null){
                return _claxx.toString();
            }
        } 
        return "";
    }
    
    void marshall() {
        super.marshall();
        if(_claxx!=null) {
        	_className = container().config().resolveAliasRuntimeName(_claxx.getName());
        }
    }
	
	public String toString(){
		String str = "QConClass ";
		if(_claxx != null){
			str += _claxx.getName() + " ";
		}
		return str + super.toString();
	}
	
    void unmarshall(Transaction a_trans) {
        if (i_trans == null) {
            super.unmarshall(a_trans);
            if(_className!=null) {
            	_className = container().config().resolveAliasStoredName(_className);
            	_claxx = a_trans.reflector().forName(_className);
            }
        }
    }
    
    void setEvaluationMode() {
        Iterator4 children = iterateChildren();
        while (children.moveNext()) {
            Object child = children.current();
            if (child instanceof QConObject) {
                ((QConObject) child).setEvaluationMode();
            }
        }
    }
    
    @Override
    public void setProcessedByIndex() {
    	// do nothing, QConClass needs to stay in the evaluation graph.
    }
    
}

