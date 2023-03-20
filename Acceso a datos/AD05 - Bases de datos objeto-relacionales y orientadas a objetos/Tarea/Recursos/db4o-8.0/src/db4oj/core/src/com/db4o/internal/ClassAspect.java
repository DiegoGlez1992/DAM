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

import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class ClassAspect {
    
    // used for identification when sending in C/S mode 
	protected int              _handle;
    
    private int _disabledFromAspectCountVersion = AspectVersionContextImpl.ALWAYS_ENABLED.declaredAspectCount();
    
    public abstract AspectType aspectType();
    
    public abstract String getName();
    
    public abstract void cascadeActivation(ActivationContext context);
    
    public abstract int linkLength(HandlerVersionContext context);
    
    public final void incrementOffset(ReadBuffer buffer, HandlerVersionContext context) {
        buffer.seek(buffer.offset() + linkLength(context));
    }

    public abstract void defragAspect(DefragmentContext context);

    public abstract void marshall(MarshallingContext context, Object child);

    public abstract void collectIDs(CollectIdContext context);
    
    public void setHandle(int handle) {
        _handle = handle;
    }

    public abstract void activate(UnmarshallingContext context);

	public abstract void delete(DeleteContextImpl context, boolean isUpdate);
	
	public abstract boolean canBeDisabled();
	
    protected boolean checkEnabled(AspectVersionContext context, HandlerVersionContext versionContext){
    	if(! isEnabledOn(context)){
    		incrementOffset((ReadBuffer)context, versionContext);
    		return false;
    	}
    	return true;
    }

	
	public void disableFromAspectCountVersion(int aspectCount) {
		if(! canBeDisabled()){
			return;
		}
		if(aspectCount < _disabledFromAspectCountVersion){
			_disabledFromAspectCountVersion = aspectCount;
		}
	}
	
	public final boolean isEnabledOn(AspectVersionContext context){
		return _disabledFromAspectCountVersion  > context.declaredAspectCount();	
	}

	public abstract void deactivate(ActivationContext context);

}
