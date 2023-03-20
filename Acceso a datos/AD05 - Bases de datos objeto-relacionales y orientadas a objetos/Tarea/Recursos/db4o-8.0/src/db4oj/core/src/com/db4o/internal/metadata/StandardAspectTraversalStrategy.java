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

/**
 * @exclude
 */
public class StandardAspectTraversalStrategy implements AspectTraversalStrategy {
	
	private final ClassMetadata _classMetadata;

	public StandardAspectTraversalStrategy(ClassMetadata classMetadata) {
		_classMetadata = classMetadata;
	}

	public void traverseAllAspects(TraverseAspectCommand command) {
		ClassMetadata classMetadata = _classMetadata;
		int currentSlot = 0;
	    while(classMetadata != null){
	        int aspectCount=command.declaredAspectCount(classMetadata);
			for (int i = 0; i < aspectCount && !command.cancelled(); i++) {
			    command.processAspect(classMetadata._aspects[i],currentSlot);
			    currentSlot++;
			}
	        if(command.cancelled()){
	            return;
	        }
	        classMetadata = classMetadata._ancestor;
	    }
	}
	
	
	
	
}