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
import com.db4o.internal.btree.*;
import com.db4o.internal.convert.*;
import com.db4o.internal.convert.ConversionStage.*;


/**
 * @exclude
 */
public class ClassIndexesToBTrees_5_5 extends Conversion {
    
    public static final int VERSION = 5;

    public void convert(LocalObjectContainer container, int classIndexId, BTree bTree){
        Transaction trans = container.systemTransaction();
        ByteArrayBuffer reader = container.readBufferById(trans, classIndexId);
        if(reader == null){
            return;
        }
        int entries = reader.readInt();
        for (int i = 0; i < entries; i++) {
            bTree.add(trans, new Integer(reader.readInt()));
        }
    }

	public void convert(SystemUpStage stage) {
        
        // calling #storedClasses forces reading all classes
        // That's good enough to load them all and to call the
        // above convert method.
        
        stage.file().storedClasses();
	}
}
