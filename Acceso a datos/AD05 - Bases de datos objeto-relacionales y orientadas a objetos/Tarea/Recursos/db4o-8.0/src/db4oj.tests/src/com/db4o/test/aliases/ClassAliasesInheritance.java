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
package com.db4o.test.aliases;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.test.*;



/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ClassAliasesInheritance {
    
    public void test(){
        
        Test.store(new Parent1(new Child1()));
        
        ObjectContainer container = Test.reOpen();
        container.ext().configure().addAlias(
            new TypeAlias("com.db4o.test.aliases.Parent1",
                        "com.db4o.test.aliases.Parent2"));
        container.ext().configure().addAlias(
            new TypeAlias("com.db4o.test.aliases.Child1",
                        "com.db4o.test.aliases.Child2"));
        
        ObjectSet os = container.query(Parent2.class);
        
        Test.ensure(os.size() > 0);
        
        Parent2 p2 = (Parent2)os.next();
        
        Test.ensure(p2.child != null);
    }

}
