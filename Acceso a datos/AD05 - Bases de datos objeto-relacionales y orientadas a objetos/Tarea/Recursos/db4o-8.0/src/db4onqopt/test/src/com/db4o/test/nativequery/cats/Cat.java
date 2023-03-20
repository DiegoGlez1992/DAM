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


public class Cat extends Animal{
    
    public String _firstName;
    
    public String _lastName;
    
    public int _age;
    
    public Cat _father;
    
    public Cat _mother;
    
    public String getFirstName(){
        return _firstName;
    }
    
    public int getAge(){
        return _age;
    }
    
    public String getFullName(){
        return _lastName==null ? null : _firstName + " " + _lastName;
        
    }

    public Cat getFather() {
    	return _father;
    }
    

}
