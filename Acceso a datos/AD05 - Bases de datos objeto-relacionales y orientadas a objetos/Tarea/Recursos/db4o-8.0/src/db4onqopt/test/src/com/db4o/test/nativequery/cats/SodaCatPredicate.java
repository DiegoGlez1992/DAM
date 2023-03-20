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
package com.db4o.test.nativequery.cats;

import com.db4o.*;
import com.db4o.query.*;


public abstract class SodaCatPredicate extends Predicate {
    
    private int _count;
    
    public void sodaQuery(ObjectContainer oc){
        Query q = oc.query();
        q.constrain(Cat.class);
        constrain(q);
        q.execute();
    }
    
    public abstract void constrain(Query q);
    
    public void setCount(int count){
        _count = count;
    }
    
    public int lower() {
        return _count / 2 - 1;
    }
    
    public int upper() {
        return _count / 2 + 1;
    }

    

}
