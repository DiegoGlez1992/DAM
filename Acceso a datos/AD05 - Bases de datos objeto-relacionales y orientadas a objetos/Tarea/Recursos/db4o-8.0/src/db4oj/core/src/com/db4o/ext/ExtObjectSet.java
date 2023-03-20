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
package com.db4o.ext;

import com.db4o.*;

/**
 * extended functionality for the
 * {@link ObjectSet ObjectSet} interface.
 * <br><br>Every db4o {@link ObjectSet ObjectSet}
 * always is an ExtObjectSet so a cast is possible.<br><br>
 * {@link ObjectSet#ext}
 * is a convenient method to perform the cast.<br><br>
 * The ObjectSet functionality is split to two interfaces to allow newcomers to
 * focus on the essential methods.
 */
public interface ExtObjectSet extends ObjectSet {
	
	/**
	 * returns an array of internal IDs that correspond to the contained objects.
	 * <br><br>
	 * @see ExtObjectContainer#getID
	 * @see ExtObjectContainer#getByID
	 */
	public long[] getIDs();
    
    /**
     * returns the item at position [index] in this ObjectSet.
     * <br><br>
     * The object will be activated.
     * @param index the index position in this ObjectSet.  
     * @return the activated object.
     */
    public Object get(int index);
	
}
