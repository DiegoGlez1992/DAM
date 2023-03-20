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

import com.db4o.foundation.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class Null implements Indexable4, PreparedComparison{
    
    public static final Null INSTANCE = new Null();
    
    private Null() {
    }

    public int compareTo(Object a_obj) {
        if(a_obj == null) {
            return 0;
        }
        return -1;
    }
    
    public int linkLength() {
        return 0;
    }

    public Object readIndexEntry(Context context, ByteArrayBuffer a_reader) {
        return null;
    }

    public void writeIndexEntry(Context context, ByteArrayBuffer a_writer, Object a_object) {
        // do nothing
    }

	public void defragIndexEntry(DefragmentContextImpl context) {
        // do nothing
	}

	public PreparedComparison prepareComparison(Context context, Object obj_) {
		return new PreparedComparison() {
			public int compareTo(Object obj) {
				if(obj == null){
					return 0;
				}
				if(obj instanceof Null){
					return 0;
				}
				return -1;
			}
		};
	}
}

