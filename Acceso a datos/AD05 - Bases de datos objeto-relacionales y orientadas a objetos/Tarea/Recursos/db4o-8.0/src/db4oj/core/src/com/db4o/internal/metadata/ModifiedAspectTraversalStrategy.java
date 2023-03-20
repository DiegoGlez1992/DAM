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

import java.util.*;

import com.db4o.internal.*;
import com.db4o.internal.metadata.HierarchyAnalyzer.*;

/**
 * @exclude
 */
public class ModifiedAspectTraversalStrategy implements AspectTraversalStrategy {
	
	private final List<Diff> _classDiffs;

	public ModifiedAspectTraversalStrategy(ClassMetadata classMetadata,
			List<Diff> ancestors) {
		_classDiffs = new ArrayList<Diff>();
		_classDiffs.add(new HierarchyAnalyzer.Same(classMetadata));
		_classDiffs.addAll(ancestors);
	}

	public void traverseAllAspects(TraverseAspectCommand command) {
		int currentSlot = 0;
	    for(HierarchyAnalyzer.Diff diff : _classDiffs){
			ClassMetadata classMetadata = diff.classMetadata();
			if(diff.isRemoved()){
		        currentSlot = skipAspectsOf(classMetadata, command,
						currentSlot);
				continue;
			}
	        currentSlot = traverseAspectsOf(classMetadata, command, currentSlot);
	        if(command.cancelled()){
	            return;
	        }
	    }
	}
	
	static interface TraverseAspectCommandProcessor {
		void process(TraverseAspectCommand command, ClassAspect currentAspect, int currentSlot);
	}

	private int traverseAspectsOf(final ClassMetadata classMetadata,
			TraverseAspectCommand command, int currentSlot) {
		return processAspectsOf(classMetadata, command, currentSlot, new TraverseAspectCommandProcessor() {
			public void process(TraverseAspectCommand command, ClassAspect currentAspect, int currentSlot) {
				command.processAspect(currentAspect,currentSlot);
		
			}
		});
	}

	private int processAspectsOf(final ClassMetadata classMetadata,
			TraverseAspectCommand command, int currentSlot,
			TraverseAspectCommandProcessor processor) {
		int aspectCount=command.declaredAspectCount(classMetadata);
		for (int i = 0; i < aspectCount && !command.cancelled(); i++) {
		    processor.process(command, classMetadata._aspects[i], currentSlot);
		    currentSlot++;
		}
		return currentSlot;
	}
	
	private int skipAspectsOf(ClassMetadata classMetadata,
			TraverseAspectCommand command, int currentSlot) {
		return processAspectsOf(classMetadata, command, currentSlot, new TraverseAspectCommandProcessor() {
			public void process(
					TraverseAspectCommand command,
					ClassAspect currentAspect,
					int currentSlot) {
				command.processAspectOnMissingClass(currentAspect, currentSlot);
			}
		});
	}


}
