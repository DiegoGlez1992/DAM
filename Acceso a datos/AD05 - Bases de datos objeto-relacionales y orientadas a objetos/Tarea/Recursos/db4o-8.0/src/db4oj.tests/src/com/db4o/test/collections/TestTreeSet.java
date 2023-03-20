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

import java.util.*;

import com.db4o.test.*;



/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TestTreeSet {
    
    private static final String[] CONTENT = new String[]{
        "a","f","d","c","b"
    };
    
    SortedSet stringTreeSet;
    
    SortedSet objectTreeSet;
    
    
    public void storeOne(){
        stringTreeSet = new TreeSet();
        stringContentTo(stringTreeSet);
        
        objectTreeSet = new TreeSet();
        objectContentTo(objectTreeSet);
    }
    
    
    public void testOne(){
        
        TreeSet stringCompareTo = new TreeSet();
        stringContentTo(stringCompareTo);
        
        TreeSet objectCompareTo = new TreeSet();
        objectContentTo(objectCompareTo);
        
        Test.ensure(stringTreeSet instanceof TreeSet);
        Test.ensure(stringTreeSet.size() == stringCompareTo.size());
        
        Test.ensure(objectTreeSet instanceof TreeSet);
        Test.ensure(objectTreeSet.size() == objectCompareTo.size());
        
        Iterator i = stringTreeSet.iterator();
        Iterator j = stringCompareTo.iterator();
        while(i.hasNext()){
            Test.ensure(i.next().equals(j.next()));
        }
        i = objectTreeSet.iterator();
        j = objectCompareTo.iterator();
        while(i.hasNext()){
            Test.ensure(i.next().equals(j.next()));
        }
        
    }
    
    private void stringContentTo(SortedSet set){
        for (int i = 0; i < CONTENT.length; i++) {
            set.add(CONTENT[i]);
        }
    }
    
    private void objectContentTo(SortedSet set){
        for (int i = 0; i < CONTENT.length; i++) {
            set.add(new ComparableContent(CONTENT[i]));
        }
    }

    
    
    

}
