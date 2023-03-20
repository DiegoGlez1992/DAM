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



public class FileDeleteBenchmark {

    private static final int COUNT = 1000;
    
    public static final String FILE = "FileDeleteBenchmark.file";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            RandomAccessFile raf = new RandomAccessFile(FILE + i, "rw");
            raf.write(1);
            raf.close();
        }
        for (int i = 0; i < COUNT; i++) {
            new File(FILE + i).delete();
        }
        
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        System.out.println("Time to create and delete " + COUNT + " files:\n" + duration + "ms");
    }

}
