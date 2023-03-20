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
package com.db4o.internal.convert.conversions;

import com.db4o.internal.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.convert.ConversionStage.*;


/**
 * @exclude
 */
public class FieldIndexesToBTrees_5_7 extends Conversion{
    
    public static final int VERSION = 6;

	public void convert(SystemUpStage stage) {
        stage.file().classCollection().writeAllClasses();        
		rebuildUUIDIndex(stage.file());
    	freeOldUUIDMetaIndex(stage.file());
    }
	
    private void rebuildUUIDIndex(LocalObjectContainer file) {
		final UUIDFieldMetadata uuid = file.uUIDIndex();
		final ClassMetadataIterator i = file.classCollection().iterator();
		while (i.moveNext()) {
			final ClassMetadata clazz = i.currentClass();
			if (clazz.generateUUIDs()) {
				uuid.rebuildIndexForClass(file, clazz);
			}
		}
	}

    /** @param file */
    private void freeOldUUIDMetaIndex(LocalObjectContainer file) {
		// updating removed here to allow removing MetaIndex class
	}
}
