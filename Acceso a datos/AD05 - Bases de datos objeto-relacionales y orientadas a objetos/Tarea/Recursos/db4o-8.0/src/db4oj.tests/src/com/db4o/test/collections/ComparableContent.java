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
package com.db4o.test.collections;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ComparableContent implements Comparable{
    
    public String _name;
    
    public ComparableContent _child;
    
    public ComparableContent(){
        
    }
    
    public ComparableContent(String name){
        _name = name;
        _child = new ComparableContent();
    }

    public int compareTo(Object o) {
        if(_name == null){
            throw new NullPointerException();
        }
        if(_child == null){
            throw new NullPointerException();
        }
        ComparableContent other = (ComparableContent) o;
        if(other._child == null){
            throw new NullPointerException();
        }
        return other._name.compareTo(_name);
    }
    
    public boolean equals(Object obj) {
        ComparableContent other = (ComparableContent) obj;
        return other._name.equals(_name);
    }
    
}
