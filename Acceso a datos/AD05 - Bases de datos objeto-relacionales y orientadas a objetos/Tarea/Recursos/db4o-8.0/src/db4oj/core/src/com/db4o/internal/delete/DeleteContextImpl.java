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
package com.db4o.internal.delete;

import com.db4o.diagnostic.DefragmentRecommendation.*;
import com.db4o.internal.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class DeleteContextImpl extends ObjectHeaderContext implements DeleteContext, ObjectIdContext {
    
    private final ReflectClass _fieldClass;
    
    private final Config4Field _fieldConfig;
    
	public DeleteContextImpl(StatefulBuffer buffer, ObjectHeader objectHeader, ReflectClass fieldClass, Config4Field fieldConfig){
		super(buffer.transaction(), buffer, objectHeader);
		_fieldClass = fieldClass;
		_fieldConfig = fieldConfig;
	}
	
	public DeleteContextImpl(DeleteContextImpl parentContext, ReflectClass fieldClass, Config4Field fieldConfig){
		this(parentContext.statefulBuffer(), parentContext._objectHeader, fieldClass, fieldConfig);
	}

	public void cascadeDeleteDepth(int depth) {
	    statefulBuffer().setCascadeDeletes(depth);
	}

	private StatefulBuffer statefulBuffer() {
		return ((StatefulBuffer)buffer());
	}

	public int cascadeDeleteDepth() {
	    return statefulBuffer().cascadeDeletes();
	}
	
    public boolean cascadeDelete() {
        return cascadeDeleteDepth() > 0;
    }

	public void defragmentRecommended() {
        DiagnosticProcessor dp = container()._handlers.diagnosticProcessor();
        if(dp.enabled()){
            dp.defragmentRecommended(DefragmentRecommendationReason.DELETE_EMBEDED);
        }
	}

	public Slot readSlot() {
		return new Slot(buffer().readInt(), buffer().readInt());
	}

	public void delete(TypeHandler4 handler){
        final TypeHandler4 correctHandlerVersion = HandlerRegistry.correctHandlerVersion(this, handler);
	    int preservedCascadeDepth = cascadeDeleteDepth();
	    cascadeDeleteDepth(adjustedDepth());
        if(Handlers4.handleAsObject(correctHandlerVersion)){
            deleteObject();
        }else{
            correctHandlerVersion.delete(DeleteContextImpl.this);    
        }
        cascadeDeleteDepth(preservedCascadeDepth);
	}

    public void deleteObject() {
        int id = buffer().readInt();
        if(cascadeDelete()){
            container().deleteByID(transaction(), id, cascadeDeleteDepth());
        }
    }
	
	private int adjustedDepth(){
        if(Platform4.isStruct(_fieldClass)){
            return 1;
        }
	    if(_fieldConfig == null){
	        return cascadeDeleteDepth();
	    }
	    if(_fieldConfig.cascadeOnDelete().definiteYes()){
	        return 1;
	    }
	    if(_fieldConfig.cascadeOnDelete().definiteNo()){
	        return 0;
	    }
	    return cascadeDeleteDepth();
	}

	public int objectId() {
		return statefulBuffer().getID();
	}

}
