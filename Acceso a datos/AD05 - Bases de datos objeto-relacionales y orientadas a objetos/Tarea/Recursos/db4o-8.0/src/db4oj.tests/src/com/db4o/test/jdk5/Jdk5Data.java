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
package com.db4o.test.jdk5;

import java.util.*;

@Jdk5Annotation(cascadeOnActivate=true, cascadeOnUpdate=true, maximumActivationDepth=3)
@decaf.Ignore
public class Jdk5Data<Item> {
    private Item item;
    // JDK1.5: typesafe enums
    private Jdk5Enum type;
    // JDK1.5: generics
    private List<Integer> list;
    
    public Jdk5Data(Item item,Jdk5Enum type) {
        this.item=item;
        this.type=type;
        list=new ArrayList<Integer>();
    }

    // JDK1.5: varargs
    public void add(int ... is) {
        // JDK1.5: enhanced for with array
        for(int i : is) {
            // JDK1.5: boxing
            list.add(i);
        }
    }
    
    public int getMax() {
        int max=Integer.MIN_VALUE;
        // JDK1.5: enhanced for with collection / unboxing
        
        for(int i : list) {
            max=Math.max(i,max);
        }
        
        return max;
    }
    
    public int getSize() {
        return list.size();
    }
    
    public Item getItem() {
        return item;
    }
    
    public Jdk5Enum getType() {
        return type;
    }
}
