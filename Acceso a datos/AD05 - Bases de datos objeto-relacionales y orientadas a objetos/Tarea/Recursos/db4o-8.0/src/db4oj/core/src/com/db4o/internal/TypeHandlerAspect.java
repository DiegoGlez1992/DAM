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
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class TypeHandlerAspect extends ClassAspect {
    
    public final TypeHandler4 _typeHandler;
	private final ClassMetadata _ownerMetadata;
    
    public TypeHandlerAspect(ClassMetadata classMetadata, TypeHandler4 typeHandler) {
    	if(Handlers4.isValueType(typeHandler)){
    		throw new IllegalStateException();
    	}
    	_ownerMetadata = classMetadata;
        _typeHandler = typeHandler;
	}

	public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null || obj.getClass() != getClass()){
            return false;
        }
        TypeHandlerAspect other = (TypeHandlerAspect) obj;
        return _typeHandler.equals(other._typeHandler);
    }
    
    public int hashCode() {
        return _typeHandler.hashCode();
    }

    public String getName() {
        return _typeHandler.getClass().getName();
    }

    public void cascadeActivation(ActivationContext context) {
    	if(! Handlers4.isCascading(_typeHandler)){
    		return;
    	}
    	Handlers4.cascadeActivation(context, _typeHandler);
    }

    public void collectIDs(final CollectIdContext context) {
    	if(! Handlers4.isCascading(_typeHandler)){
    		incrementOffset(context, context);
    		return;
    	}
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
		    	QueryingReadContext queryingReadContext = new QueryingReadContext(context.transaction(), context.handlerVersion(), context.buffer(), 0, context.collector());
		    	((CascadingTypeHandler)_typeHandler).collectIDs(queryingReadContext);
				return null;
			}
    	});
    }

    public void defragAspect(final DefragmentContext context) {
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
				_typeHandler.defragment(context);
				return null;
			}
		
		});
    }

    public int linkLength(HandlerVersionContext context) {
        return Const4.INDIRECTION_LENGTH;
    }

    public void marshall(MarshallingContext context, Object obj) {
    	context.createIndirectionWithinSlot();
    	
    	if (isNotHandlingConcreteType(context)) {
    		_typeHandler.write(context, obj);
    		return;
    	}
    	
    	if (_typeHandler instanceof InstantiatingTypeHandler) {
			InstantiatingTypeHandler instantiating = (InstantiatingTypeHandler) _typeHandler;
			instantiating.writeInstantiation(context, obj);
			instantiating.write(context, obj);
		} else {
			_typeHandler.write(context, obj);
		}
    }

	private boolean isNotHandlingConcreteType(MarshallingContext context) {
		return context.classMetadata() != _ownerMetadata;
	}

	public AspectType aspectType() {
        return AspectType.TYPEHANDLER;
    }

    public void activate(final UnmarshallingContext context) {
    	if(! checkEnabled(context, context)){
    		return;
    	}
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
		        Handlers4.activate(context, _typeHandler);
				return null;
			}
		});
    }

	public void delete(final DeleteContextImpl context, boolean isUpdate) {
    	context.slotFormat().doWithSlotIndirection(context, new Closure4() {
			public Object run() {
				_typeHandler.delete(context);
				return null;
			}
		});
	}

	public void deactivate(ActivationContext context) {
		cascadeActivation(context);
	}

	public boolean canBeDisabled() {
		return true;
	}

}
