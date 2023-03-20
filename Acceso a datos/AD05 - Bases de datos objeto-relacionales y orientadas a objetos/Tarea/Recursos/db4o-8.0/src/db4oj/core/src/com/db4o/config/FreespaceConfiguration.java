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
package com.db4o.config;

/**
 * interface to configure the freespace system to be used.
 * <br><br>All methods should be called before opening database files.
 * If db4o is instructed to exchange the system 
 * ( {@link #useBTreeSystem()} , {@link #useRamSystem()} )
 * this will happen on opening the database file.<br><br>
 * By default the ram based system will be used.  
 */
public interface FreespaceConfiguration {
    
    /**
     * tuning feature: configures the minimum size of free space slots in the database file 
     * that are to be reused.
     * <br><br>When objects are updated or deleted, the space previously occupied in the
     * database file is marked as "free", so it can be reused. db4o maintains two lists
     * in RAM, sorted by address and by size. Adjacent entries are merged. After a large
     * number of updates or deletes have been executed, the lists can become large, causing
     * RAM consumption and performance loss for maintenance. With this method you can 
     * specify an upper bound for the byte slot size to discard. 
     * <br><br>Pass <code>Integer.MAX_VALUE</code> to this method to discard all free slots for
     * the best possible startup time.<br><br>
     * The downside of setting this value: Database files will necessarily grow faster. 
     * <br><br>Default value:<br>
     * <code>0</code> all space is reused
     * @param byteCount Slots with this size or smaller will be lost.
     */
    public void discardSmallerThan(int byteCount);
    
    /**
     * Configure a way to overwrite freed space in the database file with custom
     * (for example: random) bytes. Will slow down I/O operation.
     * 
     * The value of this setting may be cached internally and can thus not be
     * reliably set after an object container has been opened.
     * 
     * @param freespaceFiller The freespace overwriting callback to use
     */
    public void freespaceFiller(FreespaceFiller freespaceFiller);
    
    /**
     * configures db4o to use a BTree-based freespace system.
     * <br><br><b>Advantages</b><br>
     * - ACID, no freespace is lost on abnormal system termination<br>
     * - low memory consumption<br>
     * <br><b>Disadvantages</b><br>
     * - slower than the RAM-based system, since freespace information
     * is written during every commit<br>
     */
    public void useBTreeSystem(); 
    
    /**
     * discontinued freespace system, only available before db4o 7.0. 
     * @deprecated Please use the BTree freespace system instead by
     * calling {@link #useBTreeSystem()}.
     */
    public void useIndexSystem(); 

    /**
     * configures db4o to use a RAM-based freespace system.
     * <br><br><b>Advantages</b><br>
     * - best performance<br>
     * <br><b>Disadvantages</b><br>
     * - upon abnormal system termination all freespace is lost<br>
     * - memory consumption<br>
     */
    public void useRamSystem();
    
}
