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
package com.db4o.reflect.generic;

import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericVirtualField extends GenericField{

    public GenericVirtualField(String name) {
        super(name, null, false);
    }
    
    public Object deepClone(Object obj) {
        return new GenericVirtualField(getName());
    }

    public Object get(Object onObject) {
        return null;
    }

    public ReflectClass getFieldType() {
        return null;
    }

    public boolean isPublic() {
        return false;
    }

    public boolean isStatic() {
        return true;
    }

    public boolean isTransient() {
        return true;
    }

    public void set(Object onObject, Object value) {
        // do nothing
    }
}
