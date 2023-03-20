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

import com.db4o.internal.marshall.*;


/**
 * @exclude
 */
public class ObjectID {
    
    public final int _id;
    
    public static final ObjectID IS_NULL = new ObjectID(-1) {
    	public String toString() {
    		return "ObjectID.IS_NULL";
    	};
    };
    
    public static final ObjectID NOT_POSSIBLE = new ObjectID(-2){
    	public String toString() {
    		return "ObjectID.NOT_POSSIBLE";
    	};
    };

    public static final ObjectID IGNORE = new ObjectID(-3){
    	public String toString() {
    		return "ObjectID.IGNORE";
    	};
    };
    
    public ObjectID(int id){
        _id = id;
    }
    
    public boolean isValid(){
        return _id > 0;
    }

    public static ObjectID read(InternalReadContext context) {
        int id = context.readInt();
        return id == 0 ? ObjectID.IS_NULL : new ObjectID(id);
    }
    
    @Override
    public String toString() {
    	return "ObjectID(" + _id + ")";
    }

}
