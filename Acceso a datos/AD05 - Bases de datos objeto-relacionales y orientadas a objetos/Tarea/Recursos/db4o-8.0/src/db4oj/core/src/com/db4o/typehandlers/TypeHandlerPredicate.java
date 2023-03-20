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
package com.db4o.typehandlers;

import com.db4o.reflect.*;


/**
 * Predicate to be able to select if a specific TypeHandler is
 * applicable for a specific Type.
 */
public interface TypeHandlerPredicate {
    
    /**
     * return true if a TypeHandler is to be used for a specific
     * Type 
     * @param classReflector the Type passed by db4o that is to
     * be tested by this predicate.
     * @return true if the TypeHandler is to be used for a specific
     * Type.
     */
    public boolean match(ReflectClass classReflector);

}
