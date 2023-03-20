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
package com.db4o.internal.metadata;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public abstract class MarshallingInfoTraverseAspectCommand implements TraverseAspectCommand {
	
    private boolean _cancelled=false;
    
	protected final MarshallingInfo _marshallingInfo;
	
	public MarshallingInfoTraverseAspectCommand(MarshallingInfo marshallingInfo) {
		_marshallingInfo = marshallingInfo;
	}
	
	public final int declaredAspectCount(ClassMetadata classMetadata) {
		int aspectCount= internalDeclaredAspectCount(classMetadata);
		_marshallingInfo.declaredAspectCount(aspectCount);
		return aspectCount;
	}

	protected int internalDeclaredAspectCount(ClassMetadata classMetadata) {
		return classMetadata.readAspectCount(_marshallingInfo.buffer());
	}
    
    public boolean cancelled() {
        return _cancelled;
    }
    
    protected void cancel() {
        _cancelled=true;
    }
    
    public boolean accept(ClassAspect aspect){
        return true;
    }
    
    public void processAspectOnMissingClass(ClassAspect aspect, int currentSlot){
		if(_marshallingInfo.isNull(currentSlot)){
			return;
		}
    	aspect.incrementOffset(_marshallingInfo.buffer(), (HandlerVersionContext) _marshallingInfo);
    }
    
    public void processAspect(ClassAspect aspect,int currentSlot){
		if(accept(aspect)){
			processAspect(aspect, currentSlot, _marshallingInfo.isNull(currentSlot));
	    }
	    _marshallingInfo.beginSlot();
    }
 
    protected abstract void processAspect(ClassAspect aspect,int currentSlot, boolean isNull);
}