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
package com.db4o.reflect;


/**
 * @exclude
 */
public class ArrayInfo {
    
    private int _elementCount;
    
    private boolean _primitive;
    
    private boolean _nullable;
    
    private ReflectClass _reflectClass;
    
    public int elementCount() {
        return _elementCount;
    }
    
    public void elementCount(int count) {
        _elementCount = count;
    }
    
    public boolean primitive() {
        return _primitive;
    }
    
    public void primitive(boolean flag) {
        _primitive = flag;
    }
    
    public boolean nullable() {
        return _nullable;
    }
    
    public void nullable(boolean flag) {
        _nullable = flag;
    }
    
    public ReflectClass reflectClass() {
        return _reflectClass;
    }
    
    public void reflectClass(ReflectClass claxx) {
        _reflectClass = claxx;
    }
    
}
