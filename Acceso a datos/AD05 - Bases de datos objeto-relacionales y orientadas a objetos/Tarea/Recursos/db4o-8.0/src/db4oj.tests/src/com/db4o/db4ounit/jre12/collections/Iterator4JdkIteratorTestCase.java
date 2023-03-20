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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class Iterator4JdkIteratorTestCase implements TestCase{

    public static void main(String[] arguments) {
        new ConsoleTestRunner(Iterator4JdkIteratorTestCase.class).run();
    }
    
    public void test(){
        Collection4 collection = new Collection4();
        Object[] content = new String[]{"one", "two", "three"};
        for (int i = 0; i < content.length; i++) {
            collection.add(content[i]);    
        }
        Iterator iterator = new Iterator4JdkIterator(collection.iterator());
        IteratorAssert.areEqual(content, iterator); 
    }

}
