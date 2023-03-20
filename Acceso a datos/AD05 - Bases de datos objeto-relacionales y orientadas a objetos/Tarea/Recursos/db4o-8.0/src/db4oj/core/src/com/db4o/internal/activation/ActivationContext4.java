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
package com.db4o.internal.activation;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class ActivationContext4 implements ActivationContext {
    
    private final Transaction _transaction;
    
    private final Object _targetObject;
    
    private final ActivationDepth _depth;
    
    public ActivationContext4(Transaction transaction, Object obj, ActivationDepth depth){
    	if (null == obj) {
			throw new ArgumentNullException();
		}
        _transaction = transaction;
        _targetObject = obj;
        _depth = depth;
    }

    public void cascadeActivationToTarget() {
    	ActivationContext context = classMetadata().descendOnCascadingActivation()
    		? descend()
    		: this; 
        cascadeActivation(context);
    }
    
    public void cascadeActivationToChild(Object obj) {
        if(obj == null){
            return;
        }
        final ActivationContext cascadingContext = forObject(obj);
		final ClassMetadata classMetadata = cascadingContext.classMetadata();
        if(classMetadata == null || !classMetadata.hasIdentity()){
            return;
        }
        cascadeActivation(cascadingContext.descend());
    }
    
    private void cascadeActivation(ActivationContext context) {
    	final ActivationDepth depth = context.depth();
        if (! depth.requiresActivation()) {
            return;
        }
        if (depth.mode().isDeactivate()) {
            container().stillToDeactivate(_transaction, context.targetObject(), depth, false);
        } else {
            // FIXME: [TA] do we really need to check for isValueType here?
        	final ClassMetadata classMetadata = context.classMetadata();
            if(classMetadata.isStruct()){
                classMetadata.cascadeActivation(context);
            }else{
                container().stillToActivate(context);
            }
        }
    }

    public ObjectContainerBase container(){
        return _transaction.container();
    }

    public Object targetObject() {
        return _targetObject;
    }

	public ClassMetadata classMetadata() {
		return container().classMetadataForObject(_targetObject);
	}

	public ActivationDepth depth() {
		return _depth;
	}

	public ObjectContainer objectContainer() {
		return container();
	}

	public Transaction transaction() {
		return _transaction;
	}

	public ActivationContext forObject(Object newTargetObject) {
		return new ActivationContext4(transaction(), newTargetObject, depth());
	}

	public ActivationContext descend() {
		return new ActivationContext4(transaction(), targetObject(), depth().descend(classMetadata()));
	}

}
