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
package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;


public class AddJustOneObject {
    
    
    private static final String FILE = "ajoob.db4o";
    
    private static final int COUNT = 100000;
    

    public static void main(String[] args) {
        new File(FILE).delete();
        ObjectContainer oc = Db4o.openFile(FILE);
        for (int i = 0; i < COUNT; i++) {
            oc.store(new AddJustOneObject());
        }
        oc.close();
        
        oc = Db4o.openFile(FILE);
        long start = System.currentTimeMillis();
        oc.store(new AddJustOneObject());
        oc.commit();
        long stop = System.currentTimeMillis();
        oc.close();
        
        long duration = stop - start;
        
        System.out.println("Add one to " + COUNT + " and commit: " + duration + "ms");
    }

}
