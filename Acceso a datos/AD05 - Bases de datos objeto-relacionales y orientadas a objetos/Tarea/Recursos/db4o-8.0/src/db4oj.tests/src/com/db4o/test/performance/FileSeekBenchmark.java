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


public class FileSeekBenchmark {
    
    private static String FILE = "FileSeekBenchmark.file";
    
    private static final int COUNT = 100000;
    
    private static final int SIZE = 100000;

    public static void main(String[] args) throws IOException {
        new File(FILE).delete();
        RandomAccessFile raf = new RandomAccessFile(FILE, "rw");
        for (int i = 0; i < SIZE; i++) {
            raf.write(1);
        }
        raf.close();
        raf = new RandomAccessFile(FILE, "rw");
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            raf.seek(1);
            raf.read();
            raf.seek(SIZE - 2);
            raf.read();
        }
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        raf.close();
        new File(FILE).delete();
        System.out.println("Time for " + COUNT + " seeks in a " + SIZE + " bytes sized file:\n" + duration + "ms");
    }

}
