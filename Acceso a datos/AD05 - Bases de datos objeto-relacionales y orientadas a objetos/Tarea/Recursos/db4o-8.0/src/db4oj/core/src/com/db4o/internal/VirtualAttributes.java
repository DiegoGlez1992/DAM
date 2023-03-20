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

import com.db4o.ext.*;
import com.db4o.foundation.*;



/**
 * @exclude
 */
public class VirtualAttributes implements ShallowClone{
    
    public Db4oDatabase i_database;
    
    @Deprecated
    public long i_version;
    
    // FIXME: should be named "uuidLongPart" or even better "creationTime" 
    public long i_uuid;
    
    public Object shallowClone() {
    	VirtualAttributes va=new VirtualAttributes();
    	va.i_database=i_database;
    	va.i_version=i_version;
    	va.i_uuid=i_uuid;
    	return va;
    }
    
    boolean suppliesUUID(){
        return i_database != null && i_uuid != 0;
    }

}
