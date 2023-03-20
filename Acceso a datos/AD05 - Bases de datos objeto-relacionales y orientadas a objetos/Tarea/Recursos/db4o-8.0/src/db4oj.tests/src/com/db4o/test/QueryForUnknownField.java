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
package com.db4o.test;

import com.db4o.query.*;


public class QueryForUnknownField {
    
    public String _name;
    
    public QueryForUnknownField(){
    }
    
    public QueryForUnknownField(String name){
        _name = name;
    }
    
    public void storeOne(){
        _name = "name";
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(QueryForUnknownField.class);
        q.descend("_name").constrain("name");
        Test.ensure(q.execute().size() == 1);
        
        q = Test.query();
        q.constrain(QueryForUnknownField.class);
        q.descend("name").constrain("name");
        Test.ensure(q.execute().size() == 0);
        
        
    }

    
    

}
