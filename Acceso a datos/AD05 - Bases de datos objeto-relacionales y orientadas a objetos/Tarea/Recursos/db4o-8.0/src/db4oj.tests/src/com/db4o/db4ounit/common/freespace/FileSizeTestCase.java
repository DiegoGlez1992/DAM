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
package com.db4o.db4ounit.common.freespace;


import db4ounit.*;


public class FileSizeTestCase extends FreespaceManagerTestCaseBase {
    
    private static final int ITERATIONS = 100;

	public static void main(String[] args) {
		new FileSizeTestCase().runSolo();
	}
	
	public void testConsistentSizeOnDefragment(){
		storeSomeItems();
		db().commit();
        assertConsistentSize(new Runnable() {
            public void run() {
            	try {
					defragment();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
	}
	
	public void testConsistentSizeOnRollback(){
		storeSomeItems();
		produceSomeFreeSpace();
        assertConsistentSize(new Runnable() {
            public void run() {
                store(new Item());
                db().rollback();
            }
        });
	}
    
    public void testConsistentSizeOnCommit(){
        storeSomeItems();
        db().commit();
        assertConsistentSize(new Runnable() {
            public void run() {
                db().commit();
            }
        });
    }
    
    public void testConsistentSizeOnUpdate(){
        storeSomeItems();
        produceSomeFreeSpace();
        final Item item = new Item(); 
        store(item);
        db().commit();
        assertConsistentSize(new Runnable() {
            public void run() {
                store(item);
                db().commit();
            }
        });
    }
    
    public void testConsistentSizeOnReopen() throws Exception{
        db().commit();
        reopen();
        assertConsistentSize(new Runnable() {
            public void run() {
                try {
                    reopen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void testConsistentSizeOnUpdateAndReopen() throws Exception{
        produceSomeFreeSpace();
        store(new Item());
        db().commit();
        assertConsistentSize(new Runnable() {
            public void run() {
                store(retrieveOnlyInstance(Item.class));
                db().commit();
                try {
                    reopen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void assertConsistentSize(Runnable runnable){
        warmup(runnable);
        int originalFileSize = databaseFileSize();
        for (int i = 0; i < ITERATIONS; i++) {
//        	System.out.println(databaseFileSize());
            runnable.run();
        }
        Assert.areEqual(originalFileSize, databaseFileSize());
    }

	private void warmup(Runnable runnable) {
		for (int i = 0; i < 10; i++) {
//        	System.out.println(databaseFileSize());
            runnable.run();
        }
	}

}
